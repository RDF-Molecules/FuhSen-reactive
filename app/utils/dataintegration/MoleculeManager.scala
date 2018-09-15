package utils.dataintegration

import com.typesafe.config.ConfigFactory
import org.apache.jena.rdf.model.{Model, ModelFactory, Resource, ResourceFactory, Statement}
import org.apache.jena.util.ResourceUtils
import play.Logger

case class Molecule(uri:Resource, var status : Option[LinkStatus])
case class LinkStatus(link: Molecule, similarity: Double)

trait Similarity {
  def getSimilarity(m1: Molecule, m2: Molecule): Double
}

object MoleculeManager extends Similarity {

  def convertToMolecules(model: Model): Seq[Molecule] = {
    val subjects = model.listSubjects()
    var molecules: Seq[Molecule] = Seq()
    while (subjects.hasNext) {
      molecules = molecules :+ Molecule(subjects.nextResource(), None)
    }
    molecules
  }

  def convertToModel(molecules : Seq[Molecule]) : Model = {
    var model = ModelFactory.createDefaultModel()
    molecules.foreach{ molecule =>
      model = model.add(molecule.uri.listProperties())
    }
    model
  }

  override def getSimilarity(m1: Molecule, m2: Molecule): Double = {
    1/distance(m1.uri.getURI, m2.uri.getURI)
    ////random similarity method for testing
    //scala.util.Random.nextFloat()
  }

  /*
  * levenshtein distance function
  */
  def distance(s1: String, s2: String): Int = {
    val dist = Array.tabulate(s2.length + 1, s1.length + 1) { (j, i) => if (j == 0) i else if (i == 0) j else 0 }
    @inline
    def minimum(i: Int*): Int = i.min

    for {j <- dist.indices.tail
         i <- dist(0).indices.tail} dist(j)(i) =
      if (s2(j - 1) == s1(i - 1)) dist(j - 1)(i - 1)
      else minimum(dist(j - 1)(i) + 1, dist(j)(i - 1) + 1, dist(j - 1)(i - 1) + 1)
    dist(s2.length)(s1.length)
  }

  def applySimilarityMetric(molecules: Seq[Molecule], mergedResults: Seq[Molecule]) : Seq[Molecule] = {
    molecules.foreach{ molecule=>
      mergedResults.foreach{ resultMolecule =>
        //compute similarity
        var similarity = MoleculeManager.getSimilarity(molecule, resultMolecule)
        if(similarity > ConfigFactory.load.getDouble("merge.similarity.threshold")){
          if(molecule.status.isEmpty || (molecule.status.isDefined && molecule.status.get.similarity < similarity)){
            //store the molecule link with highest similarity value
            molecule.status = Some(LinkStatus(resultMolecule, similarity))
          }
        }
      }
    }

    molecules
  }

  def addLinkedMolecules(moleculesWithLinks : Seq[Molecule], currentMoleculeSet : Seq[Molecule]) : Seq[Molecule] = {
    if(currentMoleculeSet.isEmpty){
      Logger.info("This is the first wrapper")
      moleculesWithLinks
    }
    else{
      var datamap = Map(currentMoleculeSet map { m => m.uri -> m }: _*)
      moleculesWithLinks.foreach{ molecule =>
        if(molecule.status.isDefined){
          val mergedMolecule = merge(molecule, molecule.status.get.link, ConfigFactory.load.getString("merge.fusion.policy"))
          datamap = datamap - molecule.status.get.link.uri
          datamap = datamap + (mergedMolecule.uri -> mergedMolecule)
        }
        else{
          datamap + (molecule.uri -> molecule)
        }
      }
      val merged = datamap.values.toSeq
      merged
    }
  }

  def merge(m1: Molecule, m2: Molecule, fusionPolicy : String) : Molecule = {
    val uid = "http://vocab.lidakra.de/minte/merged_entity/" + java.util.UUID.randomUUID.toString
    val uris = Array(m1.uri.getURI, m2.uri.getURI)
    val mergedMolecule1 = ResourceUtils.renameResource(m1.uri, uid)
    val mergedMolecule2 = ResourceUtils.renameResource(m2.uri, uid)
    val it = mergedMolecule2.listProperties()
    while(it.hasNext){
      val prop = it.nextStatement()
      mergedMolecule1.addProperty(prop.getPredicate, prop.getObject)
    }
    mergedMolecule1.addProperty(ResourceFactory.createProperty("http://vocab.lidakra.de/minte/origin"), uris.toString)
    val merged = Molecule(mergedMolecule1, None)
    merged
  }

}
