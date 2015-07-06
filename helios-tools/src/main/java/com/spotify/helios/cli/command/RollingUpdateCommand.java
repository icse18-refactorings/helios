/*
 * Copyright (c) 2015 Spotify AB.
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
 */

package com.spotify.helios.cli.command;

import com.spotify.helios.client.HeliosClient;
import com.spotify.helios.common.descriptors.JobId;
import com.spotify.helios.common.protocol.RollingUpdateResponse;

import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.ExecutionException;

public class RollingUpdateCommand extends WildcardJobCommand {

  private final Argument nameArg;

  public RollingUpdateCommand(final Subparser parser) {
    super(parser);

    parser.help("Initiate a rolling update");

    nameArg = parser.addArgument("name")
        .help("Deployment group name");
  }

  @Override
  protected int runWithJobId(final Namespace options, final HeliosClient client,
                             final PrintStream out, final boolean json, final JobId jobId,
                             final BufferedReader stdin)
      throws ExecutionException, InterruptedException, IOException {
    final String name = options.getString(nameArg.getDest());

    if (name == null) {
      throw new IllegalArgumentException("Please specify the name of the deployment-group");
    }

    final RollingUpdateResponse response = client.rollingUpdate(name, jobId).get();

    if (response.getStatus() == RollingUpdateResponse.Status.OK) {
      out.println(response.toJsonString());
      return 0;
    } else {
      if (!json) {
        out.println("Failed: " + response);
      } else {
        out.println(response.toJsonString());
      }
      return 1;
    }
  }
}

