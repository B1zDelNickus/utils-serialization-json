package codes.spectrum.serialization.jsonschema

import java.time.LocalDate
import java.util.*
import kotlin.reflect.KProperty
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.javaGetter

class JsonProperty(@Transient val prop: KProperty<*>) {
    var title: String? = null
    var description: String? = null
    var type: String? = null
    var types: List<String>? = null
    var default: String? = null
    var pattern: String? = null
    var enum: List<String>? = null
    var const: String? = null
    var update: String? = null
    var format: String? = null
    var minItems: Int? = null
    var maxItems: Int? = null
    var minimum: Int? = null
    var exclusiveMinimum: Int? = null
    var maximum: Int? = null
    var exclusiveMaximum: Int? = null
    var ref: String? = null
    var available_values: List<String>? = null
    var uniqueItems: Boolean? = null
    @Transient
    var ignore: Boolean = false

    class Items {
        var type: String = ""
        var ref: String? = null
    }

    var items: Items? = null
    @Transient
    var index: Int = 100

    init {
        val proptype = prop.javaGetter!!.returnType
        type = when {
            proptype == String::class.java -> "string"
            proptype.isEnum -> "string"
            proptype == Int::class.java -> "integer"
            proptype == Integer::class.java -> "integer"
            proptype == Long::class.java -> "integer"
            proptype == java.lang.Long::class.java -> "integer"
            proptype == Double::class.java -> "number"
            proptype == java.lang.Double::class.java -> "number"
            proptype == Float::class.java -> "number"
            proptype == java.lang.Float::class.java -> "number"
            proptype == LocalDate::class.java -> "string"
            proptype == Boolean::class.java -> "boolean"
            proptype == java.lang.Boolean::class.java -> "boolean"
            proptype.interfaces.contains(List::class.java) -> "array"
            proptype == List::class.java -> "array"
            proptype.interfaces.contains(MutableList::class.java) -> "array"
            proptype.isArray -> "array"
            else -> "object"
        }
        prop.findAnnotation<JsonTypes>()?.let {
            type = null
            types = it.types.toList()
        }
        prop.findAnnotation<JsonSchemaPattern>()?.let {
            pattern = it.pattern
        }
        prop.findAnnotation<JsonSchemaDescription>()?.let {
            description = it.description.replace("""[\r\n]+""".toRegex(), " ")
        }
        prop.findAnnotation<JsonSchemaTitle>()?.let {
            title = it.title.replace("""[\r\n]+""".toRegex(), " ")
        }
        prop.findAnnotation<JsonSchemaDefault>()?.let {
            default = it.default
        }
        prop.findAnnotation<JsonSchemaUpdateVersion>()?.let {
            update = it.version
        }
        prop.findAnnotation<JsonSchemaUniqueItems>()?.let {
            uniqueItems = it.value
        }
        prop.findAnnotation<JsonSchemaIndex>()?.let {
            index = it.index
        }
        prop.findAnnotation<JsonSchemaConst>()?.let {
            const = it.value
        }
        prop.findAnnotation<JsonSchemaIgnore>()?.let {
            ignore = true
        }
        if (proptype.isEnum && null == const) {
            enum = proptype.enumConstants.map { it.toString() }.toList()
            if (null == default) {
                default = proptype.enumConstants.first().toString()
            }
        }
        prop.findAnnotation<JsonSchemaItemType>()?.let {
            types = null
            type = "array"
            items = Items().apply { type = it.type }
        }
        prop.findAnnotation<JsonSchemaTypeRef>()?.let {
            types = null
            type = null
            ref = it.path
        }
        prop.findAnnotation<JsonSchemaItemTypeRef>()?.let {
            types = null
            type = "array"
            items = Items().apply { ref = it.path }
        }
        prop.findAnnotation<JsonSchemaLegacyAvailableValues>()?.let {
            available_values = it.values.toList()
        }
        prop.findAnnotation<JsonSchemaNumberConstraints>()?.let {
            minimum = if (it.minimum > Int.MIN_VALUE) it.minimum else null
            maximum = if (it.maximum > Int.MIN_VALUE) it.maximum else null
            exclusiveMinimum = if (it.exclusiveMinimum > Int.MIN_VALUE) it.exclusiveMinimum else null
            exclusiveMaximum = if (it.exclusiveMaximum > Int.MIN_VALUE) it.exclusiveMaximum else null
        }
        if (type == "array") {
            prop.findAnnotation<JsonSchemaMinItems>()?.let {
                minItems = it.min
            }
            prop.findAnnotation<JsonSchemaMaxItems>()?.let {
                maxItems = it.max
            }
        }
        format = when (proptype) {
            LocalDate::class.java -> "date"
            Date::class.java -> "date-time"
            else -> format
        }
    }
}