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
/**
 * 转换失败的集中状态
 * @author Yangxp
 * @since 2013-01-19
 */
public class ConvertStatus {
	/**
	 * 读写文件异常
	 */
	public static final int IO_ERROR = 1;
	/**
	 * OpenOffice转换文档时的异常
	 */
	public static final int OFFICE_ERROR = 2;
	/**
	 * 找不到文件的异常
	 */
	public static final int NOT_FOUND = 3;
	/**
	 * 内部异常，一般为线程发生异常
	 */
	public static final int INTERNAL_ERROR = 4;
}
