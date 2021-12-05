package com.poc.aem.covid.core.services.impl;

import static com.poc.aem.covid.core.constants.AppConstants.FORWARD_SLASH;

import java.util.Objects;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.sling.api.resource.ResourceResolver;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.poc.aem.covid.core.services.ResourceResolverService;
import com.poc.aem.covid.core.services.WriteCoronaDataService;

@Component(service = WriteCoronaDataService.class, property = {
		Constants.SERVICE_ID + "= Write Corona Virus Data Service",
		Constants.SERVICE_DESCRIPTION + "= This service writes corona virus data in JCR"
})
public class WriteCoronaDataServiceImpl implements WriteCoronaDataService {

	private static final String TAG = WriteCoronaDataServiceImpl.class.getSimpleName();
	private static final Logger LOGGER = LoggerFactory.getLogger(WriteCoronaDataServiceImpl.class);

	private static final String USER_ID = "userId";
	private static final String ID = "ID";
	private static final String COUNTRY = "Country";
	private static final String COUNTRY_CODE = "CountryCode";
	private static final String CONFIRMED = "Confirmed";
	private static final String DEATHS = "Deaths";
	private static final String RECOVERED = "Recovered";
	private static final String  ACTIVE = "Active";
	private static final String  DATE = "Date";
	private static final String COVID_DATA_NODE = "poc/covid/data";

	@Reference
	ResourceResolverService resourceResolverService;

	@Override
	public void writeData(String virusData) {
		// Check if data is not empty
		if (virusData != null && !virusData.isEmpty()) {
			// Get instance of ResourceResolver
			ResourceResolver resourceResolver = resourceResolverService.getResourceResolver();
			// Adapt this resource resolver to JCR session object
			Session session = resourceResolver.adaptTo(Session.class);
			// Get the reference of the node where we want to store data
			try {
				if (session != null) {
					// Get the reference of the root node
					Node node = session.getRootNode();
					if (!session.nodeExists(FORWARD_SLASH + COVID_DATA_NODE)) {
						String[] nodePaths = COVID_DATA_NODE.split(FORWARD_SLASH);
						for (String path : nodePaths) {
							node = node.addNode(path);
							session.save();
						}
					} else {
						node = node.getNode(COVID_DATA_NODE);
					}
					JSONArray jsonArray = new JSONArray(virusData);
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						Node currentNode;
						if (!Objects.requireNonNull(node).hasNode("virusdata-" + jsonObject.getString(ID))) {
							currentNode = Objects.requireNonNull(node).addNode("virusdata-" + jsonObject.getString(ID));
							currentNode.setProperty(ID, jsonObject.getString(ID));
							currentNode.setProperty(COUNTRY, jsonObject.getString(COUNTRY));
							currentNode.setProperty(COUNTRY_CODE, jsonObject.getString(COUNTRY_CODE));
							currentNode.setProperty(RECOVERED, jsonObject.getInt(RECOVERED));
							currentNode.setProperty(ACTIVE, jsonObject.getInt(ACTIVE));
							currentNode.setProperty(DEATHS, jsonObject.getInt(DEATHS));
							currentNode.setProperty(CONFIRMED, jsonObject.getInt(CONFIRMED));
							currentNode.setProperty(DATE, jsonObject.getString(DATE));
						}
					}
					session.save();

				}
			} catch (RepositoryException | JSONException e) {
				LOGGER.error("{}: Exception occurred: {}", TAG, e.getMessage());
			}
		} else {
			LOGGER.error("{}: No data to be saved in the repository", TAG);
		}
	}
}