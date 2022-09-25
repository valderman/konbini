plugins {
    kotlin("multiplatform") version "1.7.10"
    `maven-publish`
}

group = "cc.ekblad"
version = "0.1"

repositories {
    mavenCentral()
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    js(BOTH) {
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
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
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting
        val jvmTest by getting
        val jsMain by getting
        val jsTest by getting
        val nativeMain by getting
        val nativeTest by getting
    }
}

publishing {
    publications {
        create<MavenPublication>("konbini") {
            groupId = "cc.ekblad"
            artifactId = "konbini"
            version = project.version.toString()
            from(components["kotlin"])
            pom {
                name.set("konbini")
                description.set("Lightweight parser combinator library")
                url.set("https://github.com/valderman/konbini")
                licenses {
                    license {
                        name.set("MIT")
                        url.set("https://github.com/valderman/konbini/blob/main/LICENSE")
                    }
                }
                developers {
                    developer {
                        id.set("valderman")
                        name.set("Anton Ekblad")
                        email.set("anton@ekblad.cc")
                    }
                }
            }
        }
    }
}
