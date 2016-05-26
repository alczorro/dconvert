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


package cn.vlabs.dconvert.api;

import cn.vlabs.dconvert.api.rest.DocConvertServiceImpl;
import cn.vlabs.dconvert.api.transform.DocConvertService;

/**
* @Description: 产生服务的工厂类，app通过这个类拿到需要的service实例
* @author CERC
* @date Jul 12, 2011 9:10:24 AM
*
*/
public class DConvertServiceFactory {
	public static String getVersion() {
		return "1.0.0";
	}

	public static DocConvertService getDConvertService(String url) {
		return new DocConvertServiceImpl(getDConvertConnection(url, false));
	}

	public static DocConvertService getDConvertService(DConvertConnection conn) {
		return new DocConvertServiceImpl(conn);
	}

	public static DConvertConnection getDConvertConnection(String url, boolean alive){
		return buildDConvertConnection(true, url, alive);
	}
	
	private static DConvertConnection buildDConvertConnection(boolean autoConnect, String url, boolean alive){
		DConvertConnection conn = new DConvertConnection(url);
		conn.setKeepAlive(alive);
		return conn;
	}
}
