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

sealed trait Title {
  def getCode: Int
}

abstract class BaseTitle(code: Int) extends Title {
  override def getCode: Int = code
}

case object NotKnown extends BaseTitle(0)
case object Mr extends BaseTitle(1)
case object Mrs extends BaseTitle(2)
case object Miss extends BaseTitle(3)
case object Ms extends BaseTitle(4)
case object Doctor extends BaseTitle(5)
case object Reverend extends BaseTitle(6)

object Title {
  def apply(code: Int): Title = {
    code match {
      case 0 => NotKnown
      case 1 => Mr
      case 2 => Mrs
      case 3 => Miss
      case 4 => Ms
      case 5 => Doctor
      case 6 => Reverend
      case _ => throw new IllegalArgumentException(s"$code is not a valid title")
    }
  }
}
