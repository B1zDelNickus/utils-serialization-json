package codes.spectrum.serialization.json

class AnyMap(vararg pairs: Pair<String, Any?>) : HashMap<String, Any?>(pairs.toMap()), IAnyMarker