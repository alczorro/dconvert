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


package cn.vlabs.dconvert.api.transform;

import cn.vlabs.dconvert.api.rest.ConvertFailInfo;

/**
 * 文档转换服务：接受可转换文档在MongoDB中的ObjectID，然后读取并转换成
 * PDF，转换完的PDF将存入MongoDB中.
 * @author Yangxp
 * @since 2012-01-03
 */
public interface DocConvertService {
	/**
	 * 将Office文档转换为PDF文档。
	 * @param srcStorageKey 待转换文档在MongoDB中的ObjectId
	 * @param targetStorageKey PDF文档在MongoDB中的storageKey
	 */
	void convert(String srcStorageKey, String targetStorageKey);
	/**
	 * 查询指定PDF是否转换失败
	 * @param targetStorageKey pdf的storageKey
	 * @return
	 */
	ConvertFailInfo isConvertError(String targetStorageKey);
	/**
	 * 查询文档转换是否处于等待状态
	 * @param targetStorageKey pdf的storageKey
	 * @return true or false
	 */
	boolean isWaitingForConvert(String targetStorageKey);
}
