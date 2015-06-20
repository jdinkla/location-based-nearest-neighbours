package net.dinkla.lbnn.kd

import scalaz.Tree

/**
 * Created by dinkla on 20/06/15.
 */
object EmbedTree {

  import scalaz.Tree
  import scalaz.Tree.leaf
  import scalaz.Tree.node

  def embed(kdt: KdTree): Tree[Point2] = {
    kdt match {
      case Nil => leaf(null)
      case Leaf(x) => leaf(x)
      case Node(d, m, ls, es, hs) => {
        val p = Point2(d, m)
        val ss : Stream[Tree[Point2]] = Stream(embed(ls), embed(es), embed(hs))
        node(p, ss)
      }
    }
  }

}
