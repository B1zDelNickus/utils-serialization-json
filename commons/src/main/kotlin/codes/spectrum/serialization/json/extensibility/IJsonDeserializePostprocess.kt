package codes.spectrum.serialization.json.extensibility

import codes.spectrum.serialization.json.BasicGson
import codes.spectrum.serialization.json.ExposeLevel
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import java.lang.reflect.Type


/**
 * Применяется к уже созданному объекту после десериализации
 */
interface IJsonDeserializePostprocess {
    fun jsonDeserializePostProcessGson(level: ExposeLevel) = BasicGson
    fun jsonDeserializePostProcess(json: JsonElement, level: ExposeLevel, context: JsonDeserializationContext)
    class Deserializer(val level: ExposeLevel) : JsonDeserializer<IJsonDeserializePostprocess> {
        override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): IJsonDeserializePostprocess {
            val instance = Class.forName(typeOfT!!.typeName).getConstructor().newInstance() as IJsonDeserializePostprocess
            if (null == json || json is JsonNull) return instance
            val refinedInstance = instance.jsonDeserializePostProcessGson(level).fromJson<IJsonDeserializePostprocess>(json, instance.javaClass)
            refinedInstance.jsonDeserializePostProcess(json, level, context!!)
            return refinedInstance
        }
    }
}