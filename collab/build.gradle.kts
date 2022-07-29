plugins {
    kotlin("multiplatform")
}

group = "dev.fritz2"
version = "0.1-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
}

kotlin {
    js(IR) {
        browser()
    }.binaries.executable()

    sourceSets {
        val commonMain by getting {
        }
        val commonTest by getting {
        }
        val jsMain by getting {
            dependencies {
                implementation(project(":core"))
                implementation(npm("yjs", "13.5.38"))
            }
        }
        val jsTest by getting {
        }
    }
}