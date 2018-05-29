package me.lyh

object Schemas {

  case class PlayTrack(userId: Int, trackId: Int, msPlayed: Int, time: Long)

  case class Metadata(trackId: Int, title: String, genre: String)

}
