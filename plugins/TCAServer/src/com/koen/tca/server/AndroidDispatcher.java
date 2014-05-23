package com.koen.tca.server;

import com.koen.tca.server.state.DetectResult;
import com.netxforge.netxtest.dragonX.Action;
import com.netxforge.netxtest.dragonX.DragonXPackage;
import com.netxforge.netxtest.dragonX.Parameter;
import com.netxforge.netxtest.dragonX.ParameterSet;
import com.netxforge.netxtest.dragonX.UE;
import com.netxforge.netxtest.dragonX.UEMetaObject;
import com.netxforge.netxtest.dragonX.UEPARAMS;

/**
 * A dispatcher which deals with DragonX Test actions.
 */
public class AndroidDispatcher implements IExternalDispatcher {

	IExternalDispatcher DEFAULT = new IExternalDispatcher() {

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

					// The IMEI of the UE Action.

					String imei = null;

					for (UEMetaObject meta : ue.getMeta()) {
						if (meta.getParams() == UEPARAMS.IMEI) {
							imei = meta.getParamValue();

						}

						if (imei != null) {
							for (UEInfo info : DetectResult.SINGLETON()
									.getValidUEList()) {

								if (imei.equalsIgnoreCase(info.getImei())) {
									info.getIPAddress();

									// Call your RMI to dispatch to this IP.

								}

							}
						} else {
							System.out.println("NO IMEI MATCH FOR ACTION: "
									+ action);
						}

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
