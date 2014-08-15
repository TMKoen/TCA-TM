package com.koen.tca.server.state;

public class ServerStateIdle extends AbstractServerState {

	public ServerStateIdle() {

	}

	/**
	 * returns a new IServerState object.
	 * <p>
	 * 
	 * @version
	 * @author Koen Nijmeijer
	 * @param serverEvent
	 *            the ServerEvent that change the present state.
	 * @return the new IServerState object or the old one (this) if it can't
	 *         change te state.
	 */
	@Override
	public IServerState changeState(ServerEvents serverEvent) {

		IServerState state = this;
		switch (serverEvent) {
		case START_DETECT:
			state = getContext().createState(STATES.DETECT);
			break;
		default:
			// Other ServerEvents are not allowed.
			break;
		}
		return state;
	}

	@Override
	public String toString() {
		return "I am currently idle... tap tap..., any detection work for me?";
	}
}
