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

package uk.gov.hmrc.scachangeofcircumstances.utils

import org.scalatest.freespec.AnyFreeSpec
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.inject
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers
import uk.gov.hmrc.auth.core.AuthConnector
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.scachangeofcircumstances.auth.AuthAction

import scala.concurrent.ExecutionContext

abstract class BaseUnitTests extends AnyFreeSpec {

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()

  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

  val nino: Option[String] = Some("JH1234567")

  val authAction = new TestAuthAction(nino, mock[AuthConnector], Helpers.stubControllerComponents())

  def appBuilder(): GuiceApplicationBuilder = new GuiceApplicationBuilder()
    .overrides(inject.bind[AuthAction].toInstance(authAction))
    .configure(
      "metrics.enabled" -> false,
      "auditing.enabled" -> false
    )

}
