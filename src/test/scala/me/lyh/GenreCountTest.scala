package me.lyh

import com.spotify.scio.extra.json._
import com.spotify.scio.testing._

class GenreCountTest extends PipelineSpec {

  import Schemas._
  import GenreCount.GenreCount

  val play = Seq(
    (1, 100000),
    (1, 200000),
    (1, 300000),
    (2, 100000),
    (2, 200000),
    (3, 100000)
  ).map { case (trackId, msPlayed) => PlayTrack(0, trackId, msPlayed, 0L) }
  val meta = Seq(
    Metadata(1, "track1", "Metal"),
    Metadata(2, "track2", "Rock"),
    Metadata(3, "track3", "Pop"))

  val expected = Seq(
    GenreCount("Metal", 3),
    GenreCount("Rock", 2),
    GenreCount("Pop", 1))

  "GenreCount" should "work" in {
    JobTest[me.lyh.GenreCount.type]
      .args("--playTrack=play.json", "--metadata=meta.json", "--output=out.json")
      .input(JsonIO("play.json"), play)
      .input(JsonIO("meta.json"), meta)
      .output(JsonIO[GenreCount]("out.json"))(_ should containInAnyOrder (expected))
      .run()
  }
}
