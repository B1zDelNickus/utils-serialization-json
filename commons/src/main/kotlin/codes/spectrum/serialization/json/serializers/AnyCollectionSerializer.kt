package codes.spectrum.serialization.json.serializers

import codes.spectrum.serialization.json.*
import com.google.gson.*
import java.lang.reflect.Type

class AnyCollectionSerializer(val level: ExposeLevel) : JsonSerializer<AnyCollection?>, JsonDeserializer<AnyCollection?> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): AnyCollection? {
        val result = AnyCollection()
        if (null == json || json is JsonNull) return AnyCollection()
        val array = json as JsonArray
        for (value in array) {
            when {
                value is JsonPrimitive -> result.add(context!!.deserialize<AnyValue>(value, AnyValue::class.java)?.value)
                value is JsonNull -> result.add(context!!.deserialize<AnyValue>(value, AnyValue::class.java)?.value)
                value is JsonObject && value.has("clazz") && value.has("value") -> result.add(context!!.deserialize<AnyValue>(value, AnyValue::class.java)?.value)
                value is JsonArray -> result.add(context!!.deserialize<AnyCollection>(value, AnyCollection::class.java))
                value is JsonObject -> result.add(context!!.deserialize<AnyMap>(value, AnyMap::class.java))
            }
        }
        return result
    }

    override fun serialize(src: AnyCollection?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        if (null == src || src.isEmpty()) return JsonNull.INSTANCE
        val result = JsonArray()
        for (e in src) {
            if (e is IAnyMarker) {
                result.add(context!!.serialize(e))
            } else {
                result.add(context!!.serialize(AnyValue(e)))
            }
        }
        return result
    }
}

