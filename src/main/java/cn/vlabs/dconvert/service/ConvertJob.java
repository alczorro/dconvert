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

/**
 * 一个文档转换任务
 * @author Yangxp
 * @since 2012-01-09
 */
public class ConvertJob {
	private String srcKey;
	private String targetKey;
	
	public ConvertJob(String srcKey, String targetKey){
		this.srcKey = srcKey;
		this.targetKey = targetKey;
	}

	public String getSrcKey() {
		return srcKey;
	}

	public String getTargetKey() {
		return targetKey;
	}

	@Override
	public int hashCode() {
		return srcKey.hashCode()*17+targetKey.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(null == obj){
			return false;
		}
		if(obj.getClass() != ConvertJob.class){
			return false;
		}
		ConvertJob tmp = (ConvertJob)obj;
		if(srcKey.equals(tmp.getSrcKey()) && targetKey.equals(tmp.getTargetKey())){
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "[ConvertJob:{srcKey:"+srcKey+", targetKey:"+targetKey+"}]";
	}
	
}
