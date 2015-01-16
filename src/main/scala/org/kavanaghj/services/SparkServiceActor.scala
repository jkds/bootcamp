package org.kavanaghj.services

import akka.actor.Actor
import org.apache.spark.{Logging, SparkContext}
import org.apache.spark.sql.cassandra.CassandraSQLContext
import spray.routing.HttpService

case class Aggregate(id : String, min : Float, max : Float, avg : Double, total : Double, count : Long)
class ProductMinMax(id : String, negative : Int, positive : Int)

class SparkServiceActor extends Actor with SparkService {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(route)
}

// this trait defines our service behavior independently from the service actor
trait SparkService extends HttpService with StatUtils with Logging {

  val confidenceLevel = 1.96
  val sc = new SparkContext("spark://172.31.34.192:7077", "SparkSync")
  val sqlCtx = new CassandraSQLContext(sc)
  sqlCtx.setKeyspace("capstone")

  val route = path("numOfReviews") {
      get {
        val reviews = sqlCtx.sql("select count(*) from reviews").map(_.getLong(0)).collect()(0).toString
        complete(reviews)
      }
  } ~ path("mostPopularProduct") {
    get {
      sqlCtx.logInfo("Received request to retrieve the most popular product")
      complete(getProductRatings(sqlCtx).toMap.maxBy { pr =>
        val (productId, (neg,pos)) = pr
        wilsonUpperBound(pos,(pos+neg),confidenceLevel)
      })
    }

  } ~ path("leastPopularProduct") {
    get {
      complete(getProductRatings(sqlCtx).toMap.minBy { pr =>
        val (productId, (neg,pos)) = pr
        wilsonLowerBound(pos,(neg+pos),confidenceLevel)
      })
    }

  }

  private def getAggregates(ctx : CassandraSQLContext) : Array[(String,Double)] = {
    ctx.sql("select product_id, avg(score), count(product_id) from reviews group by product_id")
      .map {
      x => (x.getString(0), x.getDouble(1))
    }.collect()
  }

  private def getProductRatings(ctx : CassandraSQLContext) : scala.collection.mutable.Map[String,(Int,Int)] = {
    val productScores = ctx.sql("select product_id, score from reviews order by product_id asc").map { x => (x.getString(0),x.getFloat(1)) }.collect()
    ctx.logInfo(">>>> Retrieved list of products/scores. Processing into a map")
    val map = scala.collection.mutable.Map.empty[String,(Int,Int)]
    productScores.foreach { x =>
      val (productId,score) = x
      val (neg : Int,pos : Int) = map.getOrElse(productId,(0,0))
      if(score >= 2.5) map.put(productId,(neg,pos+1)) else map.put(productId,(neg+1,pos))
    }
    ctx.logInfo(">>>> Finished mapping of product and negative/positive scores")
    map
  }


}
