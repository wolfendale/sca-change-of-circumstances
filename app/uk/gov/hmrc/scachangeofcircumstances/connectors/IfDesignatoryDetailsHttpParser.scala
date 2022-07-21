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

package uk.gov.hmrc.scachangeofcircumstances.connectors

import play.api.http.Status.OK
import play.api.libs.json.{JsError, JsSuccess}
import uk.gov.hmrc.http.{HttpReads, HttpResponse}
import uk.gov.hmrc.scachangeofcircumstances.logging.Logging
import uk.gov.hmrc.scachangeofcircumstances.models.IfErrorResponse._
import uk.gov.hmrc.scachangeofcircumstances.models.integrationframework.IfDesignatoryDetails
import uk.gov.hmrc.scachangeofcircumstances.models.{ErrorResponse, IfErrorResponse, InvalidJson}


object IfDesignatoryDetailsHttpParser {

  type IfDesignatoryDetailsResponse = Either[ErrorResponse, IfDesignatoryDetails]

  implicit object IfDesignatoryDetailsReads extends HttpReads[IfDesignatoryDetailsResponse] with Logging {
    override def read(method: String, url: String, response: HttpResponse): IfDesignatoryDetailsResponse = {
      response.status match {
        case OK => response.json.validate[IfDesignatoryDetails] match {
          case JsSuccess(value, path) => Right(value)
          case JsError(errors) => {
            logger.error("Could not parse success response from IF", errors)
            Left(InvalidJson)
          }
        }
        case _ => response.json.validate[IfErrorResponse] match {
          case JsSuccess(value, path) => {
            Left(InvalidJson)
          }
          case JsError(errors) => {
            logger.error("Could not parse error response from IF", errors)
            Left(InvalidJson)
          }
        }
      }
    }
  }

}
