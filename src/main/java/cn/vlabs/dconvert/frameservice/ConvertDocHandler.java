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

import org.springframework.context.ApplicationContext;

import cn.vlabs.dconvert.api.ErrorCode;
import cn.vlabs.dconvert.api.rest.StorageKeyParams;
import cn.vlabs.dconvert.utils.ApplicationContextProvider;
import cn.vlabs.rest.ServiceException;
/**
 * 处理转换文档请求
 * @author Yangxp
 * @since 2012-01-03
 */
public class ConvertDocHandler extends DConvertAbstractAction {
	@Override
	protected Object doAction(Object arg)
			throws ServiceException {
		StorageKeyParams params = (StorageKeyParams)arg;
		try {
			ApplicationContext cxt=ApplicationContextProvider.getApplicationContext();
			DocumentFacade df=cxt.getBean("DocumentFacade",DocumentFacade.class);
			df.convertDoc(params.getSrcStorageKey(), params.getTargetStorageKey());
		} catch (Exception e) {
			log.error(e);
			throw new ServiceException(ErrorCode.INTERNAL_ERROR, e.getMessage());
		}
		return null;
	}
}
