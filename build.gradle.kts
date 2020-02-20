import fi.linuxbox.gradle.download.Download

plugins {
    kotlin("jvm") version "1.3.61"
    id("fi.linuxbox.download") version "0.6"
}

group = "io.github.aananko.logisim"
version = "0.1-SNAPSHOT"

val logisimVersion = "2.7.1"
val logisimJar = File("${buildDir}/logisim/logisim-generic-${logisimVersion}.jar")
val logisimUrl = "https://sourceforge.net/projects/circuit/files/${
        logisimVersion.replaceAfterLast('.', "x")
    }/${logisimVersion}/logisim-generic-${logisimVersion}.jar/download"

repositories {
    mavenCentral()
}

// The downloadLogisim task checks if logisim jar have changed at the remote server every build.
// To avoid this, we need to disable the task if logisimUrl have not changed.
// But Download class overrides its outputs.upToDateWhen to true
// and hence we need a separate task for the check.
val checkIfLogisimUrlHaveChanged by tasks.register("checkIfLogisimUrlHaveChanged") {
    inputs.property("logisimUrl", logisimUrl)
    // every task needs some output, or it might be disabled by gradle
    val outputFileName = "${buildDir}/${this.name}_dummy_output"
    outputs.files(outputFileName)
    doLast {
        File(outputFileName).writeText("")
    }
}

val downloadLogisim by tasks.register<Download>("downloadLogisim") {
    dependsOn(checkIfLogisimUrlHaveChanged)
    onlyIf{
        !checkIfLogisimUrlHaveChanged.state.upToDate
        || !logisimJar.exists()
    }
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
        from(configurations.implementationDependenciesMetadata.get()
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
            classpath = files(logisimJar)
            args("src/test/circ/instantiate.circ")
            if (taskName.startsWith("build")) dependsOn(build)
        }
    }
}