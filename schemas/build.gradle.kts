repositories {
    maven("https://jitpack.io")
}
dependencies {
    compile("com.google.code.gson:gson:2.8.5")
    compile(kotlin("reflect"))
    testCompile("com.github.everit-org.json-schema:org.everit.json.schema:1.11.0")
}

publishMaven()