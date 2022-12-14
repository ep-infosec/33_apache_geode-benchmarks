/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.geode.perftest.runner;

import java.io.File;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.util.Set;
import java.util.TreeMap;

import org.apache.geode.perftest.TestContext;
import org.apache.geode.perftest.jvms.rmi.ControllerRemote;

public class DefaultTestContext implements TestContext {

  private SharedContext sharedContext;
  private File outputDir;
  private String role;
  private ControllerRemote controller;
  private int jvmID;
  TreeMap<String, Object> attributeMap;

  public DefaultTestContext(SharedContext sharedContext, File outputDir, int jvmID,
      String role, ControllerRemote controller) {
    this.sharedContext = sharedContext;
    this.outputDir = outputDir;
    this.role = role;
    this.controller = controller;
    attributeMap = new TreeMap<>();
    this.jvmID = jvmID;
  }

  @Override
  public int getJvmID() {
    return jvmID;
  }

  @Override
  public Set<InetAddress> getHostsForRole(String role) {
    return sharedContext.getHostsForRole(role);
  }

  @Override
  public Set<Integer> getHostsIDsForRole(String role) {
    return sharedContext.getHostIDsForRole(role);
  }

  @Override
  public void setAttribute(String attribute, Object value) {
    attributeMap.put(attribute, value);
  }

  @Override
  public Object getAttribute(String key) {
    return attributeMap.get(key);
  }

  @Override
  public File getOutputDir() {
    return outputDir;
  }

  @Override
  public void logProgress(String progress) {
    try {
      controller.logProgress(String.format("%s-%02d: %s", role, jvmID, progress));
    } catch (RemoteException e) {
      throw new IllegalStateException("Controller connection lost", e);
    }

  }
}
