package com.koen.tca.server.internal;

import java.util.Collections;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.eclipse.xtext.util.Modules2;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.google.common.collect.Maps;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.koen.tca.server.TCAServer;
import com.koen.tca.server.state.IServerState;
import com.koen.tca.server.state.ServerEvents;
import com.netxforge.netxtest.DragonXRuntimeModule;

/**
 * OSGI Activator class that starts and stops the <code>TCAServer</code> class.
 * <p>
 * This activator class implements the <code>bundleActivator</code> interface
 * It also implements the <code>CommandProvider</code> interface 
 * <br>
 * 
 * @version
 * @author Koen Nijmeijer, Christophe Bouhier
 * @see BundleActivator
 * @see TCAServer
 * 
 */
public class TCAServerActivator implements BundleActivator, CommandProvider {

	// Initialize the key string for the injector object (key/value pair)
	public static final String COM_NETXFORGE_NETXTEST_DRAGONX = "com.netxforge.netxtest.DragonX";

	// Creates a error logger for the TCAServerActivator
	private static final Logger logger = Logger
			.getLogger(TCAServerActivator.class);

	// Singleton object instance
	private static TCAServerActivator INSTANCE;

	// create a key/value Hash collection to store the Dependency injector
	// object.
	private Map<String, Injector> injectors = Collections.synchronizedMap(Maps
			.<String, Injector> newHashMapWithExpectedSize(1));

	// The base server object.
	private TCAServer server;

	/**
	 * Starts the Server bundle through the <code>TCAServer</code> class.
	 * <p>
	 * This method is the starting point of the server application. Every module that
	 *  runs under Equinox - OSGI framework must have this method. It is override from 
	 *  the <code>BundleActivator</code> interface.
	 * 
	 * @version
	 * @author Koen Nijmeijer, Christophe Bouhier
	 * @see TCAServer
	 * @throws Exception
	 */
	@Override
	public void start(BundleContext context) throws Exception {

		// save instance handler of itself
		INSTANCE = this;

		// We contribute OSGI commands here.
		context.registerService(CommandProvider.class.getName(), this, null);

		// guice makes a new TCAServer object.
		server = this.getInjector().getInstance(TCAServer.class);
		server.start();

	}

	/**
	 * Stops the Server bundle.
	 * <p>
	 * This method stops the server application module under the equinox - OSGI framework.
	 * Every OSGI equinox Module must have a stop method to close all resources nicely.
	 * 
	 * @version
	 * @author Koen Nijmeijer, Christophe Bouhier
	 * @see TCAServer
	 * @throws Exception
	 */
	@Override
	public void stop(BundleContext context) throws Exception {

		// Stops the TCAServer object
		server.stop();
		
		// Clears the instance of itself.
		INSTANCE = null;
	}

	/**
	 * Returns the instance of this object
	 * <p> 
	 * @version
	 * @author Christophe Bouhier
	 * @return the instance of this object (this).
	 */
	public static TCAServerActivator getInstance() {
		return INSTANCE;
	}

	/**
	 * Create a Dependency Injection {@link Injector}.
	 * <p>
	 * The inject binds the XText module for the given grammar if it matches
	 * {@value #COM_NETXFORGE_NETXTEST_DRAGONX}. It also binds other services
	 * from this class specified in the {@link TCAModule}
	 * 
	 * @version
	 * @author Christophe Bouhier
	 * @param language
	 * @return a new created injector
	 * @throws Exception
	 * @throws RuntimeException
	 */
	protected Injector createInjector(String language) {
		try {
			// Gets the RunTimeModule and the TCAModule and merges them together
			// to one Module.
			Module runtimeModule = getRuntimeModule();
			Module sharedStateModule = getTCAModule();
			Module mergedModule = Modules2.mixin(runtimeModule,
					sharedStateModule);

			// Creates a new injector with this merged module.
			return Guice.createInjector(mergedModule);
		} catch (Exception e) {

			// Log the Exception and throws a RuntimeException
			logger.error("Failed to create injector for " + language);
			logger.error(e.getMessage(), e);
			throw new RuntimeException("Failed to create injector for "
					+ language, e);
		}
	}

	/**
	 * Gets the Dependency injection from the key value:
	 * {@value #COM_NETXFORGE_NETXTEST_DRAGONX}.
	 * <p>
	 * Gets the injector from the injectors Map. If this Map is empty, make a
	 * new injector and stores it in the Map.
	 * 
	 * @param language
	 * @return The injector
	 */
	public Injector getInjector() {
		String language = COM_NETXFORGE_NETXTEST_DRAGONX;

		// Make it Thread safe: only one Thread can access the injectors field.
		synchronized (injectors) {

			// Gets the injector stored in the Map hash-collector with the key
			// name equals language.
			// If the injectors Map is empty, add a new key/value pair in the
			// injectors Map and return the new injector.
			Injector injector = injectors.get(language);
			if (injector == null) {
				// Creates a new Injector and put it in the Map (injectors)
				// collection
				// There can be only one injector object in this Map.
				injectors.put(language, injector = createInjector(language));
			}
			return injector;
		}
	}

	/**
	 * Gets the TCAModule module.
	 * <p>
	 * 
	 * @return the TCAModule module
	 * @see TCAModule
	 */
	private Module getTCAModule() {
		return new TCAModule();
	}

	/**
	 * Gets the DragonXRuntimeModule.
	 * <p>
	 * 
	 * @param grammar
	 *            string
	 * @return the DragonXRuntimeModule module
	 * @see DragonXRuntimeModule
	 */
	private Module getRuntimeModule() {
		return new com.netxforge.netxtest.DragonXRuntimeModule();
	}

	/**
	 * Specifies the shell commands that can be used on the OSGI console to control the Server.
	 * <p> 
	 * All methods that begin with '_' and has a CommandInterpreter arguments, are used by OSGI to
	 * add new commands for the console. 
	 * @version
	 * @author Christophe Bouhier
	 * @param intp
	 * @return
	 */
	public Object _tca(CommandInterpreter intp) {
		String nextArgument = intp.nextArgument();
		if (nextArgument != null) {
			if (nextArgument.equals("state")) {
				IServerState state = this.server.getTestServer()
						.getStateMachine().getState();
				StringBuilder sb = new StringBuilder();

				sb.append("The current server state is: " + state);

				String nextArgument2 = intp.nextArgument();
				if (nextArgument2 != null) {
					if (nextArgument2.equals("details")) {
						sb.append("\n Details:\n-------------\n"
								+ state.details());
					} else {
					}
				}
				// Do something with the TCA Server state.
				return sb.toString();
			} else if (nextArgument.equals("event")) {
				String nextArgument3 = intp.nextArgument();
				if (nextArgument3 != null) {

					if (nextArgument3.equals("start_detect")) {

						this.server.getTestServer().getStateMachine()
							
						.changeState(ServerEvents.START_DETECT);

						return "fire event: " + ServerEvents.START_DETECT;

					} else if (nextArgument3.equals("stop_detect")) {

						this.server.getTestServer().getStateMachine()
								.changeState(ServerEvents.STOP_DETECT);

						return "fire event:" + ServerEvents.STOP_DETECT;

					} else {
						return "The event " + nextArgument3
								+ " is unknown to tca";
					}

				} else {
					// we need a state for the transition.
					return getHelp();
				}
			}
		}
		return getHelp();
	}

	/**
	 * return a string that explain the OSGI shell commands.
	 * <p>
	 * This method is override from the <code>CommandProvider</code> interface.
	 * It returns a string that explains the own OSGI shell commands.
	 * 
	 * @version
	 * @author Christophe Bouhier
	 * @see CommandProvider
	 */
	@Override
	public String getHelp() {

		return "---TCA Commands"
				+ "\n       With TCA commands the following activities are supported "
				+ "\n     - Manage the TCA Server" 
				+ "\n       state [details] Optionally ask for details of the current state"
				+ "\n       event start_detect|stop_detect"
				+ "\n     - [TODO]View various TCA Server objects";
	}
}