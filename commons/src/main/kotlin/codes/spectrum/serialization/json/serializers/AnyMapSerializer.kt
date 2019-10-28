package codes.spectrum.serialization.json.serializers

import codes.spectrum.serialization.json.*
import com.google.gson.*
import java.lang.reflect.Type

class AnyMapSerializer(val level: ExposeLevel) : JsonSerializer<AnyMap?>, JsonDeserializer<AnyMap?> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): AnyMap? {
        val result = AnyMap()
        if (null == json || json is JsonNull) return AnyMap()
        val array = json as JsonObject
        for (e in array.entrySet()) {
            val value = e.value
            when {
                value is JsonPrimitive -> result.put(e.key, context!!.deserialize<AnyValue>(e.value, AnyValue::class.java)?.value)
                value is JsonNull -> result.put(e.key, context!!.deserialize<AnyValue>(e.value, AnyValue::class.java)?.value)
                value is JsonObject && value.has("clazz") && value.has("value") -> result.put(e.key, context!!.deserialize<AnyValue>(e.value, AnyValue::class.java)?.value)
                value is JsonArray -> result.put(e.key, context!!.deserialize<AnyCollection>(e.value, AnyCollection::class.java))
                value is JsonObject -> result.put(e.key, context!!.deserialize<AnyMap>(e.value, AnyMap::class.java))
            }

        }
        return result
    }

    override fun serialize(src: AnyMap?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        if (null == src || src.isEmpty()) return JsonNull.INSTANCE
        val result = JsonObject()
        for (e in src) {
            when {
                e.value is IAnyMarker -> {
                    result.add(e.key, context!!.serialize(e.value))
                }
                else -> result.add(e.key, context!!.serialize(AnyValue(e.value)))
            }
        }
        return result
    }
}