package codes.spectrum.serialization.json.serializers

import com.google.gson.GsonBuilder
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class LocalDateSerializerTest : StringSpec({
    "default in out not null"{
        val gson = GsonBuilder().localDates().create()
        val value = LocalDatedNotNull()
        val json = gson.toJson(value)
        json shouldBe """{"date":"2012-08-04"}"""
        val output = gson.fromJson(json, LocalDatedNotNull::class.java)
        output shouldBe value
    }
    "default in out not from null"{
        val gson = GsonBuilder().localDates().create()
        val json = """{"date":null}"""
        val output = gson.fromJson(json, LocalDatedNotNull::class.java)
        output shouldBe LocalDatedNotNull(date = LocalDateSerializer.NullDate)
    }
    "default in out null"{
        val gson = GsonBuilder().localDates().create()
        val value = LocalDatedNull()
        val json = gson.toJson(value)
        json shouldBe """{}"""
        val output = gson.fromJson(json, LocalDatedNull::class.java)
        output shouldBe value
    }
    "default in out null from null"{
        val gson = GsonBuilder().localDates().create()
        val json = """{"date":null}"""
        val output = gson.fromJson(json, LocalDatedNull::class.java)
        output shouldBe LocalDatedNull(date = LocalDateSerializer.NullDate)
    }
    "custom input format"  {
        val json = """{"date":"04_08_2012"}"""
        val gson = GsonBuilder().localDates(input_formats =
        listOf(DateTimeFormatter.ofPattern("dd_MM_yyyy"))
        ).create()
        val output = gson.fromJson(json, LocalDatedNotNull::class.java)
        output shouldBe LocalDatedNotNull()
    }
    "custom ouput format"  {
        val gson = GsonBuilder().localDates(out_format =
        DateTimeFormatter.ofPattern("dd_MM_yyyy")
        ).create()
        val json = gson.toJson(LocalDatedNotNull())
        json shouldBe """{"date":"04_08_2012"}"""
    }
}) {
    data class LocalDatedNotNull(
        var date: LocalDate = LocalDate.of(2012, 8, 4)
    )

    data class LocalDatedNull(
        var date: LocalDate? = null
    )
}