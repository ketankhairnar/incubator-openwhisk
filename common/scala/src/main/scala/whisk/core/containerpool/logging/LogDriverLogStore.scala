/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package whisk.core.containerpool.logging

import akka.actor.ActorSystem
import whisk.core.entity.Identity
import whisk.common.TransactionId
import whisk.core.containerpool.Container
import whisk.core.entity.{ActivationLogs, ExecutableWhiskAction, WhiskActivation}

import scala.concurrent.Future

/**
 * Docker log driver based LogStore impl. Uses docker log driver to emit container logs to an external store.
 * Fetching logs from that external store is not provided in this trait. This SPI requires the
 * ContainerArgs.extraArgs to be used to indicate where the logs are shipped.
 * see https://docs.docker.com/config/containers/logging/configure/#configure-the-logging-driver-for-a-container
 *
 * Fetching logs here is a NOOP, but extended versions can customize fetching, e.g. from ELK or Splunk etc.
 */
class LogDriverLogStore(actorSystem: ActorSystem) extends LogStore {

  /** Indicate --log-driver and --log-opt flags via ContainerArgsConfig.extraArgs */
  override def containerParameters = Map()

  def collectLogs(transid: TransactionId,
                  user: Identity,
                  activation: WhiskActivation,
                  container: Container,
                  action: ExecutableWhiskAction): Future[ActivationLogs] =
    Future.successful(ActivationLogs()) //no logs collected when using docker log drivers (see DockerLogStore for json-file exception)

  /** no logs exposed to API/CLI using only the LogDriverLogStore; use an extended version,
   * e.g. the SplunkLogStore to expose logs from some external source */
  def fetchLogs(activation: WhiskActivation): Future[ActivationLogs] =
    Future.successful(ActivationLogs(Vector("Logs are not available.")))
}

object LogDriverLogStoreProvider extends LogStoreProvider {
  override def logStore(actorSystem: ActorSystem) = new LogDriverLogStore(actorSystem)
}
