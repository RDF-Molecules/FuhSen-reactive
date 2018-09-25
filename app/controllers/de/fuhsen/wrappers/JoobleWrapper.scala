/*
 * Copyright (C) 2018 SDA Uni-Bonn & Fraunhofer IAIS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package controllers.de.fuhsen.wrappers

import com.typesafe.config.ConfigFactory
import controllers.de.fuhsen.wrappers.dataintegration.{SilkTransformableTrait, SilkTransformationTask}

/**
  * Wrapper for the Jooble REST API. //
  */
class JoobleWrapper extends RestApiWrapperTrait with SilkTransformableTrait {

  /** Query parameters that should be added to the request. */
  override def queryParams: Map[String, String] = Map()

  /** Headers that should be added to the request. */
  override def headersParams: Map[String, String] = Map( "Content-Type" -> "application/x-www-form-urlencoded", "Accept" -> "application/json", "Content-Length" -> "17")

  /** Returns for a given query string the representation as query parameter for the specific API. */
  override def searchQueryAsParam(queryString: String): Map[String, String] = {
    Map("q" -> queryString)
  }

  /** The REST endpoint URL */
  override def apiUrl: String = "https://de.jooble.org/api/023840c6-11cb-47ce-8c93-be016475a3fe"

  /**
    * Returns the globally unique URI String of the source that is wrapped. This is used to track provenance.
    */
  override def sourceLocalName: String = "jooble"

  /** SILK Transformation Trait **/
  override def silkTransformationRequestTasks = Seq(
    SilkTransformationTask(
      transformationTaskId = "JoobleTransformationTask",
      createSilkTransformationRequestBody(
        basePath = "jobs",
        uriPattern = "http://vocab.edsa.de/search/jobpost/{id}"
      )
    )
  )

  /** The type of the transformation input. */
  override def datasetPluginType: DatasetPluginType = DatasetPluginType.JsonDatasetPlugin

  /** The project id of the Silk project */
  override def projectId: String = ConfigFactory.load.getString("silk.socialApiProject.id")

  override def requestType: String = "POST"

  //Todo: Update with keyword comming from the request.
  //override def postBody: String = "{'keywords':'java'}"
  //override def max_results: Int = 20
  //override def address: String = ""

}
