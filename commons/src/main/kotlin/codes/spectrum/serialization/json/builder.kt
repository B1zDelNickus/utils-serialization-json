package codes.spectrum.serialization.json

import codes.spectrum.serialization.json.extensibility.IJsonDeserializeInterceptor
import codes.spectrum.serialization.json.extensibility.IJsonDeserializePostprocess
import codes.spectrum.serialization.json.extensibility.IJsonSerializeInterceptor
import codes.spectrum.serialization.json.extensibility.IJsonSerializePostprocess
import codes.spectrum.serialization.json.serializers.*
import codes.spectrum.serialization.json.utils.ResourceJsonUtils
import com.google.gson.GsonBuilder
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.*


private class GsonExtensionDefinitionFile {
    val extensions = mutableListOf<String>()
}

/**
 * Расширение, устанавливающее в целевой GsonBuilder штатные настройки, принятые в Spectrum
 * @param installExtensions - автоматический сканинг и загрузка расширений из всех jar на classPath
 * @param format - использовать форматирование при сериализации
 * @param level - уровень сериализации и десериализации (для `JsonLevel`)
 * @param body - возможность добавить свои дополнительные настройки
 */
fun GsonBuilder.spectrumDefaults(
    installExtensions: Boolean = true,
    format: Boolean = true,
    level: ExposeLevel = ExposeLevel.DEFAULT,
    body: GsonBuilder.() -> Unit = {}
): GsonBuilder {

    fun installExtensionsReferencedInJars(gsonBuilder: GsonBuilder, format: Boolean, level: ExposeLevel) {
        val logger = LoggerFactory.getLogger("gson.initialization")
        try {
            logger.info("Start load GSON extensions")
            val extensions = ResourceJsonUtils.readAll<GsonExtensionDefinitionFile>("META-INF/gson.json")
            for (e in extensions) {
                logger.debug("Apply GSON extensions from ${e.fileName}/${e.entryName}")
                with(e.content) {
                    for (clzname in this.extensions) {
                        logger.trace("Start load class with name: ${clzname}")
                        val clz = Class.forName(clzname)
                        logger.trace("Start create instance of ${clzname}")
                        val instance = clz.getConstructor().newInstance()
                        logger.trace("Try use instance of ${clzname} as ${IGsonExtension::class.simpleName}")
                        (instance as IGsonExtension).install(gsonBuilder, format, level)
                        logger.debug("GSON extension of type ${clzname} successfully installed")
                    }
                }
            }
            logger.info("Finish load GSON extensions")
        } catch (e: Throwable) {
            logger.error("${e.javaClass} ${e.message}")
            throw e
        }
    }

    if (format) {
        setPrettyPrinting()
    }
    setLenient()
    disableHtmlEscaping()


    registerTypeAdapter(Date::class.java, DateSerializer())
    localDates()
    localDateTimes()
    registerTypeAdapter(Duration::class.java, DurationSerializer())

    throwables()


    if (installExtensions) {
        installExtensionsReferencedInJars(this, format, level)
        registerTypeHierarchyAdapter(IJsonSerializeInterceptor::class.java, IJsonSerializeInterceptor.Serializer(level))
        registerTypeHierarchyAdapter(IJsonDeserializeInterceptor::class.java, IJsonDeserializeInterceptor.Deserializer(level))
        registerTypeHierarchyAdapter(IJsonSerializePostprocess::class.java, IJsonSerializePostprocess.Serializer(level))
        registerTypeHierarchyAdapter(IJsonDeserializePostprocess::class.java, IJsonDeserializePostprocess.Deserializer(level))

    }
    registerTypeAdapter(AnyValue::class.java, AnyValueSerializer(level))
    registerTypeAdapter(AnyCollection::class.java, AnyCollectionSerializer(level))
    registerTypeAdapter(AnyMap::class.java, AnyMapSerializer(level))

    addDeserializationExclusionStrategy(level.strategy)
    addSerializationExclusionStrategy(level.strategy)


    body()
    return this
}