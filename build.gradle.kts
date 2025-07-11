import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    kotlin("plugin.serialization") version "1.9.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.example"
version = "1.0-SNAPSHOT"

//compose { kotlinCompilerPlugin.set("1.5.7") }

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    implementation(compose.desktop.currentOs)

    implementation("net.lightbody.bmp:browsermob-core:2.1.5")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
}

compose.desktop {
//    compose {
//        kotlinCompilerPlugin.set("androidx.compose.compiler:compiler:1.5.10")
//    }
    application {
        mainClass = "NewMainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "test-compose-desktop"
            packageVersion = "1.0.0"
        }
    }
}

tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    manifest {
        // Optionally, set the main class for the shadowed JAR.
        attributes["Main-Class"] = "NewMainKt"
    }
}
