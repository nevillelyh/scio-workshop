package me.lyh

import com.spotify.scio._
import com.spotify.scio.extra.json._
import com.spotify.scio.values.SCollection

import scala.concurrent.duration._

object HoursPerUser {

  import Schemas._

  case class UserHours(userId: Int, hours: Double)

  def main(cmdlineArgs: Array[String]): Unit = {
    val (sc, args) = ContextAndArgs(cmdlineArgs)

    val input = args.getOrElse("input", "in/play_track.json")
    val output = args("output")

    val playTracks: SCollection[PlayTrack] = sc
      .jsonFile[PlayTrack](input)
      .map(_.right.get)

    val userHours: SCollection[UserHours] = playTracks
      .map(track => (track.userId, track.msPlayed))
      .sumByKey
      .map { case (userId, msPlayed) =>
        UserHours(userId, msPlayed.toDouble / 1.hour.toMillis)
      }

    userHours.saveAsJsonFile(output)
    sc.close()
  }
}
