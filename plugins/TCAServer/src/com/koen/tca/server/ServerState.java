package com.koen.tca.server;
public interface ServerState {

	public void ChangeState (ServerEvents serverEvent, ServerStateMachine context);
	public void ActivateState ();
}
