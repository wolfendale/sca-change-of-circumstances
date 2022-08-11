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

package uk.gov.hmrc.scachangeofcircumstances.errors


import play.api.Configuration
import play.api.mvc.{RequestHeader, Result}
import uk.gov.hmrc.http.NotFoundException
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.bootstrap.backend.http.JsonErrorHandler
import uk.gov.hmrc.play.bootstrap.config.HttpAuditEvent
import uk.gov.hmrc.scachangeofcircumstances.errors.ErrorResponses.{ErrorInternalServer, ErrorNotFound}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CustomErrorHandler @Inject()(
                                    auditConnector: AuditConnector,
                                    httpAuditEvent: HttpAuditEvent,
                                    configuration: Configuration)(implicit ec: ExecutionContext)
  extends JsonErrorHandler(auditConnector, httpAuditEvent, configuration) {


  override def onServerError(request: RequestHeader, ex: Throwable): Future[Result] = {
    ex match {
      case _: NotFoundException =>  Future.successful(ErrorNotFound.toHttpResponse)
      case _ => Future.successful(ErrorInternalServer("Something went wrong.").toHttpResponse)
    }
  }

//  override def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {
//
//    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request = request, session = request.session)
//
//    statusCode match {
//      case NOT_FOUND =>
//        Future.failed(new NotFoundException("Record not found for provided NiNo."))
//      case _ =>
//        Future.failed(new InternalServerException("Something went wrong."))
//    }
//  }
}