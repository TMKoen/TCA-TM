package com.koen.tca.server.state;

import com.koen.tca.server.ICallBackClient;

/**
 * 
 * @author Christophe
 * 
 */
public abstract class AbstractServerState implements IServerState {

	private ICallBackClient callBack;

	@Override
	public ICallBackClient getCallBack() {
		return callBack;
	}

}
