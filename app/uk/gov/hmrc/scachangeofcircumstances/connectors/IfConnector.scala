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

import uk.gov.hmrc.http.{GatewayTimeoutException, HeaderCarrier, HttpClient}
import uk.gov.hmrc.scachangeofcircumstances.config.AppConfig
import uk.gov.hmrc.scachangeofcircumstances.connectors.IfDesignatoryDetailsHttpParser._
import uk.gov.hmrc.scachangeofcircumstances.logging.Logging
import uk.gov.hmrc.scachangeofcircumstances.models.{GatewayTimeout, IfErrorResponse}
import uk.gov.hmrc.scachangeofcircumstances.models.integrationframework.IfContactDetails

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}


class IfConnector @Inject()(http: HttpClient, appConfig: AppConfig)(implicit executionContext: ExecutionContext) extends Logging {

  val host = appConfig.integrationFrameworkHost
  val port = appConfig.integrationFrameworkPort

  val fields: String =
    "details(marriageStatusType),nameList(name(nameSequenceNumber,nameType,titleType," +
      "requestedName,nameStartDate,nameEndDate,firstForename,secondForename,surname))," +
      "addressList(address(addressSequenceNumber,countryCode,addressType,addressStartDate," +
      "addressEndDate,addressLine1,addressLine2,addressLine3,addressLine4,addressLine5," +
      "addressPostcode))"

  type IfContactDetailsResponse = Either[IfErrorResponse, IfContactDetails]

  def getDesignatoryDetails(nino: String)(implicit hc: HeaderCarrier): Future[IfDesignatoryDetailsResponse] = {

    http.GET[IfDesignatoryDetailsResponse](s"${appConfig.ifBaseUrl}/individuals/details/nino/$nino?fields=$fields").recover {
      case e: GatewayTimeoutException => {
        logger.error(s"Request timeout from IF: $e")
        Left(Seq(GatewayTimeout))
      }
    }
  }

  def getContactDetails(): Future[IfContactDetailsResponse] = ???

}
