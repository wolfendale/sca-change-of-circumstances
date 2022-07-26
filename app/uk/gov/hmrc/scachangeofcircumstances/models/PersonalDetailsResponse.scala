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
                                    residentialAddress: Option[Address] = scala.None,
                                    correspondenceAddress: Option[Address] = scala.None )

object PersonalDetailsResponse {
  implicit val format: OFormat[PersonalDetailsResponse] = Json.format[PersonalDetailsResponse]
}

case class PersonalDetails(  name: Option[Name] = scala.None,
                             maritalStatus: Option[Int] = scala.None )

object PersonalDetails {
  implicit val format: OFormat[PersonalDetails] = Json.format[PersonalDetails]
}

case class Name( firstForename: Option[String] = scala.None,
                 secondForename: Option[String] = scala.None,
                 surname: Option[String] = scala.None,
                 requestedName: Option[String] = scala.None,
                 title: Option[Int] = scala.None
               )

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

case class Address( addressLine1: Option[String] = scala.None,
                    addressLine2: Option[String] = scala.None,
                    addressLine3: Option[String] = scala.None,
                    addressLine4: Option[String] = scala.None,
                    addressLine5: Option[String] = scala.None,
                    addressPostcode: Option[String] = scala.None,
                    countryCode: Option[Int] = scala.None )

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
