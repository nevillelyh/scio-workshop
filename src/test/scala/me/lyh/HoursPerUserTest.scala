package me.lyh

import com.spotify.scio.extra.json._
import com.spotify.scio.testing._

class HoursPerUserTest extends PipelineSpec {

  import Schemas._
  import HoursPerUser.UserHours

  val inData = Seq(
    (1, 100000),
    (1, 200000),
    (1, 300000),
    (2, 100000),
    (2, 200000),
    (3, 100000)
  ).map { case (userId, msPlayed) => PlayTrack(userId, 0, msPlayed, 0L) }

  val expected = Seq(
    UserHours(1, 600.0 / 3600),
    UserHours(2, 300.0 / 3600),
    UserHours(3, 100.0 / 3600))

  "HoursPerUser" should "work" in {
    JobTest[me.lyh.HoursPerUser.type]
      .args("--input=in.json", "--output=out.json")
      .input(JsonIO("in.json"), inData)
      .output(JsonIO[UserHours]("out.json"))(_ should containInAnyOrder (expected))
      .run()
  }
}
