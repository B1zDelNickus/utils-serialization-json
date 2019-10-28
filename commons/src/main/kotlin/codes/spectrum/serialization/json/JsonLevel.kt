package codes.spectrum.serialization.json

/**
 * Устанавливает уровень отрисовки классов и полей
 * при сериализации и десериализации с использованием `Json`
 * @param level - минимальный уровень при котором сериализуется целевой класс или поле
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FIELD)
annotation class JsonLevel(val level: ExposeLevel)