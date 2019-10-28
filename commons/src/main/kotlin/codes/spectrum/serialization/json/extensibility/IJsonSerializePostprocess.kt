package codes.spectrum.serialization.json.extensibility

import codes.spectrum.serialization.json.BasicGson
import codes.spectrum.serialization.json.ExposeLevel
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

/**
 * Применяется к уже построенному JsonElement после сериализации (внутри GSON)
 */
interface IJsonSerializePostprocess {
    fun jsonPostprocessSerializeGson(level: ExposeLevel) = BasicGson
    fun jsonPostprocessSerialize(json: JsonElement, level: ExposeLevel, context: JsonSerializationContext)
    class Serializer(val level: ExposeLevel) : JsonSerializer<IJsonSerializePostprocess> {
        override fun serialize(src: IJsonSerializePostprocess?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
            if (null == src) return JsonNull.INSTANCE
            val json = src.jsonPostprocessSerializeGson(level).toJsonTree(src)
            src.jsonPostprocessSerialize(json, level, context!!)
            return json
        }

    }
}