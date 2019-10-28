package codes.spectrum.serialization.json.extensibility

import codes.spectrum.serialization.json.BasicGson
import codes.spectrum.serialization.json.ExposeLevel
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

/**
 * Полностью перехватыввает процесс своей сериализации (внутри GSON)
 */
interface IJsonSerializeInterceptor {
    fun jsonInterceptSerializeGson(level: ExposeLevel) = BasicGson
    fun jsonInterceptSerialize(context: JsonSerializationContext, level: ExposeLevel): JsonElement
    class Serializer(val level: ExposeLevel) : JsonSerializer<IJsonSerializeInterceptor> {
        override fun serialize(src: IJsonSerializeInterceptor?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
            if (null == src) return JsonNull.INSTANCE
            return src.jsonInterceptSerialize(context!!, level)
        }

    }
}