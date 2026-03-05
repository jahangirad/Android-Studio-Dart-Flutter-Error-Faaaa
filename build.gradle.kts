import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.17.0"
    kotlin("jvm") version "1.9.23"
}

group = "org.faaaa"
version = "1.0.0"

repositories {
    mavenCentral()
    google()
    maven { url = uri("https://www.jetbrains.com/intellij-repository/releases") }
    maven { url = uri("https://cache-redirector.jetbrains.com/maven.pkg.jetbrains.space/public/p/intellij/intellij-dependencies") }
}

dependencies {
    implementation("javazoom:jlayer:1.0.1")
    // Removed explicit testImplementation for test-framework, relying on the IntelliJ plugin
    testImplementation(kotlin("test"))
}

// The 'runIde' task will now launch IntelliJ IDEA.
// This is necessary to make the 'test' task work.
intellij {
    type.set("IC") // Use IntelliJ Community for testing
    version.set("2023.1.1")
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {
        // This still ensures your plugin is marked as compatible with Android Studio
        sinceBuild.set("231")
        untilBuild.set("253.*")
    }

    buildSearchableOptions {
        enabled = false
    }

    jarSearchableOptions {
        enabled = false
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }
}
