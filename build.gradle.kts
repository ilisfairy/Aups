plugins {
    val kotlinVersion = "1.7.0"
    kotlin("jvm") version kotlinVersion
    id("net.mamoe.mirai-console") version "2.12.1"
}

group = "moe.naynna"
version = "0.0.1"

repositories {
    mavenLocal()
    mavenCentral()
}
dependencies{
    implementation("com.google.code.gson:gson:2.9.1")
}
