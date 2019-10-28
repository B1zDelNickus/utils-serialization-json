package codes.spectrum.serialization.json.serializers

import codes.spectrum.serialization.json.Json
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec


internal class ThrowableSerializerTest : StringSpec({
    val native_error = Exception("x", Exception("y"))
    val check_descriptor = ThrowableDescriptor.create(native_error)

    "can serialize"{
        Json.stringify(WithError(native_error)).replace("jdk.internal.reflect.","").replace("sun.reflect.","") shouldBe """{
  "e": {
    "type": "java.lang.Exception",
    "message": "x",
    "cause": {
      "type": "java.lang.Exception",
      "message": "y",
      "stack": [
        "codes.spectrum.serialization.json.serializers.ThrowableSerializerTest${'$'}1.invoke at ThrowableSerializerTest.kt:9",
        "codes.spectrum.serialization.json.serializers.ThrowableSerializerTest${'$'}1.invoke at ThrowableSerializerTest.kt:8",
        "io.kotlintest.specs.AbstractStringSpec.<init> at AbstractStringSpec.kt:21",
        "codes.spectrum.serialization.json.serializers.ThrowableSerializerTest.<init> at ThrowableSerializerTest.kt:8",
        "jdk.internal.reflect.NativeConstructorAccessorImpl.newInstance at NativeConstructorAccessorImpl.java:62"
      ]
    },
    "stack": [
      "codes.spectrum.serialization.json.serializers.ThrowableSerializerTest${'$'}1.invoke at ThrowableSerializerTest.kt:9",
      "codes.spectrum.serialization.json.serializers.ThrowableSerializerTest${'$'}1.invoke at ThrowableSerializerTest.kt:8",
      "io.kotlintest.specs.AbstractStringSpec.<init> at AbstractStringSpec.kt:21",
      "codes.spectrum.serialization.json.serializers.ThrowableSerializerTest.<init> at ThrowableSerializerTest.kt:8",
      "jdk.internal.reflect.NativeConstructorAccessorImpl.newInstance at NativeConstructorAccessorImpl.java:62"
    ]
  }
}""".replace("jdk.internal.reflect.","").replace("sun.reflect.","")
    }


    "can serialize desc"{
        Json.stringify(WithError(native_error)) shouldBe Json.stringify(WithError(check_descriptor))
    }

    "can deserialize"{
        Json.read<WithError>(Json.stringify(WithError(native_error))) shouldBe WithError(check_descriptor)
    }
}) {
    data class WithError(var e: Throwable? = null)
}