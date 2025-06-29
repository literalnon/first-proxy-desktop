import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
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
