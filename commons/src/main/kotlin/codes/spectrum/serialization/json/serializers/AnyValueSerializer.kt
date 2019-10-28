package codes.spectrum.serialization.json.serializers

import codes.spectrum.serialization.json.AnyValue
import codes.spectrum.serialization.json.ExposeLevel
import codes.spectrum.serialization.json.set
import com.google.gson.*
import java.lang.reflect.Type

class AnyValueSerializer(val level: ExposeLevel) : JsonSerializer<AnyValue?>, JsonDeserializer<AnyValue?> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): AnyValue? {
        if (null == json || json is JsonNull) return AnyValue()
        if (json is JsonPrimitive) {
            if (json.isNumber) return AnyValue(json.asInt)
            if (json.isString) return AnyValue(json.asString)
            if (json.isBoolean) return AnyValue(json.asBoolean)
        }
        val obj = json as JsonObject
        if (!obj.has("value") || obj.get("value") == null || obj.get("value").isJsonNull) {
            return AnyValue()
        }
        val clazz = Class.forName(obj.getAsJsonPrimitive("clazz").asString)
        return AnyValue(context!!.deserialize(obj.get("value"), clazz))
    }

    override fun serialize(src: AnyValue?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        if (null == src || null == src.value) return JsonNull.INSTANCE
        if (src.value is String) return JsonPrimitive(src.value as String)
        if (src.value is Boolean) return JsonPrimitive(src.value as Boolean)
        if (src.value is Int) return JsonPrimitive(src.value as Int)
        return JsonObject()
            .set("clazz", src.value!!.javaClass.name)
            .set("value", context!!.serialize(src.value))
    }


}