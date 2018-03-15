import scala.io.Source

object FavouritePairs extends App{

  val path = args(0)
  val occurrences = args(1).toInt
  val printPerformanceReport = args(2).toBoolean
  val displayIndex = args(3).toBoolean
  val favourites = new FavouritePairs

  //read in the raw data form the spread sheet's cells into vectors of vectors of strings,
  // with each outer vector accounting for each user and each inner vector for the names of a user's favourite bands
  val data = favourites.readData(path)

  //convert raw data into into a flat vector of tuple, representing every possible combination of two most favourite bands
  val pairs = favourites.asPairs(data)

  //collect only those tuple which are occurring at least 'occurrences' times.
  val result = favourites.collect(pairs, Some(occurrences))

  //print those to the screen
  result.toList.sorted.zipWithIndex.foreach{ case (bucket, i) =>
    println(  s"${if(displayIndex)i+1 else ""} ${ bucket._1} = ${bucket._2}" ) // add to add a row count
  }

  // Report average performance over 50 iterations
  if (printPerformanceReport)
    favourites.averagePerformance(data, 50, Some(occurrences))

}

class FavouritePairs {

  /**
    *
    * @param path (to spreadsheet)
    * @return data (each user's favourite bands)
    */
  def readData(path:String):Vector[Vector[String]] = {

    val bufferedSource = Source.fromFile(path)
    //for more expediency, perform operations on each line in parallel, as they do not depend on each other
    val data = bufferedSource.getLines.toVector.par.map { line =>
      //also sort each vector. saves time when pairing them up later
      line.split(',').map(_.trim).toVector.sorted
    }.toVector

    bufferedSource.close()

    data
  }

  /**
    *
    * @param data (each user's favourite bands)
    * @return (sequence of every possible combination of two favourite bands)
    */
  def asPairs(data:Vector[Vector[String]]): Vector[(String, String)]= {

    data.flatMap { row =>
      row.zipWithIndex.flatMap { case (current, index) =>
        row.slice(index, row.length).collect { case item if item != current =>
          (current, item)
        }
      }
    }

  }

  /**
    *
    * @param pairs (sequence of every possible combination of two favourite bands)
    * @param occurrences (minimum occurrences of pairs required to be collected)
    * @return (those pairs, which pass the filter requirements)
    */
  def collect( pairs:Vector[(String, String)], occurrences:Option[Int]): Map[(String, String),Int] = {

    val buckets = pairs.foldLeft(Map[(String, String), Int]()) { (result, currentPair) =>
      result + (currentPair -> (if (result.contains(currentPair)) result(currentPair) + 1 else 1))
    }
    if(occurrences.isDefined) {
      buckets.filter { bucket => bucket._2 >= occurrences.get }
    }else{
      buckets
    }

  }

  /**
    *
    * @param data
    * @param iterations
    * @param occurrences
    */
  def averagePerformance(data:Vector[Vector[String]], iterations:Int, occurrences:Option[Int]): Unit  = {

    println(s"\nPreparing a performance report for $iterations runs. Please Wait...")
    val times = 0 to iterations map { _ =>
      val t0 = System.nanoTime()
      val pairs = asPairs(data)
      collect( pairs , occurrences ) // We don't want print output to affect with performance analysis.
      (System.nanoTime() - t0).toDouble / 1000000.0
    }
    println(s"The average execution time for $iterations  runs = " +  (times.sum / iterations.toDouble) + "ms\n\n"  )
  }

}