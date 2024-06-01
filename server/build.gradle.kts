import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.2.3"
	id("org.jetbrains.kotlin.plugin.allopen") version "1.5.31"
	id("io.spring.dependency-management") version "1.1.4"
	id("org.jetbrains.kotlin.plugin.serialization") version "1.9.0-RC"
	kotlin("jvm") version "1.9.22"
	kotlin("plugin.spring") version "1.9.22"

}

group = "ba.pascal.weissleder"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

dependencies {

	//spring

	implementation("org.springframework.boot:spring-boot-starter-integration")
	testImplementation("org.springframework.integration:spring-integration-test")
	testImplementation("org.springframework.boot:spring-boot-starter-test")

	//kotlin
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")

	//DB

	//jdbc

	//jpa
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")

	implementation("org.postgresql:postgresql")
	runtimeOnly("org.postgresql:postgresql")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")


	//security
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.integration:spring-integration-security")
	implementation("org.apache.logging.log4j:log4j-api-kotlin:1.0.0")

	testImplementation("org.springframework.security:spring-security-test")

	//connections
	implementation("org.springframework.integration:spring-integration-stomp")
	implementation("org.springframework.integration:spring-integration-websocket")
	implementation("org.springframework.session:spring-session-core")
	implementation("javax.servlet:javax.servlet-api:4.0.1")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation ("com.google.code.gson:gson:2.8.8")


	//build
	developmentOnly("org.springframework.boot:spring-boot-docker-compose")

	//mqtt
	implementation("org.eclipse.paho:org.eclipse.paho.mqttv5.client:1.2.5")

	//jackson
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.1")



}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
