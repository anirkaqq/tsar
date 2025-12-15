plugins {
    application
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

val javafxVersion = "21.0.3"
val platform = "win"

dependencies {
    /* ===== JavaFX ===== */
    implementation("org.openjfx:javafx-base:$javafxVersion:$platform")
    implementation("org.openjfx:javafx-graphics:$javafxVersion:$platform")
    implementation("org.openjfx:javafx-controls:$javafxVersion:$platform")

    /* ===== LOGGING (следующий этап) ===== */
    implementation("org.apache.logging.log4j:log4j-api:2.24.3")
    implementation("org.apache.logging.log4j:log4j-core:2.24.3")

    implementation("org.apache.logging.log4j:log4j-api:2.23.1")
    implementation("org.apache.logging.log4j:log4j-core:2.23.1")

}

application {
    mainClass.set("com.tsarskiy.Main")
}

/* ===== RUN (JavaFX) ===== */
tasks.named<JavaExec>("run") {
    jvmArgs = listOf(
        "--module-path", classpath.asPath,
        "--add-modules", "javafx.controls,javafx.graphics"
    )
}



/* ===== JAR BUILD ===== */
tasks.jar {
    archiveFileName.set("tsar.jar")

    manifest {
        attributes(
            "Main-Class" to application.mainClass.get()
        )
    }
}
