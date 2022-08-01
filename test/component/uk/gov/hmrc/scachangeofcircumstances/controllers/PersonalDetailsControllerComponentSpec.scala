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
import com.github.tomakehurst.wiremock.client.WireMock.{badRequest, notFound, ok, urlEqualTo}
import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.http.Status
import play.api.libs.json.Json
import play.api.libs.ws.{WSClient, WSResponse}
import play.api.{Application, Configuration}
import uk.gov.hmrc.http.test.HttpClientSupport
import uk.gov.hmrc.scachangeofcircumstances.models.{Name, PersonalDetails, PersonalDetailsResponse}
import uk.gov.hmrc.scachangeofcircumstances.utils.{BaseUnitTests, WireMockHelper}

class PersonalDetailsControllerComponentSpec extends BaseUnitTests
  with GuiceOneServerPerSuite with WireMockHelper with ScalaFutures with HttpClientSupport  {

  implicit val timeout: Timeout = Timeout(Span.Max)

  implicit val defaultPatienceConfig: PatienceConfig = PatienceConfig(
      timeout = scaled(Span(15, Seconds)),
      interval = scaled(Span(150, Millis)))

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

  val ifDesignatoryDetails = s"/individuals/details/nino/${nino.get}?fields=$designatoryDetailsFields"
  val ifContactDetails = s"/individuals/details/contact/nino/${nino.get}?fields=$contactDetailsFields"

  lazy val serviceUrl = s"http://localhost:$port/sca-change-of-circumstances"

  override lazy val app: Application = appBuilder()
    .configure(ifConfig)
    .build()

  def makeRequest(endpoint: String): WSResponse = {
    val wsClient = app.injector.instanceOf[WSClient]
    wsClient.url(endpoint).get().futureValue
  }

  "GET /" - {

    "return 200 response when data is returned from IF" in {

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
        WireMock.get(urlEqualTo(ifDesignatoryDetails))
          .willReturn(ok(designatoryDetailsResponse)))

      server.stubFor(
        WireMock.get(urlEqualTo(ifContactDetails))
          .willReturn(ok(contactDetailsResponse)))

      val expected = PersonalDetailsResponse(
        details = PersonalDetails(
          name = Some(Name(
            firstForename = Some("John"),
            surname = Some("Johnson")
        )))
      )

      val result = makeRequest(s"$serviceUrl/personal-details")
      result.status shouldBe Status.OK
      result.json shouldBe Json.toJson(expected)
    }

    "return 500 when IF returns Bad Request" in {

      val designatoryDetailsResponse = """{
          |"failures": [
          | {
          |   "code": "INVALID_IDTYPE",
          |   "reason": "Submission has not passed validation. Invalid parameter idType."
          | }
          |]
          |}""".stripMargin

      val contactDetailsResponse = "{}"

      server.stubFor(
        WireMock.get(urlEqualTo(ifDesignatoryDetails))
          .willReturn(badRequest().withBody(designatoryDetailsResponse)))

      server.stubFor(
        WireMock.get(urlEqualTo(ifContactDetails))
          .willReturn(ok(contactDetailsResponse)))

      val expected =
        """{
          |   "statusCode": 500,
          |   "message": "Something went wrong."
          |}""".stripMargin

      val result = makeRequest(s"$serviceUrl/personal-details")
      result.status shouldBe Status.INTERNAL_SERVER_ERROR
      result.json shouldBe Json.parse(expected)
    }

    "return 404 when IF cannot find record for NiNo" in {

      val designatoryDetailsResponse = """{
                                         |  "failures": [
                                         |    {
                                         |      "code": "IDENTIFIER_NOT_FOUND",
                                         |      "reason": "The remote endpoint has indicated that identifier supplied can not be found."
                                         |    }
                                         |  ]
                                         |}""".stripMargin


      val contactDetailsResponse = "{}"

      server.stubFor(
        WireMock.get(urlEqualTo(ifDesignatoryDetails))
          .willReturn(notFound().withBody(designatoryDetailsResponse)))

      server.stubFor(
        WireMock.get(urlEqualTo(ifContactDetails))
          .willReturn(ok(contactDetailsResponse)))

      val expected =
        """{
          |   "statusCode": 404,
          |   "message": "Record not found for provided NiNo."
          |}""".stripMargin

      val result = makeRequest(s"$serviceUrl/personal-details")
      result.status shouldBe Status.NOT_FOUND
      result.json shouldBe Json.parse(expected)
    }
  }
}
