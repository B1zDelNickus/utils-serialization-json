package codes.spectrum.serialization.json.extensibility

import codes.spectrum.serialization.json.BasicGson
import codes.spectrum.serialization.json.ExposeLevel
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import java.lang.reflect.Type

/**
 * Полностью перехватыввает процесс своей десериализации (внутри GSON)
 */
interface IJsonDeserializeInterceptor {
    fun jsonInterceptDeSerializeGson(level: ExposeLevel) = BasicGson
    fun jsonInterceptDeserialize(json: JsonElement, context: JsonDeserializationContext, level: ExposeLevel)
    class Deserializer(val level: ExposeLevel) : JsonDeserializer<IJsonDeserializeInterceptor> {
        override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): IJsonDeserializeInterceptor {
            val instance = Class.forName(typeOfT!!.typeName).getConstructor().newInstance() as IJsonDeserializeInterceptor
            if (null == json || json is JsonNull) return instance
            instance.jsonInterceptDeserialize(json, context!!, level)
            return instance
        }
    }
}