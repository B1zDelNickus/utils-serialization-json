package codes.spectrum.serialization.json

import codes.spectrum.serialization.json.extensibility.IJsonDeserializeInterceptor
import codes.spectrum.serialization.json.extensibility.IJsonDeserializePostprocess
import codes.spectrum.serialization.json.extensibility.IJsonSerializeInterceptor
import codes.spectrum.serialization.json.extensibility.IJsonSerializePostprocess
import com.google.gson.*
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.StringSpec


internal class JsonTest : StringSpec({
    val obj = ExposeSample()
    "can serialize by defualt - format and normal level"{
        Json.stringify(obj) shouldBe """{
  "none": 1,
  "min": 2,
  "opt": 3,
  "norm": 4
}"""
    }
    "can serialize no format"{
        Json.stringify(obj, format = false) shouldBe """{"none":1,"min":2,"opt":3,"norm":4}"""
    }
    "min level"{
        Json.stringify(obj, format = false, level = ExposeLevel.MINIMAL) shouldBe """{"min":2}"""
    }
    "opt level"{
        Json.stringify(obj, format = false, level = ExposeLevel.OPTIMIZED) shouldBe """{"min":2,"opt":3}"""
    }

    "class level more than field - not exposed"{
        Json.stringify(ExposeSample().apply { payload = MinClassExample() }, format = false, level = ExposeLevel.MINIMAL) shouldBe """{"min":2}"""
    }

    "class level match field - exposed"{
        Json.stringify(ExposeSample().apply { payload = MinClassExample() }, format = false, level = ExposeLevel.OPTIMIZED) shouldBe """{"min":2,"opt":3,"payload":{"y":13}}"""
    }
    "class level match field - exposed - debug example"{
        Json.stringify(ExposeSample().apply { payload = DebugClassExample() }, format = false, level = ExposeLevel.DEBUG) shouldBe """{"none":1,"min":2,"opt":3,"norm":4,"detail":5,"debug":6,"payload":{"x":12}}"""
    }


    "class level less than field - not exposed"{
        Json.stringify(ExposeSample().apply { payload = DebugClassExample() }, format = false, level = ExposeLevel.NORMAL) shouldBe """{"none":1,"min":2,"opt":3,"norm":4}"""
    }

    "detail level"{
        Json.stringify(obj, format = false, level = ExposeLevel.DETAILED) shouldBe """{"none":1,"min":2,"opt":3,"norm":4,"detail":5}"""
    }
    "debug level"{
        Json.stringify(obj, format = false, level = ExposeLevel.DEBUG) shouldBe """{"none":1,"min":2,"opt":3,"norm":4,"detail":5,"debug":6}"""
    }

    "all and ignorable level"{
        Json.stringify(obj, format = false, level = ExposeLevel.IGNORABLE) shouldBe """{"none":1,"min":2,"opt":3,"norm":4,"detail":5,"debug":6,"ignorable":7}"""
    }

    "basic convert"{
        val result = Json.convert<ConvertTargetExample>(obj)
        result shouldBe ConvertTargetExample(min = obj.min)
    }

    "convert with modifier"{
        val result = Json.convert<ConvertTargetExample>(obj) {
            it.set(ConvertTargetExample::specNorm.name, obj.norm)
        }
        result shouldBe ConvertTargetExample(min = obj.min, specNorm = obj.norm)
    }

    "convert with extended modifier body"{
        val result = Json.convert<ExposeSample, ConvertTargetExample>(obj) { src, j ->
            val subst = JsonObject()
            subst.set("min", src.min + 1)
            subst.set("specNorm", j.toString().length / 4)
            subst
        }
        result shouldBe ConvertTargetExample(min = obj.min + 1, specNorm = 11)
    }

    "lenient relax prop definitions" {
        Json.stringify(Json.read("""{
            hello:world;
            good=bad,
            1=>2
            }""")) shouldBe """{
  "hello": "world",
  "good": "bad",
  "1": 2
}"""
    }

    "lenient relax arrays"{
        Json.stringify(Json.read("[1;'test',hell,,]")) shouldBe """[
  1,
  "test",
  "hell",
  null,
  null
]"""
    }

    "lenient comments"{
        Json.stringify(Json.read("""{
            /* long
            */
            a:1  //short
            # another short
}""")) shouldBe """{
  "a": 1
}"""

    }

    "serialize interceptors"{
        Json.stringify(InterceptorsExample()) shouldBe """{
  "si": {
    "_i_": 1
  },
  "sp": {
    "i": 1,
    "_x_": 2
  },
  "di": {
    "content": ""
  },
  "dp": {
    "i": 1,
    "content": ""
  }
}"""
    }
    "deserialize interceptors"{
        Json.read<InterceptorsExample>("{di:{x:hello},dp:{i:2}}").toString() shouldBe
            """InterceptorsExample(si=SerializeInterceptorExample(i=1), sp=SerializePostprocessExample(i=1), di=DeSerializeInterceptorExample(content={"x":"hello"}), dp=DeserializePostProcessExample(i=2, content={"i":2}))"""
    }

    "can write any"{
        Json.stringify(WithAny(AnyValue(1), AnyValue(ForAny(2)), AnyCollection().apply {
            add(1)
            add(ForAny(3))
        })) shouldBe """{
  "any1": 1,
  "any2": {
    "clazz": "codes.spectrum.serialization.json.JsonTest${'$'}ForAny",
    "value": {
      "i": 2
    }
  },
  "anyCollection": [
    1,
    {
      "clazz": "codes.spectrum.serialization.json.JsonTest${'$'}ForAny",
      "value": {
        "i": 3
      }
    }
  ]
}"""
    }

    "can read any"{
        val srcObj = WithAny(AnyValue(1), AnyValue(ForAny(2)), AnyCollection().apply {
            add(1)
            add(ForAny(3))
        })
        val json = Json.stringify(srcObj)
        val resultObj = Json.read<WithAny>(json)
        Json.isJsonEqual(srcObj, resultObj) shouldBe true
    }


    "can compare jsonelements"{
        //GSON from scratch can compare deep equals of JSON
        JsonObject().set("x", "1") shouldBe JsonObject().set("x", "1")
        JsonObject().set("x", "2") shouldNotBe JsonObject().set("x", "1")
        JsonObject().set("x", "1").set("y", "2") shouldBe JsonObject().set("y", "2").set("x", "1")
        JsonObject().set("x", "1").set("y", "2").toString() shouldNotBe JsonObject().set("y", "2").set("x", "1").toString()
        JsonObject().set("x", JsonArray().apply {
            add(1)
            add("dsds")
        }) shouldBe JsonObject().set("x", JsonArray().apply {
            add(1)
            add("dsds")
        })

        JsonObject().set("x", JsonArray().apply {
            add("dsds")
            add(1)
        }) shouldNotBe JsonObject().set("x", JsonArray().apply {
            add(1)
            add("dsds")
        })
    }


}) {

    class WithAny(
        var any1: AnyValue? = null,
        var any2: AnyValue? = null,
        var anyCollection: AnyCollection? = null
    )

    class ForAny(var i: Int = 1)

    class ExposeSample {
        var none: Int = 1
        @JsonLevel(ExposeLevel.MINIMAL)
        var min: Int = 2
        @JsonLevel(ExposeLevel.OPTIMIZED)
        var opt: Int = 3
        @JsonLevel(ExposeLevel.NORMAL)
        var norm: Int = 4
        @JsonLevel(ExposeLevel.DETAILED)
        var detail: Int = 5
        @JsonLevel(ExposeLevel.DEBUG)
        var debug: Int = 6
        @JsonLevel(ExposeLevel.IGNORABLE)
        var ignorable: Int = 7
        @Transient
        var transient: Int = 8

        @JsonLevel(ExposeLevel.OPTIMIZED)
        var payload: Any? = null
    }

    @JsonLevel(ExposeLevel.DEBUG)
    data class DebugClassExample(var x: Int = 12)

    @JsonLevel(ExposeLevel.MINIMAL)
    data class MinClassExample(
        @JsonLevel(ExposeLevel.MINIMAL)
        var y: Int = 13
    )

    data class ConvertTargetExample(
        var min: Int = 10,
        var specNorm: Int = 11
    )

    data class InterceptorsExample(
        var si: SerializeInterceptorExample = SerializeInterceptorExample(),
        var sp: SerializePostprocessExample = SerializePostprocessExample(),
        var di: DeSerializeInterceptorExample = DeSerializeInterceptorExample(),
        var dp: DeserializePostProcessExample = DeserializePostProcessExample()
    )

    data class SerializeInterceptorExample(var i: Int = 1) : IJsonSerializeInterceptor {
        override fun jsonInterceptSerialize(context: JsonSerializationContext, level: ExposeLevel): JsonElement {
            return JsonObject().set("_i_", i)
        }
    }

    data class DeSerializeInterceptorExample(var content: String = "") : IJsonDeserializeInterceptor {
        override fun jsonInterceptDeserialize(json: JsonElement, context: JsonDeserializationContext, level: ExposeLevel) {
            this.content = json.toString()
        }
    }

    data class SerializePostprocessExample(var i: Int = 1) : IJsonSerializePostprocess {
        override fun jsonPostprocessSerialize(json: JsonElement, level: ExposeLevel, context: JsonSerializationContext) {
            (json as JsonObject).set("_x_", i * 2)
        }
    }

    data class DeserializePostProcessExample(var i: Int = 1, var content: String = "") : IJsonDeserializePostprocess {
        override fun jsonDeserializePostProcess(json: JsonElement, level: ExposeLevel, context: JsonDeserializationContext) {
            content = json.toString()
        }

    }
}