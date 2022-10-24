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

package uk.gov.hmrc.scachangeofcircumstances.services

import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito
import org.mockito.Mockito.when
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.scachangeofcircumstances.connectors.AddressLookupConnector
import uk.gov.hmrc.scachangeofcircumstances.models.addresslookup.{Country, ProposedAddress}
import uk.gov.hmrc.scachangeofcircumstances.utils.BaseUnitTests

import scala.concurrent.Future

class AddressLookupServiceSpec extends BaseUnitTests with ScalaFutures with BeforeAndAfterEach {

  private val mockAddressLookupConnector = mock[AddressLookupConnector]

  implicit val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", s"/lookup/")

  override protected def beforeEach(): Unit =
    Mockito.reset(mockAddressLookupConnector)

  "find" - {

    "must return addresses when connector response is successful" in {

      val nino     = "AA999999A"
      val postcode = "AA11 1AA"
      val house    = Option("1")

      val address =
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

      when(mockAddressLookupConnector.find(eqTo(postcode), eqTo(house), eqTo(true))(any()))
        .thenReturn(Future.successful(Seq(address)))

      val service = new AddressLookupService(mockAddressLookupConnector)

      service.find("AA11 1AA", Some("1"), true).futureValue shouldBe Seq(address)
    }
  }
}
