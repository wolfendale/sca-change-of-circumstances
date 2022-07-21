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

import play.api.http.Status._
import play.api.libs.json.{JsError, JsSuccess}
import uk.gov.hmrc.http.{HttpReads, HttpResponse}
import uk.gov.hmrc.scachangeofcircumstances.logging.Logging
import uk.gov.hmrc.scachangeofcircumstances.models.IfErrorResponse._
import uk.gov.hmrc.scachangeofcircumstances.models._
import uk.gov.hmrc.scachangeofcircumstances.models.integrationframework.IfDesignatoryDetails


object IfDesignatoryDetailsHttpParser {

  type IfDesignatoryDetailsResponse = Either[Seq[ErrorResponse], IfDesignatoryDetails]

  implicit object IfDesignatoryDetailsReads extends HttpReads[IfDesignatoryDetailsResponse] with Logging {
    override def read(method: String, url: String, response: HttpResponse): IfDesignatoryDetailsResponse = {
      response.status match {
        case OK => response.json.validate[IfDesignatoryDetails] match {
          case JsSuccess(value, path) => Right(value)
          case JsError(errors) => {
            logger.error("Could not parse success response from IF", errors)
            Left(Seq(InvalidJson))
          }
        }
        case _ => getErrorResponse(response)
      }
    }

    def getErrorResponse(response: HttpResponse): IfDesignatoryDetailsResponse = {
      response.json.validate[IfErrorResponse] match {
        case JsSuccess(value, path) => {
          Left(value.failures.map(x =>
            response.status match {
              case BAD_REQUEST => BadRequest
              case NOT_FOUND => NotFound
              case UNPROCESSABLE_ENTITY => UnprocessableEntity
              case PRECONDITION_REQUIRED => PreconditionRequired
              case INTERNAL_SERVER_ERROR => InternalServerError
              case BAD_GATEWAY => BadGateway
              case SERVICE_UNAVAILABLE => ServiceUnavailable
            }
          ).toSeq)
        }
        case JsError(errors) => {
          logger.error("Could not parse error response from IF", errors)
          Left(Seq(InvalidJson))
        }
      }
    }
  }

}
