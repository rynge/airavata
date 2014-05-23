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

package org.apache.airavata.api.server;

import org.apache.airavata.api.appcatalog.ApplicationCatalogAPI;
import org.apache.airavata.api.server.handler.ApplicationCatalogHandler;
import org.apache.airavata.api.server.util.Constants;
import org.apache.airavata.common.utils.AiravataUtils;
import org.apache.airavata.common.utils.IServer;
import org.apache.airavata.common.utils.ServerSettings;
import org.apache.airavata.model.error.AiravataErrorType;
import org.apache.airavata.model.error.AiravataSystemException;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationCatalogServer implements IServer{

    private final static Logger logger = LoggerFactory.getLogger(ApplicationCatalogServer.class);
	private static final String SERVER_NAME = "Airavata Application Catalog Server";
	private static final String SERVER_VERSION = "1.0";

    private ServerStatus status;

	private TServer server;

	public ApplicationCatalogServer() {
		setStatus(ServerStatus.STOPPED);
	}
	
    public void StartAiravataServer(ApplicationCatalogAPI.Processor<ApplicationCatalogAPI.Iface> appCatalogServerHandler) throws AiravataSystemException {
        try {
            AiravataUtils.setExecutionAsServer();
            final int serverPort = Integer.parseInt(ServerSettings.getSetting(Constants.APP_CATALOG_SERVER_PORT,"8931"));
			TServerTransport serverTransport = new TServerSocket(serverPort);
			server = new TThreadPoolServer(new TThreadPoolServer.Args(serverTransport).processor(appCatalogServerHandler));
            new Thread() {
				public void run() {
					server.serve();
					setStatus(ServerStatus.STOPPED);
					logger.info("Airavata API Server Stopped.");
				}
			}.start();
			new Thread() {
				public void run() {
					while(!server.isServing()){
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							break;
						}
					}
					if (server.isServing()){
						setStatus(ServerStatus.STARTED);
			            logger.info("Starting Airavata Application Catalog Server on Port " + serverPort);
			            logger.info("Listening to Application Catalog Clients ....");
					}
				}
			}.start();
        } catch (TTransportException e) {
            logger.error(e.getMessage());
            setStatus(ServerStatus.FAILED);
            throw new AiravataSystemException(AiravataErrorType.INTERNAL_ERROR);
        }
    }

    public static void main(String[] args) {
    	try {
			ApplicationCatalogServer server = new ApplicationCatalogServer();
			server.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

	@Override
	public void start() throws Exception {
		setStatus(ServerStatus.STARTING);
		ApplicationCatalogAPI.Processor<ApplicationCatalogAPI.Iface> server =
                new ApplicationCatalogAPI.Processor<ApplicationCatalogAPI.Iface>(new ApplicationCatalogHandler());
    	StartAiravataServer(server);
	}

	@Override
	public void stop() throws Exception {
		if (server.isServing()){
			setStatus(ServerStatus.STOPING);
			server.stop();
		}
		
	}

	@Override
	public void restart() throws Exception {
		stop();
		start();
	}

	@Override
	public void configure() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ServerStatus getStatus() throws Exception {
		return status;
	}
	
	private void setStatus(ServerStatus stat){
		status=stat;
		status.updateTime();
	}

	@Override
	public String getName() {
		return SERVER_NAME;
	}

	@Override
	public String getVersion() {
		return SERVER_VERSION;
	}

}
