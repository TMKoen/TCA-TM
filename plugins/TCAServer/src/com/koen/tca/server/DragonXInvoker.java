package com.koen.tca.server;

import java.io.File;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.linking.lazy.LazyLinkingResource;
import org.eclipse.xtext.resource.IResourceFactory;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.util.CancelIndicator;

import com.google.inject.Inject;
import com.netxforge.netxtest.dragonX.DragonX;
import com.netxforge.netxtest.interpreter.DragonXInterpreter;
import com.netxforge.netxtest.interpreter.IExternalDispatcher;

/**
 * A service for calling the Dragon X Interpreter. Injects a Factory for
 * producing XTextResources.
 * 
 */
public class DragonXInvoker {

	@Inject
	private IResourceFactory xResourceFactory;

	@Inject
	private FolderPollingService scriptObtainer;

	@Inject
	private DragonXInterpreter interpreter;

	@Inject
	private IExternalDispatcher androidDispatcher;

	protected XtextResource doGetResource(URI uri) throws Exception {

		XtextResource resource = null;

		if (resource == null) {
			resource = (XtextResource) xResourceFactory.createResource(uri);
		}

		resource.load(null);

		if (resource instanceof LazyLinkingResource) {
			// Linking process here.
			((LazyLinkingResource) resource)
					.resolveLazyCrossReferences(CancelIndicator.NullImpl);
		} else {
			EcoreUtil.resolveAll(resource);
		}

		return resource;
	}

	/**
	 * Invokes a {@link DragonX} script by scanning a directory for script
	 * files. Parse it and produce an AST. Then configure an
	 * {@link DragonXInterpreter} and finally invoke the interpreter with the
	 * AST. This will emmit actions on the specified {@link IExternalDispatcher}
	 */
	public void invoke() {
		
		// TODO Create a runnable. 
		
		
		try {

			// Get all the files in the directory.
			// NOTE: Directory is hard-coded.

			File[] filesInDirectory = scriptObtainer.getFilesInDirectory();

			if (filesInDirectory.length > 1) {
				URI fileAsURI = scriptObtainer.fileAsURI(filesInDirectory[0]);

				XtextResource scriptAsResource = this.doGetResource(fileAsURI);
				if (!scriptAsResource.getContents().isEmpty()) {
					EList<EObject> contents = scriptAsResource.getContents();
					if (contents.size() == 1) {
						EObject eObject = contents.get(0);
						if (eObject instanceof DragonX) {
							DragonX script = (DragonX) eObject;
							// Setup the interpreter.
							interpreter.setExtDispatcher(androidDispatcher);
							interpreter.evaluate(script);
						} else {
							// invalid EMF Object, puke here.
						}
					}

				} else {
					// invalid script, throw Exception
				}
			} else {
				// No files throw Exception
			}
		} catch (Exception e) {

			// catch any other exception
		}

	}
}
