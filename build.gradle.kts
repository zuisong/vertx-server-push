plugins {
  java
  application
  kotlin("jvm") version "1.6.21"
  id("com.github.johnrengelman.shadow") version "7.1.2"
  `maven-publish`
}
repositories {
  mavenLocal()
  mavenCentral()
  maven("https://repo.maven.apache.org/maven2/")
}

dependencies {
  implementation(platform("io.vertx:vertx-dependencies:4.2.7"))
  implementation(enforcedPlatform("org.jetbrains.kotlin:kotlin-bom:1.6.21"))
  implementation(enforcedPlatform("com.fasterxml.jackson:jackson-bom:2.13.2"))

  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  implementation("com.fasterxml.jackson.core:jackson-databind")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("io.vertx:vertx-core")
  implementation("io.vertx:vertx-web")
  implementation("io.vertx:vertx-lang-kotlin-coroutines")
  implementation("io.vertx:vertx-stomp")
  implementation("io.github.microutils:kotlin-logging-jvm:2.1.21")
  implementation("ch.qos.logback:logback-classic:1.2.11")
}

group = "cn.mmooo"
version = "1.0.0"
description = "vertx-server-push"
java.sourceCompatibility = JavaVersion.VERSION_1_8

java {
  withSourcesJar()
}

publishing {
  publications.create<MavenPublication>("maven") {
    from(components["java"])
  }
}

tasks.withType<JavaCompile>() {
  options.encoding = "UTF-8"
}
application {
  mainClass.set("cn.mmooo.MainKt")
}

