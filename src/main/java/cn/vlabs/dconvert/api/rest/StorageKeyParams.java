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

import java.io.Serializable;

/**
 * 传递给DConvert的参数：srcStorageKey和targetStorageKey
 * @author Yangxp
 * @since 2012-01-04
 */
public class StorageKeyParams implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String srcStorageKey;
	private String targetStorageKey;
	
	public String getSrcStorageKey() {
		return srcStorageKey;
	}
	public void setSrcStorageKey(String srcStorageKey) {
		this.srcStorageKey = srcStorageKey;
	}
	public String getTargetStorageKey() {
		return targetStorageKey;
	}
	public void setTargetStorageKey(String targetStorageKey) {
		this.targetStorageKey = targetStorageKey;
	}
	
	public static StorageKeyParams build(String srcSK, String targetSK){
		StorageKeyParams skp = new StorageKeyParams();
		skp.setSrcStorageKey(srcSK);
		skp.setTargetStorageKey(targetSK);
		return skp;
	}
}
