package me.lyh

import com.spotify.scio._
import com.spotify.scio.extra.json._
import com.spotify.scio.values.SCollection

object GenreCount {

  import Schemas._

  case class GenreCount(genre: String, plays: Long)

  def main(cmdlineArgs: Array[String]): Unit = {
    val (sc, args) = ContextAndArgs(cmdlineArgs)

    val inPlay = args.getOrElse("playTrack", "in/play_track.json")
    val inMeta = args.getOrElse("metadata", "in/metadata.json")
    val output = args("output")

    val playTracks: SCollection[(Int, PlayTrack)] = sc
      .jsonFile[PlayTrack](inPlay)
      .map(_.right.get)
      .keyBy(_.trackId)

    val metadata: SCollection[(Int, Metadata)] = sc
      .jsonFile[Metadata](inMeta)
      .map(_.right.get)
      .keyBy(_.trackId)

    val trackCount: SCollection[GenreCount] = playTracks.join(metadata)
      .map { case (trackId, (play, meta)) => (meta.genre, 1L) }
      .reduceByKey(_ + _)
      .map { case (genre, plays) =>
        GenreCount(genre, plays)
      }

    trackCount.saveAsJsonFile(output)
    sc.close()
  }
}
