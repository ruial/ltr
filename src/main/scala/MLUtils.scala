package com.briefbytes.ltr

import breeze.linalg.{CSCMatrix, DenseVector}

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
        val tuples = parts.flatMap(x => {
          val sepIndex = x.indexOf(":")
          val colIndex = x.substring(0, sepIndex).toInt - 1
          val value = x.substring(sepIndex + 1).toDouble
          if (value != 0.0) Some(colIndex, value) else None
        })
        val lastIndex = tuples.last._1
        if (lastIndex > max) {
          max = lastIndex
        }
        tuples
      }).toIndexedSeq
    source.close()

    val builder = new CSCMatrix.Builder[Double](rows = lines.length, cols = max + 1)
    lines.zipWithIndex.foreach { case (line, rowIndex) =>
      line.foreach { case (colIndex, value) =>
        val scaledValue = if (scale != null) value / scale(colIndex) else value
        builder.add(rowIndex, colIndex, scaledValue)
      }
    }
    builder.result()
  }

}
