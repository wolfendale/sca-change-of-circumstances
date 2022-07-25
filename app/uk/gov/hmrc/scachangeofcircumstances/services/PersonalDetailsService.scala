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

package uk.gov.hmrc.scachangeofcircumstances.services

import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.scachangeofcircumstances.connectors.IfConnector
import uk.gov.hmrc.scachangeofcircumstances.logging.Logging
import uk.gov.hmrc.scachangeofcircumstances.models.integrationframework.{IfAddress, IfName}
import uk.gov.hmrc.scachangeofcircumstances.models.{Address, MaritalStatus, Name, PersonalDetails}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class PersonalDetailsService @Inject()(connector: IfConnector)(implicit ec: ExecutionContext) extends Logging {

  def getPersonalDetails(nino: String)(implicit hc: HeaderCarrier): Future[PersonalDetails] = {

    connector.getDesignatoryDetails(nino).map { details =>

        // TODO: Check assumption of real name
        // TODO: Check assumption that address / nameIncrementNumber can be used to find most recent

        val realKnownAsNames: (Seq[IfName], Seq[IfName]) = details.nameList.name.partition(_.nameType.contains(1))
        val residentialCorrespondenceAddresses: (Seq[IfAddress], Seq[IfAddress]) = details.addressList.address.partition(_.addressType.contains(1))

        val name: Option[IfName] = if(realKnownAsNames._1.nonEmpty)
          realKnownAsNames._1.sortBy(_.nameSequenceNumber).takeRight(1).headOption
        else
          realKnownAsNames._2.sortBy(_.nameSequenceNumber).takeRight(1).headOption

        PersonalDetails(
          name.map(Name.apply),
          details.details.marriageStatusType.map(MaritalStatus.apply),
          residentialCorrespondenceAddresses._1.sortBy(_.addressSequenceNumber).takeRight(1).headOption.map(Address.apply),
          residentialCorrespondenceAddresses._2.sortBy(_.addressSequenceNumber).takeRight(1).headOption.map(Address.apply)
        )
    }
  }
}
