package codes.spectrum.serialization.json

import java.util.*


class AnyCollection(vararg items: Any?) : ArrayList<Any?>(items.toList()), IAnyMarker

