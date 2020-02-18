import fi.linuxbox.gradle.download.Download

plugins {
    kotlin("jvm") version "1.3.61"
    id("fi.linuxbox.download") version "0.6"
}

group = "io.github.aananko.logisim"
version = "0.1-SNAPSHOT"

val logisimVersion = "2.7.1"
val logisimJar = "logisim/logisim-generic-${logisimVersion}.jar"
val logisimUrl = "https://sourceforge.net/projects/circuit/files/${
        logisimVersion.replaceAfterLast('.', "x")
    }/${logisimVersion}/logisim-generic-${logisimVersion}.jar/download"

repositories {
    mavenCentral()
}

val downloadLogisim by tasks.register<Download>("downloadLogisim") {
    from(logisimUrl)
    to(logisimJar)
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(files(logisimJar) {
        builtBy(downloadLogisim)
    })
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    jar {
        from("src") {
            into("src")
        }
        manifest {
            attributes(
                "Library-Class" to "io.github.aananko.logisim.vcdlogger.Components"
            )
        }
    }
//    register<JavaExec>("runLogisim") {
//        classpath = files(logisimJar)
//    }
    register<Exec>("runLogisim") {
        commandLine("java", "-jar", logisimJar)
    }
}