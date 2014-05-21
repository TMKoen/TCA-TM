package com.koen.tca.server.internal;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.koen.tca.server.AndroidDispatcher;
import com.koen.tca.server.DragonXInvoker;
import com.koen.tca.server.FolderPollingService;
import com.koen.tca.server.IRemoteServer;
import com.koen.tca.server.TCAServer;
import com.koen.tca.server.TestServer;
import com.koen.tca.server.state.ServerStateMachine;
import com.netxforge.netxtest.interpreter.IExternalDispatcher;

public class TCAModule implements Module {

	@Override
	public void configure(Binder binder) {

		// Bindings for the TCAServer.

		/**
		 * Binds our server.
		 */
		binder.bind(TCAServer.class);

		/**
		 * A service which provides Remote Method Invokation facilities to
		 * interact with the TCA Server.
		 */
		binder.bind(IRemoteServer.class).to(TestServer.class);

		/**
		 * The state of our server. 
		 */
		binder.bind(ServerStateMachine.class);

		/**
		 * A service for invoking an interpreter with a given dragonX script.
		 * initialize, by annotating a class @Inject
		 */
		binder.bind(DragonXInvoker.class);

		/**
		 * A service which polls a certain file location and provides access to
		 * the files through URI's which other EMF based services can consume.
		 * (Like Xtext).
		 */
		binder.bind(FolderPollingService.class);

		/**
		 * A service which can dispatch DragonX Action's
		 */
		binder.bind(IExternalDispatcher.class).to(AndroidDispatcher.class);

	}
}
