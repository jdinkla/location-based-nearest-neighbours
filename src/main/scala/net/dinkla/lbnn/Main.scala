package net.dinkla.lbnn

import java.text.SimpleDateFormat
import java.time.chrono.Chronology
import java.util
import java.util.Date
import java.util.logging.SimpleFormatter

import com.esotericsoftware.kryo.Kryo
import net.dinkla.lbnn.kd.KdTree
import net.dinkla.lbnn.preprocess.Preprocess
import net.dinkla.lbnn.spark.{CIO, CheckIn}
import org.apache.hadoop.fs.FileUtil
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.spark.serializer.KryoRegistrator
import org.joda.time.chrono.GregorianChronology
import org.joda.time.format.{DateTimeFormatter, DateTimeFormat}
import org.joda.time.{LocalDate, DateTime}
import sun.util.calendar.Gregorian

/**
 * Created by dinkla on 19/06/15.
 */
object Main {

  val testRun: Boolean = true

  val workDir = "temp"
  val url = "https://snap.stanford.edu/data/loc-gowalla_totalCheckins.txt.gz"
  //val url = "http://mathforum.org/workshops/sum96/data.collections/datalibrary/Eur.pop.XL.zip.xls"
  val srcFile = workDir + "/" + "loc-gowalla_totalCheckins.txt.gz"
  val srcSmallSample = workDir + "/" + "checkins_small_sample.txt.gz"

  def testData = if (testRun) srcSmallSample else srcFile

  val srcSortedByUser = workDir + "/" + "temp-sorted-by-user"
  val srcSortedByTime = workDir + "/" + "temp-sorted-by-time"

  val tmpOutputDir = workDir + "/" + "temp-out"

  val dateTimeOrd = new Ordering[DateTime] {
    def compare(x: DateTime, y: DateTime) = x.getMillis().compare(y.getMillis())
  }

  val localDateOrd = new Ordering[LocalDate] {
    def compare(x: LocalDate, y: LocalDate) = x.compareTo(y)
  }


  def getSparkContext(): SparkContext = {
    val conf = new SparkConf()
      .setMaster("local")
      .setAppName("net.dinkla.lbnn")
      .set("spark.executor.memory", "7g")
      .set("spark.kryo.registrator", "net.dinkla.lbnn.spark.CustomKryoRegistrator")
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    new SparkContext(conf)
  }

  lazy val sc = getSparkContext()

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
    val intermediate = s"$dest.tmp"
    Utilities.deldir(intermediate)
    top.saveAsTextFile(intermediate, classOf[org.apache.hadoop.io.compress.GzipCodec])
    Utilities.merge(sc, intermediate, dest)   // TODO parts are gzipped, does merge work for partioned files?
  }

  /**
   *
   * @param src
   * @param dest
   * @tparam T
   * @return
   */
  def createSortedByUser(src: String, dest: String) = {
    val input: RDD[String] = sc.textFile(src)
    val tokenized = input.map(CheckIn.split)
    val parsed = tokenized.map(CheckIn.parse)
    val sorted = parsed.sortBy(c => c, true)
    sorted.saveAsObjectFile(dest)
  }

  def createSortedByTime(src: String, dest: String) = {
    val input: RDD[String] = sc.textFile(src)
    val tokenized = input.map(CheckIn.split)
    val parsed = tokenized.map(CheckIn.parse)
    implicit val ord = dateTimeOrd
    val sorted = parsed.sortBy(c => c.date, true)
    sorted.saveAsObjectFile(dest)
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

    implicit val ord = dateTimeOrd
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

  def countPerDateFormat(format: String, input: RDD[CheckIn]): RDD[(String, Int)] = {
    val fmt = new SimpleDateFormat(format)
    def f(dt: DateTime): String = fmt.format(dt.toDate())

    val pairs: RDD[(String, Int)] = input.map { x => (f(x.date), 1) }
    val sums = pairs.reduceByKey( _ + _ )
    sums
  }

  def statsTime(src: String): Unit = {
    val input: RDD[CheckIn] = sc.objectFile(src)

    val sumsDD = countPerDateFormat("yyyyMMdd", input)
    println(sumsDD.sortBy(c => c._2, false).take(10).mkString("\n"))

    val pairsMM = sumsDD.map { x => (x._1.substring(0, 6), x._2)}
    val sumsMM = pairsMM.reduceByKey { _ + _ }

    //val sumsMM = countPerDateFormat("yyyyMM", input)
    println(sumsMM.sortBy(c => c._2, false).take(10).mkString("\n"))
  }

  def statsUser(src: String): Unit = {
    val input: RDD[CheckIn] = sc.objectFile(src, 1)
    val pairs = input.map ( x => (x.id, 1) )
    val sumPerUser = pairs.reduceByKey(_ + _)
    val sorted = sumPerUser.sortBy[Int]( x => x._2, false)
    println(sorted.take(100).mkString("\n"))
  }

  def statsGeo(src: String): Unit = {
    val input: RDD[CheckIn] = sc.objectFile(src, 1)
    val pairs = input.map ( x => ((x.locX.toInt, x.locY.toInt), 1) )
    val sums = pairs.reduceByKey( _ + _ )
    val sorted = sums.sortBy(x => x._2, false)
    println(sorted.take(25).mkString("\n"))
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

    val format = new SimpleDateFormat("yyyyMMddhhmmss")
    val pit: DateTime = new DateTime(format.parse(now))
    val input: RDD[CheckIn] = sc.objectFile(src, 1)

    val filtered = input.filter { x => x.date.compareTo(pit) <= 0 }       // ignore the data newer than now
//    debugFilterPrint[CheckIn](p => p.id == 10971)(filtered)

    val pairs: RDD[Pair] = filtered.map { x => (x.id, x)}                 // pair

    val red = pairs.reduceByKey( (c, d) => if (c.date.compareTo(d.date) < 0) d else c )
//    debugFilterPrint[Pair](p => p._1 == 10971)(red)

    val xs = red.map { p => (p._1, new Point2(p._2.locX, p._2.locY)) }
    println(s"### num xs: ${xs.count()}")
    xs
  }

  abstract sealed class Command
  case object Download extends Command
  case class CreateSample(val num: Int) extends Command
  case object SortByUser extends Command
  case object SortByTime extends Command
  case object StatsGlobal extends Command
  case object StatsTime extends Command
  case object StatsUser extends Command
  case object StatsGeo extends Command
  case object Tmp extends Command
  case class FindUser(val name: Int) extends Command
  case class PointInTime(val dt: String) extends Command

  def main(args: Array[String]) {

//    val cmd: Command = new Download()
//    val cmd: Command = new CreateSample(1000)
//    val cmd: Command = new SortByUser
//    val cmd: Command = new SortByTime
//    val cmd: Command = new StatsGlobal
//    val cmd: Command = new StatsTime
//    val cmd: Command = new StatsUser
//    val cmd: Command = new StatsGeo
//    val cmd: Command = new Tmp
//    val cmd: Command = new FindUser(10971)
    val cmd: Command = PointInTime("20100904000000")

    cmd match {
      case Download => {
        Utilities.mkdir(workDir)
        require(!Utilities.exists(srcFile)) // precondition not downloaded
        Utilities.download(url, srcFile)
      }
      case CreateSample(n) => {
        require(Utilities.exists(srcFile)) // precondition downloaded
        createSample(srcFile, srcSmallSample, n)
      }
      case SortByUser => {
        require(Utilities.exists(srcFile)) // precondition downloaded
        Utilities.deldir(srcSortedByUser)
        createSortedByUser(srcFile, srcSortedByUser)
      }
      case SortByTime => {
        require(Utilities.exists(srcSortedByUser)) // precondition srcSorted
        Utilities.deldir(srcSortedByTime)
        createSortedByTime(srcFile, srcSortedByTime)
      }
      case StatsGlobal => {
        require(Utilities.exists(srcSortedByUser)) // precondition srcSorted
        statsGlobal(srcSortedByUser)
      }
      case StatsTime => {
        require(Utilities.exists(srcSortedByTime)) // precondition srcSorted
        statsTime(srcSortedByTime)
      }
      case StatsUser => {
        require(Utilities.exists(srcSortedByUser)) // precondition srcSorted
        statsUser(srcSortedByUser)
      }
      case StatsGeo => {
        require(Utilities.exists(srcSortedByUser)) // precondition srcSorted
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
      case Tmp => {
        findUser(srcSortedByUser, 10971)
      }

//      case "prepare_save" => {
//        Utilities.deldir(srcSortedByUser)
//        val sc : SparkContext = getSparkContext()
//
//        val input: RDD[String] = sc.textFile(testData)
//        val input2 = input.map(CheckIn.split)
//        //      val objs = input2.map(CI.parse)
//        //val objs = input2.map(CheckIn.parse)
//        val objs = input2.map(CIO.parse)
//        val objs2 = objs
//        objs2.saveAsObjectFile(srcSortedByUser)
//      }
//      case "prepare_1" => {
//        val sc : SparkContext = getSparkContext()
//        // load
//        Utilities.deldir(tmpOutputDir)
//        //      val rdd = sc.objectFile[CI](tmpInputSortedDir)
//        //val rdd = sc.objectFile[CheckIn](tmpInputSortedDir)
//        val rdd = sc.objectFile[CIO](srcSortedByUser)
//        rdd.take(10).map(println)
//
//      }
      case _ => {
        println(s"Unknown command $cmd")
      }
    }

  }
}
