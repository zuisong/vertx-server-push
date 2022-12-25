import org.jetbrains.kotlin.gradle.tasks.*

plugins {
  java
  application
  kotlin("jvm") version "1.7.20"
  id("com.github.johnrengelman.shadow") version "7.1.2"
}
repositories {
  mavenLocal()
  mavenCentral()
  maven("https://repo.maven.apache.org/maven2/")
}

dependencies {
  implementation(platform("io.vertx:vertx-dependencies:4.3.7"))
  implementation(enforcedPlatform("org.jetbrains.kotlin:kotlin-bom:1.7.20"))
  implementation(enforcedPlatform("com.fasterxml.jackson:jackson-bom:2.14.1"))

  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  implementation("com.fasterxml.jackson.core:jackson-databind")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("io.vertx:vertx-core")
  implementation("io.vertx:vertx-web")
  implementation("io.vertx:vertx-lang-kotlin-coroutines")
  implementation("io.vertx:vertx-stomp")
  implementation("io.github.microutils:kotlin-logging:3.0.4")
  implementation("ch.qos.logback:logback-classic:1.4.5")
  implementation("net.logstash.logback:logstash-logback-encoder:7.2")
}

group = "cn.mmooo"
version = "1.0.0"
description = "vertx-server-push"

val javaVersion = JavaVersion.VERSION_17
java {
  sourceCompatibility = javaVersion
  targetCompatibility = javaVersion
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


tasks.withType<KotlinCompile> {
  kotlinOptions.useK2 = true
  kotlinOptions.jvmTarget = javaVersion.toString()
}
