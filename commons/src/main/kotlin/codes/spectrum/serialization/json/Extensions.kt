package codes.spectrum.serialization.json

import com.google.gson.*


fun JsonObject.getOrNull(name: String): JsonElement? = if (this.has(name)) this[name] else null
fun <T> JsonObject.getOrNull(name: String, clazz: Class<T>): T? {
    val element = getOrNull(name)
    if (null == element || element is JsonNull) return null
    return when (clazz) {
        JsonElement::class.java -> element as T
        JsonObject::class.java -> element as T
        JsonArray::class.java -> element as T
        String::class.java -> when (element) {
            is JsonPrimitive -> element.asString as T
            else -> element.toString() as T
        }
        Int::class.java -> when (element) {
            is JsonPrimitive -> element.asInt as T
            else -> element.toString().toInt() as T
        }
        Boolean::class.java -> when (element) {
            is JsonPrimitive -> element.asBoolean as T
            else -> element.toString().toBoolean() as T
        }
        else -> Json.read(element, clazz)
    }
}

inline fun <reified T> JsonObject.getOrNull(name: String) = this.getOrNull(name, T::class.java)
inline fun <reified T> JsonObject.get(name: String, default: () -> T) = this.getOrNull(name, T::class.java) ?: default()
inline fun <reified T> JsonObject.get(name: String) = this.getOrNull(name, T::class.java)!!

/**
 * Упрощает установку поля в JsonObject
 */
operator fun JsonObject.set(name: String, value: JsonElement): JsonObject = this.apply { add(name, value) }

fun JsonObject.setDefault(name: String, value: JsonElement): JsonObject = this.apply {
    if (!this.has(name)) {
        add(name, value)
    }
}


fun JsonObject.setDefault(name: String, value: () -> Any): JsonObject = this.apply {
    if (!this.has(name)) {
        val _value = value()
        when (_value) {
            is JsonElement -> setDefault(name, _value)
            is String -> setDefault(name, _value)
            is Int -> setDefault(name, _value)
            is Long -> setDefault(name, _value)
            is Boolean -> setDefault(name, _value)
            else -> setDefault(name, Json.jsonify(_value))
        }
    }
}

/**
 * Упрощает установку поля в JsonObject
 */
operator fun JsonObject.set(name: String, value: String?): JsonObject = this.apply { add(name, JsonPrimitive(value)) }

fun JsonObject.setDefault(name: String, value: String?): JsonObject = this.apply {
    if (!this.has(name)) {
        add(name, JsonPrimitive(value))
    }
}


/**
 * Упрощает установку поля в JsonObject
 */
operator fun JsonObject.set(name: String, value: Int?): JsonObject = this.apply { add(name, JsonPrimitive(value)) }

fun JsonObject.setDefault(name: String, value: Int?): JsonObject = this.apply {
    if (!this.has(name)) {
        add(name, JsonPrimitive(value))
    }
}
/**
 * Упрощает установку поля в JsonObject
 */
operator fun JsonObject.set(name: String, value: Long?): JsonObject = this.apply { add(name, JsonPrimitive(value)) }

fun JsonObject.setDefault(name: String, value: Long?): JsonObject = this.apply {
    if (!this.has(name)) {
        add(name, JsonPrimitive(value))
    }
}
/**
 * Упрощает установку поля в JsonObject
 */
operator fun JsonObject.set(name: String, value: Boolean?): JsonObject = this.apply { add(name, JsonPrimitive(value)) }

fun JsonObject.setDefault(name: String, value: Boolean?): JsonObject = this.apply {
    if (!this.has(name)) {
        add(name, JsonPrimitive(value))
    }
}





/**
 * Специальный GSON, не включающий в себя расширений, который может использоваться
 * в качестве прокси-генератора JSONElement в составе своих расширений
 */
val BasicGson = GsonBuilder().spectrumDefaults(installExtensions = false, format = false).create()


/**
 * Инетрфейс для реализации собственных расширений для загрузки в Gson
 */
interface IGsonExtension {
    /**
     * Устанавливает расщирение в GSON
     * @param builder - целевой билдер для настройки
     * @param format - контекстный признак использования форматирования
     * @param level - контекстный уровень отрисовки
     */
    fun install(builder: GsonBuilder, format: Boolean, level: ExposeLevel)
}
