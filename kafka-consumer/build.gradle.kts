import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management") version "1.0.10.RELEASE"
    kotlin("jvm") version "1.7.20"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.7.20"
    kotlin("plugin.spring") version "1.7.20"
}

group = "com.stosik.kafka.consumer"
version = "1.0.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
    mavenLocal()
    maven { url = uri("https://repo.spring.io/release") }
    maven { url = uri("https://repo.spring.io/milestone") }
    maven { url = uri("https://repo.spring.io/snapshot") }
    maven { url = uri("https://packages.confluent.io/maven") }
}

dependencies {
    // Spring
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Kotlin
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

    //Kafka
    implementation("org.apache.kafka:kafka-clients:3.3.1")
    implementation("io.confluent:kafka-schema-registry-client:5.3.0")
    implementation("org.apache.avro:avro:1.10.1")
    implementation("io.confluent:kafka-avro-serializer:5.3.0")
    implementation("com.sksamuel.avro4k:avro4k-core:0.41.0")
    implementation("io.confluent:kafka-protobuf-serializer:7.3.1")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
        exclude(group = "junit", module = "junit")
    }
    testImplementation("org.junit.jupiter:junit-jupiter-engine")
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}