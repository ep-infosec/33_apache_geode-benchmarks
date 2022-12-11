/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.benchmark.tests;

import static java.lang.Long.getLong;
import static org.apache.geode.benchmark.Config.before;
import static org.apache.geode.benchmark.Config.workload;
import static org.apache.geode.benchmark.tests.GeodeBenchmark.WITH_MAX_KEY;
import static org.apache.geode.benchmark.tests.GeodeBenchmark.WITH_MIN_KEY;
import static org.apache.geode.benchmark.topology.Roles.CLIENT;
import static org.apache.geode.benchmark.topology.Roles.SERVER;

import org.junit.jupiter.api.Test;

import org.apache.geode.benchmark.LongRange;
import org.apache.geode.benchmark.tasks.CreateClientProxyRegion;
import org.apache.geode.benchmark.tasks.CreateIndexOnID;
import org.apache.geode.benchmark.tasks.CreateReplicatedRegion;
import org.apache.geode.benchmark.tasks.OQLQuery;
import org.apache.geode.benchmark.tasks.PrePopulateRegion;
import org.apache.geode.perftest.TestConfig;
import org.apache.geode.perftest.TestRunners;

public class ReplicatedIndexedQueryBenchmark extends AbstractPerformanceTest {
  private LongRange keyRange =
      new LongRange(getLong(WITH_MIN_KEY, 0), getLong(WITH_MAX_KEY, 500_000));
  private long queryRange = 100;

  public ReplicatedIndexedQueryBenchmark() {}

  public void setKeyRange(final LongRange keyRange) {
    this.keyRange = keyRange;
  }

  public void setQueryRange(final long queryRange) {
    this.queryRange = queryRange;
  }

  @Test
  public void run() throws Exception {
    TestRunners.defaultRunner().runTest(this);
  }

  @Override
  public TestConfig configure() {
    TestConfig config = ClientServerBenchmark.createConfig();
    config.threads(Runtime.getRuntime().availableProcessors() * 8);
    before(config, new CreateReplicatedRegion(), SERVER);
    before(config, new CreateClientProxyRegion(), CLIENT);
    before(config, new CreateIndexOnID(), SERVER);
    before(config, new PrePopulateRegion(keyRange), CLIENT);
    workload(config, new OQLQuery(keyRange, queryRange, isValidationEnabled()), CLIENT);
    return config;
  }
}
