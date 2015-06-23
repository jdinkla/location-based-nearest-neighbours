package net.dinkla.lbnn.spark

/**
 * Created by Dinkla on 23.06.2015.
 */
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
