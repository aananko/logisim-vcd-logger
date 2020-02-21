import java.net.URI

plugins {
    kotlin("jvm") version "1.3.61"
}

group = "io.github.aananko.logisim"
version = "0.1-SNAPSHOT"

val logisimVersion = "2.7.1"

repositories {
    mavenCentral()
    val sourceforge = ivy {
        metadataSources { artifact() }
        url = URI("https://sourceforge.net/")
        patternLayout {
            artifact("projects/circuit/files/[classifier]/[revision]/[module]-[revision].jar")
        }
    }
    exclusiveContent {
        forRepositories(sourceforge)
        filter {
            includeGroup("com.cburch.logisim")
        }
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.cburch.logisim:logisim-generic:${logisimVersion}:${
        logisimVersion.replaceAfterLast('.', "x")
    }")
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
        from(configurations.runtimeClasspath.get()
            .filter { it.name.startsWith("kotlin-")}
            .map { zipTree(it) }
        )
        manifest {
            attributes(
                "Library-Class" to "io.github.aananko.logisim.vcdlogger.Components"
            )
        }
    }

    for(taskName in listOf("runLogisim", "buildAndRunLogisim")) {
        register<JavaExec>(taskName) {
            val logisimJar: File =
                configurations.runtimeClasspath.get()
                    .find { file -> file.name.startsWith("logisim-generic-") }
                    ?: throw Exception("Can't find main logisim jar")

            classpath = files(logisimJar)
            args("src/test/circ/instantiate.circ")
            if (taskName.startsWith("build")) dependsOn(build)
        }
    }
}