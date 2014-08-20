package com.koen.tca.server.internal;

import java.rmi.RemoteException;
import java.util.Collections;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.resource.Resource;
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
import com.koen.tca.server.state.ServerStateDetect;
import com.koen.tca.server.state.ServerStateIdle;
import com.koen.tca.server.state.ServerStateReady;
import com.koen.tca.server.state.ServerStateTest;
import com.koen.tca.server.thread.AndroidDetector;
import com.netxforge.netxtest.DragonXRuntimeModule;

/**
 * OSGI Activator class that starts and stops the <code>TCAServer</code> class.
 * <p>
 * This activator class implements the <code>bundleActivator</code> interface It
 * also implements the <code>CommandProvider</code> interface <br>
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
	 * This method is the starting point of the server application. Every module
	 * that runs under Equinox - OSGI framework must have this method. It is
	 * override from the <code>BundleActivator</code> interface.
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

		// Make sure, we register the extension, which normally only comes
		// through the UI plugin.xml
		org.eclipse.xtext.resource.IResourceFactory resourceFactory = this
				.getInjector().getInstance(
						org.eclipse.xtext.resource.IResourceFactory.class);
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(
				"dragonx", resourceFactory);

		if (!Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap()
				.containsKey("xtextbin"))
			Resource.Factory.Registry.INSTANCE
					.getExtensionToFactoryMap()
					.put("xtextbin",
							new org.eclipse.xtext.resource.impl.BinaryGrammarResourceFactoryImpl());

		// We contribute OSGI commands here.
		context.registerService(CommandProvider.class.getName(), this, null);

		// guice makes a new TCAServer object.
		server = this.getInjector().getInstance(TCAServer.class);
		server.start();

	}

	/**
	 * Stops the Server bundle.
	 * <p>
	 * This method stops the server application module under the equinox - OSGI
	 * framework. Every OSGI equinox Module must have a stop method to close all
	 * resources nicely.
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
	 * 
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
	 * Specifies the shell commands that can be used on the OSGI console to
	 * control the Server.
	 * <p>
	 * All methods that begin with '_' and has a CommandInterpreter arguments,
	 * are used by OSGI to add new commands for the console.
	 * 
	 * @version
	 * @author Christophe Bouhier, Koen Nijmeijer
	 * @param intp
	 * @return
	 */
	public Object _tca(CommandInterpreter intp) {

		// The event that was fired.
		@SuppressWarnings("unused")
		ServerEvents fireEvent = null;

		// gets the present state of the server.
		IServerState state = this.server.getTestServer().getStateMachine()
				.getState();

		// The message to be send to the user.
		String message = "Wrong or no arguments!\n" + getHelp();

		// Other arguments on the command line.
		String nextArgument2 = null;
		String nextArgument3 = null;

		// get the next command line argument after 'tca'..
		String nextArgument = intp.nextArgument();

		// There must be an argument after 'tca' on the command line
		if (nextArgument != null) {

			// this command line argument can be used in all the server states.
			if (nextArgument.equals("state")) {

				// finds the next argument if any.
				nextArgument2 = intp.nextArgument();
				if (nextArgument2 != null) {
					if (nextArgument2.equals("details")) {

						// check is there is no other argument on the command
						// line.
						nextArgument3 = intp.nextArgument();
						if (nextArgument3 == null) {
							message = "\n Details:\n-------------\n"
									+ state.details();
						}
					}
				} else {
					message = "The current server state is: " + state;
				}

			} else if (nextArgument.equals("upload")) {
				nextArgument2 = intp.nextArgument();
				if (nextArgument2 != null) {
					// TODO: check the path and testcase name..
					message = "TODO: upload";
				}

			} else if (nextArgument.equals("result")) {
				nextArgument2 = intp.nextArgument();
				// check if there is no other argument on the command line.
				if (nextArgument2 == null) {
					message = "TODO: result";
				}
			} else if (nextArgument.equals("event")) {
				nextArgument2 = intp.nextArgument();
				if (nextArgument2 != null) {
					message = eventArgument(state, nextArgument2);
				}
			}
		}
		return message;
	}

	private String eventArgument(IServerState state, String argument) {

		String message = "fire event: ";
		if (argument != null) {
			if (argument.equals("idle") && (state instanceof ServerStateReady)) {
				// change server state to idle
				try {
					server.getTestServer().idle();
					message += ServerEvents.IDLE;
				} catch (RemoteException e) {
					e.printStackTrace();
				}

			} else if (argument.equals("start_detect")
					&& (state instanceof ServerStateIdle || state instanceof ServerStateReady)) {
				// change server state to detect
				try {
					server.getTestServer().startDetect(null);
					message += ServerEvents.START_DETECT;
				} catch (RemoteException e) {
					e.printStackTrace();
				}

			} else if (argument.equals("stop_detect")
					&& state instanceof ServerStateDetect) {
				// change server state to Ready or Idle, depends on the founded
				// Android device.
				try {
					server.getTestServer().stopDetect(null);
					message += ServerEvents.STOP_DETECT;
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			} else if (argument.equals("start_test")
					&& state instanceof ServerStateReady) {
				try {
					server.getTestServer().startTestSet(null, "");
					message += ServerEvents.START_TEST;
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			} else if (argument.equals("stop_test")
					&& state instanceof ServerStateTest) {
				try {
					server.getTestServer().stopTest(null);
					message += ServerEvents.STOP_TEST;
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			} else if (argument.equals("fake_ue")
					&& state instanceof ServerStateDetect) {

				IServerState currentState = server.getTestServer()
						.getStateMachine().getState();
				ServerStateDetect stateDetect = (ServerStateDetect) currentState;
				AndroidDetector androidDetector = stateDetect
						.getAndroidDetector();
				androidDetector.storeDeviceInfo("1234567", "061234567",
						"10.10.100.253");
				androidDetector.storeDeviceInfo("2345678", "062345678",
						"10.10.100.254");

				message += "Fakin UE detection";
			} else {
				message = "Wrong event or wrong state for that event!\n"
						+ getHelp();
			}
		} else {
			// empty argument
			message += "no event\n" + getHelp();
		}
		return message;
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
				+ "\n      In Idle state:"
				+ "\n           - event start_detect"
				+ "\n      In Detect state:"
				+ "\n           - event stop_detect"
				+ "\n           - event fake_ue => Fake the discovery of UE's with dummy values"
				+ "\n      In Ready state:"
				+ "\n           - event start_detect"
				+ "\n           - event start_test"
				+ "\n           - event idle"
				+ "\n           - upload <testcase>"
				+ "\n           - results"
				+ "\n      In Test state:"
				+ "\n           -event stop_test"
				+ "\n      in any state:"
				+ "\n       state [details] Optionally ask for details of the current state"
				+ "\n     - [TODO]View various TCA Server objects"
				+ "\n"
				+ "\n     Configuration:"
				+ "\n           - config script_path [path to folder with dragon X scripts]";
	}
}