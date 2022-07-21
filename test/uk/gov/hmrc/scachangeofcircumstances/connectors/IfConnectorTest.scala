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

package uk.gov.hmrc.scachangeofcircumstances.connectors

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, badRequest, ok, urlEqualTo}
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers.running
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.scachangeofcircumstances.models.integrationframework._
import uk.gov.hmrc.scachangeofcircumstances.models.{BadRequest, GatewayTimeout, InvalidJson}

import java.time.LocalDate

class IfConnectorTest extends AnyFreeSpec with ScalaFutures with BeforeAndAfterAll with BeforeAndAfterEach {

  val server: WireMockServer = new WireMockServer(wireMockConfig().dynamicPort())

  override protected def beforeAll(): Unit = {
    server.start()
    super.beforeAll()
  }

  override protected def beforeEach(): Unit = {
    server.resetAll()
    super.beforeEach()
  }

  override protected def afterAll(): Unit = {
    server.stop()
    super.afterAll()
  }

  private def application(): Application = new GuiceApplicationBuilder()
    .configure(
      "microservice.services.integration-framework.host" -> "127.0.0.1",
      "microservice.services.integration-framework.port" -> server.port(),
      "microservice.services.integration-framework.authorizationToken" -> "auth-token",
      "microservice.services.integration-framework.environment" -> "test-environment",
      "metrics.enabled" -> false,
      "auditing.enabled" -> false
    ).build()

  val nino: String = "AB049513"

  val fields: String =
    "details(marriageStatusType),nameList(name(nameSequenceNumber,nameType,titleType," +
      "requestedName,nameStartDate,nameEndDate,firstForename,secondForename,surname))," +
      "addressList(address(addressSequenceNumber,countryCode,addressType,addressStartDate," +
      "addressEndDate,addressLine1,addressLine2,addressLine3,addressLine4,addressLine5," +
      "addressPostcode))"

  val url = s"/individuals/details/nino/$nino?fields=$fields"

  implicit private lazy val hc: HeaderCarrier = HeaderCarrier()

  "getDesignatoryDetails" - {

    "when response is 200" - {

      "should return IfDesignatoryDetails when response can be parsed" in {

        val app = application()

        running(app) {



          val expectedResult =
            """
              |{
              |  "details": {
              |    "marriageStatusType": 4
              |  },
              |  "nameList": {
              |    "name": [
              |      {
              |        "nameSequenceNumber": 1,
              |        "nameType": 1,
              |        "titleType": 1,
              |        "requestedName": "Mister Thirty Five Characters Exact",
              |        "nameStartDate": "1996-12-28",
              |        "nameEndDate": "2017-03-31",
              |        "firstForename": "BBTESTFIRSTNAME",
              |        "secondForename": "CCTESTSECONDNAME",
              |        "surname": "AATESTSURNAME"
              |      }
              |    ]
              |  },
              |  "addressList": {
              |    "address": [
              |      {
              |        "addressSequenceNumber": 2,
              |        "countryCode": 1,
              |        "addressType": 1,
              |        "addressStartDate": "2003-04-30",
              |        "addressEndDate": "2009-12-31",
              |        "addressLine1": "88 TESTING ROAD",
              |        "addressLine2": "TESTTOWN",
              |        "addressLine3": "TESTREGION",
              |        "addressLine4": "TESTAREA",
              |        "addressLine5": "TESTSHIRE",
              |        "addressPostcode": "XX77 6YY"
              |      }
              |    ]
              |  }
              |}
              |""".stripMargin

          server.stubFor(
            WireMock.get(urlEqualTo(url))
              .willReturn(ok(expectedResult))
          )

          val expectedObj = IfDesignatoryDetails(
            details = IfDetails(4),
            nameList = IfNameList(Seq(
              IfName(
                nameSequenceNumber = 1,
                nameType = 1,
                titleType = 1,
                requestedName = "Mister Thirty Five Characters Exact",
                nameStartDate = LocalDate.of(1996, 12, 28),
                nameEndDate = LocalDate.of(2017, 3, 31),
                firstForename = "BBTESTFIRSTNAME",
                secondForename = "CCTESTSECONDNAME",
                surname = "AATESTSURNAME"
              ))),
            addressList = IfAddressList(Seq(
              IfAddress(
                addressSequenceNumber = 2,
                countryCode = 1,
                addressType = 1,
                addressStartDate = LocalDate.of(2003, 4, 30),
                addressEndDate = LocalDate.of(2009, 12, 31),
                addressLine1 = "88 TESTING ROAD",
                addressLine2 = "TESTTOWN",
                addressLine3 = "TESTREGION",
                addressLine4 = "TESTAREA",
                addressLine5 = "TESTSHIRE",
                addressPostcode = "XX77 6YY"
              )))
          )

          val connector = app.injector.instanceOf[IfConnector]
          connector.getDesignatoryDetails(nino).futureValue mustEqual expectedObj
        }
      }

      "should return ErrorResponse when response cannot be parsed" in {

        val app = application()

        running(app) {

          val expectedResult =
            """
              |{
              |  "details": {
              |    "marriageStatusType": 4
              |  },
              |  "nameList": {
              |    "name": [
              |      {
              |        "nameSequenceNumber": 1,
              |        "nameType": 1,
              |        "titleType": 1,
              |        "requestedName": "Mister Thirty Five Characters Exact",
              |        "nameStartDate": "!InvalidDate!",
              |        "nameEndDate": "2017-03-31",
              |        "firstForename": "BBTESTFIRSTNAME",
              |        "secondForename": "CCTESTSECONDNAME",
              |        "surname": "AATESTSURNAME"
              |      }
              |    ]
              |  },
              |  "addressList": {
              |    "address": [
              |    ]
              |  }
              |}
              |""".stripMargin

          server.stubFor(
            WireMock.get(urlEqualTo(url))
              .willReturn(ok(expectedResult))
          )

          val connector = app.injector.instanceOf[IfConnector]
          connector.getDesignatoryDetails(nino).futureValue mustEqual Left(InvalidJson)
        }
      }
    }

    "should return ErrorResponse when IF returns error" in {

      val expectedResponse = """{
                               |  "failures": [
                               |    {
                               |      "code": "INVALID_CORRELATIONID",
                               |      "reason": "Submission has not passed validation. Invalid Header CorrelationId."
                               |    }
                               |  ]
                               |}""".stripMargin

      val app = application()

      running(app) {
        server.stubFor(
          WireMock.get(urlEqualTo(url))
            .willReturn(badRequest().withBody(expectedResponse))
        )

        val connector = app.injector.instanceOf[IfConnector]
        connector.getDesignatoryDetails(nino).futureValue mustEqual Left(BadRequest)
      }
    }

    "should return ErrorResponse when returns timeout exception" in {

      val app = application()

      running(app) {
        server.stubFor(
          WireMock.get(urlEqualTo(url))
            .willReturn(aResponse()
            .withStatus(200)
            .withFixedDelay(50000))
        )

        val connector = app.injector.instanceOf[IfConnector]
        connector.getDesignatoryDetails(nino).futureValue mustEqual GatewayTimeout
      }
    }
  }
}
