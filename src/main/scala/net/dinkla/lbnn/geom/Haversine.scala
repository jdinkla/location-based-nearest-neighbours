package net.dinkla.lbnn.geom

/**
 * https://en.wikipedia.org/wiki/Haversine_formula
 *
 * https://en.wikipedia.org/wiki/Latitude#The_length_of_a_degree_of_latitude
 * Created by Dinkla on 27.06.2015.
 */
object Haversine {

  import Math.{PI, sin, cos, atan2, sqrt}

  private val R: Double = 6371.0

  private val pi180: Double = PI/180.0

  private def deg2rad(deg: Double): Double = deg * pi180

  private def rad2deg(rad: Double): Double = rad/pi180

  private val kmPerDegree: Double = 111.2

  private def degrees(km: Double): Double = km / kmPerDegree

  /**
   * converted from http://stackoverflow.com/questions/27928/how-do-i-calculate-distance-between-two-latitude-longitude-points
   * @param lat1
   * @param lon1
   * @param lat2
   * @param lon2
   * @return
   */
  def distanceInKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double = {
    val dLat2: Double = deg2rad(lat2 - lat1) / 2.0
    val dLon2: Double = deg2rad(lon2 - lon1) / 2.0
    val a = sin(dLat2) * sin(dLat2) + cos(deg2rad(lat1)) * cos(deg2rad(lat2)) * sin(dLon2) * sin(dLon2)
    val c = 2.0 * atan2(sqrt(a), sqrt(1-a))
    R * c                                      // Distance in km
  }

  /**
   *
   * See http://www.movable-type.co.uk/scripts/latlong.html
   *
   * @param lat
   * @param lon
   * @param dist
   * @param bearing
   * @return
   */
  def moveInKm(lat: Double, lon: Double, dist: Double, bearing: Double) = {
    import Math.asin
    val φ1 = deg2rad(lat)
    val λ1 = deg2rad(lon)
    val brng = deg2rad(bearing)
    val φ2 = asin( sin(φ1)*cos(dist/R) + cos(φ1)*sin(dist/R)*cos(brng) )
    val λ2 = λ1 + atan2(sin(brng)*sin(dist/R)*cos(φ1), cos(dist/R)-sin(φ1)*sin(φ2))
    (rad2deg(φ2), rad2deg(λ2))
  }

  /**
   *
   * @param p
   * @param size
   * @return
   */
  def neighborhood(p: Point2, size: Double): Rectangle = {
    val s2 = size / 2.0
    val (a, b) = moveInKm(p.x, p.y, s2, 270.0)        // 1/2 west
    val (c, d) = moveInKm(a, b, s2, 180.0)        // 1/2 south: lower left
    val (e, f) = moveInKm(c, d, size, 90.0)       // 1 east
    val (g, h) = moveInKm(e, f, size, 0.0)        // 1 north:   upper right
    new Rectangle(Point2(c, d), Point2(g, h))
  }

}
