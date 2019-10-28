package codes.spectrum.serialization.json.serializers

import codes.spectrum.serialization.json.Json

data class ThrowableDescriptor(
    var type: String = "",
    override var message: String = "",
    val stack: MutableList<String> = mutableListOf(),
    override var cause: ThrowableDescriptor? = null
) : Throwable() {
    companion object {
        fun create(e: Throwable): ThrowableDescriptor {
            return Json.convert(e)
        }
    }
}