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
package org.particleframework.discovery.client;

import org.particleframework.context.exceptions.ConfigurationException;
import org.particleframework.core.util.StringUtils;
import org.particleframework.discovery.DiscoveryConfiguration;
import org.particleframework.discovery.ServiceInstance;
import org.particleframework.discovery.registration.RegistrationConfiguration;
import org.particleframework.http.client.HttpClientConfiguration;
import org.particleframework.runtime.ApplicationConfiguration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Abstract class for all {@link org.particleframework.discovery.DiscoveryClient} configurations
 *
 * @author graemerocher
 * @since 1.0
 */
public abstract class DiscoveryClientConfiguration extends HttpClientConfiguration {


    private List<ServiceInstance> defaultZone = Collections.emptyList();
    private List<ServiceInstance> otherZones = Collections.emptyList();

    private String host = LOCALHOST;
    private int port = -1;
    private boolean secure;

    public DiscoveryClientConfiguration() {
    }

    public DiscoveryClientConfiguration(ApplicationConfiguration applicationConfiguration) {
        super(applicationConfiguration);
    }

    /**
     * @return The Discovery servers within the default zone
     */
    public List<ServiceInstance> getDefaultZone() {
        return defaultZone;
    }

    /**
     * @return The Discovery servers within all zones
     */
    public List<ServiceInstance> getAllZones() {
        List<ServiceInstance> allZones = new ArrayList<>(defaultZone.size() + otherZones.size());
        allZones.addAll(defaultZone);
        allZones.addAll(otherZones);
        return allZones;
    }
    /**
     * Sets the Discovery servers to use for the default zone
     *
     * @param defaultZone The default zone
     */
    public void setDefaultZone(List<URL> defaultZone) {
        this.defaultZone = defaultZone.stream().map(uriMapper()).map(uri-> ServiceInstance.builder(getServiceID(), uri).build())
          .collect(Collectors.toList());
    }

    private Function<URL, URI> uriMapper() {
        return url -> {
            try {
                return url.toURI();
            } catch (URISyntaxException e) {
                throw new ConfigurationException("Invalid Eureka server URL: " + url);
            }
        };
    }

    /**
     * Configures Discovery servers in other zones
     * @param zones The zones
     */
    public void setZones(Map<String, List<URL>> zones) {
        if(zones != null) {
            this.otherZones = zones.entrySet()
                    .stream()
                    .flatMap((Function<Map.Entry<String, List<URL>>, Stream<ServiceInstance>>) entry ->
                            entry.getValue()
                                    .stream()
                                    .map(uriMapper())
                                    .map(uri ->
                                            ServiceInstance.builder(getServiceID(), uri)
                                                    .zone(entry.getKey())
                                                    .build()
                                    ))
                    .collect(Collectors.toList());
        }
    }
    /**
     * @return Is the discovery server exposed over HTTPS (defaults to false)
     */
    public boolean isSecure() {
        return secure;
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setHost(String host) {
        if(StringUtils.isNotEmpty(host)) {
            this.host = host;
        }
    }

    /**
     * @return The ID of the {@link org.particleframework.discovery.DiscoveryClient}
     */
    protected abstract String getServiceID();

    /**
     * @return The Discovery server instance host name. Defaults to 'localhost'.
     **/
    @Nonnull public String getHost() {
        return host;
    }

    /**
     * @return The default Discovery server port
     */
    public int getPort() {
        return port;
    }

    /**
     * @return The default discovery configuration
     */
    @Nonnull
    public abstract DiscoveryConfiguration getDiscovery();

    /**
     * @return The default registration configuration
     */
    @Nullable
    public abstract RegistrationConfiguration getRegistration();


    @Override
    public String toString() {
        return "DiscoveryClientConfiguration{" +
                "defaultZone=" + defaultZone +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", secure=" + secure +
                "} ";
    }
}
