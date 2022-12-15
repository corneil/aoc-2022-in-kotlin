repositories {
    mavenCentral()
}

plugins {
    kotlin("jvm") version "1.7.22"
    application
}



dependencies {
    testImplementation(kotlin("test"))
}

tasks {
    test {
        useJUnitPlatform()
    }
    wrapper {
        gradleVersion = "7.6"
    }
}

val currentDay: String by project

application {
    mainClass.set("day${currentDay}.Day${currentDay}Kt")
}

defaultTasks("run")
