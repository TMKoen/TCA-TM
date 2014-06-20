package com.koen.tca.server.state;

public class ServerStateIdle extends AbstractServerState {

	public ServerStateIdle() {

	}

	@Override
	public void changeState(ServerEvents serverEvent) {

		switch (serverEvent) {
		case START_DETECT:
			getContext().setState(new ServerStateDetect());
			break;
		default:
			break;
		}
	}

	@Override
	public String toString() {
		return "I am currently idle... tap tap..., any detection work for me?";
	}
}
