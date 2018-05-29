package me.lyh

import com.spotify.scio.extra.json._
import com.spotify.scio.testing._

class TopUsersTest extends PipelineSpec {

  import Schemas._
  import TopUsers.UserHours

  val inData = Seq(
    (1, 100000),
    (1, 200000),
    (1, 300000),
    (2, 100000),
    (2, 200000),
    (3, 100000)
  ).map { case (userId, msPlayed) => PlayTrack(userId, 0, msPlayed, 0L) }

  val expected = UserHours(1, 600.0 / 3600)

  "TopUsers" should "work" in {
    JobTest[me.lyh.TopUsers.type]
      .args("--input=in.json", "--output=out.json", "--topN=1")
      .input(JsonIO("in.json"), inData)
      .output(JsonIO[UserHours]("out.json"))(_ should containSingleValue (expected))
      .run()
  }
}
