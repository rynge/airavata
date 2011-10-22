/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package org.apache.airavata.xbaya.component.registry;

import java.net.URI;
import java.util.HashMap;
import java.util.List;

import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

import org.apache.airavata.commons.gfac.type.ApplicationDeploymentDescription;
import org.apache.airavata.commons.gfac.type.HostDescription;
import org.apache.airavata.commons.gfac.type.ServiceDescription;
import org.apache.airavata.registry.api.Registry;
import org.apache.airavata.registry.api.exception.DeploymentDescriptionRetrieveException;
import org.apache.airavata.registry.api.exception.HostDescriptionRetrieveException;
import org.apache.airavata.registry.api.exception.ServiceDescriptionRetrieveException;
import org.apache.airavata.registry.api.impl.JCRRegistry;
import org.apache.airavata.registry.api.user.UserManager;
import org.apache.airavata.xbaya.component.gui.ComponentTreeNode;

public class JCRComponentRegistry extends ComponentRegistry {

    private static final String NAME = "JCR Components";

    private JCRRegistry registry;

    public JCRComponentRegistry(URI url, String username, String password) throws RepositoryException {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("org.apache.jackrabbit.repository.uri", url.toString());
        try {
            this.registry = new JCRRegistry(url, "org.apache.jackrabbit.rmi.repository.RmiRepositoryFactory", username,
                    password, map);
        } catch (RepositoryException e) {
            throw e;
        }
    }

    static {
        registerUserManagers();
    }

    /**
     * to manually trigger user manager registrations
     */
    private static void registerUserManagers() {
        try {
            Class.forName("org.apache.airavata.xbaya.component.registry.jackrabbit.user.JackRabbitUserManagerWrap");
        } catch (ClassNotFoundException e) {
            // error in registering user managers
        }
    }

    /**
     * @see org.apache.airavata.xbaya.component.registry.ComponentRegistry#getComponentTree()
     */
    @Override
    public ComponentTreeNode getComponentTree() {
        ComponentTreeNode tree = new ComponentTreeNode(this);
        try {
            List<ServiceDescription> services = this.registry.searchServiceDescription("");
            for (ServiceDescription serviceDescription : services) {
                String serviceName = serviceDescription.getId();
                JCRComponentReference jcr = new JCRComponentReference(serviceName, registry.getWSDL(serviceName));
                tree.add(new ComponentTreeNode(jcr));
            }
        } catch (PathNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ServiceDescriptionRetrieveException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return tree;
    }

    /**
     * @see org.apache.airavata.xbaya.component.registry.ComponentRegistry#getName()
     */
    @Override
    public String getName() {
        return NAME;
    }

    public List<String> getGFacURLList() {
        return this.registry.getGFacDescriptorList();
    }

    public UserManager getUserManager() {
        return registry.getUserManager();
    }

    public String saveDeploymentDescription(String service, String host, ApplicationDeploymentDescription app) {
        // deploy the service on host
        registry.deployServiceOnHost(service, host);

        // save deployment description
        return registry.saveDeploymentDescription(service, host, app);
    }

    public String saveHostDescription(String name, HostDescription host) {
        return registry.saveHostDescription(host);
    }

    public List<HostDescription> searchHostDescription(String nameRegEx) throws HostDescriptionRetrieveException,
            PathNotFoundException {
        return registry.searchHostDescription(nameRegEx);
    }

    public HostDescription getHostDescription(String nameRegEx) throws HostDescriptionRetrieveException,
            PathNotFoundException {
        return registry.getHostDescription(nameRegEx);
    }

    public List<ApplicationDeploymentDescription> searchApplicationDescription(String serviceName, String host)
            throws HostDescriptionRetrieveException, PathNotFoundException, DeploymentDescriptionRetrieveException {
        return registry.searchDeploymentDescription(serviceName, host);
    }

    public ApplicationDeploymentDescription getApplicationDescription(String serviceName, String host)
            throws PathNotFoundException, DeploymentDescriptionRetrieveException {
        return registry.getDeploymentDescription(serviceName, host);
    }

    public String saveServiceDescription(String name, ServiceDescription service) {
        return registry.saveServiceDescription(service);
    }

    public ServiceDescription getServiceDescription(String serviceName) throws PathNotFoundException,
            ServiceDescriptionRetrieveException {
        return registry.getServiceDescription(serviceName);
    }

    public List<ServiceDescription> searchServiceDescription(String serviceName)
            throws ServiceDescriptionRetrieveException, PathNotFoundException {
        return registry.searchServiceDescription(serviceName);
    }

    public Registry getRegistry() {
        return registry;
    }

    public String saveWSDL(ServiceDescription service) {
        return registry.saveWSDL(service);
    }
}