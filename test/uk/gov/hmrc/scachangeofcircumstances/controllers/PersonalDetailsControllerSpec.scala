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

import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.http.Status
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.auth.core.AuthConnector
import uk.gov.hmrc.scachangeofcircumstances.models.{Name, PersonalDetails, PersonalDetailsResponse}
import uk.gov.hmrc.scachangeofcircumstances.services.PersonalDetailsService
import uk.gov.hmrc.scachangeofcircumstances.utils.TestAuthAction

import scala.concurrent.{ExecutionContext, Future}

class PersonalDetailsControllerSpec extends AnyWordSpec with Matchers {

  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

  private val fakeRequest = FakeRequest("GET", "/personal-details")

  private val mockService = mock[PersonalDetailsService]

  "GET /" should {

    "return 200" in {

      val nino = Some("J1234567D")

      val mockAuthAction = new TestAuthAction(nino, mock[AuthConnector], Helpers.stubControllerComponents())
      val controller = new PersonalDetailsController(mockAuthAction, mockService, Helpers.stubControllerComponents())

      val expected = PersonalDetailsResponse(
        details = PersonalDetails(
          name = Some(Name(
            firstForename = Some("John"),
            surname = Some("Johnson")
        )))
      )

      when(mockService.getPersonalDetails(ArgumentMatchers.eq(nino.get))(any())).thenReturn(Future.successful(expected))
      val result = controller.getPersonalDetails()(fakeRequest)
      status(result) shouldBe Status.OK
      contentAsJson(result) shouldBe Json.toJson(expected)
    }

    "return 400 when NiNo cannot be identified" in {

      val nino = None

      val mockAuthAction = new TestAuthAction(nino, mock[AuthConnector], Helpers.stubControllerComponents())
      val controller = new PersonalDetailsController(mockAuthAction, mockService, Helpers.stubControllerComponents())

      val result = controller.getPersonalDetails()(fakeRequest)
      status(result) shouldBe Status.BAD_REQUEST
//      contentAsJson()
    }
  }
}
