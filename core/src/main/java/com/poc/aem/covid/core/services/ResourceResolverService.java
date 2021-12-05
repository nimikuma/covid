package com.poc.aem.covid.core.services;

import org.apache.sling.api.resource.ResourceResolver;

public interface ResourceResolverService {

	/**
	 * This method returns the instance of resource resolver
	 *
	 * @return {@link ResourceResolver}
	 */
	ResourceResolver getResourceResolver();
}
