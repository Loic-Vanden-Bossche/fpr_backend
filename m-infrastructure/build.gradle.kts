import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar
import java.util.*

plugins {
    id("org.springframework.boot") version "3.1.0"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.liquibase.gradle") version "2.0.4"
    kotlin("jvm") version "1.7.22"
    kotlin("plugin.spring") version "1.7.22"
    kotlin("plugin.jpa") version "1.7.22"
    kotlin("kapt")
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

tasks.withType<BootJar> {
    enabled to true
}

tasks.withType<Jar> {
    enabled to false
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.1.0")
    implementation("org.springframework.boot:spring-boot-starter-web:3.1.0")
    implementation("org.springframework.boot:spring-boot-starter-security:3.1.0")
    implementation("org.springframework.boot:spring-boot-starter-actuator:3.1.0")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server:3.1.0")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.1.0")
    implementation("org.springframework.boot:spring-boot-starter-websocket:3.1.0")
    implementation("org.springframework.security:spring-security-messaging:6.1.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
    implementation("com.amazonaws:aws-java-sdk-ecs:1.12.481")
    implementation("com.amazonaws:aws-java-sdk-ec2:1.12.481")
    implementation("com.amazonaws:aws-java-sdk-ecr:1.12.481")
    implementation("com.amazonaws:aws-java-sdk-s3:1.12.481")
    implementation("software.amazon.awssdk:s3:2.19.1")
    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.1.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.20-RC")
    implementation("org.liquibase:liquibase-core:4.20.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2")
    implementation(project(mapOf("path" to ":m-application-services")))
    implementation(project(mapOf("path" to ":m-domain-models")))
    developmentOnly("org.springframework.boot:spring-boot-devtools:3.1.0")
    implementation("com.github.docker-java:docker-java:3.3.0")
    implementation("com.github.docker-java:docker-java-transport-httpclient5:3.3.0")
    runtimeOnly("org.apache.httpcomponents.client5:httpclient5:5.2.1")
    runtimeOnly("org.postgresql:postgresql:42.5.4")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:3.1.0")
    kapt("org.mapstruct:mapstruct-processor:1.5.5.Final")
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.1.0")
    liquibaseRuntime("org.liquibase:liquibase-core:4.20.0")
    liquibaseRuntime("org.liquibase:liquibase-groovy-dsl:3.0.3")
    liquibaseRuntime("info.picocli:picocli:4.7.1")
    liquibaseRuntime("org.postgresql:postgresql:42.5.4")
    liquibaseRuntime("org.liquibase.ext:liquibase-hibernate6:4.21.1")
    liquibaseRuntime("org.springframework.boot:spring-boot-starter-data-jpa:3.1.0")
    liquibaseRuntime(sourceSets.getByName("main").output)
    liquibaseRuntime(sourceSets.getByName("main").compileClasspath)
    liquibaseRuntime(sourceSets.getByName("main").runtimeClasspath)

}

val cfg = project.file("src/main/resources/application.yaml")

val projectProperties = Properties().apply {
    load(cfg.inputStream())
}

tasks.diffChangeLog {
    dependsOn("compileKotlin")
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
    liquibase {
        activities.register("main") {
            this.arguments = mapOf(
                "changeLogFile" to (projectProperties["change-log"] as String).replace(
                    "classpath:",
                    "$rootDir/m-infrastructure/src/main/resources"
                ),
                "url" to projectProperties["url"],
                "referenceUrl" to "hibernate:spring:com.esgi.infrastructure.persistence.entities?dialect=org.hibernate.dialect.PostgreSQLDialect&hibernate.physical_naming_strategy=${projectProperties["physical-strategy"]}&hibernate.implicit_naming_strategy=${projectProperties["implicit-strategy"]}",
                "driver" to projectProperties["driver-class-name"],
                "referenceDriver" to "liquibase.ext.hibernate.database.connection.HibernateDriver",
                "username" to projectProperties["username"],
                "password" to projectProperties["password"],
            )
        }
        runList = "main"
    }
}


tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
