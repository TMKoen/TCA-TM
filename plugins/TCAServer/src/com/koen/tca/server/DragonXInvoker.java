package com.koen.tca.server;

import java.io.InputStream;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.linking.lazy.LazyLinkingResource;
import org.eclipse.xtext.resource.IResourceFactory;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.util.CancelIndicator;

import com.google.inject.Inject;

/**
 * A service for calling the Dragon X Interpreter.  
 * Injects a Factory for producing XTextResources. 
 *
 */
public class DragonXInvoker {

	@Inject
	private IResourceFactory xResourceFactory;

	public DragonXInvoker() {
	}

	protected XtextResource doGetResource(InputStream in, URI uri)
			throws Exception {

		XtextResource resource = null;

		if (resource == null) {
			resource = (XtextResource) xResourceFactory.createResource(uri);
		}
		// xResourceSet.getResources().add(resource);
		resource.load(in, null);
		if (resource instanceof LazyLinkingResource) {
			// Linking process here.
			((LazyLinkingResource) resource)
					.resolveLazyCrossReferences(CancelIndicator.NullImpl);
		} else {
			EcoreUtil.resolveAll(resource);
		}

		return resource;
	}

}
