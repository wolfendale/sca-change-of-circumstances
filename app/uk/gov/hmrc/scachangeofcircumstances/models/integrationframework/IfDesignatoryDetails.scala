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

import java.time.LocalDate

case class IfDesignatoryDetails( details: IfDetails,
                                 nameList: IfNameList,
                                 addressList: IfAddressList )

case class IfDetails(marriageStatusType: Int)

case class IfNameList(name: Seq[IfName])

case class IfName(nameSequenceNumber: Int,
                  nameType: Int,
                  titleType: Int,
                  requestedName: String,
                  nameStartDate: LocalDate,
                  nameEndDate: LocalDate,
                  firstForename: String,
                  secondForename: String,
                  surname: String )

case class IfAddressList(address: Seq[IfAddress])

case class IfAddress( addressSequenceNumber: Int,
                      countryCode: Int,
                      addressType: Int,
                      addressStartDate: LocalDate,
                      addressEndDate: LocalDate,
                      addressLine1: String,
                      addressLine2: String,
                      addressLine3: String,
                      addressLine4: String,
                      addressLine5: String,
                      addressPostcode: String)