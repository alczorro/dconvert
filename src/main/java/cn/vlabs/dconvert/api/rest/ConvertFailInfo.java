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
import java.util.Date;

/**
 * 文档转换失败记录
 * @author Yangxp
 * @since 2012-01-08
 */
public class ConvertFailInfo implements Serializable{
	
	public static final String SRC_STORAGE_KEY = "srcKey";
	public static final String TARGET_STORAGE_KEY = "targetKey";
	public static final String CREATE_TIME = "createTime";
	public static final String STATUS = "status";
	public static final String REASON = "reason";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String srcStorageKey;
	private String targetStorageKey;
	private Date createTime;
	private int status;
	private String reason;
	
	public ConvertFailInfo(){}
	
	public ConvertFailInfo(String srcKey, String targetKey, Date createTime, int status, String reason){
		this.srcStorageKey = srcKey;
		this.targetStorageKey = targetKey;
		this.createTime = createTime;
		this.status = status;
		this.reason = reason;
	}
	
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
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}

	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	
}
