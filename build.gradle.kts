import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    kotlin("multiplatform") version "1.4.30"
    `maven-publish`
}

group = "com.santojon"
version = "3.0.0-alpha-1"

val local = Properties()
val localProperties: File = rootProject.file("local.properties")
if (localProperties.exists()) localProperties.inputStream().use { local.load(it) }

repositories {
    mavenCentral()
    maven("https://dl.bintray.com/badoo/maven")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
    }
    js {
        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                    webpackConfig.cssSupport.enabled = true
                }
            }
        }
    }
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("com.badoo.reaktive:reaktive:1.1.20")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }
        val jsMain by getting
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
        val nativeMain by getting
        val nativeTest by getting
    }
}

publishing {
    publications {
        val kotlinMultiplatform by getting {
            repositories {
                maven {
                    credentials {
                        username = local.getProperty("bintray.user")
                        password = local.getProperty("bintray.apikey")
                    }
                    url = uri(
                        "https://api.bintray.com/maven/santojon/Eventer/Eventer-MP"
                    )
                }
            }
        }
    }
}