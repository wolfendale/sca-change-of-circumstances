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

import uk.gov.hmrc.http.HttpReadsInstances._
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, InternalServerException, JsValidationException, NotFoundException, Upstream4xxResponse, Upstream5xxResponse}
import uk.gov.hmrc.scachangeofcircumstances.config.AppConfig
import uk.gov.hmrc.scachangeofcircumstances.logging.Logging
import uk.gov.hmrc.scachangeofcircumstances.models.integrationframework.{IfContactDetails, IfDesignatoryDetails}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class IfConnector @Inject()(http: HttpClient, appConfig: AppConfig)(implicit executionContext: ExecutionContext) extends Logging {

  private val designatoryDetailsFields: String =
    "details(marriageStatusType),nameList(name(nameSequenceNumber,nameType,titleType," +
      "requestedName,nameStartDate,nameEndDate,firstForename,secondForename,surname))," +
      "addressList(address(addressSequenceNumber,countryCode,addressType,addressStartDate," +
      "addressEndDate,addressLine1,addressLine2,addressLine3,addressLine4,addressLine5," +
      "addressPostcode))"

  private val contactDetailsFields: String = "contactDetails(code,type,detail)"

  def getDesignatoryDetails(nino: String)(implicit hc: HeaderCarrier): Future[IfDesignatoryDetails] = {
    http.GET[IfDesignatoryDetails](s"${appConfig.ifBaseUrl}/individuals/details/nino/$nino?fields=$designatoryDetailsFields")
      .recoverWith(errorHandling[IfDesignatoryDetails])
  }

  def getContactDetails(nino: String)(implicit hc: HeaderCarrier): Future[IfContactDetails] = {
    http.GET[IfContactDetails](s"${appConfig.ifBaseUrl}/individuals/details/contact/nino/${nino}?fields=$contactDetailsFields")
      .recoverWith(errorHandling[IfContactDetails])
  }

  private def errorHandling[T]: PartialFunction[Throwable, Future[T]] = {
    case validationError: JsValidationException =>
      logger.warn(s"Integration Framework JsValidationException encountered: $validationError")
      Future.failed(new InternalServerException("Something went wrong."))
    case Upstream5xxResponse(msg, code, _, _) =>
      logger.warn(s"Integration Framework Upstream5xxResponse encountered: $code, $msg")
      Future.failed(new InternalServerException("Something went wrong."))
    case Upstream4xxResponse(msg, 404, _, _) if msg.contains("IDENTIFIER_NOT_FOUND") || msg.contains("PERSON_NOT_FOUND") =>
      logger.warn(s"Integration Framework returned NiNo not found")
      Future.failed(new NotFoundException(msg))
    case Upstream4xxResponse(msg, code, _, _) =>
      logger.warn(s"Integration Framework Upstream4xxResponse encountered: $code, $msg")
      Future.failed(new InternalServerException("Something went wrong."))
    case e: Exception =>
      logger.warn(s"Integration Framework Exception encountered: ${e.getMessage}")
      Future.failed(new InternalServerException("Something went wrong."))
  }
}
