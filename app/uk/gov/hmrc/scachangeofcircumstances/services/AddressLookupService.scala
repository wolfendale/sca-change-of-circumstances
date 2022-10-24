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
import uk.gov.hmrc.scachangeofcircumstances.connectors.AddressLookupConnector
import uk.gov.hmrc.scachangeofcircumstances.models.addresslookup.ProposedAddress

import javax.inject.Inject
import scala.concurrent.Future

class AddressLookupService @Inject() (connector: AddressLookupConnector) {

  def find(postcode: String, filter: Option[String] = None, isukMode: Boolean)(implicit
                                                                               hc: HeaderCarrier
  ): Future[Seq[ProposedAddress]] = connector.find(postcode, filter, isukMode)

}