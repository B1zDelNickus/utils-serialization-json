package codes.spectrum.serialization.json.serializers

import com.google.gson.GsonBuilder
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LocalDateTimeSerializerTest : StringSpec({
    "default in out not null"{
        val gson = GsonBuilder().localDateTimes().create()
        val value = LocalDatedNotNull()
        val json = gson.toJson(value)
        json shouldBe """{"date":"2012-08-04T11:12:23"}"""
        val output = gson.fromJson(json, LocalDatedNotNull::class.java)
        output shouldBe value
    }
    "default in out not from null"{
        val gson = GsonBuilder().localDateTimes().create()
        val json = """{"date":null}"""
        val output = gson.fromJson(json, LocalDatedNotNull::class.java)
        output shouldBe LocalDatedNotNull(date = LocalDateTimeSerializer.NullDate)
    }
    "default in out null"{
        val gson = GsonBuilder().localDateTimes().create()
        val value = LocalDatedNull()
        val json = gson.toJson(value)
        json shouldBe """{}"""
        val output = gson.fromJson(json, LocalDatedNull::class.java)
        output shouldBe value
    }
    "default in out null from null"{
        val gson = GsonBuilder().localDateTimes().create()
        val json = """{"date":null}"""
        val output = gson.fromJson(json, LocalDatedNull::class.java)
        output shouldBe LocalDatedNull(date = LocalDateTimeSerializer.NullDate)
    }
    "custom input format"  {
        val json = """{"date":"04_08_2012_11_12_23"}"""
        val gson = GsonBuilder().localDateTimes(input_formats =
        listOf(DateTimeFormatter.ofPattern("dd_MM_yyyy_HH_mm_ss"))
        ).create()
        val output = gson.fromJson(json, LocalDatedNotNull::class.java)
        output shouldBe LocalDatedNotNull()
    }
    "custom ouput format"  {
        val gson = GsonBuilder().localDateTimes(out_format =
        DateTimeFormatter.ofPattern("dd_MM_yyyy_HH_mm_ss")
        ).create()
        val json = gson.toJson(LocalDatedNotNull())
        json shouldBe """{"date":"04_08_2012_11_12_23"}"""
    }
}) {
    data class LocalDatedNotNull(
        var date: LocalDateTime = LocalDateTime.of(2012, 8, 4, 11, 12, 23)
    )

    data class LocalDatedNull(
        var date: LocalDateTime? = null
    )
}