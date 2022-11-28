plugins {
    val kotlinVersion = "1.7.21"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    
    id("net.mamoe.mirai-console") version "2.14.0-dev-shadow-11"
}

group = "moe.nyadoki"
version = "0.0.1"

repositories {
    maven("https://maven.aliyun.com/repository/public")
    mavenCentral()
}
dependencies{
    implementation("com.google.code.gson:gson:2.10")
    implementation(kotlin("stdlib-jdk8"))
}
