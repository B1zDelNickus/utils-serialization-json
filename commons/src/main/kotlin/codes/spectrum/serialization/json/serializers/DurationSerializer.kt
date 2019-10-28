package codes.spectrum.serialization.json.serializers

import com.google.gson.*
import java.lang.reflect.Type
import java.time.Duration

class DurationSerializer : JsonSerializer<Duration>, JsonDeserializer<Duration> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Duration {
        if (null == json || json is JsonNull) return Duration.ofMillis(0)
        if (json is JsonPrimitive && json.isString) return Duration.parse(json.asString)
        if (json is JsonPrimitive && json.isNumber) return Duration.ofMillis(json.asNumber.toLong())
        throw Exception("Cannot deserialize ${json} as duration")
    }

    override fun serialize(src: Duration?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        if (null == src) return JsonNull.INSTANCE
        return JsonPrimitive(src.toString())
    }
}