package codes.spectrum.serialization.jsonschema

import com.google.gson.annotations.SerializedName

class JsonSchema {
    @field:SerializedName("\$schema")
    var schema = "http://json-schema.org/draft-07/schema#"
    @field:SerializedName("\$id")
    var id = ""
    var type = "object"
    var version: String? = null
    var description: String? = null
    val properties = PropMap()
    val required: MutableList<String> = mutableListOf()
    var additionalProperties: Boolean? = null
}