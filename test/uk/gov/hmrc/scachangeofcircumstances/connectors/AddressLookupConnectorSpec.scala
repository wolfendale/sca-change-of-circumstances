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
import com.github.tomakehurst.wiremock.client.WireMock.{equalTo, ok, urlEqualTo}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import play.api.libs.json.Json
import play.api.test.Helpers.running
import uk.gov.hmrc.http.test.WireMockSupport
import uk.gov.hmrc.scachangeofcircumstances.config.AppConfig
import uk.gov.hmrc.scachangeofcircumstances.models.addresslookup.{Country, LookupAddressByPostcodeRequest, ProposedAddress}
import uk.gov.hmrc.scachangeofcircumstances.utils.BaseUnitTests

class AddressLookupConnectorSpec extends BaseUnitTests with BeforeAndAfterEach with WireMockSupport with ScalaFutures {

  "AddressLookupConnector" - {

    "must return success response when request is successful" - {

      val addressLookupRequest = LookupAddressByPostcodeRequest("AA11 1AA", Some("1"))

      val addressOne =
        new ProposedAddress(
          "id",
          uprn = None,
          parentUprn = None,
          usrn = None,
          organisation = None,
          postcode = Some("AA11 1AA"),
          town = Some("Town"),
          lines = List("1 Street", "Line 2"),
          country = Country("GB", "Great Britain"),
          poBox = None
        )

      val addressTwo =
        new ProposedAddress(
          "id",
          uprn = None,
          parentUprn = None,
          usrn = None,
          organisation = None,
          postcode = Some("AA11 1AA"),
          town = Some("Town"),
          lines = List("2 Street", "Line 2"),
          country = Country("GB", "Great Britain"),
          poBox = None
        )

      "with a single match" in {

        val app = appBuilder()
          .configure(
            "microservice.services.address-lookup.host" -> wireMockHost,
            "microservice.services.address-lookup.port" -> wireMockPort
          )
          .build()

        running(app) {

          val lookupService = app.injector.instanceOf[AddressLookupConnector]
          val config        = app.injector.instanceOf[AppConfig]
          val url           = s"/lookup"

          val expectedObj = Seq(addressOne)

          val expectedResult = Json.stringify(Json.toJson(expectedObj))

          wireMockServer.stubFor(
            WireMock
              .post(urlEqualTo(url))
              .withRequestBody(equalTo(Json.stringify(Json.toJson(addressLookupRequest))))
              .willReturn(ok(expectedResult))
          )

          val response = lookupService.find(addressLookupRequest.postcode, addressLookupRequest.filter, true)

          response.futureValue shouldBe expectedObj
        }
      }

      "with multiple matches" in {

        val app = appBuilder()
          .configure(
            "microservice.services.address-lookup.host" -> wireMockHost,
            "microservice.services.address-lookup.port" -> wireMockPort
          )
          .build()

        running(app) {

          val lookupService = app.injector.instanceOf[AddressLookupConnector]
          val config        = app.injector.instanceOf[AppConfig]
          val url           = s"/lookup"

          val expectedObj = Seq(addressOne, addressTwo)

          val expectedResult = Json.stringify(Json.toJson(expectedObj))

          wireMockServer.stubFor(
            WireMock
              .post(urlEqualTo(url))
              .withRequestBody(equalTo(Json.stringify(Json.toJson(addressLookupRequest.copy(filter = None)))))
              .willReturn(ok(expectedResult))
          )

          val response = lookupService.find(addressLookupRequest.postcode, None, true)

          response.futureValue shouldBe expectedObj
        }
      }

      "with no matches" in {
        val app = appBuilder()
          .configure(
            "microservice.services.address-lookup.host" -> wireMockHost,
            "microservice.services.address-lookup.port" -> wireMockPort
          )
          .build()

        running(app) {

          val lookupService = app.injector.instanceOf[AddressLookupConnector]
          val config        = app.injector.instanceOf[AppConfig]
          val url           = s"/lookup"

          val expectedObj = Seq[ProposedAddress]()

          val expectedResult = Json.stringify(Json.toJson(expectedObj))

          wireMockServer.stubFor(
            WireMock
              .post(urlEqualTo(url))
              .withRequestBody(equalTo(Json.stringify(Json.toJson(addressLookupRequest.copy(filter = None)))))
              .willReturn(ok(expectedResult))
          )

          val response = lookupService.find(addressLookupRequest.postcode, None, true)

          response.futureValue shouldBe expectedObj
        }
      }

    }

    "must return error when request is unsuccessful" - {}
  }
}