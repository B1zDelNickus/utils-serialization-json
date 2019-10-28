spectrumMultimodule("0.5") {
    //Устанавливаем проект :commons
    project(":commons").let {
        //что она - источник для публикации в maven
        it.publishMaven()
        //в качестве зависимости по умолчанию для всех остальных
    }
}
