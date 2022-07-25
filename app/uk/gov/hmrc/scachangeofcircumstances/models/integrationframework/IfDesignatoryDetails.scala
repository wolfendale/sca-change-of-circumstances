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

package uk.gov.hmrc.scachangeofcircumstances.models.integrationframework

import play.api.libs.json.{Json, Reads}

import java.time.LocalDate

case class IfDesignatoryDetails( details: IfDetails,
                                 nameList: IfNameList,
                                 addressList: IfAddressList )

object IfDesignatoryDetails {

  implicit val reads: Reads[IfDesignatoryDetails] = Json.reads[IfDesignatoryDetails]

}

case class IfDetails(marriageStatusType: Option[Int])

object IfDetails {

  implicit val reads: Reads[IfDetails] = Json.reads[IfDetails]

}

case class IfNameList(name: Seq[IfName])

object IfNameList {

  implicit val reads: Reads[IfNameList] = Json.reads[IfNameList]

}

case class IfName(nameSequenceNumber: Option[Int],
                  nameType: Option[Int],
                  titleType: Option[Int],
                  requestedName: Option[String],
                  nameStartDate: Option[LocalDate],
                  nameEndDate: Option[LocalDate],
                  firstForename: Option[String],
                  secondForename: Option[String],
                  surname: Option[String] )

object IfName {

  implicit val reads: Reads[IfName] = Json.reads[IfName]

}

case class IfAddressList(address: Seq[IfAddress])

object IfAddressList {

  implicit val reads: Reads[IfAddressList] = Json.reads[IfAddressList]

}

case class IfAddress( addressSequenceNumber: Option[Int],
                      countryCode: Option[Int],
                      addressType: Option[Int],
                      addressStartDate: Option[LocalDate],
                      addressEndDate: Option[LocalDate],
                      addressLine1: Option[String],
                      addressLine2: Option[String],
                      addressLine3: Option[String],
                      addressLine4: Option[String],
                      addressLine5: Option[String],
                      addressPostcode: Option[String])

object IfAddress {

  implicit val reads: Reads[IfAddress] = Json.reads[IfAddress]

}