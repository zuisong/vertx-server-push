import org.jetbrains.kotlin.gradle.tasks.*

plugins {
  java
  application
  kotlin("jvm") version "1.9.10"
  id("com.github.johnrengelman.shadow") version "8.1.1"
  id("org.graalvm.buildtools.native") version "0.9.27"
}
repositories {
  mavenLocal()
  mavenCentral()
  maven("https://repo.maven.apache.org/maven2/")
}

dependencies {
  implementation(platform("io.vertx:vertx-dependencies:4.4.5"))
  implementation(enforcedPlatform("org.jetbrains.kotlin:kotlin-bom:1.9.10"))
  implementation(enforcedPlatform("com.fasterxml.jackson:jackson-bom:2.15.2"))

  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  implementation("com.fasterxml.jackson.core:jackson-databind")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("io.vertx:vertx-core")
  implementation("io.vertx:vertx-web")
  implementation("io.vertx:vertx-lang-kotlin-coroutines")
  implementation("io.vertx:vertx-stomp")
  implementation("io.github.microutils:kotlin-logging:3.0.5")
  implementation("ch.qos.logback:logback-classic:1.4.5")
//  implementation("net.logstash.logback:logstash-logback-encoder:7.3")
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
  kotlinOptions.jvmTarget = javaVersion.toString()
}

graalvmNative {
  binaries.all {
    resources.autodetect()
  }
  toolchainDetection.set(true)
}
