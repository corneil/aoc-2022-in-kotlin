plugins {
    kotlin("jvm") version "1.7.22"
    application
}

repositories {
    mavenCentral()
}

tasks {

    wrapper {
        gradleVersion = "7.6"
    }
}

val currentDay: String by project

application {
    mainClass.set("Day${currentDay}Kt")
}

defaultTasks("run")
