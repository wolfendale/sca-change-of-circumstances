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

package uk.gov.hmrc.scachangeofcircumstances.models

import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.scachangeofcircumstances.models.integrationframework.{IfAddress, IfName}

case class PersonalDetailsResponse( details: PersonalDetails = PersonalDetails(),
                                    contactDetails: Option[ContactDetails] = None,
                                    residentialAddress: Option[Address] = None,
                                    correspondenceAddress: Option[Address] = None )

object PersonalDetailsResponse {
  implicit val format: OFormat[PersonalDetailsResponse] = Json.format[PersonalDetailsResponse]
}

case class PersonalDetails(  name: Option[Name] = None,
                             maritalStatus: Option[Int] = None )

object PersonalDetails {
  implicit val format: OFormat[PersonalDetails] = Json.format[PersonalDetails]
}

case class Name( firstForename: Option[String] = None,
                 secondForename: Option[String] = None,
                 surname: Option[String] = None,
                 requestedName: Option[String] = None,
                 title: Option[Int] = None )

object Name {

  implicit val format: OFormat[Name] = Json.format[Name]

  def apply(name: IfName): Name = new Name(
      firstForename = name.firstForename,
      secondForename = name.secondForename,
      surname = name.surname,
      requestedName = name.requestedName,
      title = name.titleType
  )
}

case class ContactDetails( email: Option[String] = None,
                           phoneNumber: Option[String] = None)

object ContactDetails {

  implicit val format: OFormat[ContactDetails] = Json.format[ContactDetails]

}

case class Address( addressLine1: Option[String] = None,
                    addressLine2: Option[String] = None,
                    addressLine3: Option[String] = None,
                    addressLine4: Option[String] = None,
                    addressLine5: Option[String] = None,
                    addressPostcode: Option[String] = None,
                    countryCode: Option[Int] = None )

object Address {

  implicit val format: OFormat[Address] = Json.format[Address]

  def apply(address: IfAddress): Address = new Address(
      addressLine1 = address.addressLine1,
      addressLine2 = address.addressLine2,
      addressLine3 = address.addressLine3,
      addressLine4 = address.addressLine4,
      addressLine5 = address.addressLine5,
      addressPostcode = address.addressPostcode,
      countryCode = address.countryCode
    )

}
