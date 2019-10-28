package codes.spectrum.serialization.json.serializers

import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*


class LocalDateSerializer(
    val out_format: DateTimeFormatter = DefaultOutputFormat,
    val input_formats: List<DateTimeFormatter> = DefaultInputFormats,
    val defaultValue: LocalDate? = null
) : TypeAdapter<LocalDate?>() {
    val six_digit_format: DateTimeFormatter = DateTimeFormatter.ofPattern("yyMMdd")
    val eight_digit_format: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
    override fun write(out: JsonWriter, value: LocalDate?) {
        if (null == value) {
            out.nullValue()
        } else {
            out.value(value.format(out_format))
        }
    }

    val date_convert_format = SimpleDateFormat("yyyy-MM-dd")
    override fun read(input: JsonReader): LocalDate? {
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
                    return LocalDate.parse(value, f)
                } catch (e: Exception) {

                }
            }
            throw DateTimeParseException("Invalid date format", value, 0)
        }
        if (token == JsonToken.NUMBER) {
            val value = input.nextDouble().toLong()
            if (value.toString().length == 8) {
                return LocalDate.parse(value.toString(), eight_digit_format)
            }
            if (value.toString().length == 6) {
                return LocalDate.parse(value.toString(), six_digit_format)
            }
            return LocalDate.parse(date_convert_format.format(Date(value)))
        }
        if (token == JsonToken.BEGIN_OBJECT) {
            input.beginObject()
            var year = NullDate.year
            var month = 1
            var day = 1
            while (input.peek() == JsonToken.NAME) {
                val src_name = input.nextName()
                val name = src_name.toLowerCase().replace("_", "")
                if (name == "year" || name == "month" || name == "day") {
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
                        throw DateTimeParseException("Invalid type for field ${name}", subtoken.toString(), 0)
                    }
                    val intval = value.toIntOrNull()
                    if (null == intval) {
                        throw DateTimeParseException("Invalid type for field ${name}", value, 0)
                    }
                    when (name) {
                        "year" -> year = intval
                        "month" -> month = intval
                        "day" -> month = intval
                    }
                } else {
                    input.skipValue()
                }
            }
            input.endObject()
            return LocalDate.of(year, month, day)
        }
        throw DateTimeParseException("Invalid type for LocalDate", token.toString(), 0)
    }

    companion object {
        val NullDate = LocalDate.of(1800, 1, 1)
        val DefaultOutputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val DefaultInputFormats = listOf(
            "yyyy-MM-dd",
            "dd.MM.yyyy",
            "dd.MM.yy",
            "MM/dd/yyyy",
            "MM/dd/yy",
            "yyyyMMdd",
            "yyMMdd"
        ).map { DateTimeFormatter.ofPattern(it) }
        val Iso = LocalDateSerializer()
        val IsoNotNull = LocalDateSerializer(defaultValue = NullDate)
        val Rus = LocalDateSerializer(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        val RusNotNull = LocalDateSerializer(DateTimeFormatter.ofPattern("dd.MM.yyyy"), defaultValue = NullDate)
    }
}


fun GsonBuilder.localDates(adapter: LocalDateSerializer? = LocalDateSerializer.IsoNotNull): GsonBuilder {
    this.registerTypeAdapter(LocalDate::class.java, adapter)
    return this
}


fun GsonBuilder.localDates(out_format: DateTimeFormatter? = null,
                           input_formats: List<DateTimeFormatter>? = null,
                           defaultValue: LocalDate? = null,
                           nullable: Boolean? = null): GsonBuilder {
    return this.localDates(
        LocalDateSerializer(
            out_format
                ?: LocalDateSerializer.DefaultOutputFormat,
            input_formats
                ?: LocalDateSerializer.DefaultInputFormats,
            defaultValue
                ?: (if (nullable == false) LocalDateSerializer.NullDate else null)
        )
    )
}

