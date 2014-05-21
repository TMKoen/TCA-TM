package com.koen.tca.server.internal;

import java.util.Collections;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.xtext.util.Modules2;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.google.common.collect.Maps;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.koen.tca.server.TCAServer;

public class TCAServerActivator implements BundleActivator {

	public static final String COM_NETXFORGE_NETXTEST_DRAGONX = "com.netxforge.netxtest.DragonX";

	private static final Logger logger = Logger
			.getLogger(TCAServerActivator.class);

	private static TCAServerActivator INSTANCE;

	private Map<String, Injector> injectors = Collections.synchronizedMap(Maps
			.<String, Injector> newHashMapWithExpectedSize(1));

	private TCAServer rmiServer;

	@Override
	public void start(BundleContext context) throws Exception {
		INSTANCE = this;
		rmiServer = this.getInjector().getInstance(TCAServer.class);
		rmiServer.start();

	}

	@Override
	public void stop(BundleContext context) throws Exception {
		rmiServer.stop();
	}

	public static TCAServerActivator getInstance() {
		return INSTANCE;
	}

	/**
	 * Create a Dependency Injection {@link Injector} The inject binds the XText
	 * module for the given grammar if it matches
	 * {@value #COM_NETXFORGE_NETXTEST_DRAGONX}. It also binds other services
	 * from this class specified in the {@link TCAModule}
	 * 
	 * @param language
	 * @return
	 */
	protected Injector createInjector(String language) {
		try {
			Module runtimeModule = getRuntimeModule(language);
			Module sharedStateModule = getTCAModule();
			Module mergedModule = Modules2.mixin(runtimeModule,
					sharedStateModule);
			return Guice.createInjector(mergedModule);
		} catch (Exception e) {
			logger.error("Failed to create injector for " + language);
			logger.error(e.getMessage(), e);
			throw new RuntimeException("Failed to create injector for "
					+ language, e);
		}
	}

	/**
	 * 
	 * @param language
	 * @return
	 */
	public Injector getInjector() {
		String language = COM_NETXFORGE_NETXTEST_DRAGONX;
		synchronized (injectors) {
			Injector injector = injectors.get(language);
			if (injector == null) {
				injectors.put(language, injector = createInjector(language));
			}
			return injector;
		}
	}

	private Module getTCAModule() {
		return new TCAModule();
	}

	protected Module getRuntimeModule(String grammar) {
		return new com.netxforge.netxtest.DragonXRuntimeModule();

	}
}
