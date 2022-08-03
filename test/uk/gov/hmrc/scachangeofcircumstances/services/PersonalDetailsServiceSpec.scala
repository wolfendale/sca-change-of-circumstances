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
import org.mockito.Mockito
import org.mockito.Mockito.when
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.scachangeofcircumstances.connectors.IfConnector
import uk.gov.hmrc.scachangeofcircumstances.models.integrationframework._
import uk.gov.hmrc.scachangeofcircumstances.models.{Address, ContactDetails, Name, PersonalDetails, PersonalDetailsResponse}
import uk.gov.hmrc.scachangeofcircumstances.utils.BaseUnitTests

import scala.concurrent.Future


class PersonalDetailsServiceSpec extends BaseUnitTests with ScalaFutures with BeforeAndAfterEach {

  private val mockIfConnector = mock[IfConnector]

  implicit val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", s"/personal-details/")

  override protected def beforeEach(): Unit = {
    Mockito.reset(mockIfConnector)
  }

  "getPersonalDetails" - {

    "when IF returns successful response" - {

      "must return real name if there is one" in {

        val designatoryDetailsResponse = IfDesignatoryDetails(
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

        val contactDetailsResponse = IfContactDetails(None)

        when(mockIfConnector.getDesignatoryDetails(any())(any(), any()))
          .thenReturn(Future.successful(designatoryDetailsResponse))

        when(mockIfConnector.getContactDetails(any())(any(), any()))
          .thenReturn(Future.successful(contactDetailsResponse))

        val service = new PersonalDetailsService(mockIfConnector)

        val expected = PersonalDetailsResponse(
          details = PersonalDetails(
            name = Some(Name(
              firstForename = Some("John"),
              surname = Some("Johnson")
            )))
        )

        service.getPersonalDetails("123123132").futureValue shouldBe expected
      }

      "must return most recent known-as name if there is there is no real name" in {

        val designatoryDetailsResponse = IfDesignatoryDetails(
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

        val contactDetailsResponse = IfContactDetails(None)

        when(mockIfConnector.getDesignatoryDetails(any())(any(), any()))
          .thenReturn(Future.successful(designatoryDetailsResponse))

        when(mockIfConnector.getContactDetails(any())(any(), any()))
          .thenReturn(Future.successful(contactDetailsResponse))

        val service = new PersonalDetailsService(mockIfConnector)

        val expected = PersonalDetailsResponse(
          details = PersonalDetails(
            name = Some(Name(
              firstForename = Some("Brian"),
              surname = Some("Brianson")
            )))
        )

        service.getPersonalDetails("123123132").futureValue shouldBe expected
      }

      "must return real name with highest nameSequenceNumber" in {

        val designatoryDetailsResponse = IfDesignatoryDetails(
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

        val contactDetailsResponse = IfContactDetails(None)

        when(mockIfConnector.getDesignatoryDetails(any())(any(), any()))
          .thenReturn(Future.successful(designatoryDetailsResponse))

        when(mockIfConnector.getContactDetails(any())(any(), any()))
          .thenReturn(Future.successful(contactDetailsResponse))

        val service = new PersonalDetailsService(mockIfConnector)

        val expected = PersonalDetailsResponse(
          details = PersonalDetails(
            name = Some(Name(
              firstForename = Some("Brian"),
              surname = Some("Brianson")
            )))
        )

        service.getPersonalDetails("123123132").futureValue shouldBe expected
      }

      "must return no name or address if no name or address is returned" in {

        val designatoryDetailsResponse = IfDesignatoryDetails(
          IfDetails(None),
          IfNameList(Seq()),
          IfAddressList(Seq())
        )

        val contactDetailsResponse = IfContactDetails(None)

        when(mockIfConnector.getDesignatoryDetails(any())(any(), any()))
          .thenReturn(Future.successful(designatoryDetailsResponse))

        when(mockIfConnector.getContactDetails(any())(any(), any()))
          .thenReturn(Future.successful(contactDetailsResponse))

        val service = new PersonalDetailsService(mockIfConnector)

        val expected = PersonalDetailsResponse()

        service.getPersonalDetails("123123132").futureValue shouldBe expected
      }

      "must return most recent residential and correspondence addresses" in {

        val designatoryDetailsResponse = IfDesignatoryDetails(
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


        val contactDetailsResponse = IfContactDetails(None)

        when(mockIfConnector.getDesignatoryDetails(any())(any(), any()))
          .thenReturn(Future.successful(designatoryDetailsResponse))

        when(mockIfConnector.getContactDetails(any())(any(), any()))
          .thenReturn(Future.successful(contactDetailsResponse))

        val service = new PersonalDetailsService(mockIfConnector)

        val expected = PersonalDetailsResponse(
          residentialAddress = Some(Address(addressLine1 = Some("Residential 2"))),
          correspondenceAddress = Some(Address(addressLine1 = Some("Correspondence 2")))
        )

        service.getPersonalDetails("123123132").futureValue shouldBe expected
      }


      "must prioritise daytime phone number if returned" in {

        val designatoryDetailsResponse = IfDesignatoryDetails(
          IfDetails(None),
          IfNameList(Seq()),
          IfAddressList(Seq()))

        val contactDetailsResponse = IfContactDetails(Some(
          Seq(
            IfContactDetail(9, "MOBILE TELEPHONE", "07123 987654"),
            IfContactDetail(7, "DAYTIME TELEPHONE", "01613214567"),
            IfContactDetail(8, "EVENING TELEPHONE", "01619873210")
          )
        ))

        when(mockIfConnector.getDesignatoryDetails(any())(any(), any()))
          .thenReturn(Future.successful(designatoryDetailsResponse))

        when(mockIfConnector.getContactDetails(any())(any(), any()))
          .thenReturn(Future.successful(contactDetailsResponse))

        val service = new PersonalDetailsService(mockIfConnector)

        val expected = PersonalDetailsResponse(
          contactDetails = Some(ContactDetails(
            phoneNumber = Some("01613214567")
          ))
        )

        service.getPersonalDetails("123123132").futureValue shouldBe expected
      }

      "must prioritise evening phone number if no daytime phone number returned" in {

        val designatoryDetailsResponse = IfDesignatoryDetails(
          IfDetails(None),
          IfNameList(Seq()),
          IfAddressList(Seq()))

        val contactDetailsResponse = IfContactDetails(Some(
          Seq(
            IfContactDetail(8, "EVENING TELEPHONE", "01619873210"),
            IfContactDetail(9, "MOBILE TELEPHONE", "07123 987654"),
          )
        ))

        when(mockIfConnector.getDesignatoryDetails(any())(any(), any()))
          .thenReturn(Future.successful(designatoryDetailsResponse))

        when(mockIfConnector.getContactDetails(any())(any(), any()))
          .thenReturn(Future.successful(contactDetailsResponse))

        val service = new PersonalDetailsService(mockIfConnector)

        val expected = PersonalDetailsResponse(
          contactDetails = Some(ContactDetails(
            phoneNumber = Some("01619873210")
          ))
        )

        service.getPersonalDetails("123123132").futureValue shouldBe expected
      }

      "must return mobile phone number if no other phone number returned" in {

        val designatoryDetailsResponse = IfDesignatoryDetails(
          IfDetails(None),
          IfNameList(Seq()),
          IfAddressList(Seq()))

        val contactDetailsResponse = IfContactDetails(Some(
          Seq(
            IfContactDetail(9, "MOBILE TELEPHONE", "07123 987654")
          )
        ))

        when(mockIfConnector.getDesignatoryDetails(any())(any(), any()))
          .thenReturn(Future.successful(designatoryDetailsResponse))

        when(mockIfConnector.getContactDetails(any())(any(), any()))
          .thenReturn(Future.successful(contactDetailsResponse))

        val service = new PersonalDetailsService(mockIfConnector)

        val expected = PersonalDetailsResponse(
          contactDetails = Some(ContactDetails(
            phoneNumber = Some("07123 987654")
          ))
        )

        service.getPersonalDetails("123123132").futureValue shouldBe expected
      }


      "must prioritise primary e-mail address over secondary e-mail address" in {

        val designatoryDetailsResponse = IfDesignatoryDetails(
          IfDetails(None),
          IfNameList(Seq()),
          IfAddressList(Seq()))

        val contactDetailsResponse = IfContactDetails(Some(
          Seq(
            IfContactDetail(11, "PRIMARY E-MAIL", "fred.blogs@hotmail.com"),
            IfContactDetail(12, "SECONDARY E-MAIL", "john.blogs@hotmail.com")
          )
        ))

        when(mockIfConnector.getDesignatoryDetails(any())(any(), any()))
          .thenReturn(Future.successful(designatoryDetailsResponse))

        when(mockIfConnector.getContactDetails(any())(any(), any()))
          .thenReturn(Future.successful(contactDetailsResponse))

        val service = new PersonalDetailsService(mockIfConnector)

        val expected = PersonalDetailsResponse(
          contactDetails = Some(ContactDetails(
            email = Some("fred.blogs@hotmail.com")
          ))
        )

        service.getPersonalDetails("123123132").futureValue shouldBe expected
      }

      "must return secondary e-mail address if no primary e-mail address returned" in {

        val designatoryDetailsResponse = IfDesignatoryDetails(
          IfDetails(None),
          IfNameList(Seq()),
          IfAddressList(Seq()))

        val contactDetailsResponse = IfContactDetails(Some(
          Seq(
            IfContactDetail(12, "SECONDARY E-MAIL", "john.blogs@hotmail.com")
          )
        ))

        when(mockIfConnector.getDesignatoryDetails(any())(any(), any()))
          .thenReturn(Future.successful(designatoryDetailsResponse))

        when(mockIfConnector.getContactDetails(any())(any(), any()))
          .thenReturn(Future.successful(contactDetailsResponse))

        val service = new PersonalDetailsService(mockIfConnector)

        val expected = PersonalDetailsResponse(
          contactDetails = Some(ContactDetails(
            email = Some("john.blogs@hotmail.com")
          ))
        )

        service.getPersonalDetails("123123132").futureValue shouldBe expected
      }
    }
  }
}
