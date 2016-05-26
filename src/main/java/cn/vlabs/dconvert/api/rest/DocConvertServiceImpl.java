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


package cn.vlabs.dconvert.api.rest;

import cn.vlabs.dconvert.api.DConvertConnection;
import cn.vlabs.dconvert.api.transform.DocConvertService;

public class DocConvertServiceImpl implements DocConvertService {
	private static final String CONVERT_DOCUMENT = "document.dconvert";
	private static final String IS_CONVERT_ERROR = "document.error";
	private static final String IS_WAITING = "document.waiting";

	private DConvertConnection conn;

	public DocConvertServiceImpl(DConvertConnection conn) {
		this.conn = conn;
	}

	@Override
	public void convert(String srcStorageKey, String targetStorageKey) {
		StorageKeyParams skp = StorageKeyParams.build(srcStorageKey, targetStorageKey);
		conn.sendService(CONVERT_DOCUMENT, skp);
	}
	
	public ConvertFailInfo isConvertError(String targetStorageKey){
		return (ConvertFailInfo)conn.sendService(IS_CONVERT_ERROR, targetStorageKey);
	}

	@Override
	public boolean isWaitingForConvert(String targetStorageKey) {
		return (Boolean)conn.sendService(IS_WAITING, targetStorageKey);
	}

}
