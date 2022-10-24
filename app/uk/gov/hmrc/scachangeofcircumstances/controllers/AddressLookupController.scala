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

import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, ControllerComponents, Request}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.scachangeofcircumstances.auth.AuthAction
import uk.gov.hmrc.scachangeofcircumstances.models.addresslookup.AddressLookupRequest
import uk.gov.hmrc.scachangeofcircumstances.services.AddressLookupService

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton()
class AddressLookupController @Inject() (
  authAction: AuthAction,
  addressLookupService: AddressLookupService,
  cc: ControllerComponents
)(implicit val ec: ExecutionContext)
    extends BackendController(cc) {

  def lookup(): Action[JsValue] = cc.actionBuilder(cc.parsers.json) andThen authAction async {
    implicit request: Request[JsValue] =>
      withJsonBody[AddressLookupRequest] { lookupRequest =>
        for {
          postcodeLookup <- addressLookupService.find(lookupRequest.postcode, lookupRequest.filter, true)
        } yield Ok(Json.toJson(postcodeLookup))
      }
  }
}
