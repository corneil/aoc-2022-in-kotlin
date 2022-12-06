plugins {
    kotlin("jvm") version "1.7.22"
    application
}

repositories {
    mavenCentral()
}

tasks {
    sourceSets {
        main {
            java.srcDirs("src")
        }
    }

    wrapper {
        gradleVersion = "7.6"
    }
}

val currentDay: String by project

application {
    mainClass.set("Day${currentDay}Kt")
}

defaultTasks("run")
