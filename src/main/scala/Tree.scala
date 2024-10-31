package com.briefbytes.ltr

import ml.dmlc.xgboost4j.scala.{Booster, DMatrix, XGBoost}

import java.nio.file.Paths

object Tree extends App {
  println("Testing XGBoost model")

  val test = Paths.get(System.getProperty("user.home"), "Downloads/MSLR-WEB10K/Fold1/test.txt")
  val dmatrix = new DMatrix(test.toString + "?format=libsvm&indexing_mode=1")
  val model = XGBoost.loadModel("src/main/resources/xgboost.json")

  val start1 = System.nanoTime
  val predictions = model.predict(dmatrix)
  val end1 = System.nanoTime
  println("Elapsed ms XGBoost: " + (end1-start1) / 1e6)

  val limit = 10
  predictions.slice(0, limit).foreach(p => println(p.mkString("Array(", ", ", ")")))

  // example predicting single vectors, slower than batch
  def singlePrediction(model: Booster, arr: Array[Float]) = {
    val vec = new DMatrix(arr, 1, arr.length, Float.NaN)
    val predicts = model.predict(vec)
    predicts(0)(0)
  }

  val matrix = MLUtils.svmread(test.toString, null, limit)
  // println(matrix.toDense)

  val individualPredictions = (0 until matrix.rows).map{ rowIndex =>
    val row = matrix(rowIndex, 0 until matrix.cols)
    val rowArr = row.inner.toArray
    singlePrediction(model, rowArr.map(_.toFloat))
  }

  println(individualPredictions)
}
