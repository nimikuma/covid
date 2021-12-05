/*
 *  Copyright 2015 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.poc.aem.covid.core.models;

import static org.apache.sling.api.resource.ResourceResolver.PROPERTY_RESOURCE_TYPE;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.poc.aem.covid.core.services.ResourceResolverService;

@Model(adaptables = Resource.class)
public class CoronaVirusDataModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoronaVirusDataModel.class);

    @ValueMapValue(name=PROPERTY_RESOURCE_TYPE, injectionStrategy=InjectionStrategy.OPTIONAL)
    @Default(values="No resourceType")
    protected String resourceType;

    @SlingObject
    private Resource currentResource;
    @Inject
    ResourceResolverService resourceResolverService;

    private String virusDataObject;

    @PostConstruct
    protected void init() {
        ResourceResolver resourceResolver = resourceResolverService.getResourceResolver();
        Resource virusData = resourceResolver.getResource("/poc/covid/data");
        JSONArray array = new JSONArray();
        int count=0;
        for (Resource child : virusData.getChildren()) {

            ValueMap valueMap = child.getValueMap();
            JSONObject obj = new JSONObject();
            try {

                    obj.put("time", valueMap.get("Date").toString());
                    obj.put("cases", (Long) valueMap.get("Confirmed"));
                    array.put(obj);

            } catch (JSONException e) {
                LOGGER.error("Exception occurred: {}", e.getMessage());
            }
            count ++;
        }
        virusDataObject = array.toString();
    }
    public String getVirusDataObject() {
        return virusDataObject;
    }

}
