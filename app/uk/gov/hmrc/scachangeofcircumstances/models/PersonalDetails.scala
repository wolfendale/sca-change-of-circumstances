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

import uk.gov.hmrc.scachangeofcircumstances.models.integrationframework.{IfAddress, IfName}

case class PersonalDetails(
                          name: Option[Name],
                          maritalStatus: Option[MaritalStatus],
                          residentialAddress: Option[Address],
                          correspondenceAddress: Option[Address]
                          )

case class Name( firstForename: Option[String],
                 secondForename: Option[String],
                 surname: Option[String],
                 requestedName: Option[String],
                 title: Option[Title]
               )

object Name {

  def apply(name: IfName): Name =
    new Name(
      firstForename = name.firstForename,
      secondForename = name.secondForename,
      surname = name.surname,
      requestedName = name.requestedName,
      title = name.titleType.map(Title.apply))
}

case class Address( addressLine1: Option[String],
                    addressLine2: Option[String],
                    addressLine3: Option[String],
                    addressLine4: Option[String],
                    addressLine5: Option[String],
                    addressPostcode: Option[String],
                    countryCode: Option[Int]
                  )

object Address {

  def apply(address: IfAddress): Address =
    new Address(
      addressLine1 = address.addressLine1,
      addressLine2 = address.addressLine2,
      addressLine3 = address.addressLine3,
      addressLine4 = address.addressLine4,
      addressLine5 = address.addressLine5,
      addressPostcode = address.addressPostcode,
      countryCode = address.countryCode)

}
