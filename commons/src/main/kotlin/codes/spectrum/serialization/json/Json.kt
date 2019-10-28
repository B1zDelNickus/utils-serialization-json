package codes.spectrum.serialization.json

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.io.Reader

object Json {
    /**
     * Преобразовать любой объект в строку JSON
     * @param obj - объект любого типа
     * @param format - использовать форматирование при выводе строки
     * @param level - минимальный уровень `ExposeLevel` классов и полей для сериализации, по умолчанию `ExposeLevel.DEFAULT_OUT`
     */
    fun stringify(obj: Any, format: Boolean = true, level: ExposeLevel = ExposeLevel.DEFAULT_OUT): String = resolveGson(format, level).toJson(obj)

    /**
     * Преобразовать любой объект в `JsonElement`
     * @param obj - объект любого типа
     * @param level - минимальный уровень `ExposeLevel` классов и полей для сериализации, по умолчанию `ExposeLevel.DEFAULT_OUT`
     */
    fun jsonify(obj: Any, level: ExposeLevel = ExposeLevel.DEFAULT_OUT): JsonElement = resolveGson(false, level).toJsonTree(obj)

    /**
     * Записать объект в формате JSON в целвой `Appendable` (например Writer)
     * @param obj - объект любого типа
     * @param format - использовать форматирование при выводе строки
     * @param level - минимальный уровень `ExposeLevel` классов и полей для сериализации, по умолчанию `ExposeLevel.DEFAULT_OUT`
     */
    fun write(obj: Any, output: Appendable, level: ExposeLevel = ExposeLevel.DEFAULT_OUT, format: Boolean = true) = resolveGson(format, level).toJson(obj, output)

    /**
     * Создать объект указанного класса `T` из строки JSON
     * @param json - строка, содержащая JSON
     * @param clazz - тип возвращаемого объекта
     * @param level - уровень полей и классов, обновляемых при десериализации
     */
    fun <T> read(json: String, clazz: Class<T>, level: ExposeLevel = ExposeLevel.DEFAULT_IN): T = resolveGson(false, level).fromJson(json, clazz)

    /**
     * Создать объект указанного класса `T` из строки JSON
     * @param json - строка, содержащая JSON
     * @param level - уровень полей и классов, обновляемых при десериализации
     */
    inline fun <reified T> read(json: String, level: ExposeLevel = ExposeLevel.DEFAULT_IN): T = read(json, T::class.java, level)


    /**
     * Создать `JsonElement` из строки JSON
     * @param json - строка, содержащая JSON
     * @param level - уровень полей и классов, обновляемых при десериализации
     */
    fun read(json: String, level: ExposeLevel = ExposeLevel.DEFAULT_IN): JsonElement = read(json, JsonElement::class.java, level)


    /**
     * Создать объект указанного класса `T` из строки JSON с промежуточным преобразованием JSONElement
     * @param json - строка, содержащая JSON
     * @param clazz - тип возвращаемого объекта
     * @param level - уровень полей и классов, обновляемых при десериализации
     * @param modify - преобразователь JsonElement -> JsonElement для подмены JSON в ходе десериализации
     */
    fun <T> read(json: String, clazz: Class<T>, level: ExposeLevel = ExposeLevel.DEFAULT_IN, modify: (JsonElement) -> JsonElement): T {
        val jsonobj = read(json, JsonElement::class.java, level)
        val modified_json = modify(jsonobj)
        return read<T>(modified_json, clazz, level)
    }

    /**
     * Создать объект указанного класса `T` из строки JSON с промежуточным преобразованием JSONElement
     * @param json - строка, содержащая JSON
     * @param level - уровень полей и классов, обновляемых при десериализации
     * @param modify - преобразователь JsonElement -> JsonElement для подмены JSON в ходе десериализации
     */
    inline fun <reified T> read(json: String, level: ExposeLevel = ExposeLevel.DEFAULT_IN, noinline modify: (JsonElement) -> JsonElement): T = read(json, T::class.java, level, modify)

    /**
     * Создать объект указанного класса `T` из `Reader`
     * @param json - `Reader` читающий строку JSON, содержащая JSON
     * @param clazz - тип возвращаемого объекта
     * @param level - уровень полей и классов, обновляемых при десериализации
     */
    fun <T> read(json: Reader, clazz: Class<T>, level: ExposeLevel = ExposeLevel.DEFAULT_IN): T = resolveGson(false, level).fromJson(json, clazz)

    /**
     * Создать объект указанного класса `T` из `Reader`
     * @param json - `Reader` читающий строку JSON, содержащая JSON
     * @param level - уровень полей и классов, обновляемых при десериализации
     */
    inline fun <reified T> read(json: Reader, level: ExposeLevel = ExposeLevel.DEFAULT_IN): T = read(json, T::class.java, level)

    /**
     * Создать объект указанного класса `T` из `Reader` с промежуточным преобразованием JSONElement
     * @param json - `Reader`, содержащий JSON
     * @param clazz - тип возвращаемого объекта
     * @param level - уровень полей и классов, обновляемых при десериализации
     * @param modify - преобразователь JsonElement -> JsonElement для подмены JSON в ходе десериализации
     */
    fun <T> read(json: Reader, clazz: Class<T>, level: ExposeLevel = ExposeLevel.DEFAULT_IN, modify: (JsonElement) -> JsonElement): T {
        val jsonobj = read(json, JsonElement::class.java, level)
        val modified_json = modify(jsonobj)
        return read<T>(modified_json, clazz, level)
    }

    /**
     * Создать объект указанного класса `T` из `Reader` с промежуточным преобразованием JSONElement
     * @param json - строка, содержащая JSON
     * @param level - уровень полей и классов, обновляемых при десериализации
     * @param modify - преобразователь JsonElement -> JsonElement для подмены JSON в ходе десериализации
     */
    inline fun <reified T> read(json: Reader, level: ExposeLevel = ExposeLevel.DEFAULT_IN, noinline modify: (JsonElement) -> JsonElement): T = read(json, T::class.java, level, modify)

    /**
     * Создать объект указанного класса `T` из `JsonElement`
     * @param json - `JsonElement` в качестве источника преобразования
     * @param clazz - тип возвращаемого объекта
     * @param level - уровень полей и классов, обновляемых при десериализации
     */
    fun <T> read(json: JsonElement, clazz: Class<T>, level: ExposeLevel = ExposeLevel.DEFAULT_IN): T = resolveGson(false, level).fromJson(json, clazz)

    /**
     * Создать объект указанного класса `T` из `JsonElement`
     * @param json - `JsonElement` в качестве источника преобразования
     * @param level - уровень полей и классов, обновляемых при десериализации
     */
    inline fun <reified T> read(json: JsonElement, level: ExposeLevel = ExposeLevel.DEFAULT_IN): T = read(json, T::class.java, level)


    /**
     * Преобразовать входной объект в объект класса `T` с промежуточным преобразованием в JSON
     * @param src - исходный объект
     * @param targetClass - класс целевого объекта
     * @param level - уровень классов и полей при сереализации и десериализации
     */
    fun <T> convert(src: Any, targetClass: Class<T>, level: ExposeLevel = ExposeLevel.DEFAULT_IN): T = read(stringify(src), targetClass, level)

    /**
     * Преобразовать входной объект в объект класса `T` с промежуточным преобразованием в JSON
     * @param src - исходный объект
     * @param level - уровень классов и полей при сереализации и десериализации
     */
    inline fun <reified T> convert(src: Any, level: ExposeLevel = ExposeLevel.DEFAULT_IN): T = convert(src, T::class.java, level)

    /**
     * Преобразовать входной объект типа `IN` в объект класса `OUT` с промежуточным преобразованием в JSON и его модификацией
     * @param src - исходный объект
     * @param targetClass - класс целевого объекта
     * @param level - уровень классов и полей при сереализации и десериализации
     * @param modify - функция преобразрования JsonElement в альтернативный JsonElement
     */
    fun <IN : Any, OUT> convert(src: IN, targetClass: Class<OUT>, level: ExposeLevel = ExposeLevel.DEFAULT_IN, modify: (src: IN, json: JsonElement) -> JsonElement): OUT {
        val raw_json = jsonify(src, level)
        val modified_json = modify(src, raw_json)
        return read(modified_json, targetClass, level)
    }

    /**
     * Преобразовать входной объект типа `IN` в объект класса `OUT` с промежуточным преобразованием в JSON и его модификацией
     * @param src - исходный объект
     * @param level - уровень классов и полей при сереализации и десериализации
     * @param modify - функция преобразрования JsonElement в альтернативный JsonElement
     */
    inline fun <IN : Any, reified OUT> convert(src: IN, level: ExposeLevel = ExposeLevel.DEFAULT_IN, noinline modify: (src: IN, json: JsonElement) -> JsonElement): OUT =
        convert(src, OUT::class.java, level, modify)

    /**
     * Преобразовать входной объект в объект класса `OUT` с промежуточным преобразованием в JSON с уточнением JsonElement
     * @param src - исходный объект
     * @param level - уровень классов и полей при сереализации и десериализации
     * @param modify - функция обновления промежуточного JsonElement
     */
    inline fun <reified OUT> convert(src: Any, level: ExposeLevel = ExposeLevel.DEFAULT_IN, noinline modify: (json: JsonObject) -> Unit): OUT =
        convert(src, OUT::class.java, level) { src: Any, json: JsonElement -> modify(json as JsonObject);json }

    private val gsons = sequence {
        for (v in ExposeLevel.values()) {
            yield("${v}_true" to GsonBuilder().spectrumDefaults(format = true, level = v).create())
            yield("${v}_false" to GsonBuilder().spectrumDefaults(format = false, level = v).create())
        }
    }.toMap()

    /**
     * Сравнивает эквивалентность сериализованных структур JSON
     */
    fun isJsonEqual(first: Any, second: Any, level: ExposeLevel = ExposeLevel.DEFAULT): Boolean {
        return jsonify(first, level = level) == jsonify(second, level = level)
    }

    private fun resolveGson(format: Boolean, level: ExposeLevel) = gsons["${level}_${format.toString().toLowerCase()}"]!!
}