package net.dinkla.lbnn.spark

/**
 * Created by Dinkla on 23.06.2015.
 */
abstract sealed class Command
object NullCommand extends Command
case class Download(val url: String, val dest: String) extends Command
case class CreateSample(val num: Int) extends Command
case class SortByUser() extends Command
case class SortByTime() extends Command
case class Sort() extends Command
case class Statistics() extends Command
case class StatsGlobal() extends Command
case class StatsTime() extends Command
case class StatsUser() extends Command
case class StatsGeo() extends Command
case class Tmp() extends Command
case class FindUser(val name: Int) extends Command
case class PointInTime(val dt: String) extends Command
case class TestWrite() extends Command
case class NumberOfNeighbors(val dt: String, val windowSizeInKm: Double) extends Command

