package net.dinkla.lbnn

import net.dinkla.lbnn.preprocess.Preprocess

/**
 * Created by dinkla on 19/06/15.
 */
object Main {

  def main(args: Array[String]) {

    Utilities.mkdir(Preprocess.destDir)
    Utilities.download(Preprocess.url, Preprocess.dest)

  }

}
