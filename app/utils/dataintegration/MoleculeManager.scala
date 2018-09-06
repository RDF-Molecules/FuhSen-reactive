package utils.dataintegration

import com.typesafe.config.ConfigFactory
import org.apache.jena.rdf.model.{Model, ModelFactory, Resource, Statement}

case class Molecule(uri:Resource, properties : Seq[Statement], var status : Option[LinkStatus]){
  def this() = this("", Seq(), None)
  def addProperty(property: Statement) : Molecule = Molecule(uri, properties :+ property, status)
}
case class LinkStatus(link: Molecule, similarity: Double)

trait Similarity {
  def getSimilarity(m1: Molecule, m2: Molecule): Double
}

object MoleculeManager extends Similarity {

  def convertToMolecules(model: Model): Seq[Molecule] = {
    val subjects = model.listSubjects()
    var molecules: Seq[Molecule] = Seq()
    while (subjects.hasNext) {
      val subject = subjects.nextResource()
      var molecule = Molecule(subject, Seq(), None)
      val properties = subject.listProperties()
      while (properties.hasNext) {
        val property = properties.next()
        molecule.addProperty(property)
      }
      molecules = molecules :+ molecule
    }
    molecules
  }

  def convertToModel(molecules : Seq[Molecule]) : Model = {
    val model = ModelFactory.createDefaultModel()
    molecules.foreach{ molecule =>
      model.add(scala.collection.JavaConversions.seqAsJavaList(molecule.properties))
    }
    model
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
