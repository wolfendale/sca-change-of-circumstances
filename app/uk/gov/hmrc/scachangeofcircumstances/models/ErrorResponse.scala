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

trait ErrorResponse {
  val msg: String
}

case object InvalidJson extends ErrorResponse {
  val msg = "InvalidJson"
}

case object GatewayTimeout extends ErrorResponse {
  val msg = "Gateway timeout"
}

case object BadRequest extends ErrorResponse {
  val msg = "Bad Request"
}

case object NotFound extends ErrorResponse {
  val msg = "Not Found"
}

case object UnprocessableEntity extends ErrorResponse {
  val msg = "UnprocessableEntity"
}

case object PreconditionRequired extends ErrorResponse {
  val msg = "PreconditionRequired"
}

case object InternalServerError extends ErrorResponse {
  val msg = "InternalServerError"
}

case object BadGateway extends ErrorResponse {
  val msg = "BadGateway"
}

case object ServiceUnavailable extends ErrorResponse {
  val msg = "ServiceUnavailable"
}


