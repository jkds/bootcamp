package org.kavanaghj.services

/**
 * Created by jameskavanagh on 16/01/2015.
 */
trait StatUtils {

  def pnormdist(qn:Double):Double = {
    val b = Array(1.570796288, 0.03706987906, -0.8364353589e-3,
      -0.2250947176e-3, 0.6841218299e-5, 0.5824238515e-5,
      -0.104527497e-5, 0.8360937017e-7, -0.3231081277e-8,
      0.3657763036e-10, 0.6936233982e-12)
    if (qn <= 0.0 || qn >= 1.0) throw new IllegalArgumentException("out of bounds")
    if (qn == 0.5) return 0.0
    val x = if(qn > 0.5) 1.0 - qn else qn
    val w3 = -scala.math.log(4.0 * x * (1.0 - x))
    var w1 = (1 to 10).foldLeft(b(0)){ case (soFar:Double, i:Int) =>
      soFar + b(i) * scala.math.pow(w3, i.toDouble)
    }
    if (qn > 0.5) scala.math.sqrt(w1 * w3) else -scala.math.sqrt(w1 * w3)
  }

  def wilsonBound(positive:Double, total:Double, confidence:Double, bound:(Double,Double)=>Double = _ - _):Double = {
    if (total == 0) return 0.0
    val z:Double = pnormdist(1.0 - (1.0 - confidence) / 2.0)
    val phat:Double = 1.0 * positive / total
    bound(phat + z*z/(2.0*total), z * scala.math.sqrt((phat*(1.0-phat)+z*z/(4.0*total))/total))/(1.0+z*z/total)
  }

  def wilsonLowerBound(positive:Double, total:Double, confidence:Double):Double
  = wilsonBound(positive, total, confidence, {_ - _})

  def wilsonUpperBound(positive:Double, total:Double, confidence:Double):Double
  = wilsonBound(positive, total, confidence, _ + _)



}
