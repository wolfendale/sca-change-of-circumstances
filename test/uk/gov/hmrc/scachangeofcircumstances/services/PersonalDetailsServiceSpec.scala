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

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatestplus.mockito.MockitoSugar.mock
import uk.gov.hmrc.scachangeofcircumstances.connectors.IfConnector
import uk.gov.hmrc.scachangeofcircumstances.models.integrationframework._
import uk.gov.hmrc.scachangeofcircumstances.models.{Address, Name, PersonalDetails}
import uk.gov.hmrc.scachangeofcircumstances.utils.BaseUnitTests

import scala.concurrent.Future


class PersonalDetailsServiceSpec extends BaseUnitTests with ScalaFutures {

  "getPersonalDetails" - {

    val mockIfConnector = mock[IfConnector]

    "when IF returns successful response" - {

      "must return real name if there is one" in {

        val ifResponse = IfDesignatoryDetails(
          IfDetails(None),
          IfNameList(Seq(
            IfName(
              nameSequenceNumber = Some(1),
              nameType = Some(1),
              firstForename = Some("John"),
              surname = Some("Johnson")
            ),
            IfName(
              nameSequenceNumber = Some(2),
              nameType = Some(2),
              firstForename = Some("Brian"),
              surname = Some("Brianson")
            )
          )),
          IfAddressList(Seq())
        )

        when(mockIfConnector.getDesignatoryDetails(any())(any()))
          .thenReturn(Future.successful(ifResponse))

        val service = new PersonalDetailsService(mockIfConnector)

        val expected = PersonalDetails(
          name = Some(Name(
            firstForename = Some("John"),
            surname = Some("Johnson")
          ))
        )

        service.getPersonalDetails("123123132").futureValue shouldBe expected
      }

      "must return most recent known-as name if there is there is no real name" in {

        val ifResponse = IfDesignatoryDetails(
          IfDetails(None),
          IfNameList(Seq(
            IfName(
              nameSequenceNumber = Some(1),
              nameType = Some(2),
              firstForename = Some("John"),
              surname = Some("Johnson")
            ),
            IfName(
              nameSequenceNumber = Some(2),
              nameType = Some(2),
              firstForename = Some("Brian"),
              surname = Some("Brianson")
            )
          )),
          IfAddressList(Seq())
        )

        when(mockIfConnector.getDesignatoryDetails(any())(any()))
          .thenReturn(Future.successful(ifResponse))

        val service = new PersonalDetailsService(mockIfConnector)

        val expected = PersonalDetails(
          name = Some(Name(
            firstForename = Some("Brian"),
            surname = Some("Brianson")
          ))
        )

        service.getPersonalDetails("123123132").futureValue shouldBe expected
      }

      "must return real name with highest nameSequenceNumber" in {

        val ifResponse = IfDesignatoryDetails(
          IfDetails(None),
          IfNameList(Seq(
            IfName(
              nameSequenceNumber = Some(1),
              nameType = Some(1),
              firstForename = Some("John"),
              surname = Some("Johnson")
            ),
            IfName(
              nameSequenceNumber = Some(2),
              nameType = Some(1),
              firstForename = Some("Brian"),
              surname = Some("Brianson")
            )
          )),
          IfAddressList(Seq())
        )

        when(mockIfConnector.getDesignatoryDetails(any())(any()))
          .thenReturn(Future.successful(ifResponse))

        val service = new PersonalDetailsService(mockIfConnector)

        val expected = PersonalDetails(
          name = Some(Name(
            firstForename = Some("Brian"),
            surname = Some("Brianson")
          ))
        )

        service.getPersonalDetails("123123132").futureValue shouldBe expected
      }

      "must return no name or address if no name or address is returned" in {

        val ifResponse = IfDesignatoryDetails(
          IfDetails(None),
          IfNameList(Seq()),
          IfAddressList(Seq())
        )

        when(mockIfConnector.getDesignatoryDetails(any())(any()))
          .thenReturn(Future.successful(ifResponse))

        val service = new PersonalDetailsService(mockIfConnector)

        val expected = PersonalDetails()

        service.getPersonalDetails("123123132").futureValue shouldBe expected
      }

      "must return most recent residential and correspondence addresses" in {

        val ifResponse = IfDesignatoryDetails(
          IfDetails(None),
          IfNameList(Seq()),
          IfAddressList(Seq(
            IfAddress(
              addressLine1 = Some("Residential 1"),
              addressSequenceNumber = Some(1),
              addressType = Some(1)),
            IfAddress(
              addressLine1 = Some("Residential 2"),
              addressSequenceNumber = Some(2),
              addressType = Some(1)),
            IfAddress(
              addressLine1 = Some("Correspondence 1"),
              addressSequenceNumber = Some(3),
              addressType = Some(2)),
            IfAddress(
              addressLine1 = Some("Correspondence 2"),
              addressSequenceNumber = Some(4),
              addressType = Some(2)),
          ))
        )

        when(mockIfConnector.getDesignatoryDetails(any())(any()))
          .thenReturn(Future.successful(ifResponse))

        val service = new PersonalDetailsService(mockIfConnector)

        val expected = PersonalDetails(
          residentialAddress = Some(Address(addressLine1 = Some("Residential 2"))),
          correspondenceAddress = Some(Address(addressLine1 = Some("Correspondence 2")))
        )

        service.getPersonalDetails("123123132").futureValue shouldBe expected
      }
    }
  }
}
