package codes.spectrum.serialization.jsonschema

import com.google.gson.*
import java.io.File
import java.lang.reflect.Type
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

class JsonSchemaGenerator {


    fun build(clz: Class<*>): JsonSchema {
        val result = JsonSchema()
        build(clz, result)
        return result
    }

    fun generate(clz: Class<*>): String {
        val result = build(clz)
        return gson.toJson(result)
    }


    private fun build(clz: Class<*>, result: JsonSchema) {
        val clzSchemaPath = clz.name.replace("^codes\\.spectrum\\.(schemas\\.)?".toRegex(), "")
            .replace(".", "/")
            .toLowerCase() + ".json"
        result.id = "https://spectrum.codes/schemas/$clzSchemaPath"
        clz.kotlin.findAnnotation<JsonSchemaId>()?.let {
            result.id = it.id
        }
        clz.kotlin.findAnnotation<JsonSchemaVersion>()?.let {
            result.version = it.version
        }
        clz.kotlin.findAnnotation<JsonSchemaDescription>()?.let {
            result.description = it.description.replace("""[\r\n]+""".toRegex(), " ")
        }
        clz.kotlin.findAnnotation<JsonSchemaAdditionProperties>()?.let {
            result.additionalProperties = it.value
        }
        buildProperties(clz, result)
    }

    private fun buildProperties(clz: Class<*>, result: JsonSchema) {
        for (p in clz.kotlin.memberProperties) {
            val property = JsonProperty(p)
            if (!property.ignore) {
                result.properties[p.name] = property
                if (p.findAnnotation<JsonSchemaRequired>() != null) {
                    result.required.add(p.name)
                }
            }
        }


    }

    inline fun <reified T> generate() = generate(T::class.java)
    inline fun <reified T> write(file: File) = file.writeText(generate(T::class.java))
    inline fun write(cls: KClass<*>, file: File) = file.writeText(generate(cls.java))

    companion object {
        val propadapter = object : JsonSerializer<JsonProperty> {
            override fun serialize(src: JsonProperty?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
                var tree = defaultGson.toJsonTree(src) as JsonObject
                if (tree.has("types")) {
                    val types = tree.get("types")
                    tree.remove("types")
                    tree.add("type", types)
                }
                src?.items?.ref?.let {
                    if (it.isNotBlank()) {
                        val items = tree.get("items") as JsonObject
                        items.remove("type")
                        items.remove("ref")
                        items.add("\$ref", JsonPrimitive(it))
                    }
                }
                src?.ref?.let {
                    tree.remove("type")
                    tree.remove("ref")
                    tree.add("\$ref", JsonPrimitive(it))
                }
                return tree
            }
        }
        val gson = GsonBuilder().setPrettyPrinting()
            .registerTypeAdapter(JsonProperty::class.java, propadapter)
            .registerTypeAdapter(PropMap::class.java, object : JsonSerializer<PropMap> {
                override fun serialize(src: PropMap?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
                    val obj = JsonObject()
                    val entries = src!!.entries.sortedBy { it.value.index }
                    for (e in entries) {
                        obj.add(e.key, propadapter.serialize(e.value, null, null))
                    }
                    return obj
                }
            })

            .create()
        val Instance = JsonSchemaGenerator()
        private val defaultGson = GsonBuilder().create()
        fun build(clz: Class<*>) = Instance.build(clz)
        inline fun <reified T> build() = build(T::class.java)
    }
}