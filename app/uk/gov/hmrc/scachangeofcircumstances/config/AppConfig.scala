/*
 * Copyright 2022 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.scachangeofcircumstances.config

import javax.inject.{Inject, Singleton}
import play.api.Configuration

@Singleton
class AppConfig @Inject()(config: Configuration) {

  val appName: String = config.get[String]("appName")

  val integrationFrameworkProtocol = config.get[String]("microservice.services.integration-framework.protocol")
  val integrationFrameworkHost = config.get[String]("microservice.services.integration-framework.host")
  val integrationFrameworkPort = config.get[Int]("microservice.services.integration-framework.port")
  val integrationFrameworkAuthToken = config.get[String]("microservice.services.integration-framework.authorization-token")
  val integrationFrameworkEnvironment = config.get[String]("microservice.services.integration-framework.environment")

  val ifBaseUrl = s"$integrationFrameworkProtocol://$integrationFrameworkHost:$integrationFrameworkPort"

}
