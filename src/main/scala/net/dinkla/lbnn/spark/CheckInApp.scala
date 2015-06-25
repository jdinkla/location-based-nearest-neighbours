package net.dinkla.lbnn.spark

import net.dinkla.lbnn.geom.{Rectangle, Point2}
import net.dinkla.lbnn.kd.KdTree
import net.dinkla.lbnn.utils.{Parameters, TextDate, Utilities}
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

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

  val testRun = props.getOrDefault("testRun", "false")
  def testData = if (testRun == "true") srcSmallSample else srcFile

  val url = props.get("url")
  val srcFile = props.get("srcFile")
  val srcSmallSample = props.get("srcSmallSample")
  val srcSortedByUser = props.get("srcSortedByUser")
  val srcSortedByTime = props.get("srcSortedByTime")
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

  def statsGlobal(src: String): Unit = {
    val input: RDD[CheckIn] = sc.objectFile(src)

    val numLines = input.count
    println(s"### #lines: ${numLines}")

    val allUsers = input.map (c => c.id)
    val distinctUsers = allUsers.distinct()

    val numDistinctUsers = distinctUsers.count
    println(s"### #users: ${numDistinctUsers}")

    val allDates = input.map (c => c.date)
    allDates.persist()

    val minDate = allDates.min()
    val maxDate = allDates.max()

    println(s"### min date: ${minDate}")
    println(s"### max date: ${maxDate}")

    // report
    println(s"### #lines: ${numLines}")
    println(s"### #users: ${numDistinctUsers}")
    println(s"### min date: ${minDate}")
    println(s"### max date: ${maxDate}")
  }

  val srcSumsYMDpart = "hdfs://v1/tmp/srcSumsYMDpart.txt"
  val srcSumsYMD = "hdfs://v1/tmp/srcSumsYMD.txt"
  val srcSumsYMpart = "hdfs://v1/tmp/srcSumsYMpart.txt"
  val srcSumsYM = "hdfs://v1/tmp/srcSumsYM.txt"
  val srcSumsYpart = "hdfs://v1/tmp/srcSumsYpart.txt"
  val srcSumsY = "hdfs://v1/tmp/srcSumsY.txt"
  val srcSumsHHpart = "hdfs://v1/tmp/srcSumsHHpart.txt"
  val srcSumsHH = "hdfs://v1/tmp/srcSumsHH.txt"
  val srcSumsUserPart = "hdfs://v1/tmp/srcSumsUserPart.txt"
  val srcSumsUser = "hdfs://v1/tmp/srcSumsUser.txt"
  val srcSumsGeoPart = "hdfs://v1/tmp/srcSumsGeoPart.txt"
  val srcSumsGeo = "hdfs://v1/tmp/srcSumsGeo.txt"

  def statsTime(src: String): Unit = {

    val input: RDD[CheckIn] = sc.objectFile(src)
    val pairs: RDD[(String, Int)] = input.flatMap { x => xs.createTimePairs(x.date) }
    val sums: RDD[(String, Int)] = pairs.reduceByKey( _ + _ )

    sums.persist()

    val sumsYMD = sums.filter { x => x._1.size == 8 }.sortByKey(true)
    utils.deldir(srcSumsYMDpart)
    sumsYMD.saveAsTextFile(srcSumsYMDpart)
//    utils.merge(srcSumsYMDpart, srcSumsYMD)

    val sumsYM = sums.filter { x => x._1.size == 6 }.sortByKey(true)
    utils.deldir(srcSumsYMpart)
    sumsYM.saveAsTextFile(srcSumsYMpart)
//    utils.merge(srcSumsYMpart, srcSumsYM)

    val sumsY = sums.filter { x => x._1.size == 4 }.sortByKey(true)
    utils.deldir(srcSumsYpart)
    sumsY.saveAsTextFile(srcSumsYpart)
//    utils.merge(srcSumsYpart, srcSumsY)

    val sumsHH = sums.filter { x => x._1.size == 2 }.sortByKey(true)
    utils.deldir(srcSumsHHpart)
    sumsHH.saveAsTextFile(srcSumsHHpart)
//    utils.merge(srcSumsHHpart, srcSumsHH)
  }

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
        createSample(srcFile, srcSmallSample, n)
      }
      case SortByUser() => {
        require(utils.exists(srcFile)) // precondition downloaded
        utils.deldir(srcSortedByUser)
        sortByUser(srcFile, srcSortedByUser)
      }
      case SortByTime() => {
        require(utils.exists(srcSortedByUser)) // precondition srcSorted
        utils.deldir(srcSortedByTime)
        sortByTime(srcFile, srcSortedByTime)
      }
      case Sort() => {
        require(utils.exists(srcFile)) // precondition downloaded
        utils.deldir(srcSortedByTime)
        utils.deldir(srcSortedByUser)
        sorted(srcFile, srcSortedByUser, srcSortedByTime)
      }
      case StatsGlobal() => {
        require(utils.exists(srcSortedByUser)) // precondition srcSorted
        statsGlobal(srcSortedByUser)
      }
      case StatsTime() => {
        require(utils.exists(srcSortedByTime)) // precondition srcSorted
        statsTime(srcSortedByTime)
      }
      case StatsUser() => {
        require(utils.exists(srcSortedByUser)) // precondition srcSorted
        statsUser(srcSortedByUser)
      }
      case StatsGeo() => {
        require(utils.exists(srcSortedByUser)) // precondition srcSorted
        statsGeo(srcSortedByUser)
      }
      case FindUser(id) => {
        findUser(srcSortedByUser, id)
      }
      case PointInTime(dt) => {
        val xs = findAtPointInTime(srcSortedByUser, dt)
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
        findUser(srcSortedByUser, 10971)
      }
      case _ => {
        println(s"Unknown command $cmd")
      }
    }
  }

  def parse(xs: Array[String]): Command = {
    xs match {
      case Array("download") => new Download(url, srcFile)
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
      case _ => NullCommand
    }
  }

}

object CheckInApp {
  import org.apache.log4j.Logger

  val log = Logger.getLogger(getClass.getName)

}