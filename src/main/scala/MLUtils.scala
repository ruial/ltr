package com.briefbytes.ltr

import breeze.linalg.{CSCMatrix, DenseVector, SparseVector}

import scala.io.Source

object MLUtils {

  def svmread(file: String, scale: DenseVector[Double] = null, maxLines: Integer = Integer.MAX_VALUE) = {
    var max = 0
    val source = Source.fromFile(file)
    val lines = source.getLines().slice(0, maxLines)
      .filter(l => l.nonEmpty && !l.startsWith("#"))
      .map(line => {
        val commentPos = line.indexOf("#")
        val processedLine = if (commentPos == -1) line else line.substring(0, commentPos)
        val dropColumns = if (line.contains("qid:")) 2 else 1
        val parts = processedLine.split(" ").drop(dropColumns)
        val tuples = parts.map(x => {
          val part = x.split(":")
          (part(0).toInt - 1, part(1).toDouble)
        })
        val lastIndex = tuples.last._1
        if (lastIndex > max) {
          max = lastIndex
        }
        tuples
      }).toIndexedSeq
    source.close()

    val vectors = lines.map(line => {
      val arr = DenseVector.fill(max + 1) {
        0.0
      }
      line.foreach(tuple => {
        val idx = tuple._1
        arr(idx) = tuple._2
      })
      if (scale == null) {
        SparseVector(arr.toArray)
      } else {
        SparseVector((arr / scale).toArray)
      }
    })

    // based on breeze.linalg.csvread, this is slow, takes 30s out of the method's 37s
    if (vectors.isEmpty) {
      CSCMatrix.zeros[Double](0, 0)
    } else {
      CSCMatrix.tabulate(vectors.length, max + 1)((i, j) => {
        val s = vectors(i)
        if (j > s.length - 1) {
          0
        } else {
          s.valueAt(j)
        }
      })
    }

  }

}
