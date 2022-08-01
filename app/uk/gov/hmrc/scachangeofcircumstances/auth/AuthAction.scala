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

package uk.gov.hmrc.scachangeofcircumstances.auth

import com.google.inject.ImplementedBy
import play.api.mvc._
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals._
import uk.gov.hmrc.auth.core.{AuthConnector, AuthorisedFunctions}
import uk.gov.hmrc.http.{HeaderCarrier, UnauthorizedException}
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import uk.gov.hmrc.scachangeofcircumstances.logging.Logging

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

@ImplementedBy(classOf[AuthActionImpl])
trait AuthAction extends ActionRefiner[Request, AuthorisedRequest]

class AuthActionImpl @Inject()( override val authConnector: AuthConnector,
                                cc: ControllerComponents
                              ) (implicit val executionContext: ExecutionContext)
  extends AuthAction with AuthorisedFunctions with Logging {

  override protected def refine[A](request: Request[A]): Future[Either[Result, AuthorisedRequest[A]]] = {
    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequest(request)
    authorised().retrieve(nino) {
      case nino@Some(value) => Future.successful(Right(AuthorisedRequest(request, nino)))
      case _ => throw new UnauthorizedException("Unable to retrieve National Insurance number")
    }
  }
}