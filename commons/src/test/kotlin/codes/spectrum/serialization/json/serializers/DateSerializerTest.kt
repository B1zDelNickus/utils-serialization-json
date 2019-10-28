package codes.spectrum.serialization.json.serializers

import codes.spectrum.serialization.json.Json
import io.kotlintest.matchers.string.shouldStartWith
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import java.text.SimpleDateFormat
import java.util.*

class DateSerializerTest : StringSpec({
    val utcformat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").apply {
        this.timeZone = TimeZone.getTimeZone("UTC")
    }
    "serialize"{
        Json.jsonify(utcformat.parse("2014-01-15T16:10:12.786+0500")).asString shouldBe """2014-01-15T11:10:12.786+0000"""
    }
    for (d in arrayOf(
        "2014-01-15T11:10:12.786+0000",
        "2014-01-15T11:10:12.786",
        "2014-01-15T11:10:12+0000",
        "2014-01-15T11:10:12",
        "2014-01-15T11:10+0000",
        "2014-01-15T11:10",
        "2014-01-15",
        "2019-06-19T07:18:43"
    ).map { "\"${it}\"" }) {
        "deserialize ${d}"{
            val date = Json.read<Date>(d)
            utcformat.format(date) shouldStartWith d.drop(1).split("+")[0].dropLast(1)
        }
    }
})