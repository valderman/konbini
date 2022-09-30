import org.jetbrains.kotlin.util.capitalizeDecapitalize.toLowerCaseAsciiOnly
import java.nio.file.Paths
import kotlin.io.path.inputStream

plugins {
    kotlin("multiplatform") version "1.7.20"
    id("org.jetbrains.dokka") version "1.7.10"
    id("org.jlleitschuh.gradle.ktlint") version "11.0.0"
    id("com.github.ben-manes.versions") version "0.42.0"
    id("org.jetbrains.kotlinx.kover") version "0.6.0"
    `maven-publish`
}

group = "cc.ekblad"
version = "0.1.3"

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

data class DependencyVersion(val module: String, val version: String)

val excludedVersions: Set<Pair<String, String>> = setOf(
    // Ktlint >=0.46 doesn't work with the ktlint plugin.
    "ktlint" to "0.46.0",
    "ktlint" to "0.46.1",
    "ktlint" to "0.47.0",
    "ktlint" to "0.47.1",
    // Forcing this build-time dependency is a bit messy
    "intellij-coverage-agent" to "1.0.681",
    "intellij-coverage-reporter" to "1.0.681",
    "intellij-coverage-agent" to "1.0.682",
    "intellij-coverage-reporter" to "1.0.682",
)

ktlint {
    version.set("0.45.2")
}

val nonProductionSuffixes = listOf("beta", "rc")
fun notProductionVersion(version: String): Boolean =
    nonProductionSuffixes.any { version.toLowerCaseAsciiOnly().endsWith(it) }

tasks {
    val test by getting

    dependencyUpdates {
        rejectVersionIf {
            (candidate.module to candidate.version) in excludedVersions || notProductionVersion(candidate.version)
        }
    }

    val dependencyUpdateSentinel = register<DependencyUpdateSentinel>("dependencyUpdateSentinel") {
        dependsOn(dependencyUpdates)
    }

    test.apply {
        finalizedBy(koverReport)
    }

    kover {
        verify {
            onCheck.set(true)
            rule {
                bound {
                    minValue = 80
                    counter = kotlinx.kover.api.CounterType.BRANCH
                    valueType = kotlinx.kover.api.VerificationValueType.COVERED_PERCENTAGE
                }

                bound {
                    minValue = 80
                    counter = kotlinx.kover.api.CounterType.LINE
                    valueType = kotlinx.kover.api.VerificationValueType.COVERED_PERCENTAGE
                }
            }
        }
    }

    build {
        dependsOn("javadocJar")
    }

    check {
        dependsOn(test)
        dependsOn(ktlintCheck)
        dependsOn(dependencyUpdateSentinel)
    }
}

abstract class DependencyUpdateSentinel : DefaultTask() {
    @kotlin.io.path.ExperimentalPathApi
    @org.gradle.api.tasks.TaskAction
    fun check() {
        val updateIndicator = "The following dependencies have later milestone versions:"
        Paths.get("build", "dependencyUpdates", "report.txt").inputStream().bufferedReader().use { reader ->
            if (reader.lines().anyMatch { it == updateIndicator }) {
                throw GradleException("Dependency updates are available.")
            }
        }
    }
}
