import com.google.protobuf.gradle.id
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.7.20"
    id("com.google.protobuf") version "0.9.2"
    `maven-publish`
}

group = "com.stosik.kafka.models"
version = "1.0.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
    mavenLocal()
    maven { url = uri("https://packages.confluent.io/maven") }
}

dependencies {
    // Kotlin
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

    //Kafka
    implementation("org.apache.avro:avro:1.10.1")
    implementation("com.sksamuel.avro4k:avro4k-core:0.41.0")
    implementation("io.confluent:kafka-protobuf-serializer:7.3.1")
    implementation("com.google.protobuf:protobuf-kotlin:3.22.0")
}

tasks {
    withType<KotlinCompile>().configureEach {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "17"
        }
    }

    withType<Test> {
        useJUnitPlatform()
    }
}

protobuf {
    protobuf {
        protoc {
            artifact = "com.google.protobuf:protoc:3.22.0"
        }
        plugins {
            id("kotlin")
        }
        generateProtoTasks {
            all().forEach {
                it.builtins {
                    id("kotlin")
                }
            }
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "pl.stosik"
            artifactId = "event-models"
            version = "1.0"

            from(components["kotlin"])
        }
    }
}