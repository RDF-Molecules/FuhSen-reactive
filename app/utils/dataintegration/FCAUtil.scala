package utils.dataintegration

import javax.inject.Inject
import com.typesafe.config.ConfigFactory
import org.apache.jena.rdf.model.{ModelFactory, Property, RDFNode, ResourceFactory}
import play.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.{JsObject, Json}
import play.api.libs.ws.WSClient
import play.api.mvc._

/**
  * Created by mtasnim on 10/22/2018
  */
class FCAUtil @Inject()(ws: WSClient) extends Controller {

  def bool2int(b:Boolean): Int = if (b) 1 else 0
  case class Predicate(predicate: Property, value: RDFNode)

  def convertMoleculeToFCAMatrix(molecules: Seq[Molecule]): Array[Array[Int]] = {
    val properties = getUniqueProperties(molecules)
    val result = molecules.map( m =>{
      var row = Array[Int]()
      properties.foreach { prop =>
        row = row :+ bool2int(m.uri.hasProperty(prop.predicate, prop.value))
      }
      row
    })
    result.toArray
  }

  def getUniqueProperties(molecules : Seq[Molecule]): Seq[Predicate] = {
    var stmt = Set[Predicate]()
    molecules.foreach{ m =>
      val iterator = m.uri.listProperties()
      while(iterator.hasNext){
        val s = iterator.nextStatement()
        stmt += Predicate(s.getPredicate, s.getObject)
      }
    }
    stmt.toSeq
  }

  def applyFCA(): Action[AnyContent] = Action.async { implicit request =>
    Logger.info("Calling FCA Service")
    val fcaServiceUri = ConfigFactory.load.getString("minte.fca.service.url")
    val data = Json.toJson(convertMoleculeToFCAMatrix(createDummyMolecule()))
    Logger.info(s"sending POST request to $fcaServiceUri with data $data")
    ws.url(fcaServiceUri)
      .post(Json.obj("data" -> data))
      .map {
        response =>
          val res = Json.parse(response.body).as[List[JsObject]]
          Ok(parseFCAResults(res))
      }
  }

  def parseFCAResults(result : List[JsObject]) : String = {
    var message = ""
    val molecules = createDummyMolecule()
    val properties = getUniqueProperties(molecules)
    result.foreach{ obj =>
      val mArray = (obj \ "molecules").as[Array[Int]]
      val pArray = (obj \ "properties").as[Array[Int]]
      mArray.foreach( i => message += s"Molecule(${molecules(i).uri.getURI}) ,")
      pArray.foreach( i => message += s"Has same property( ${properties(i).predicate.getURI} ), ")
      message += "\n"
    }
    message
  }

  def createDummyMolecule() : Seq[Molecule] = {
    val model = ModelFactory.createDefaultModel()
    val res1 = model.createResource("abox:baspirin")
    res1.addProperty(ResourceFactory.createProperty("tbox:prod"), "abox:binc")
    res1.addProperty(ResourceFactory.createProperty("owl:sameAs"), "X")
    res1.addProperty(ResourceFactory.createProperty("tbox:chem"), "abox:aspirin")
    res1.addProperty(ResourceFactory.createProperty("rdf:type"), "Drug")


    val res2 = model.createResource("abox:caspirin")
    res2.addProperty(ResourceFactory.createProperty("tbox:prod"), "abox:cinc")
    res2.addProperty(ResourceFactory.createProperty("owl:sameAs"), "X")
    res2.addProperty(ResourceFactory.createProperty("tbox:chem"), "abox:aspirin")
    res2.addProperty(ResourceFactory.createProperty("rdf:type"), "Drug")

    Seq(Molecule(res1, None), Molecule(res2, None))
  }

}