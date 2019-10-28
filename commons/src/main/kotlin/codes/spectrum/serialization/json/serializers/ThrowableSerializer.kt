package codes.spectrum.serialization.json.serializers


import codes.spectrum.serialization.json.set
import com.google.gson.*
import java.lang.reflect.Type


class ThrowableSerializer(val stackSize: Int = DefaultStackSize) : JsonSerializer<Throwable>, JsonDeserializer<Throwable> {
    private val InternalGson = GsonBuilder().registerTypeAdapter(ThrowableDescriptor::class.java, object : JsonDeserializer<ThrowableDescriptor> {
        override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): ThrowableDescriptor {
            if (null == json || !(json is JsonObject)) return ThrowableDescriptor(message = json?.toString() ?: "null")
            val result = ThrowableDescriptor()
            if (json.has("type")) {
                result.type = json.getAsJsonPrimitive("type").asString
            }
            if (json.has("message")) {
                result.message = json.getAsJsonPrimitive("message").asString
            }
            if (json.has("stack")) {
                result.stack.addAll(context!!.deserialize<List<String>>(json.getAsJsonArray("stack"), List::class.java))
            }
            if (json.has("cause")) {
                result.cause = context!!.deserialize<ThrowableDescriptor>(json.getAsJsonObject("cause"), ThrowableDescriptor::class.java)
            }
            return result
        }

    }).create()

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Throwable {
        if (null == json || json is JsonNull) return ThrowableDescriptor()
        return InternalGson.fromJson<ThrowableDescriptor>(json, ThrowableDescriptor::class.java)
    }

    override fun serialize(src: Throwable?, typeOfSrc: Type?, context: JsonSerializationContext): JsonElement {
        if (null == src) return JsonNull.INSTANCE
        val result = JsonObject()

        result["type"] = if (src is ThrowableDescriptor) src.type else src.javaClass.name
        result["message"] = src.message ?: "No message"
        src.cause?.let {
            result["cause"] = context.serialize(it)
        }
        if (src is ThrowableDescriptor) {
            result["stack"] = context.serialize(src.stack)
        } else {
            if (stackSize > 0) {
                var stack = JsonArray()
                var wassystem = false
                for (i in src.stackTrace) {
                    if (stack.size() >= stackSize) break
                    if (i.isNativeMethod) continue
                    if (!(i.className.startsWith("codes.spectrum"))) {
                        if (wassystem) continue
                        wassystem = true
                    } else {
                        wassystem = false
                    }
                    stack.add(JsonPrimitive("${i.className}.${i.methodName} at ${i.fileName}:${i.lineNumber}"))
                }
                result["stack"] = stack
            }
        }
        return result
    }

    companion object {
        val DefaultStackSize = 5
        val Instance = ThrowableSerializer()
    }
}

fun GsonBuilder.throwables(adapter: ThrowableSerializer? = ThrowableSerializer.Instance): GsonBuilder {
    this.registerTypeHierarchyAdapter(Throwable::class.java, adapter)
    return this
}

fun GsonBuilder.throwables(stackSize: Int): GsonBuilder {
    this.throwables(ThrowableSerializer(stackSize))
    return this
}