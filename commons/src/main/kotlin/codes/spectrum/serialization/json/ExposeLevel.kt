package codes.spectrum.serialization.json

import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes

/**
 * Уровени сериализации и десериализации
 */
enum class ExposeLevel(val intLevel: Int) {
    /**
     * Отмечает элементы которые в любом случае необходимы при сериализации
     * (используется по умолчанию для КЛАССОВ)
     */
    MINIMAL(5),
    /**
     * Отмечает элементы, который должны попасть в компактное оптимизированное отображение
     */
    OPTIMIZED(4),
    /**
     * Отмечает нормальный уровень отображения (основной)
     * (используется по умолчанию для ПОЛЕЙ), также
     * является дефолтным режимом для СЕРИАЛИЗАЦИИ
     */
    NORMAL(3),
    /**
     * Отмечает элементы для детального представления
     * (является дефолтным режимом при ДЕСЕРИАЛИЗАЦИИ)
     */
    DETAILED(2),

    /**
     * Отмечает элементы, которые сериализуются только для отладочных целей
     */
    DEBUG(1),
    /**
     * Отмечает элементы, которые по сути являются `Transient`, не
     * должны сериализоваться, но при этом БЕЗОПАСНЫ для отрисовки
     * этот уровень сериализации может потребоваться только для каких-то
     * особых сценариев тестирования, отладки или организации распределенной работы
     * с тем или иным объектом (для полного восстановления состояния)
     */
    IGNORABLE(0);

    companion object {
        /**
         * Уровень по умолчанию для сериализации - `NORMAL`
         */
        val DEFAULT_OUT = NORMAL
        /**
         * Уровень по умолчанию для десериализации - `DETAILED`
         */
        val DEFAULT_IN = DETAILED
        /**
         * В целом уровень по умолчанию - `NORMAL`
         */
        val DEFAULT = NORMAL
        /**
         * Уровень по умолчанию для классов - `MINIMAL`
         */
        val DEFAULT_CLASS = MINIMAL
        /**
         * Уровень по умолчанию для полей - `NORMAL`
         */
        val DEFAULT_FIELD = NORMAL
        /**
         * Псеводним для IGNORABLE - выражает, что все сериализуется
         */
        val ALL = IGNORABLE
    }

    private inner class ExposeExlusionStrategy : ExclusionStrategy {
        override fun shouldSkipClass(clazz: Class<*>?): Boolean {
            if (intLevel > 0) {
                val clazzLevel = (clazz!!.getAnnotation<JsonLevel>(JsonLevel::class.java)?.level?.intLevel)
                    ?: DEFAULT_CLASS.intLevel
                if (clazzLevel < intLevel) return true
            }
            return false
        }

        override fun shouldSkipField(f: FieldAttributes?): Boolean {
            if (intLevel > 0) {
                val clazzLevel = (f!!.getAnnotation<JsonLevel>(JsonLevel::class.java)?.level?.intLevel)
                    ?: DEFAULT_FIELD.intLevel
                if (clazzLevel < intLevel) return true
            }
            return false
        }

    }

    /**
     * Акцессор для стратегии пропуска классов и полей на основе текущего уровня
     * сериализации/десериализации
     */
    val strategy = ExposeExlusionStrategy() as ExclusionStrategy
}