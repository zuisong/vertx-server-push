plugins {
  java
  application
  kotlin("jvm") version "1.7.10"
  id("com.github.johnrengelman.shadow") version "7.1.2"
}
repositories {
  mavenLocal()
  mavenCentral()
  maven("https://repo.maven.apache.org/maven2/")
}

dependencies {
  implementation(platform("io.vertx:vertx-dependencies:4.3.3"))
  implementation(enforcedPlatform("org.jetbrains.kotlin:kotlin-bom:1.7.10"))
  implementation(enforcedPlatform("com.fasterxml.jackson:jackson-bom:2.13.3"))

  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  implementation("com.fasterxml.jackson.core:jackson-databind")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("io.vertx:vertx-core")
  implementation("io.vertx:vertx-web")
  implementation("io.vertx:vertx-lang-kotlin-coroutines")
  implementation("io.vertx:vertx-stomp")
  implementation("io.github.microutils:kotlin-logging-jvm:2.1.23")
  implementation("ch.qos.logback:logback-classic:1.2.11")
}

group = "cn.mmooo"
version = "1.0.0"
description = "vertx-server-push"
java.sourceCompatibility = JavaVersion.VERSION_17

java {
  withSourcesJar()
}

tasks.withType<JavaCompile> {
  options.apply {
    encoding = Charsets.UTF_8.displayName()
    isDeprecation = true
  }
}

application {
  mainClass.set("cn.mmooo.MainKt")
}

