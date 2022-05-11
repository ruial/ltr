package com.briefbytes.ltr

import breeze.linalg.csvread
import breeze.numerics.sigmoid

import java.io.File
import java.nio.file.Paths

object Linear extends App {
  println("Testing linear models")

  val scale = csvread(new File("src/main/resources/scale.csv"))(0, ::).inner

  val regCoef = csvread(new File("src/main/resources/reg-coef.csv"))(0, ::).inner
  val regIntercept = csvread(new File("src/main/resources/reg-intercept.csv"))(0, 0)

  val clfCoef = csvread(new File("src/main/resources/clf-coef.csv"))(0, ::).inner
  val clfIntercept = csvread(new File("src/main/resources/clf-intercept.csv"))(0, 0)

  println("Reading test dataset")
  val test = Paths.get(System.getProperty("user.home"), "Downloads/MSLR-WEB10K/Fold1/test.txt")
  val limit = 3
  val start1 = System.nanoTime
  val matrix = MLUtils.svmread(test.toString, scale)
  val end1 = System.nanoTime
  println("Elapsed ms reading: "+ (end1-start1) / 1e6)

  val start2 = System.nanoTime
  val reg_result = matrix * regCoef + regIntercept
  val end2 = System.nanoTime
  println("Linear regression", reg_result(0 until limit ))
  println("Elapsed ms linear regression: "+ (end2-start2) / 1e6)

  val start3 = System.nanoTime
  val log_result = sigmoid(matrix * clfCoef + clfIntercept)
  val end3 = System.nanoTime
  println("Logistic regression", log_result(0 until limit))
  println("Elapsed ms logistic regression: "+ (end3-start3) / 1e6)
}
