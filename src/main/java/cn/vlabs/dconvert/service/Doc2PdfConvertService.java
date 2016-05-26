/*
 * Copyright (c) 2008-2016 Computer Network Information Center (CNIC), Chinese Academy of Sciences.
 * 
 * This file is part of Duckling project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 *
 */

package cn.vlabs.dconvert.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.log4j.Logger;
import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeConnectionProtocol;
import org.artofsolving.jodconverter.office.OfficeException;
import org.artofsolving.jodconverter.office.OfficeManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.vlabs.dconvert.api.ConvertStatus;
import cn.vlabs.dconvert.api.rest.ConvertFailInfo;
import cn.vlabs.dconvert.exception.BaseException;
import cn.vlabs.dconvert.exception.DocNotFoundException;
import cn.vlabs.dconvert.model.PDFFile;
import cn.vlabs.dconvert.utils.Config;

import com.mongodb.gridfs.GridFSDBFile;
/**
 * Office文档转换成PDF文档的服务类
 * @author Yangxp
 *
 */
@Service
public class Doc2PdfConvertService {
	@Autowired
	private MongoService mongoService;
	private OfficeManager officeManager;
	private BlockingQueue<ConvertJob> queue;
	private ConvertThread convert;
	
	protected static Logger LOG = Logger.getLogger(Doc2PdfConvertService.class);
	
	/**
	 * 初始化OpenOffice相关的东西
	 */
	@PostConstruct
	public void init(){
		DefaultOfficeManagerConfiguration dmc = new DefaultOfficeManagerConfiguration();
		String home = Config.getInstance().getStringProp("dconvert.openOfficeHome", null);
		home = (null == home)?"C:\\Program Files\\OpenOffice.org 3":home;
		dmc.setOfficeHome(home);
		dmc.setConnectionProtocol(OfficeConnectionProtocol.SOCKET);
		dmc.setPortNumber(8100);
		officeManager=dmc.buildOfficeManager();
	    officeManager.start(); 
	    queue = new LinkedBlockingQueue<ConvertJob>();
	    convert = new ConvertThread("DocConvertThread");
	    convert.start();
	}
	/**
	 * 销毁officeManager
	 */
	@PreDestroy
	public void stop(){
		officeManager.stop();
		convert.setStop(true);
		convert.interrupt();
	}
	
	public void sendPdfTransformEvent(String srcStorageKey, String targetStorageKey){
		if(null != queue){
			try{
				ConvertJob job = new ConvertJob(srcStorageKey, targetStorageKey);
				if(!queue.contains(job)){
					queue.put(job);
				}
			}catch(InterruptedException e){
				saveConvertFailRecord(srcStorageKey, targetStorageKey,ConvertStatus.INTERNAL_ERROR, "添加文档转换任务失败");
				LOG.error("添加文档转换任务失败，无法添加到队列("+srcStorageKey+","+targetStorageKey+")！",e);
			}
		}
	}
	/**
	 * 文档转换
	 * @param srcStorageKey 源文档的storageKey
	 * @param targetStorageKey PDF的storageKey
	 * @return
	 */
	private void doTransform(String srcStorageKey, String targetStorageKey){
		GridFSDBFile dbFile = null;
		try {
			//读取源文件
			dbFile = mongoService.getDocContent(srcStorageKey);
			if(null == dbFile){
				LOG.error("MongoDB中未找到该文件("+srcStorageKey+")！");
				saveConvertFailRecord(srcStorageKey, targetStorageKey,ConvertStatus.NOT_FOUND, "未找到待转换的文件");
				return;
			}
			//准备临时文件 
			File[] tempFile = prepareTempFile(dbFile);
			//转换成PDF并保存
			transform(tempFile[0], tempFile[1], targetStorageKey);
			//删除临时文件
			deleteTempFile(tempFile);
		} catch (DocNotFoundException e) {
			saveConvertFailRecord(srcStorageKey, targetStorageKey, ConvertStatus.NOT_FOUND, "未找到待转换的文件");
			LOG.error("找不到文档！MongoDB中的文档ObjectID为("+srcStorageKey+")！",e);
		} catch (BaseException e) {
			saveConvertFailRecord(srcStorageKey, targetStorageKey,ConvertStatus.IO_ERROR, "文档转换完成，但保存PDF失败");
			LOG.error("在数据库中保存PDF失败！MongoDB中的文档ObjectID为("+srcStorageKey+")！",e);
		} catch (OfficeException e){
			saveConvertFailRecord(srcStorageKey, targetStorageKey,ConvertStatus.OFFICE_ERROR, "文档转换失败，解析待转换文件时发生错误");
			LOG.error("OpenOffice转换PDF失败！MongoDB中的文档ObjectID为("+srcStorageKey+")！",e);
		} catch (IOException e) {
			saveConvertFailRecord(srcStorageKey, targetStorageKey,ConvertStatus.IO_ERROR, "文档转换失败，写文件时发生错误");
			LOG.error("写临时文件失败！",e);
		}
	}
	/**
	 * 利用从Mongodb中读取的原始文件，构造用于文档转换的两个临时文件。
	 * @param dbFile 原始文件流
	 * @return 用于文档转换的输入文件和输出文件
	 * @throws IOException 
	 */
	private File[] prepareTempFile(GridFSDBFile dbFile) throws IOException{
		String title=dbFile.getFilename();
		String newTitle=title.substring(0, title.lastIndexOf(".")+1)+"pdf";
		File inputFile=writeToFile(dbFile.getInputStream(),title); //写临时文件
		File outputFile=new File(newTitle);
		return new File[]{inputFile, outputFile};
	}
	/**
	 * OpenOffice将inputFile转换成outputFile，并将outputFile保存到mongodb
	 * @param inputFile Office文档
	 * @param outputFile PDF文档
	 * @param targetStorageKey 保存PDF时的唯一性键值
	 * @return
	 * @throws BaseException 
	 */
	private void transform(File inputFile, File outputFile, String targetStorageKey) throws BaseException{
		
		String newTitle = outputFile.getName();
		OfficeDocumentConverter converter = new OfficeDocumentConverter(officeManager);
		converter.convert(inputFile, outputFile);
		PDFFile pdfFile = new PDFFile(newTitle, outputFile);
		mongoService.savePDFContent(targetStorageKey, pdfFile);
		LOG.info("转换完成："+newTitle);
	}
	
	private void deleteTempFile(File[] tempFile){
		if(null != tempFile && tempFile.length>0){
			for(File file : tempFile){
				file.delete();
			}
		}
	}
	
	private void saveConvertFailRecord(String srcKey, String targetKey, int status, String message){
		ConvertFailInfo failInfo = new ConvertFailInfo(srcKey, targetKey, 
				new Date(), status, message);
		mongoService.saveConvertFailInfo(failInfo);
	}
	
	/**
	 * 写临时文件
	 * @param ins 文件流
	 * @param filename 文件名
	 * @return 临时文件对象
	 * @throws IOException 
	 */
	private File writeToFile(InputStream ins,String filename) throws IOException{
		File file=new File(filename);
		FileOutputStream fos=null;
		file.createNewFile();
		fos=new FileOutputStream(file);
		byte[] b=new byte[1024];
		int len=0;
		while((len=ins.read(b))>0){
			fos.write(b,0,len);
		}
		closeStream(ins, fos);
		return file;
	}
	
	private void closeStream(InputStream ins, OutputStream out) throws IOException{
		if(null!=ins){
			ins.close();
		}
		if(null!=out){
			out.close();
		}
	}

	private class ConvertThread extends Thread{
		
		private boolean stop = false;
		
		public ConvertThread(String message){
			super(message);
		}
		public void setStop(boolean stop){
			this.stop = stop;
		}
		
		@Override
		public void run() {
			while(!stop){
				try {
					ConvertJob job = queue.take();
					LOG.info("开始转换：("+job.getSrcKey()+","+job.getTargetKey()+")");
					doTransform(job.getSrcKey(), job.getTargetKey());
					LOG.info("转换结束：("+job.getSrcKey()+","+job.getTargetKey()+")");
				} catch (InterruptedException e) {
					String key = Math.random()+"error";
					saveConvertFailRecord(key, key, ConvertStatus.INTERNAL_ERROR,"文档转换失败，线程意外终止");
					LOG.error("文档转换失败，线程意外终止("+key+","+key+")！",e);
				}
				
			}
		}
		
	}

	public boolean isWaitingForConvert(String targetStorageKey) {
		if(null != queue){
			for(ConvertJob cj : queue){
				if(cj.getTargetKey().equals(targetStorageKey)){
					return true;
				}
			}
		}
		return false;
	}
}
