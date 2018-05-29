package me.lyh

import com.spotify.scio._
import com.spotify.scio.extra.json._
import com.spotify.scio.values.SCollection

object TrackCount {

  import Schemas._

  case class TrackCount(trackId: Int, plays: Long)

  def main(cmdlineArgs: Array[String]): Unit = {
    val (sc, args) = ContextAndArgs(cmdlineArgs)

    val input = args.getOrElse("input", "in/play_track.json")
    val output = args("output")

    val playTracks: SCollection[PlayTrack] = sc
      .jsonFile[PlayTrack](input)
      .map(_.right.get)

    val trackCount: SCollection[TrackCount] = playTracks
      .map(track => (track.trackId, 1L))
      .reduceByKey(_ + _)
      .map { case (trackId, plays) =>
        TrackCount(trackId, plays)
      }

    trackCount.saveAsJsonFile(output)
    sc.close()
  }
}
