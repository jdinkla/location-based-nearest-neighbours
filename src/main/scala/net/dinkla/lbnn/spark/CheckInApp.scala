package net.dinkla.lbnn.spark

import net.dinkla.lbnn.geom.{Rectangle, Point2}
import net.dinkla.lbnn.kd.KdTree
import net.dinkla.lbnn.utils.{CSV, Parameters, TextDate, Utilities}
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

// TODO better name than xs
object xs extends Serializable {

  def createTimePairs(dt: TextDate): List[(String, Int)] = {
    val ds = List(dt.getDate, dt.getYYYYMM, dt.getYear, dt.getHour)
    ds map { x => (x, 1) }
  }

}

/**
 * Created by Dinkla on 23.06.2015.
 */
class CheckInApp(val props: Parameters) extends App {

  type Command = net.dinkla.lbnn.spark.Command

  import CheckInApp.log

  val workDir = props.get("workDir")
  val resultsDir = props.get("resultsDir")
  val testRun = props.getOrDefault("testRun", "false")
  def testData = if (testRun == "true") fileCheckinsSample else fileCheckins

  val url = props.get("url")
  val fileCheckins = props.get("fileCheckins")
  val fileCheckinsSample = props.get("fileCheckinsSample")
  val fileSortedByUser = props.get("fileSortedByUser")
  val fileSortedByTime = props.get("fileSortedByTime")
  val tmpOutputDir = props.get("tmpOutputDir")

  var sc: SparkContext = null
  var utils: Utilities = null

  /**
   * creates a 'sample' of ca num lines. Not exactly num lines
   *
   * @param src
   * @param dest
   * @param num
   */
  def createSample(src: String, dest: String, num: Int): Unit = {
    val input: RDD[String] = sc.textFile(src)
    val fraction = 1.0 * num / input.count()
    val top = input.sample(true, fraction)
    val intermediate = s"${src}.sample.tmp"
    utils.deldir(intermediate)
    top.saveAsTextFile(intermediate, classOf[org.apache.hadoop.io.compress.GzipCodec])
    utils.merge(intermediate, dest)   // TODO parts are gzipped, does merge work for partioned files?
  }

  def createSample2(src: String, dest: String, num: Int): Unit = {
    val input: RDD[String] = sc.textFile(src)
    val fraction = 1.0 * num / input.count()
    val top = input.sample(true, fraction)
    val intermediate = s"${src}.sample.tmp"
    utils.deldir(intermediate)
    top.saveAsTextFile(intermediate, classOf[org.apache.hadoop.io.compress.GzipCodec])
    //utils.merge(intermediate, dest)   // TODO parts are gzipped, does merge work for partioned files?
  }

  /**
   *
   * @param src
   * @param dest
   * @return
   */
  def sortByUser(src: String, dest: String) = {
    val input: RDD[String] = sc.textFile(src)
    val tokenized = input.map(CheckIn.split)
    val parsed = tokenized.map(CheckIn.parse)
    val sorted = parsed.sortBy(c => c, true)
    sorted.saveAsObjectFile(dest)
  }

  def sortByTime(src: String, dest: String) = {
    val input: RDD[String] = sc.textFile(src)
    val tokenized = input.map(CheckIn.split)
    val parsed = tokenized.map(CheckIn.parse)
    val sorted = parsed.sortBy(c => c.date, true)
    sorted.saveAsObjectFile(dest)
  }

  def sorted(src: String, destUser: String, destTime: String) = {
    log.info(s"### SORTED $src $destUser $destTime")
    val input: RDD[String] = sc.textFile(src)
    val tokenized = input.map(CheckIn.split)
    val parsed = tokenized.map(CheckIn.parse)
    parsed.persist()
    // sort by user
    val sortedUser = parsed.sortBy(c => c, true)
    sortedUser.saveAsObjectFile(destUser)
    // sort by time
    val sortedTime = parsed.sortBy(c => c.date, true)
    sortedTime.saveAsObjectFile(destTime)
  }

  val fileStatsGlobal = resultsDir + "/" + props.getOrDefault("fileStatsGlobal", "stats_global.csv")

  def statsGlobal(src: String): Unit = {
    val input: RDD[CheckIn] = sc.objectFile(src)
    val numLines = input.count

    val allUsers = input.map (c => c.id)
    val distinctUsers = allUsers.distinct()
    val numDistinctUsers = distinctUsers.count

    val allDates = input.map (c => c.date)
    allDates.persist()
    val minDate = allDates.min()
    val maxDate = allDates.max()

    // report
    val csv = new CSV("key", "value")
    csv.add("number of lines", numLines)
    csv.add("number of users", numDistinctUsers)
    csv.add("minimal datetime", minDate)
    csv.add("minimal datetime", maxDate)
    utils.write(fileStatsGlobal, csv.toString())
  }

//  val srcSumsYMDpart = workDir + "/srcSumsYMDpart.txt"
//  val srcSumsYMpart = workDir + "/srcSumsYMpart.txt"
//  val srcSumsYpart = workDir + "/srcSumsYpart.txt"
//  val srcSumsHHpart = workDir + "/srcSumsHHpart.txt"
  
  val fileSumsYMD = resultsDir + "/" + props.getOrDefault("fileSumsYMD", "sums_yyyymmdd.csv")
  val fileSumsYM = resultsDir + "/" + props.getOrDefault("fileSumsYM", "sums_yyyymm.csv")
  val fileSumsY = resultsDir + "/" + props.getOrDefault("fileSumsY", "sums_yyyy.csv")
  val fileSumsHH = resultsDir + "/" + props.getOrDefault("fileSumsHH", "sums_hh.csv")

  def statsTime(src: String): Unit = {
    utils.mkdir(resultsDir)

    val input: RDD[CheckIn] = sc.objectFile(src)
    val pairs: RDD[(String, Int)] = input.flatMap { x => xs.createTimePairs(x.date) }
    val sums: RDD[(String, Int)] = pairs.reduceByKey( _ + _ )
    sums.persist()

    // create a text representation
    def mkCSV[K, V](header: List[String], rdd: RDD[(K, V)]): String = {
      val csv = new CSV(header: _*)
      rdd.collect().foreach { x => csv.add(x._1, x._2) }
      csv.toString
    }

    val sumsYMD = sums.filter { x => x._1.size == 8 }.sortByKey(true)
//    utils.delete(srcSumsYMDpart, fileSumsYMD)
//    sumsYMD.saveAsTextFile(srcSumsYMDpart)
    utils.write(fileSumsYMD, mkCSV(List("yyyymmdd", "value"), sumsYMD))

    val sumsYM = sums.filter { x => x._1.size == 6 }.sortByKey(true)
//    utils.delete(srcSumsYMpart, fileSumsYM)
//    sumsYM.saveAsTextFile(srcSumsYMpart)
    utils.write(fileSumsYM, mkCSV(List("yyyymm", "value"), sumsYM))

    val sumsY = sums.filter { x => x._1.size == 4 }.sortByKey(true)
//    utils.delete(srcSumsYpart, fileSumsY)
//    sumsY.saveAsTextFile(srcSumsYpart)
    utils.write(fileSumsY, mkCSV(List("yyyy", "value"), sumsY))

    val sumsHH = sums.filter { x => x._1.size == 2 }.sortByKey(true)
//    utils.delete(srcSumsHHpart, fileSumsHH)
//    sumsHH.saveAsTextFile(srcSumsHHpart)
    utils.write(fileSumsHH, mkCSV(List("hh", "value"), sumsHH))
  }

  val srcSumsUserPart = workDir + "/srcSumsUserPart.txt"
  val srcSumsUser = resultsDir + "/srcSumsUser.txt"
  val srcSumsGeoPart = workDir + "/srcSumsGeoPart.txt"
  val srcSumsGeo = resultsDir + "/srcSumsGeo.txt"

  def statsUser(src: String): Unit = {
    val input: RDD[CheckIn] = sc.objectFile(src, 1)
    val pairs = input.map ( x => (x.id, 1) )
    val sumPerUser = pairs.reduceByKey(_ + _)
    val sorted = sumPerUser.sortBy[Int]( x => x._2, false)
    utils.deldir(srcSumsUserPart)
    sorted.saveAsTextFile(srcSumsUserPart)
//    println(sorted.take(100).mkString("\n"))
  }

  def statsGeo(src: String): Unit = {
    val input: RDD[CheckIn] = sc.objectFile(src, 1)
    val pairs = input.map ( x => ((x.locX.toInt, x.locY.toInt), 1) )
    val sums = pairs.reduceByKey( _ + _ )
    val sorted = sums.sortBy(x => x._2, false)
    utils.deldir(srcSumsGeoPart)
    sorted.saveAsTextFile(srcSumsGeoPart)
//    println(sorted.take(25).mkString("\n"))
  }

  def findUser(src: String, user: Int): Unit = {
    val input: RDD[CheckIn] = sc.objectFile(src, 1)
    val ds = input.filter { c => c.id == user}
    println(ds.collect().mkString("\n"))
  }

  /**
   *
   * @param src
   * @param now
   * @return
   */
  def findAtPointInTime(src: String, now: String): RDD[(CheckIn.CustomerId, Point2)] = {
    type Pair = (Int, CheckIn)

    def debugFilterPrint[T](p: T => Boolean)(rdd: RDD[T]) = {
      val rdd2 = rdd.filter(p)
      println(rdd2.collect().mkString("\n"))
    }

    val pit = new TextDate(now)
    val input: RDD[CheckIn] = sc.objectFile(src, 1)

    val filtered = input.filter { x => x.date.compareTo(pit) <= 0 }       // ignore the data newer than 'now'
    //    debugFilterPrint[CheckIn](p => p.id == 10971)(filtered)

    val pairs: RDD[Pair] = filtered.map { x => (x.id, x)}                 // pair

    val red = pairs.reduceByKey( (c, d) => if (c.date.compareTo(d.date) < 0) d else c )
    //    debugFilterPrint[Pair](p => p._1 == 10971)(red)

    val xs = red.map { p => (p._1, new Point2(p._2.locX, p._2.locY)) }
    println(s"### num xs: ${xs.count()}")
    xs
  }

  val srcTestWrite = "hdfs://v1/tmp/testwrite.txt"

  def run(cmd: Command, sc: SparkContext, utils: Utilities): Unit = {
    this.sc = sc
    this.utils = utils
    cmd match {
      case Download(url, dest) => {
        utils.mkdir(workDir)
        require(!utils.exists(dest)) // precondition not downloaded
        utils.download(url, dest)
      }
      case CreateSample(n) => {
        //require(utils.exists(srcFile)) // precondition downloaded
        createSample(fileCheckins, fileCheckinsSample, n)
      }
      case SortByUser() => {
        require(utils.exists(fileCheckins)) // precondition downloaded
        utils.deldir(fileSortedByUser)
        sortByUser(fileCheckins, fileSortedByUser)
      }
      case SortByTime() => {
        require(utils.exists(fileSortedByUser)) // precondition srcSorted
        utils.deldir(fileSortedByTime)
        sortByTime(fileCheckins, fileSortedByTime)
      }
      case Sort() => {
        require(utils.exists(fileCheckins)) // precondition downloaded
        utils.deldir(fileSortedByTime)
        utils.deldir(fileSortedByUser)
        sorted(fileCheckins, fileSortedByUser, fileSortedByTime)
      }
      case StatsGlobal() => {
        require(utils.exists(fileSortedByUser)) // precondition srcSorted
        statsGlobal(fileSortedByUser)
      }
      case StatsTime() => {
        require(utils.exists(fileSortedByTime)) // precondition srcSorted
        statsTime(fileSortedByTime)
      }
      case StatsUser() => {
        require(utils.exists(fileSortedByUser)) // precondition srcSorted
        statsUser(fileSortedByUser)
      }
      case StatsGeo() => {
        require(utils.exists(fileSortedByUser)) // precondition srcSorted
        statsGeo(fileSortedByUser)
      }
      case FindUser(id) => {
        findUser(fileSortedByUser, id)
      }
      case PointInTime(dt) => {
        val xs = findAtPointInTime(fileSortedByUser, dt)
        println(xs.take(25).mkString("\n"))

        val ps = xs.map { x => x.swap }.take(100).toList //collect().toList

        val kdt = KdTree.fromList(ps)

        //val pD = new Point2(0.05, 0.05)
        val pD = new Point2(0.5, 0.5)
        xs.take(25).foreach { x =>
          val id = x._1
          val p = x._2
          val p1 = p - pD;
          val p2 = p + pD;
          val r = new Rectangle(p1, p2)
          val ps = kdt.rangeQuery(r)
          println(s"## ${p} = ${ps}")
        }
      }
      case Tmp() => {
        findUser(fileSortedByUser, 10971)
      }
      case TestWrite() => {
        utils.write(srcTestWrite, "This is a test.")
      }
      case _ => {
        println(s"Unknown command $cmd")
      }
    }
  }

  def parse(xs: Array[String]): Command = {
    xs match {
      case Array("download") => new Download(url, fileCheckins)
      case Array("download", url, dest) => new Download(url, dest)
      case Array("sample", ns) =>new CreateSample(ns.toInt)
      case Array("sort-by-user") => new SortByUser()
      case Array("sort-by-time") => new SortByTime()
      case Array("sort") => new Sort()
      case Array("global") => new StatsGlobal()
      case Array("time") => new StatsTime()
      case Array("user") => new StatsUser()
      case Array("geo") => new StatsGeo()
      case Array("tmp") => new Tmp()
      case Array("find", ns) => new FindUser(ns.toInt)
      case Array("pit", ns) => new PointInTime(ns)
      case Array("testwrite") => new TestWrite()
      case _ => NullCommand
    }
  }

}

object CheckInApp {
  import org.apache.log4j.Logger

  val log = Logger.getLogger(getClass.getName)

}