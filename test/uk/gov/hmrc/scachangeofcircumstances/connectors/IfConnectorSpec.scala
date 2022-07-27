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

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock._
import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import org.scalatest.matchers.should.Matchers.{a, convertToAnyShouldWrapper}
import org.scalatest.time.Span
import play.api.Configuration
import play.api.test.Helpers.running
import uk.gov.hmrc.http.{InternalServerException, NotFoundException}
import uk.gov.hmrc.scachangeofcircumstances.models.integrationframework._
import uk.gov.hmrc.scachangeofcircumstances.utils.{BaseUnitTests, WireMockHelper}

import java.time.LocalDate

class IfConnectorSpec extends BaseUnitTests with WireMockHelper with ScalaFutures  {

  val timeout: Timeout = Timeout(Span.Max)

  lazy val ifConfig: Configuration = Configuration(
    "microservice.services.integration-framework.host" -> "127.0.0.1",
    "microservice.services.integration-framework.port" -> server.port(),
    "microservice.services.integration-framework.authorizationToken" -> "auth-token",
    "microservice.services.integration-framework.environment" -> "test-environment")

  override val nino: Option[String] = Some("AB049513")

  val designatoryDetailsFields: String =
    "details(marriageStatusType),nameList(name(nameSequenceNumber,nameType,titleType," +
      "requestedName,nameStartDate,nameEndDate,firstForename,secondForename,surname))," +
      "addressList(address(addressSequenceNumber,countryCode,addressType,addressStartDate," +
      "addressEndDate,addressLine1,addressLine2,addressLine3,addressLine4,addressLine5," +
      "addressPostcode))"

  val contactDetailsFields: String = "contactDetails(code,type,detail)"


  "getDesignatoryDetails" - {

    val url = s"/individuals/details/nino/${nino.get}?fields=$designatoryDetailsFields"

    "when response is 200" - {

      "should return IfDesignatoryDetails when response can be parsed" in {

        val app = appBuilder().configure(ifConfig).build()

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
            details = IfDetails(Option(4)),
            nameList = IfNameList(Seq(
              IfName(
                nameSequenceNumber = Option(1),
                nameType = Option(1),
                titleType = Option(1),
                requestedName = Option("Mister Thirty Five Characters Exact"),
                nameStartDate = Option(LocalDate.of(1996, 12, 28)),
                nameEndDate = Option(LocalDate.of(2017, 3, 31)),
                firstForename = Option("BBTESTFIRSTNAME"),
                secondForename = Option("CCTESTSECONDNAME"),
                surname = Option("AATESTSURNAME")
              ))),
            addressList = IfAddressList(Seq(
              IfAddress(
                addressSequenceNumber = Option(2),
                countryCode = Option(1),
                addressType = Option(1),
                addressStartDate = Option(LocalDate.of(2003, 4, 30)),
                addressEndDate = Option(LocalDate.of(2009, 12, 31)),
                addressLine1 = Option("88 TESTING ROAD"),
                addressLine2 = Option("TESTTOWN"),
                addressLine3 = Option("TESTREGION"),
                addressLine4 = Option("TESTAREA"),
                addressLine5 = Option("TESTSHIRE"),
                addressPostcode = Option("XX77 6YY")
              )))
          )

          val connector = app.injector.instanceOf[IfConnector]
          connector.getDesignatoryDetails(nino.get).futureValue(timeout) mustEqual expectedObj
        }
      }

      "should return ErrorResponse when response cannot be parsed" in {

        val app = appBuilder().configure(ifConfig).build()

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

          ScalaFutures.whenReady(connector.getDesignatoryDetails(nino.get).failed) { e =>
            e shouldBe a[InternalServerException]
          }
        }
      }
    }

    "should return Exception when IF returns" - {

      "not found response" in {
        val expectedResponse =
          """{
            |  "failures": [
            |    {
            |      "code": "IDENTIFIER_NOT_FOUND",
            |      "reason": "The remote endpoint has indicated that identifier supplied can not be found."
            |    }
            |  ]
            |}""".stripMargin

        val app = appBuilder().configure(ifConfig).build()

        running(app) {
          server.stubFor(
            WireMock.get(urlEqualTo(url))
              .willReturn(notFound().withBody(expectedResponse))
          )

          val connector = app.injector.instanceOf[IfConnector]

          ScalaFutures.whenReady(connector.getDesignatoryDetails(nino.get).failed) { e =>
            e shouldBe a[NotFoundException]
          }
        }
      }

      "bad request error response" in {
        val expectedResponse =
          """{
            |  "failures": [
            |    {
            |      "code": "INVALID_CORRELATIONID",
            |      "reason": "Submission has not passed validation. Invalid Header CorrelationId."
            |    }
            |  ]
            |}""".stripMargin

        val app = appBuilder().configure(ifConfig).build()

        running(app) {
          server.stubFor(
            WireMock.get(urlEqualTo(url))
              .willReturn(badRequest().withBody(expectedResponse))
          )

          val connector = app.injector.instanceOf[IfConnector]

          ScalaFutures.whenReady(connector.getDesignatoryDetails(nino.get).failed) { e =>
            e shouldBe a[InternalServerException]
          }
        }
      }

      "server error response" in {
        val expectedResponse = """{
                                 |  "failures": [
                                 |    {
                                 |      "code": "SERVER_ERROR",
                                 |      "reason": "IF is currently experiencing problems that require live service intervention."
                                 |    }
                                 |  ]
                                 |}""".stripMargin

        val app = appBuilder().configure(ifConfig).build()

        running(app) {
          server.stubFor(
            WireMock.get(urlEqualTo(url))
              .willReturn(serverError().withBody(expectedResponse))
          )

          val connector = app.injector.instanceOf[IfConnector]

          ScalaFutures.whenReady(connector.getDesignatoryDetails(nino.get).failed) { e =>
            e shouldBe a[InternalServerException]
          }
        }
      }

      "service unavailable error response" in {
        val expectedResponse = """{
                                 |        "failures": [
                                 |        {
                                 |          "code": "SERVICE_UNAVAILABLE",
                                 |          "reason": "Dependent systems are currently not responding."
                                 |        }
                                 |        ]
                                 |      }""".stripMargin

        val app = appBuilder().configure(ifConfig).build()

        running(app) {
          server.stubFor(
            WireMock.get(urlEqualTo(url))
              .willReturn(serviceUnavailable().withBody(expectedResponse))
          )

          val connector = app.injector.instanceOf[IfConnector]
          ScalaFutures.whenReady(connector.getDesignatoryDetails(nino.get).failed) { e =>
            e shouldBe a[InternalServerException]
          }
        }
      }
    }

    "should throw internal server exception when timeout exception encountered" in {

      val app = appBuilder().configure(ifConfig).build()

      running(app) {
        server.stubFor(
          WireMock.get(urlEqualTo(url))
            .willReturn(aResponse()
            .withStatus(200)
            .withBody("true")
            .withFixedDelay(30000))
        )

        val connector = app.injector.instanceOf[IfConnector]
        ScalaFutures.whenReady(connector.getDesignatoryDetails(nino.get).failed, timeout) { e =>
          e shouldBe a[InternalServerException]
        }
      }
    }
  }


  "getContactDetails" - {

    val url = s"/individuals/details/contact/nino/${nino.get}?fields=$contactDetailsFields"

    "when response is 200" - {

      "should return IfContactDetails when response can be parsed" in {

        val app = appBuilder().configure(ifConfig).build()

        running(app) {

          val expectedResult =
            """{
              |"contactDetails": [
              |{
              |   "code": 9,
              |   "type": "MOBILE TELEPHONE",
              |   "detail": "07123 987654"
              |},
              |{
              |   "code": 7,
              |   "type": "DAYTIME TELEPHONE",
              |   "detail": "01613214567"
              |},
              |{
              |   "code": 8,
              |   "type": "EVENING TELEPHONE",
              |   "detail": "01619873210"
              |},
              |{
              |   "code": 11,
              |   "type": "PRIMARY E-MAIL",
              |   "detail": "fred.blogs@hotmail.com"
              |}
              |] }""".stripMargin

          server.stubFor(
            WireMock.get(urlEqualTo(url))
              .willReturn(ok(expectedResult))
          )

          val expectedObj = IfContactDetails(Some(
            Seq(
              IfContactDetail(9, "MOBILE TELEPHONE", "07123 987654"),
              IfContactDetail(7, "DAYTIME TELEPHONE", "01613214567"),
              IfContactDetail(8, "EVENING TELEPHONE", "01619873210"),
              IfContactDetail(11, "PRIMARY E-MAIL", "fred.blogs@hotmail.com")
            )
          ))

          val connector = app.injector.instanceOf[IfConnector]
          connector.getContactDetails(nino.get).futureValue(timeout) mustEqual expectedObj
        }
      }

      "should return ErrorResponse when response cannot be parsed" in {

        val app = appBuilder().configure(ifConfig).build()

        running(app) {

          val expectedResult =
            """{
              |"contactDetails": "string" }""".stripMargin

          server.stubFor(
            WireMock.get(urlEqualTo(url))
              .willReturn(ok(expectedResult))
          )

          val connector = app.injector.instanceOf[IfConnector]

          ScalaFutures.whenReady(connector.getContactDetails(nino.get).failed) { e =>
            e shouldBe a[InternalServerException]
          }
        }
      }
    }

    "should return Exception when IF returns" - {

      "not found response" in {

        val expectedResponse =
          """{
            |  "failures": [
            |    {
            |      "code": "PERSON_NOT_FOUND",
            |      "reason": "The remote endpoint has indicated that no record could be found."
            |    }
            |  ]
            |}""".stripMargin

        val app = appBuilder().configure(ifConfig).build()

        running(app) {
          server.stubFor(
            WireMock.get(urlEqualTo(url))
              .willReturn(notFound().withBody(expectedResponse))
          )

          val connector = app.injector.instanceOf[IfConnector]

          ScalaFutures.whenReady(connector.getContactDetails(nino.get).failed) { e =>
            e shouldBe a[NotFoundException]
          }
        }
      }

      "bad request error response" in {
        val expectedResponse =
          """{
            |"failures": [
            | {
            |   "code": "INVALID_IDTYPE",
            |   "reason": "Submission has not passed validation. Invalid parameter idType."
            | }
            |]
            |}""".stripMargin

        val app = appBuilder().configure(ifConfig).build()

        running(app) {
          server.stubFor(
            WireMock.get(urlEqualTo(url))
              .willReturn(badRequest().withBody(expectedResponse))
          )

          val connector = app.injector.instanceOf[IfConnector]

          ScalaFutures.whenReady(connector.getContactDetails(nino.get).failed) { e =>
            e shouldBe a[InternalServerException]
          }
        }
      }

      "server error response" in {
        val expectedResponse = """{
                                 |  "failures": [
                                 |    {
                                 |      "code": "SERVER_ERROR",
                                 |      "reason": "IF is currently experiencing problems that require live service intervention."
                                 |    }
                                 |  ]
                                 |}""".stripMargin

        val app = appBuilder().configure(ifConfig).build()

        running(app) {
          server.stubFor(
            WireMock.get(urlEqualTo(url))
              .willReturn(serverError().withBody(expectedResponse))
          )

          val connector = app.injector.instanceOf[IfConnector]

          ScalaFutures.whenReady(connector.getContactDetails(nino.get).failed) { e =>
            e shouldBe a[InternalServerException]
          }
        }
      }

      "service unavailable error response" in {
        val expectedResponse = """{
                                 |        "failures": [
                                 |        {
                                 |          "code": "SERVICE_UNAVAILABLE",
                                 |          "reason": "Dependent systems are currently not responding."
                                 |        }
                                 |        ]
                                 |      }""".stripMargin

        val app = appBuilder().configure(ifConfig).build()

        running(app) {
          server.stubFor(
            WireMock.get(urlEqualTo(url))
              .willReturn(serviceUnavailable().withBody(expectedResponse))
          )

          val connector = app.injector.instanceOf[IfConnector]
          ScalaFutures.whenReady(connector.getContactDetails(nino.get).failed) { e =>
            e shouldBe a[InternalServerException]
          }
        }
      }
    }

    "should throw internal server exception when timeout exception encountered" in {

      val app = appBuilder().configure(ifConfig).build()

      running(app) {
        server.stubFor(
          WireMock.get(urlEqualTo(url))
            .willReturn(aResponse()
              .withStatus(200)
              .withBody("true")
              .withFixedDelay(30000))
        )

        val connector = app.injector.instanceOf[IfConnector]
        ScalaFutures.whenReady(connector.getContactDetails(nino.get).failed, timeout) { e =>
          e shouldBe a[InternalServerException]
        }
      }
    }
  }
}
