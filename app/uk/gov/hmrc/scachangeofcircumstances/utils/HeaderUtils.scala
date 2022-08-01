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

package uk.gov.hmrc.scachangeofcircumstances.utils

import play.api.mvc.RequestHeader
import uk.gov.hmrc.scachangeofcircumstances.exceptions.CorrelationIdException

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

trait HeaderUtils {

  def withCorrelationId[A](block: UUID => Future[A])(implicit requestHeader: RequestHeader, ec: ExecutionContext): Future[A] = {

    def validateOpt(correlationId: Option[String]): UUID = {
      correlationId match {
        case Some(id) => validate(id)
        case _ => throw new CorrelationIdException("CorrelationId is missing")
      }
    }

    def validate(correlationId: String): UUID = {
      if (UuidValidator.validate(correlationId)) {
        UUID.fromString(correlationId)
      } else {
        throw new CorrelationIdException("CorrelationId is malformed")
      }
    }

    Future(validateOpt(requestHeader.headers.get("CorrelationId"))).flatMap(x => block(x))
  }
}
