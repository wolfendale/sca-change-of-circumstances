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

import play.api.mvc.RequestHeader
import uk.gov.hmrc.http.HttpReadsInstances._
import uk.gov.hmrc.http.{HeaderCarrier, HeaderNames, HttpClient, InternalServerException, JsValidationException, NotFoundException, Upstream4xxResponse, Upstream5xxResponse, UpstreamErrorResponse}
import uk.gov.hmrc.scachangeofcircumstances.config.AppConfig
import uk.gov.hmrc.scachangeofcircumstances.exceptions.CorrelationIdException
import uk.gov.hmrc.scachangeofcircumstances.logging.Logging
import uk.gov.hmrc.scachangeofcircumstances.models.integrationframework.{IfContactDetails, IfDesignatoryDetails}
import uk.gov.hmrc.scachangeofcircumstances.utils.HeaderUtils

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class IfConnector @Inject()(http: HttpClient, appConfig: AppConfig)(implicit executionContext: ExecutionContext) extends Logging with HeaderUtils {

  private val designatoryDetailsFields: String =
    "details(marriageStatusType),nameList(name(nameSequenceNumber,nameType,titleType," +
      "requestedName,nameStartDate,nameEndDate,firstForename,secondForename,surname))," +
      "addressList(address(addressSequenceNumber,countryCode,addressType,addressStartDate," +
      "addressEndDate,addressLine1,addressLine2,addressLine3,addressLine4,addressLine5," +
      "addressPostcode))"

  private val contactDetailsFields: String = "contactDetails(code,type,detail)"

  // TODO: Check Authorisation, OriginatorID

  private def setHeaders(correlationId: UUID) = Seq(
    HeaderNames.authorisation -> s"Bearer ${appConfig.integrationFrameworkAuthToken}",
    "Environment" -> appConfig.integrationFrameworkEnvironment,
    "CorrelationId" -> correlationId.toString
  )

  def getDesignatoryDetails(nino: String)(implicit hc: HeaderCarrier, request: RequestHeader): Future[IfDesignatoryDetails] = {
    withCorrelationId { correlationId =>
      val headers = setHeaders(correlationId).+:(("OriginatorId" -> "DA2_BS_UNATTENDED"))
      http.GET[IfDesignatoryDetails](
        url = s"${appConfig.ifBaseUrl}/individuals/details/NINO/$nino?fields=$designatoryDetailsFields",
        headers = headers
      )
    } recoverWith errorHandling[IfDesignatoryDetails]
  }

  def getContactDetails(nino: String)(implicit hc: HeaderCarrier, request: RequestHeader): Future[IfContactDetails] = {
    withCorrelationId { correlationId =>
      http.GET[IfContactDetails](
        url = s"${appConfig.ifBaseUrl}/individuals/details/contact/nino/${nino}?fields=$contactDetailsFields",
        headers = setHeaders(correlationId)
      )
    } recoverWith errorHandling[IfContactDetails]
  }

  private def errorHandling[T]: PartialFunction[Throwable, Future[T]] = {
    case validationError: JsValidationException =>
      logger.warn(validationError.getMessage())
      Future.failed(validationError)
    case error: CorrelationIdException =>
      logger.warn(error.getMessage)
      Future.failed(error)
    case Upstream5xxResponse(msg, code, _, _) =>
      logger.warn(s"IF Upstream5xxResponse encountered: $code, $msg")
      Future.failed(UpstreamErrorResponse(msg, code))
    case Upstream4xxResponse(msg, 404, _, _) if msg.contains("IDENTIFIER_NOT_FOUND") || msg.contains("PERSON_NOT_FOUND") =>
      Future.failed(new NotFoundException(msg))
    case Upstream4xxResponse(msg, code, _, _) =>
      logger.warn(s"IF Upstream4xxResponse encountered: $code, $msg")
      Future.failed(UpstreamErrorResponse(msg, code))
    case e: Exception =>
      logger.warn(s"IF Exception encountered: ${e.getMessage}")
      Future.failed(new InternalServerException(e.getMessage))
  }
}
