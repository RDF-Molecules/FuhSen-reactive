package controllers.de.fuhsen.wrappers

class WrapperManager{
  val wrappers = Seq(
    new GooglePlusWrapper(),
    new TwitterWrapper(),
    new FacebookWrapper(),
    //Knowledge base
    new GoogleKnowledgeGraphWrapper(),
    //eCommerce
    new EBayWrapper(),
    //Darknet
    new Tor2WebWrapper(),
    //Linked leaks
    new LinkedLeaksWrapper(),
    //OCCRP
    new OCCRPWrapper(),
    //Xing
    new XingWrapper(),
    //Elastic Searchd
    new ElasticSearchWrapper(),
    //pipl
    new PiplWrapper(),
    //vk
    new VkWrapper(),
    //darknetmarkets
    new DarknetMarketsWrapper()
  )
  val wrapperMap: Map[String, RestApiWrapperTrait] = wrappers.map { wrapper =>
    (wrapper.sourceLocalName, wrapper)
  }.toMap
  val sortedWrapperIds = wrapperMap.keys.toSeq.sortWith(_ < _)
}
