package codes.spectrum.serialization.json.serializers

import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*

class LocalDateTimeSerializer(
    val out_format: DateTimeFormatter = DefaultOutputFormat,
    val input_formats: List<DateTimeFormatter> = DefaultInputFormats,
    val defaultValue: LocalDateTime? = null
) : TypeAdapter<LocalDateTime?>() {
    val six_digit_format: DateTimeFormatter = DateTimeFormatter.ofPattern("yyMMdd")
    val eight_digit_format: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    override fun write(out: JsonWriter, value: LocalDateTime?) {
        if (null == value) {
            out.nullValue()
        } else {
            out.value(value.format(out_format))
        }
    }

    val date_convert_format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    override fun read(input: JsonReader): LocalDateTime? {
        val token = input.peek()
        if (token == JsonToken.NULL) {
            input.skipValue()
            return defaultValue
        }
        if (token == JsonToken.STRING) {
            val value = input.nextString()
            if (value.isBlank()) return defaultValue
            for (f in input_formats) {
                try {
                    return LocalDateTime.parse(value, f)
                } catch (e: Exception) {

                }
            }
            throw DateTimeParseException("Invalid date format", value, 0)
        }
        if (token == JsonToken.NUMBER) {
            val value = input.nextDouble().toLong()
            if (value.toString().length == 8) {
                return LocalDateTime.parse(value.toString(), eight_digit_format)
            }
            if (value.toString().length == 6) {
                return LocalDateTime.parse(value.toString(), six_digit_format)
            }
            return LocalDateTime.parse(date_convert_format.format(Date(value)))
        }
        if (token == JsonToken.BEGIN_OBJECT) {
            input.beginObject()
            var date: LocalDate = LocalDate.MIN
            var hour = 0
            var minute = 0
            var second = 0
            while (input.peek() == JsonToken.NAME) {
                val src_name = input.nextName()
                val name = src_name.toLowerCase().replace("_", "")
                if (name == "hour" || name == "minute" || name == "second" || name == "date" || name == "time") {
                    val subtoken = input.peek()
                    var value = ""
                    if (subtoken == JsonToken.NUMBER) {
                        value = input.nextInt().toString()
                    } else if (subtoken == JsonToken.STRING) {
                        value = input.nextString()
                    } else if (subtoken == JsonToken.NULL) {
                        value = ""
                        input.skipValue()
                    } else {
                        continue
                    }
                    when (name) {
                        "date" -> date = LocalDate.parse(value)
                        "hour" -> hour = value.toInt()
                        "minute" -> minute = value.toInt()
                        "second" -> second = value.toInt()
                    }
                } else {
                    input.skipValue()
                }
            }
            input.endObject()
            return LocalDateTime.of(date.year, date.month, date.dayOfMonth, hour, minute, second)
        }
        throw DateTimeParseException("Invalid type for LocalDateTime", token.toString(), 0)
    }

    companion object {
        val NullDate = LocalDateTime.of(1800, 1, 1, 0, 0, 0)
        val DefaultOutputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val DefaultInputFormats = listOf(
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd'T'HH:mm",
            "yyyy-MM-dd",
            "dd.MM.yyyy HH:mm:ss",
            "dd.MM.yyyy HH:mm",
            "dd.MM.yyyy",
            "dd.MM.yy HH:mm:ss",
            "dd.MM.yy HH:mm",
            "dd.MM.yy",
            "MM/dd/yyyy HH:mm:ss",
            "MM/dd/yyyy HH:mm",
            "MM/dd/yyyy",
            "MM/dd/yy HH:mm:ss",
            "MM/dd/yy HH:mm",
            "MM/dd/yy",
            "yyyyMMddHHmmss",
            "yyyyMMddHHmmss",
            "yyMMddHHmmss",
            "yyyyMMdd",
            "yyMMdd"
        ).map { DateTimeFormatter.ofPattern(it) }
        val Iso = LocalDateTimeSerializer()
        val IsoNotNull = LocalDateTimeSerializer(defaultValue = NullDate)
        val Rus = LocalDateTimeSerializer(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))
        val RusNotNull = LocalDateTimeSerializer(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"), defaultValue = NullDate)
    }
}


fun GsonBuilder.localDateTimes(adapter: LocalDateTimeSerializer? = LocalDateTimeSerializer.IsoNotNull): GsonBuilder {
    this.registerTypeAdapter(LocalDateTime::class.java, adapter)
    return this
}


fun GsonBuilder.localDateTimes(out_format: DateTimeFormatter? = null,
                               input_formats: List<DateTimeFormatter>? = null,
                               defaultValue: LocalDateTime? = null,
                               nullable: Boolean? = null): GsonBuilder {
    return this.localDateTimes(
        LocalDateTimeSerializer(
            out_format
                ?: LocalDateTimeSerializer.DefaultOutputFormat,
            input_formats
                ?: LocalDateTimeSerializer.DefaultInputFormats,
            defaultValue
                ?: (if (nullable == false) LocalDateTimeSerializer.NullDate else null)
        )
    )
}