import fi.linuxbox.gradle.download.Download

plugins {
    kotlin("jvm") version "1.3.61"
    id("fi.linuxbox.download") version "0.6"
}

group = "io.github.aananko.logisim"
version = "0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks.register<Download>("downloadLogisim") {
    from("https://sourceforge.net/projects/circuit/files/2.7.x/2.7.1/logisim-generic-2.7.1.jar/download")
    to("logisim/logisim-generic-2.7.1.jar")
}
tasks.named("build") { dependsOn("downloadLogisim") }

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    compile(files("logisim/logisim-generic-2.7.1.jar"))
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}