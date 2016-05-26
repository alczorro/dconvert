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

import java.io.IOException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.vlabs.dconvert.api.rest.ConvertFailInfo;
import cn.vlabs.dconvert.exception.BaseException;
import cn.vlabs.dconvert.exception.DocNotFoundException;
import cn.vlabs.dconvert.model.PDFFile;
import cn.vlabs.dconvert.utils.Config;
import cn.vlabs.dconvert.utils.MimeType;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
/**
 * 文档内容服务：从MongoDB读取和写入文档
 * @author Yangxp
 * @since 2012-01-03
 */
@Service
public class MongoService {
	private static String DB = Config.getInstance().getStringProp("clb.mongo.db", "docs");
	private static String DB_COLL_DOCS = Config.getInstance().getStringProp("clb.mongo.db.docs", "fs");
	private static String DB_COLL_PDF = Config.getInstance().getStringProp("clb.mongo.db.pdf", "pdf");
	private static String DB_COLL_FAIL = Config.getInstance().getStringProp("clb.mongo.db.fail", "fail");
	private static final String COLL_KEY_STORAGEKEY = "storageKey";
	private static final String COLL_KEY_CONTENT_TYPE = "contentType";
	
	
	private Logger log=Logger.getLogger(MongoService.class);

	@Autowired
	private Mongo mongo;

	/**
	 * 保存PDF文件, 若targetStorageKey对应的PDF已存在，则删除之
	 * @param targetStorageKey
	 * @param readable
	 * @return 新保存的PDF的ObjectID
	 * @throws BaseException
	 */
	public void savePDFContent(String targetStorageKey, PDFFile pdf) throws BaseException {
		try {
			if (mongo != null) {
				DB db = mongo.getDB(DB);
				GridFS fs = new GridFS(db, DB_COLL_PDF);
				removeIfExist(fs, db, targetStorageKey);
				GridFSInputFile inputfile = fs.createFile(pdf
							.getInputStream(), pdf.getFileName());
				inputfile.put(COLL_KEY_STORAGEKEY, new ObjectId(targetStorageKey));
				inputfile.put(COLL_KEY_CONTENT_TYPE, MimeType.getContentType("pdf"));
				inputfile.save();
			} else {
				log.error("mongo==null，可能MongoDB无法连接或连接失败");
			}
		} catch (IOException e) {
			log.error("文件在保存到MongoDB时失败！",e);
		}
	}

	/**
	 * 获取原始文件内容
	 * @param _id ObjectId
	 * @return
	 * @throws DocNotFoundException
	 */
	public GridFSDBFile getDocContent(String _id) throws DocNotFoundException {
		if (mongo != null) {
			DB db = mongo.getDB(DB);
			GridFS fs = new GridFS(db, DB_COLL_DOCS);
			BasicDBObject query=new BasicDBObject();
			query.append(COLL_KEY_STORAGEKEY, new ObjectId(_id));
			GridFSDBFile dbfile = fs.findOne(query);
			if (dbfile != null) {
				return dbfile;
			} else {
				log.error("Requested file with storageKey=" + _id + " is not found.");
				throw new DocNotFoundException("Requested file with storageKey=" + _id + " is not found.");
			}
		} else {
			log.error("mongo==null，可能MongoDB关闭或者连接失败！");
		}
		return null;
	}
	
	/**
	 * 保存转换失败信息
	 * @param failInfo
	 */
	public void saveConvertFailInfo(ConvertFailInfo failInfo){
		DB db = mongo.getDB(DB);
		DBCollection coll = db.getCollection(DB_COLL_FAIL);
		//若存在则删除targetKey的失败信息
		DBObject query = new BasicDBObject();
		query.put(ConvertFailInfo.TARGET_STORAGE_KEY, failInfo.getTargetStorageKey());
		coll.remove(query);
		//保存新的转换失败信息
		DBObject obj = new BasicDBObject();
		obj.put(ConvertFailInfo.SRC_STORAGE_KEY, failInfo.getSrcStorageKey());
		obj.put(ConvertFailInfo.TARGET_STORAGE_KEY, failInfo.getTargetStorageKey());
		obj.put(ConvertFailInfo.CREATE_TIME, failInfo.getCreateTime());
		obj.put(ConvertFailInfo.STATUS, failInfo.getStatus());
		obj.put(ConvertFailInfo.REASON, failInfo.getReason());
		coll.insert(obj);
	}
	
	public ConvertFailInfo isConvertError(String targetStorageKey){
		DB db = mongo.getDB(DB);
		DBCollection coll = db.getCollection(DB_COLL_FAIL);
		DBObject query = new BasicDBObject();
		query.put(ConvertFailInfo.TARGET_STORAGE_KEY, targetStorageKey);
		DBObject obj = coll.findOne(query);
		if(null != obj){
			return new ConvertFailInfo((String)obj.get(ConvertFailInfo.SRC_STORAGE_KEY),
					(String)obj.get(ConvertFailInfo.TARGET_STORAGE_KEY),
					(Date)obj.get(ConvertFailInfo.CREATE_TIME),
					(Integer)obj.get(ConvertFailInfo.STATUS),
					(String)obj.get(ConvertFailInfo.REASON));
		}else{
			return null;
		}
	}
	
	/**
	 * 如果targetStorageKey对应的PDF已存在，则删除；否则不进行操作
	 * @param fs GridFS
	 * @param db DB
	 * @param targetStorageKey ObjectId
	 */
	private void removeIfExist(GridFS fs, DB db, String targetStorageKey){
		ObjectId _oldId=new ObjectId(targetStorageKey);
		DBCollection coll=db.getCollection(DB_COLL_PDF+".files");
		BasicDBObject query=new BasicDBObject();
		query.append(COLL_KEY_STORAGEKEY, _oldId);
		DBObject obj=coll.findOne(query);
		if(null != obj){
			fs.remove(query);
		}
	}
}
