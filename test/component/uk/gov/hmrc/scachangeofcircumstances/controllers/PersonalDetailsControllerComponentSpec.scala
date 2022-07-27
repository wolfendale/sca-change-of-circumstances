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

package component.uk.gov.hmrc.scachangeofcircumstances.controllers

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.{ok, urlEqualTo}
import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatest.time.Span
import play.api.Configuration
import play.api.http.Status
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.scachangeofcircumstances.controllers.PersonalDetailsController
import uk.gov.hmrc.scachangeofcircumstances.models.{Name, PersonalDetails, PersonalDetailsResponse}
import uk.gov.hmrc.scachangeofcircumstances.utils.{BaseUnitTests, WireMockHelper}

class PersonalDetailsControllerComponentSpec extends BaseUnitTests with WireMockHelper with ScalaFutures  {

  private val fakeRequest = FakeRequest("GET", "/personal-details")

  val timeout: Timeout = Timeout(Span.Max)

  lazy val ifConfig: Configuration = Configuration(
    "microservice.services.integration-framework.host" -> "127.0.0.1",
    "microservice.services.integration-framework.port" -> server.port(),
    "microservice.services.integration-framework.authorizationToken" -> "auth-token",
    "microservice.services.integration-framework.environment" -> "test-environment")

  val designatoryDetailsFields: String =
    "details(marriageStatusType),nameList(name(nameSequenceNumber,nameType,titleType," +
      "requestedName,nameStartDate,nameEndDate,firstForename,secondForename,surname))," +
      "addressList(address(addressSequenceNumber,countryCode,addressType,addressStartDate," +
      "addressEndDate,addressLine1,addressLine2,addressLine3,addressLine4,addressLine5," +
      "addressPostcode))"

  val contactDetailsFields: String = "contactDetails(code,type,detail)"

  val designatoryDetailsUrl = s"/individuals/details/nino/${nino.get}?fields=$designatoryDetailsFields"

  val contactDetailsUrl = s"/individuals/details/contact/nino/${nino.get}?fields=$contactDetailsFields"


  "GET /" - {

    "return 200" in {

      val designatoryDetailsResponse = """{
                            |  "details": { },
                            |  "nameList": {
                            |    "name": [
                            |      {
                            |        "nameSequenceNumber": 1,
                            |        "nameType": 1,
                            |        "firstForename": "John",
                            |        "surname": "Johnson"
                            |      }
                            |    ]
                            |  },
                            |  "addressList": {
                            |    "address": [ ]
                            |  }
                            |}
                            |""".stripMargin


      val contactDetailsResponse = "{}"

      server.stubFor(
        WireMock.get(urlEqualTo(designatoryDetailsUrl))
          .willReturn(ok(designatoryDetailsResponse)))

      server.stubFor(
        WireMock.get(urlEqualTo(contactDetailsUrl))
          .willReturn(ok(contactDetailsResponse)))

      val expected = PersonalDetailsResponse(
        details = PersonalDetails(
          name = Some(Name(
            firstForename = Some("John"),
            surname = Some("Johnson")
        )))
      )

      val app = appBuilder()
        .configure(ifConfig)
        .build()

      running(app) {
        val controller = app.injector.instanceOf[PersonalDetailsController]
        val result = controller.getPersonalDetails()(fakeRequest)
        status(result) shouldBe Status.OK
        contentAsJson(result) shouldBe Json.toJson(expected)
      }
    }
  }
}
