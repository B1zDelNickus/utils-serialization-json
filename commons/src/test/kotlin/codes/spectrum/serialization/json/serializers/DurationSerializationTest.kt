package codes.spectrum.serialization.json.serializers

import codes.spectrum.serialization.json.Json
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import java.time.Duration

class DurationSerializationTest : StringSpec({
    "can serialize"{
        Json.stringify(WithDuration(duration = Duration.ofHours(1))) shouldBe """{
  "duration": "PT1H"
}"""
    }
    "can deserialize string"{
        Json.read<WithDuration>("{duration:PT1H}").duration.toHours() shouldBe 1L
    }
    "can deserialize number"{
        Json.read<WithDuration>("{duration:100}").duration.toMillis() shouldBe 100
    }
}) {
    class WithDuration(var duration: Duration = Duration.ofMillis(0))
}