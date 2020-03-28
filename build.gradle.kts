import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.2.5.RELEASE"
	id("io.spring.dependency-management") version "1.0.9.RELEASE"
	kotlin("jvm") version "1.3.61"
	kotlin("plugin.spring") version "1.3.61"
	kotlin("plugin.jpa") version "1.3.61"
}

group = "ru.balezz"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-devtools")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.apache.httpcomponents:httpclient:4.5.6")
	implementation("org.hsqldb:hsqldb")
	implementation("com.google.guava:guava:17.0")
	implementation("org.apache.commons:commons-lang3:3.3.2")
	implementation("commons-io:commons-io:2.4")
	implementation("com.squareup.retrofit2:retrofit:2.7.1")
	implementation("com.squareup.retrofit2:converter-gson:2.3.0")
	implementation("com.squareup.retrofit2:adapter-rxjava2:2.3.0")

	// testImplementation("junit:junit: 4.12")
	testImplementation("org.springframework.boot:spring-boot-starter-test") 
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "1.8"
	}
}
