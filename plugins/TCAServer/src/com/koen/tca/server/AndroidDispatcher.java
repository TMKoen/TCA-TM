package com.koen.tca.server;

import com.netxforge.netxtest.dragonX.Action;
import com.netxforge.netxtest.dragonX.DragonXPackage;
import com.netxforge.netxtest.dragonX.Parameter;
import com.netxforge.netxtest.dragonX.ParameterSet;
import com.netxforge.netxtest.dragonX.UE;
import com.netxforge.netxtest.dragonX.UEMetaObject;
import com.netxforge.netxtest.interpreter.IExternalDispatcher;

/**
 * A dispatcher which deals with DragonX Test actions.
 */
public class AndroidDispatcher implements IExternalDispatcher {

	IExternalDispatcher DEFAULT = new IExternalDispatcher() {

		@Override
		public void dispatch(Action action) {

			System.out.println("Dispatching Action: " + action.getName());
			ParameterSet parameterSet = action.getParameterSet();
			for (Parameter p : parameterSet.getParameters()) {

				if (p.eIsSet(DragonXPackage.Literals.PARAMETER__VALUE)) {
					System.out.println(" parameter: " + p.getName() + " value:"
							+ p.getValue());
				} else if (p.eIsSet(DragonXPackage.Literals.PARAMETER__UE_REF)) {
					System.out.println(" parameter: " + p.getName() + " UE:"
							+ p.getUeRef());

					// UE Parameters....

					UE ue = p.getUeRef();
					for (UEMetaObject ueObject : ue.getMeta()) {
						System.out.println(" UE param: " + ueObject.getParams()
								+ ueObject.getParamValue());
					}
				}
			}
		}

	};

	@Override
	public void dispatch(Action action) {
		DEFAULT.dispatch(action);
	}

}
