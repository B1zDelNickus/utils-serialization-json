package codes.spectrum.serialization.json.serializers

import codes.spectrum.serialization.json.Json
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong


class AtomicsSerializerTest : StringSpec({
    // в принципе все работает итак из коробки
    "AtomicInteger serializer"{
        Json.stringify(AtomicInteger(100)) shouldBe "100"
    }
    "AtomicInteger deserializer"{
        Json.read<AtomicInteger>("100").get() shouldBe AtomicInteger(100).get()
    }

    "AtomicLong serializer"{
        Json.stringify(AtomicLong(100L)) shouldBe "100"
    }
    "AtomicLong deserializer"{
        Json.read<AtomicLong>("100").get() shouldBe AtomicLong(100).get()
    }
})