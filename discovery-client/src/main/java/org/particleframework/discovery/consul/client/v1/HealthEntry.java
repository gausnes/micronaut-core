/*
 * Copyright 2018 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.particleframework.discovery.consul.client.v1;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.Collections;
import java.util.List;

/**
 * Models a Consul Health Entry. See https://www.consul.io/api/health.html
 *
 * @author graemerocher
 * @since 1.0
 */
@JsonNaming(PropertyNamingStrategy.UpperCamelCaseStrategy.class)
public class HealthEntry {
    private NodeEntry node;
    private ServiceEntry service;
    @SuppressWarnings("unchecked")
    private List<Check> checks = Collections.EMPTY_LIST;

    /**
     * @return The node for this health entry
     */
    public NodeEntry getNode() {
        return node;
    }

    /**
     * @return The service for the health entry
     */
    public ServiceEntry getService() {
        return service;
    }

    /**
     * @return The checks
     */
    public List<Check> getChecks() {
        return checks;
    }

    @JsonDeserialize(contentAs = CheckEntry.class)
    void setChecks(List<Check> checks) {
        this.checks = checks;
    }

    void setNode(NodeEntry node) {
        this.node = node;
    }

    void setService(ServiceEntry service) {
        this.service = service;
    }


}
