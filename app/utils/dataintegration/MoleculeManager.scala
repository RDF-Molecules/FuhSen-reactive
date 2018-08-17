package utils.dataintegration

import com.typesafe.config.ConfigFactory
import org.apache.jena.rdf.model.{Model, ModelFactory}

case class Molecule(uri:String, properties : Seq[Property], var status : Option[LinkStatus])
case class Property(uri: String, value: String)
case class LinkStatus(link: Molecule, similarity: Double)

trait Similarity {
  def getSimilarity(m1: Molecule, m2: Molecule): Double
}

object MoleculeManager extends Similarity {

  def convertToMolecules(model: Model) : Seq[Molecule] = {
    val molecules : Seq[Molecule] = Seq(Molecule("uri1", Seq(Property("prop1", "val1"), Property("prop2", "val2")), None))



    molecules
  }

  def convertToModel(molecules : Seq[Molecule]) : Model = {
    ModelFactory.createDefaultModel()
  }

  override def getSimilarity(m1: Molecule, m2: Molecule): Double = {

    0.11
  }

  def applySimilarityMetric(molecules: Seq[Molecule], mergedResults: Seq[Molecule]) : Seq[Molecule] = {
    molecules.foreach{ molecule=>
      mergedResults.foreach{ resultMolecule =>
        //compute similarity
        var similarity = MoleculeManager.getSimilarity(molecule, resultMolecule)
        if(similarity > ConfigFactory.load.getDouble("merge.similarity.threshold")){
          if(molecule.status.isDefined && molecule.status.get.similarity < similarity){
            //store the molecule link with highest similarity value
            molecule.status = Some(LinkStatus(resultMolecule, similarity))
          }
        }
      }
    }

    molecules
  }

  def addLinkedMolecules(moleculesWithLinks : Seq[Molecule], currentMoleculeSet : Seq[Molecule]) : Seq[Molecule] = {

    currentMoleculeSet
  }

}
