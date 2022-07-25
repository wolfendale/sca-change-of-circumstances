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

import play.api.libs.json._

import scala.util.Try

sealed trait Title {
  def getCode: Int
}

abstract class BaseTitle(code: Int) extends Title {
  override def getCode: Int = code
}

case object NotKnown extends BaseTitle(0) {
  override def toString: String = "Not Known"
}
case object Mr extends BaseTitle(1) {
  override def toString: String = "Mr"
}
case object Mrs extends BaseTitle(2) {
  override def toString: String = "Mrs"
}
case object Miss extends BaseTitle(3) {
  override def toString: String = "Miss"
}
case object Ms extends BaseTitle(4) {
  override def toString: String = "Ms"
}
case object Doctor extends BaseTitle(5) {
  override def toString: String = "Doctor"
}
case object Reverend extends BaseTitle(6) {
  override def toString: String = "Reverend"
}

object Title {

  implicit val format: Format[Title] = Format(
    Reads {
      jsValue => jsValue match {
        case JsString(value) =>
          Try(Title(value))
            .map(t => JsSuccess(t))
            .getOrElse(JsError())
        case _ => JsError()
      }
    },
    Writes[Title] {
      x: Title => JsString(x.toString)
    },
  )

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

  def apply(string: String): Title = {
    string match {
      case "Not Known" => NotKnown
      case "Mr" => Mr
      case "Mrs" => Mrs
      case "Miss" => Miss
      case "Ms" => Ms
      case "Doctor" => Doctor
      case "Reverend" => Reverend
      case _ => throw new IllegalArgumentException(s"$string is not a valid title")
    }
  }
}
