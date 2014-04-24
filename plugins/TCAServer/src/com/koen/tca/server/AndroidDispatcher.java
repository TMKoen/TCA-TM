package com.koen.tca.server;

import com.netxforge.netxtest.dragonX.Action;
import com.netxforge.netxtest.dragonX.Parameter;
import com.netxforge.netxtest.interpreter.IExternalDispatcher;

/**
 * A dispatcher which deals with DragonX Test actions.
 */
public class AndroidDispatcher implements IExternalDispatcher {

	IExternalDispatcher DEFAULT = new IExternalDispatcher() {

		@Override
		public void dispatch(Action action) {

			System.out.println("Dispatching Action: " + action.getAction());

			for (Parameter p : action.getParameters()) {
				System.out.println(" parameter: " + p.getName() + " value:"
						+ p.getValue());
			}
		}

	};

	@Override
	public void dispatch(Action action) {
		// DISPATCH THE ACTION to
	}

}
