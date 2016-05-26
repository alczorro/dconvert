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


package cn.vlabs.dconvert.frameservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.vlabs.dconvert.api.rest.ConvertFailInfo;
import cn.vlabs.dconvert.exception.BaseException;
import cn.vlabs.dconvert.service.Doc2PdfConvertService;
import cn.vlabs.dconvert.service.MongoService;

/**
 * 这个是文档服务的统一访问入口。 作为一个统一的访问Facade目的是减少别的模块和该模块之间的直接依赖关系。
 * 目前已经有三个访问入口需要使用到这里的访问代码。将这些代码独立出来是有实际意义的。
 * 
 * @author Yangxp
 * @since 2012-01-03
 */
@Component("DocumentFacade")
public class DocumentFacade {
	@Autowired
	private Doc2PdfConvertService transform;
	@Autowired
	private MongoService mongoService;

	/**
	 * 转换文档
	 * @param srcStorageKey 原文档的storageKey
	 * @param targetStorageKey pdf文档的storageKey
	 * @throws BaseException
	 */
	public void convertDoc(String srcStorageKey, String targetStorageKey) throws BaseException {
		transform.sendPdfTransformEvent(srcStorageKey, targetStorageKey);
	}
	
	public ConvertFailInfo isConvertError(String targetStorageKey){
		return mongoService.isConvertError(targetStorageKey);
	}

	public Object isWaitingForConvert(String targetStorageKey) {
		return transform.isWaitingForConvert(targetStorageKey);
	}
}
