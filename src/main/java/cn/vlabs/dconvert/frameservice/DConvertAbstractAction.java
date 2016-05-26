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

import org.apache.log4j.Logger;

import cn.vlabs.dconvert.api.ErrorCode;
import cn.vlabs.dconvert.exception.BaseException;
import cn.vlabs.rest.RestSession;
import cn.vlabs.rest.ServiceAction;
import cn.vlabs.rest.ServiceException;

public abstract class DConvertAbstractAction implements ServiceAction {
	protected static Logger log = Logger.getLogger(DConvertAbstractAction.class);
	private RestSession session;

	public Object doAction(RestSession session, Object arg)
			throws ServiceException {
		try {
			return doAction(arg);
		} catch (BaseException e) {
			log.error(e.toLog());
			throw new ServiceException(ErrorCode.INTERNAL_ERROR, e.getMessage());
		}
	}

	protected abstract Object doAction(Object arg) throws ServiceException,
			BaseException;

	protected RestSession getSession() {
		return this.session;
	}
}
