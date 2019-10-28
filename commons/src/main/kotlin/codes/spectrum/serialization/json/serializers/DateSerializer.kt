package codes.spectrum.serialization.json.serializers

import com.google.gson.*
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

class DateSerializer(
    val out_format: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").apply {
        this.timeZone = TimeZone.getTimeZone("UTC")
    },
    val input_formats: List<SimpleDateFormat> = listOf(
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").apply {
            this.timeZone = TimeZone.getTimeZone("UTC")
        },
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").apply {
            this.timeZone = TimeZone.getTimeZone("UTC")
        },
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").apply {
            this.timeZone = TimeZone.getTimeZone("UTC")
        },
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").apply {
            this.timeZone = TimeZone.getTimeZone("UTC")
        },
        SimpleDateFormat("yyyyMMddHHmmss").apply {
            this.timeZone = TimeZone.getTimeZone("UTC")
        },
        SimpleDateFormat("dd.MM.yyyy HH:mm:ss").apply {
            this.timeZone = TimeZone.getTimeZone("UTC")
        },
        SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ").apply {
            this.timeZone = TimeZone.getTimeZone("UTC")
        },
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm").apply {
            this.timeZone = TimeZone.getTimeZone("UTC")
        },
        SimpleDateFormat("dd.MM.yyyy HH:mm").apply {
            this.timeZone = TimeZone.getTimeZone("UTC")
        },
        SimpleDateFormat("yyyy-MM-dd").apply {
            this.timeZone = TimeZone.getTimeZone("UTC")
        },
        SimpleDateFormat("dd.MM.yyyy").apply {
            this.timeZone = TimeZone.getTimeZone("UTC")
        }
    )
) : JsonSerializer<Date>, JsonDeserializer<Date> {
    override fun serialize(src: Date?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        if (null == src) return JsonNull.INSTANCE
        return JsonPrimitive(out_format.format(src!!))
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Date {
        if (null == json || json is JsonNull) return out_format.parse("1900-01-01T00:00:00.000Z")
        if (!json.isJsonPrimitive) throw Exception("Invalid JSON for date: ${json}")
        val primitive = json as JsonPrimitive
        if (primitive.isNumber) {
            return Date(primitive.asLong)
        }
        val str = primitive.asString
        for (f in input_formats) {
            try {
                return f.parse(str)
            } catch (e: Exception) {

            }
        }
        throw Exception("Invalid date format ${str}")
    }

}