package utils.dataintegration

import javax.inject.Inject
import com.typesafe.config.ConfigFactory
import org.apache.jena.riot.Lang
import play.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.{JsArray, Json}
import play.api.libs.ws.WSClient
import play.api.mvc._

import scala.concurrent.Future

/**
  * Created by mtasnim on 10/22/2018
  */
class FCAUtil @Inject()(ws: WSClient) extends Controller {

  //TODO: Complete this method with actual Molecule example
  //TODO: Implement this function for 2+ Molecules
  def convertMoleculeToFCAMatrix(): Array[Array[Int]] = {
    Array(Array(1, 0, 1, 1, 1), Array(0, 1, 1, 1, 1))
  }

  def applyFCA(): Action[AnyContent] = Action.async { implicit request =>
    Logger.info("Calling FCA Service")

    val fcaServiceUri = ConfigFactory.load.getString("minte.fca.service.url")
    val data = convertMoleculeToFCAMatrix()

    Logger.info(s"FCA Service URL: $fcaServiceUri")
    ws.url(fcaServiceUri)
      .post(Json.obj("data" -> Json.toJson(data)))
      .map {
        response =>
          Ok(response.body.toString)
      }
  }

}