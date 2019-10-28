package codes.spectrum.serialization.json.serializers

import codes.spectrum.serialization.json.AnyCollection
import codes.spectrum.serialization.json.AnyMap
import codes.spectrum.serialization.json.AnyValue
import codes.spectrum.serialization.json.Json
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec


class AnyValueSerializerTest : StringSpec({
    "AnyValue serializer"{
        Json.stringify(AnyValue(SomeClass()), format = false) shouldBe """{"clazz":"codes.spectrum.serialization.json.serializers.AnyValueSerializerTest${'$'}SomeClass","value":{"x":1}}"""
    }
    "AnyValue de-serializer"{
        Json.read<AnyValue>("""{"clazz":"codes.spectrum.serialization.json.serializers.AnyValueSerializerTest${'$'}SomeClass","value":{"x":1}}""") shouldBe AnyValue(SomeClass())
    }
    "AnyCollection serializer"{
        Json.stringify(AnyCollection(1, "2", SomeClass()), format = false) shouldBe
            """[1,"2",{"clazz":"codes.spectrum.serialization.json.serializers.AnyValueSerializerTest${'$'}SomeClass","value":{"x":1}}]"""
    }
    "AnyCollection deserializer"{
        Json.read<AnyCollection>("""[1,"2",{"clazz":"codes.spectrum.serialization.json.serializers.AnyValueSerializerTest${'$'}SomeClass","value":{"x":1}}]""") shouldBe
            AnyCollection(1, "2", SomeClass())
    }
    "AnyMap serializer"{
        Json.stringify(AnyMap("x" to 1, "y" to "2", "z" to SomeClass()), format = false) shouldBe
            """{"x":1,"y":"2","z":{"clazz":"codes.spectrum.serialization.json.serializers.AnyValueSerializerTest${'$'}SomeClass","value":{"x":1}}}"""
    }
    "AnyMap deserializer"{
        Json.read<AnyMap>("""{"x":1,"y":"2","z":{"clazz":"codes.spectrum.serialization.json.serializers.AnyValueSerializerTest${'$'}SomeClass","value":{"x":1}}}""") shouldBe
            AnyMap("x" to 1, "y" to "2", "z" to SomeClass())
    }
    "Auto Nesting AnyMap,AnyCollection deserialize" {
        Json.read<AnyMap>("""{a:{x:y},b:[x,{u:y}]}""") shouldBe
            AnyMap("a" to AnyMap("x" to "y"), "b" to AnyCollection("x", AnyMap("u" to "y")))
    }
    "Auto Nesting AnyMap,AnyCollection serialize" {
        Json.stringify(AnyMap("a" to AnyMap("x" to "y"), "b" to AnyCollection("x", AnyMap("u" to "y"))), format = false) shouldBe
            """{"a":{"x":"y"},"b":["x",{"u":"y"}]}"""

    }
}) {
    data class SomeClass(val x: Int = 1)
}