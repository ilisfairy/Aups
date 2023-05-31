plugins {
    val kotlinVersion = "1.8.10"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    
    id("net.mamoe.mirai-console") version "2.14.0"
}

group = "moe.kusuri"
version = "0.0.1"

repositories {
    if (System.getenv("CI")?.toBoolean() != true) {
        maven("https://maven.aliyun.com/repository/public") // 阿里云国内代理仓库
    }
    mavenCentral()
}
dependencies{
    implementation("com.google.code.gson:gson:2.8.9")
    implementation(kotlin("stdlib-jdk8"))
}
