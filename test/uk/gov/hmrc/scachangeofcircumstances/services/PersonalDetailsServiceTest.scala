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

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatestplus.mockito.MockitoSugar.mock
import uk.gov.hmrc.scachangeofcircumstances.connectors.IfConnector
import uk.gov.hmrc.scachangeofcircumstances.models.PersonalDetails
import uk.gov.hmrc.scachangeofcircumstances.models.integrationframework.{IfAddressList, IfDesignatoryDetails, IfDetails, IfNameList}
import uk.gov.hmrc.scachangeofcircumstances.utils.{BaseUnitTests, WireMockHelper}

import scala.concurrent.Future


class PersonalDetailsServiceTest extends BaseUnitTests with WireMockHelper with ScalaFutures {

  import scala.concurrent.ExecutionContext.Implicits.global

  "getPersonalDetails" - {

    val mockIfConnector = mock[IfConnector]

    "successful response should return personal details" in {

      val testObj = IfDesignatoryDetails(
        IfDetails(None),
        IfNameList(Seq()),
        IfAddressList(Seq())
      )

      when(mockIfConnector.getDesignatoryDetails(any())(any()))
        .thenReturn(Future.successful(testObj))

      val service = new PersonalDetailsService(mockIfConnector)

      val expected = PersonalDetails(None, None, None, None)

      service.getPersonalDetails("123123132").futureValue shouldBe expected

    }
  }
}
