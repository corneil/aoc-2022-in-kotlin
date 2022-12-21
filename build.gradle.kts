repositories {
    mavenCentral()
    maven("https://repo.kotlin.link")
}

plugins {
    kotlin("jvm") version "1.7.22"
    application
}



dependencies {
    implementation("space.kscience:kmath-polynomial:0.3.1-dev-5")
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of("17"))
    }
}

tasks {
    test {
        useJUnitPlatform()
    }
    wrapper {
        gradleVersion = "7.6"
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xcontext-receivers"
    }
}
val currentDay: String by project

application {
    mainClass.set("day${currentDay}.Day${currentDay}Kt")
}

defaultTasks("run")
