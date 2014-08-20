package com.koen.tca.server.test;

import java.io.File;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.koen.tca.server.FolderPollingService;

public class TestFileServices {

	private Injector createInjector;

	@Inject
	private FolderPollingService folderService;

	@Before
	public void setUp() throws Exception {
		createInjector = Guice.createInjector(new TCAModule());
		createInjector.injectMembers(this);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {

		Assert.assertNotNull(folderService);

		folderService.setScriptPath("/Users/Christophe/Desktop/dragonX/");
		folderService.poll();
		
		
		File[] filesInDirectory = folderService.getFilesInDirectory();

		Assert.assertTrue(filesInDirectory.length > 0);
	}
}
