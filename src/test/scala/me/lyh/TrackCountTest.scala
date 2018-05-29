package me.lyh

import com.spotify.scio.extra.json._
import com.spotify.scio.testing._

class TrackCountTest extends PipelineSpec {

  import Schemas._
  import TrackCount.TrackCount

  val inData = Seq(1, 1, 1, 2, 2, 3)
    .map { case trackId => PlayTrack(0, trackId, 0, 0L) }

  val expected = Seq(
    TrackCount(1, 3L),
    TrackCount(2, 2L),
    TrackCount(3, 1L))

  "TrackCount" should "work" in {
    JobTest[me.lyh.TrackCount.type]
      .args("--input=in.json", "--output=out.json")
      .input(JsonIO("in.json"), inData)
      .output(JsonIO[TrackCount]("out.json"))(_ should containInAnyOrder (expected))
      .run()
  }
}
