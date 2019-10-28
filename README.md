# Библиотека для серелиазации и десериализации JSON

## Быстрый старт

### Зависимость

```kotlin
dependencies{
   implementation("codes.spectrum.serialization-json:spectrum-serialization-json-commons:0.5-dev-SNAPSHOT")
}
```

### Простая сериализация
```kotlin
import codes.spectrum.serialization.json.Json
class Hello(var hello : String = "world")
//serialization
val jsonString = Json.stringify(Hello()) 
/*
{
  "hello": "world"
}
 */
 ```
 
### Простая десериализация
```kotlin
val hello = Json.read<Hello>(jsonString)
```


Очень значимый набор ссылок

[полезные статьи](https://futurestud.io/tutorials/gson-builder-relax-gson-with-lenient)