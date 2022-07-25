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

import uk.gov.hmrc.scachangeofcircumstances.logging.Logging


object IfDesignatoryDetailsHttpParser extends Logging {

//  type IfDesignatoryDetailsResponse = Either[IfError, IfDesignatoryDetails]
//
//  private def validate[T](response: HttpResponse)(implicit reads: Reads[T]): T = {
//      response.json.validate[T] match {
//        case JsSuccess(value, path) => value
//        case JsError(errors) => {
//          logger.error("Could not parse response from IF", errors)
//          throw IfException("Could not parse response from IF")
//        }
//      }
//  }
//
//  implicit object IfDesignatoryDetailsReads extends HttpReads[IfDesignatoryDetailsResponse] {
//    override def read(method: String, url: String, response: HttpResponse): IfDesignatoryDetailsResponse = {
//      response.status match {
//        case OK => {
//          val x = Try(validate[IfDesignatoryDetails](response))
//          x match {
//            case Success(value) => Right(value)
//            case Failure(exception) => Left(IfExceptionResponse(exception))
//          }
//        }
//        case _ => {
//          val x = Try(validate[IfErrorResponse](response))
//          x match {
//            case Success(value) => Left(value)
//            case Failure(exception) => Left(IfExceptionResponse(exception))
//          }
//        }
//      }
//    }
//  }

//  .failures.map(x =>
//    response.status match {
//      case BAD_REQUEST => BadRequest
//      case NOT_FOUND => NotFound
//      case UNPROCESSABLE_ENTITY => UnprocessableEntity
//      case PRECONDITION_REQUIRED => PreconditionRequired
//      case INTERNAL_SERVER_ERROR => InternalServerError
//      case BAD_GATEWAY => BadGateway
//      case SERVICE_UNAVAILABLE => ServiceUnavailable
//    }
//  ).toSeq)

}
