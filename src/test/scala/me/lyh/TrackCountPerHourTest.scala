package me.lyh

import com.spotify.scio.extra.json._
import com.spotify.scio.testing._

class TrackCountPerHourTest extends PipelineSpec {

  import Schemas._
  import TrackCountPerHour.TrackCount

  val inData = Seq(
    (1, 0L), (1, 1L), (1, 2L), (2, 3L), (2, 4L), (3, 5L),
    (1, 600000L), (1, 600001L)
  ).map { case (trackId, time) => PlayTrack(0, trackId, 0, time) }

  val expected = Seq(
    TrackCount(1, 3L, "1970-01-01T00:00:00.000Z", "1970-01-01T00:10:00.000Z"),
    TrackCount(2, 2L, "1970-01-01T00:00:00.000Z", "1970-01-01T00:10:00.000Z"),
    TrackCount(3, 1L, "1970-01-01T00:00:00.000Z", "1970-01-01T00:10:00.000Z"),
    TrackCount(1, 2L, "1970-01-01T00:10:00.000Z", "1970-01-01T00:20:00.000Z"))

  "TrackCountPerHour" should "work" in {
    JobTest[me.lyh.TrackCountPerHour.type]
      .args("--input=in.json", "--output=out.json")
      .input(JsonIO("in.json"), inData)
      .output(JsonIO[TrackCount]("out.json"))(_ should containInAnyOrder (expected))
      .run()
  }
}
