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

sealed trait MaritalStatus {
  def getCode: Int
}

abstract class BaseMaritalStatus(code: Int) extends MaritalStatus {
  override def getCode: Int = code
}

// MARRIAGE TERMINATED - REASON NOT KNOWN
case object MarriageTerminated extends BaseMaritalStatus(0)
case object MarriageAnnulled extends BaseMaritalStatus(1)
case object Void extends BaseMaritalStatus(2)
case object Divorced extends BaseMaritalStatus(3)
case object Widowed extends BaseMaritalStatus(4)
case object Married extends BaseMaritalStatus(5)
case object Single extends BaseMaritalStatus(7)
case object CivilPartnership extends BaseMaritalStatus(9)
case object CivilPartnershipDissolved extends BaseMaritalStatus(10)
case object SurvivingCivilPartner extends BaseMaritalStatus(11)
case object CivilPartnershipTerminated extends BaseMaritalStatus(12)
case object CivilPartnershipAnnulled extends BaseMaritalStatus(13)
case object None extends BaseMaritalStatus(99)

object MaritalStatus {
  def apply(code: Int): MaritalStatus = {
      code match {
        case 0 => MarriageTerminated
        case 1 => MarriageAnnulled
        case 2 => Void
        case 3 => Divorced
        case 4 => Widowed
        case 5 => Married
        case 7 => Single
        case 9 => CivilPartnership
        case 10 => CivilPartnershipDissolved
        case 11 => SurvivingCivilPartner
        case 12 => CivilPartnershipTerminated
        case 13 => CivilPartnershipAnnulled
        case 99 => None
        case _ => throw new IllegalArgumentException(s"$code is not a valid marital status")
      }
  }
}
