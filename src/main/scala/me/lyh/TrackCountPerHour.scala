package me.lyh

import com.spotify.scio._
import com.spotify.scio.extra.json._
import com.spotify.scio.values.SCollection
import org.apache.beam.sdk.transforms.windowing.IntervalWindow
import org.joda.time.{Duration, Instant}

object TrackCountPerHour {

  import Schemas._

  case class TrackCount(trackId: Int, plays: Long, windowStart: String, windowEnd: String)

  def main(cmdlineArgs: Array[String]): Unit = {
    val (sc, args) = ContextAndArgs(cmdlineArgs)

    val input = args.getOrElse("input", "in/play_track.json")
    val output = args("output")

    val playTracks: SCollection[PlayTrack] = sc
      .jsonFile[PlayTrack](input)
      .map(_.right.get)
      .timestampBy(t => new Instant(t.time))

    val trackCount: SCollection[TrackCount] = playTracks
      .withFixedWindows(Duration.standardMinutes(10))
      .map(track => (track.trackId, 1L))
      .reduceByKey(_ + _)
      .withWindow[IntervalWindow]
      .map { case ((trackId, plays), window) =>
        TrackCount(trackId, plays, window.start().toString, window.end().toString())
      }

    trackCount.saveAsJsonFile(output)
    sc.close()
  }
}
