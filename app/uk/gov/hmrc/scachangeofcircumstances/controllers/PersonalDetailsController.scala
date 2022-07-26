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

package uk.gov.hmrc.scachangeofcircumstances.controllers

import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.play.http.HeaderCarrierConverter
import uk.gov.hmrc.scachangeofcircumstances.auth.AuthAction
import uk.gov.hmrc.scachangeofcircumstances.models.PersonalDetailsResponse._
import uk.gov.hmrc.scachangeofcircumstances.services.PersonalDetailsService

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton()
class PersonalDetailsController @Inject()( authAction: AuthAction,
                                           personalDetailsService: PersonalDetailsService,
                                           cc: ControllerComponents )
                                         ( implicit val ec: ExecutionContext) extends BackendController(cc) {

  def getPersonalDetails(): Action[AnyContent] = authAction.async { request =>
      implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequestAndSession(request, request.session)
      request.nino match {
        case Some(nino) => personalDetailsService.getPersonalDetails(nino).map(details => Ok(Json.toJson(details)))
        case None => Future.successful(BadRequest)
      }
  }
}
