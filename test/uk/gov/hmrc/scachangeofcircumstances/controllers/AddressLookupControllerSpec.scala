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

package uk.gov.hmrc.scachangeofcircumstances.controllers

import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.http.Status
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.auth.core.AuthConnector
import uk.gov.hmrc.scachangeofcircumstances.models.addresslookup.{AddressLookupRequest, Country, ProposedAddress}
import uk.gov.hmrc.scachangeofcircumstances.services.AddressLookupService
import uk.gov.hmrc.scachangeofcircumstances.utils.TestAuthAction

import scala.concurrent.{ExecutionContext, Future}

class AddressLookupControllerSpec extends AnyWordSpec with Matchers {

  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

  val correlationId = "e4206b42-11ac-11ed-861d-0242ac120002"

  private val mockService = mock[AddressLookupService]

  "POST /lookup" should {

    "return 200" in {

      val nino     = Some("J1234567D")
      val postcode = "AA11 1AA"
      val house    = Some("1")

      val fakeRequest = FakeRequest("POST", "/lookup")
        .withHeaders("CorrelationId" -> correlationId)
        .withBody(Json.toJson(AddressLookupRequest(postcode = postcode, filter = house)))

      val mockAuthAction = new TestAuthAction(nino, mock[AuthConnector], Helpers.stubControllerComponents())
      val controller     = new AddressLookupController(mockAuthAction, mockService, Helpers.stubControllerComponents())

      val expected =
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

      when(mockService.find(eqTo(postcode), eqTo(house), eqTo(true))(any()))
        .thenReturn(Future.successful(Seq(expected)))

      val result = controller.lookup()(fakeRequest)

      status(result)        shouldBe Status.OK
      contentAsJson(result) shouldBe Json.toJson(Seq(expected))
    }
  }
}
