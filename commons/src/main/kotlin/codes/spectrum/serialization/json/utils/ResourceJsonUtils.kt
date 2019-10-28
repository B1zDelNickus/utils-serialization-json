package codes.spectrum.serialization.json.utils

import codes.spectrum.serialization.json.BasicGson
import java.io.File
import java.io.InputStreamReader
import java.net.JarURLConnection
import java.util.jar.JarEntry
import java.util.jar.JarFile


/**
 * Обертка над конфигурациями в ресурсах JAR в формате JSON
 * @param T - тип ресурса для сериализации из JSON
 */
class ResourceJsonRecord<T> private constructor(
    /**
     * Имя файла в котором обнаружен искомый ресурс
     */
    val fileName: String = "",
    /**
     * Имя ресурса
     */
    val entryName : String = "",
    /**
     * Исходная строка JSON
     */
    val sourceJson: String = "",
    /**
     * Десериализоав
     */
    val content:T
){
    companion object {
        fun <T> create(file: File, clz: Class<T>): ResourceJsonRecord<T> {
            val json = file.readText()
            val content = BasicGson.fromJson<T>(json, clz)
            return ResourceJsonRecord(file.canonicalPath, file.name, json, content)
        }

        fun <T> create(jar: JarFile, entry:JarEntry, clz:Class<T> ): ResourceJsonRecord<T> {
            val json = jar.getInputStream(entry).use {
                InputStreamReader(it).readText()
            }
            val content = BasicGson.fromJson<T>(json, clz)
            return ResourceJsonRecord(jar.name, entry.name, json, content)
        }
    }
}

/**
 * Утилита для поиска всех JSON ресрусов в отреферированных JAR и в локальной папке
 * с загрузкой списка ResourceJsonRecord указанного типа
 */
object ResourceJsonUtils {
    /**
     * Загрузить все JSON ресурсы указанного типа `T`
     * @param name - локальное имя файла в ресурсах или на диске, например `META-INF/some.json`
     * @param clz - класс целевого объекта, который десериализуется из ресурса
     * @param extDir - перекрытие директории по умолчанию для прогрузки локального ресурса с диска (`${FILE_EXTENSIONS_DIR:-./}`)
     */
    fun <T> readAll(name: String, clz: Class<T>, extDir: String? = null): List<ResourceJsonRecord<T>> {
        var result = mutableListOf<ResourceJsonRecord<T>>()
        val en = ResourceJsonUtils.javaClass.getClassLoader().getResources(name)
        if (en.hasMoreElements()) {
            val url = en.nextElement()
            val urlcon = url.openConnection() as JarURLConnection
            urlcon.getJarFile().use({ jar ->
                val entries = jar.entries()
                while (entries.hasMoreElements()) {
                    val entry = entries.nextElement()
                    if (entry.name == name) {
                        result.add(ResourceJsonRecord.create(jar, entry, clz))
                    }
                }
            })
        }
        val FILE_EXTENSIONS_DIR = if (extDir?.isNotBlank()
                ?: false) File(extDir) else File(System.getenv("FILE_EXTENSIONS_DIR") ?: "./")
        val RESOURCE_FULL_FILE = File(FILE_EXTENSIONS_DIR, name)
        if (RESOURCE_FULL_FILE.exists()) {
            result.add(ResourceJsonRecord.create(RESOURCE_FULL_FILE, clz))
        }
        return result
    }

    inline fun <reified T> readAll(name: String) = readAll(name, T::class.java)
}
