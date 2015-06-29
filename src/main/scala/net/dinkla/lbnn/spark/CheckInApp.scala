package net.dinkla.lbnn.spark

import net.dinkla.lbnn.geom.{Haversine, Rectangle, Point2}
import net.dinkla.lbnn.kd.KdTree
import net.dinkla.lbnn.spark.CheckIn.CustomerId
import net.dinkla.lbnn.utils.{CSV, Parameters, TextDate, Utilities}
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

/**
 * Created by Dinkla on 23.06.2015.
 */
class CheckInApp(val props: Parameters) extends App {

  type Command = net.dinkla.lbnn.spark.Command

  import CheckInApp.log
  import ETLFunctions._

  val workDir = props.get("workDir")
  val resultsDir = props.get("resultsDir")

  val url = props.get("url")
  val fileCheckins = props.get("fileCheckins")
  val fileCheckinsSample = props.get("fileCheckinsSample")
  val fileSortedByUser = props.get("fileSortedByUser")
  val fileSortedByTime = props.get("fileSortedByTime")

  val testRun = props.getOrDefault("testRun", "false")
  def testData = if (testRun == "true") fileCheckinsSample else fileCheckins

  // as a var, because of later initialization
  var sc: SparkContext = null
  var utils: Utilities = null

  def getPath(dir: String, key: String, default: String)
    = ETLFunctions.getPath(props, dir, key, default)

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

  /**
   * Sort by user id.
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

  /**
   * Sort by time.
   *
   * @param src
   * @param dest
   */
  def sortByTime(src: String, dest: String) = {
    val input: RDD[String] = sc.textFile(src)
    val tokenized = input.map(CheckIn.split)
    val parsed = tokenized.map(CheckIn.parse)
    val sorted = parsed.sortBy(c => c.date, true)
    sorted.saveAsObjectFile(dest)
  }

  /**
   * Sort by user id and save, sort by time and save.
   *
   * @param src
   * @param destUser
   * @param destTime
   */
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

  /**
   * Calculate some global statistics.
   *
   * @param src     The source objectFile
   * @param dest    The destination text file.
   */
  def statsGlobal(src: String, dest: String): Unit = {

    // read and count
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
    utils.write(dest, csv.toString())
  }

  /**
   * Statistics based on checkin time.
   *
   * @param src
   */
  def statsTime(src: String): Unit = {

    // declare output file names
    val fileSumsYMD = getPath(resultsDir, "fileSumsYMD", "sums_yyyymmdd.csv")
    val fileSumsYM = getPath(resultsDir, "fileSumsYM", "sums_yyyymm.csv")
    val fileSumsY = getPath(resultsDir, "fileSumsY", "sums_yyyy.csv")
    val fileSumsHH = getPath(resultsDir, "fileSumsHH", "sums_hh.csv")

    // transform to pairs of YYYYMMDD, YYYYMM, YYYY and HH
    val input: RDD[CheckIn] = sc.objectFile(src)
    val pairs: RDD[(String, Int)] = input.flatMap { x => ETLFunctions.createTimePairs(x.date) }
    // sum up
    val sums: RDD[(String, Int)] = pairs.reduceByKey( _ + _ )
    sums.persist()

    val sumsYMD = sums.filter { x => x._1.size == 8 }.sortByKey(true)
    utils.write(fileSumsYMD, mkCSV("yyyymmdd", "value", sumsYMD))

    val sumsYM = sums.filter { x => x._1.size == 6 }.sortByKey(true)
    utils.write(fileSumsYM, mkCSV("yyyymm", "value", sumsYM))

    val sumsY = sums.filter { x => x._1.size == 4 }.sortByKey(true)
    utils.write(fileSumsY, mkCSV("yyyy", "value", sumsY))

    val sumsHH = sums.filter { x => x._1.size == 2 }.sortByKey(true)
    utils.write(fileSumsHH, mkCSV("hh", "value", sumsHH))
  }

  /**
   * Statistics based on the user id.
   *
   * @param src
   */
  def statsUser(src: String, dest: String): Unit = {
    // read the input
    val input: RDD[CheckIn] = sc.objectFile(src, 1)
    val pairs = input.map { x => (x.id, 1) }
    // count the users and sort them by checkings descending
    val sumPerUser = pairs.reduceByKey(_ + _)
    val sorted = sumPerUser.sortBy[Int]( x => x._2, false)
    // save the top 100
    val top100 = sorted.take(100)
    utils.write(dest, mkCSV("user", "number of checkins", top100))
  }

  /**
   * Statistics based on location
   * @param src
   */
  def statsGeo(src: String, dest: String): Unit = {
    // read and transform
    val input: RDD[CheckIn] = sc.objectFile(src, 1)
    val pairs = input.map ( x => ((x.locX.toInt, x.locY.toInt), 1) )
    val sums = pairs.reduceByKey { _ + _  }
    val sorted = sums.sortBy(x => x._2, false)

    // save all
    val text = sorted.map { x => (x._1.toString(), x._2) }
    utils.write(dest, mkCSV("location", "number of checkins", text))
  }

  /**
   * 
   * @param src
   * @param user
   */
  def findUser(src: String, user: Int): Unit = {
    val input: RDD[CheckIn] = sc.objectFile(src, 1)
    val ds = input.filter { c => c.id == user}
    println(ds.collect().mkString("\n"))
  }

  /**
   *
   * @param src
   * @param date
   * @return
   */
  def filterToLatest(src: String, date: String): RDD[CheckIn] = {
    val pit = new TextDate(date)
    val input: RDD[CheckIn] = sc.objectFile(src)
    // ignore the data newer than 'date'
    val filtered = input.filter { x => x.date.compareTo(pit) <= 0 }
    // take the newest / latest by reducing by customer id (this is because we use the source file sorted by this id)
    val pairs: RDD[(Int, CheckIn)] = filtered.map { x => (x.id, x)}
    val latestPairs = pairs.reduceByKey( (c, d) => if (c.date.compareTo(d.date) < 0) d else c )
    // drop the customer id, return CheckIn's
    val latest = latestPairs.map { x => x._2 }
    latest
  }

  /**
   *
   * @param src
   * @param now
   * @return
   */
  def findAtPointInTime(src: String, now: String): RDD[(CheckIn.CustomerId, Point2)] = {
    type Pair = (Int, CheckIn)

    val pit = new TextDate(now)
    val input: RDD[CheckIn] = sc.objectFile(src, 1)

    val filtered = input.filter { x => x.date.compareTo(pit) <= 0 }       // ignore the data newer than 'now'
    // debugFilterPrint[CheckIn](p => p.id == 10971)(filtered)

    val pairs: RDD[Pair] = filtered.map { x => (x.id, x)}                 // pair

    val red = pairs.reduceByKey( (c, d) => if (c.date.compareTo(d.date) < 0) d else c )
    // debugFilterPrint[Pair](p => p._1 == 10971)(red)

    val xs = red.map { p => (p._1, new Point2(p._2.locX, p._2.locY)) }
    println(s"### num xs: ${xs.count()}")
    xs
  }

  /**
   *
   * @param dest
   * @param dt
   * @param windowSizeInKm
   */
  def neighbors(dest: String, dt: String, windowSizeInKm: Double): Unit = {
    // get all the latest checkins for dt
    val rdd: RDD[CheckIn] = filterToLatest(fileSortedByUser, dt)
    rdd.persist()

    // build the KD tree
    // pair: Point2 x CheckIn
    val ps: RDD[(Point2, CustomerId)] = rdd.map { c => ( Point2(c.locX, c.locY), c.id ) }
    val ps2: RDD[(Point2, Iterable[CustomerId])] = ps.groupByKey()
    val ps3: RDD[(Point2, List[CustomerId])] = ps2.mapValues { p => p.toList }
    val num = ps3.count()
    log.info(s"### Building KdTree for $num nodes")
    val ps4 = ps3.collect() // ps3.take(100)
    val kdt: KdTree[List[CustomerId]] = KdTree.fromList(ps4)

    // query for each customer in rdd
    val ns : RDD[(CheckIn,Seq[(Point2, List[CustomerId])])] = rdd.map { c =>
      val loc = Point2(c.locX, c.locY)
      val rect = Haversine.neighborhood(loc, windowSizeInKm)
      val ps: Seq[(Point2, List[CustomerId])] = kdt.rangeQuery(rect)
      (c, ps.filter { x => x._1 != loc })     // ignore the point at loc, this is the current row
    }
    // val ns2 = ns.filter { x => x._2.size > 0}
    // reduce to compact output format: (CustId, #Neighbours)
    val ns3 = ns.map { x => (x._1.id, x._2.size )}
    utils.write(dest, mkCSV("CustomerId", "number of neighbors", ns3.collect()))
  }

  /**
   *
   * @param cmd
   * @param sc
   * @param utils
   */
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
        val fileStatsGlobal = getPath(resultsDir, "fileStatsGlobal", "stats_global.csv")
        utils.mkdir(resultsDir)
        statsGlobal(fileSortedByUser, fileStatsGlobal)
      }
      case StatsTime() => {
        utils.mkdir(resultsDir)
        require(utils.exists(fileSortedByTime)) // precondition srcSorted
        statsTime(fileSortedByTime)
      }
      case StatsUser() => {
        require(utils.exists(fileSortedByUser)) // precondition srcSorted
        val fileSumsUser = getPath(resultsDir, "fileSumsUser", "sums_user_top100.csv")
        utils.mkdir(resultsDir)
        statsUser(fileSortedByUser, fileSumsUser)
      }
      case StatsGeo() => {
        require(utils.exists(fileSortedByUser)) // precondition srcSorted
        val fileSumsGeo = getPath(resultsDir, "fileSumsLocation", "sums_location.csv")
        utils.mkdir(resultsDir)
        statsGeo(fileSortedByUser, fileSumsGeo)
      }
      case FindUser(id) => {
        utils.mkdir(resultsDir)
        findUser(fileSortedByUser, id)
      }
      case PointInTime(dt) => {
        // defs
        val fileNeighbors10 = getPath(resultsDir, "fileNeighbors10", "neighbors_10.csv")
        val windowSizeInKm = 10

        // prepare
        utils.mkdir(resultsDir)
        utils.deldir(fileNeighbors10)

        // get all the latest checkins for dt
        val rdd: RDD[CheckIn] = filterToLatest(fileSortedByUser, dt)
        rdd.persist()

        // build the KD tree
        // pair: Point2 x CheckIn
        val ps: RDD[(Point2, CustomerId)] = rdd.map { c => ( Point2(c.locX, c.locY), c.id ) }
        val ps2: RDD[(Point2, Iterable[CustomerId])] = ps.groupByKey()
        val ps3: RDD[(Point2, List[CustomerId])] = ps2.mapValues { p => p.toList }
        val num = ps3.count()
        log.info(s"### Building KdTree for $num nodes")
        val ps4 = ps3.collect() // ps3.take(100)
        val kdt: KdTree[List[CustomerId]] = KdTree.fromList(ps4)

        // query for each customer in rdd
        val ns : RDD[(CheckIn,Seq[(Point2, List[CustomerId])])] = rdd.map { c =>
          val loc = Point2(c.locX, c.locY)
          val rect = Haversine.neighborhood(loc, windowSizeInKm)
          val ps: Seq[(Point2, List[CustomerId])] = kdt.rangeQuery(rect)
          (c, ps.filter { x => x._1 != loc })     // ignore the point at loc, this is the current row
        }

        // ns.saveAsTextFile(fileNeighbors10)
        val ns2 = ns.map { x => (x._1.id, x._2.size )}
        ns2.saveAsTextFile(fileNeighbors10)
      }
      case NumberOfNeighbors(dt, km) => {
        // prepare
        val dest = resultsDir + "/" + s"num_neighbors_${dt.toString}_${km.toString}.csv"
        utils.mkdir(resultsDir)
        utils.deldir(dest)
        neighbors(dest, dt, km)
      }
      case Tmp() => {
        val rdd : RDD[CheckIn] = sc.objectFile(fileSortedByUser)
        val locs = rdd.map { c => (Point2(c.locX, c.locY), c.id)}

        val ps = locs.take(100)
        val kdt = KdTree.fromList(ps)
        //findUser(fileSortedByUser, 10971)
      }
      case TestWrite() => {
        val fileTestWrite = getPath(workDir, "fileTestWrite", "testwrite.txt")
        utils.write(fileTestWrite, "This is a test.")
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
      case Array("pit", dt) => new PointInTime(dt)
      case Array("testwrite") => new TestWrite()
      case Array("neighbors", dt, km) => new NumberOfNeighbors(dt, km.toDouble)
      case _ => NullCommand
    }
  }

}

/**
 *
 */
object CheckInApp {

  import org.apache.log4j.Logger

  val log = Logger.getLogger(getClass.getName)

}