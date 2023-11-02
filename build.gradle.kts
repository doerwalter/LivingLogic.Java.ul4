plugins {
	`java-library`
	`maven-publish`
	`antlr`
	id("com.github.ben-manes.versions") version "0.46.0"
}

val releaseRepository = "https://nexus.livinglogic.de/repository/maven-releases"
val snapshotRepository = "https://nexus.livinglogic.de/repository/maven-snapshots"

repositories {
	mavenLocal()
	maven{
		url=uri(releaseRepository)
		name="llnexus"
	}
	maven{
		url=uri("https://nexus.livinglogic.de/repository/maven-central")
		name="maven-central"
	}
}

tasks.withType<JavaCompile> {
	options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
	options.encoding = "UTF-8"
}

dependencies {
	implementation("org.apache.commons:commons-lang3:3.11")
	implementation("org.apache.commons:commons-text:1.10.0")
	implementation("commons-collections:commons-collections:3.2.2")
	antlr("org.antlr:antlr:3.5.2")
	implementation("com.googlecode.json-simple:json-simple:1.1.1")
	implementation("com.lambdaworks:scrypt:1.4.0")
	testImplementation("junit:junit:4.13.1")
	testImplementation("com.oracle.database.jdbc:ojdbc6:11.2.0.4")
}

group = "com.livinglogic"
description = "Java implementation of the UL4 templating language"
java.sourceCompatibility = JavaVersion.VERSION_17
java.targetCompatibility = JavaVersion.VERSION_17

java {
	withSourcesJar()
	withJavadocJar()
}

publishing {
	publications.create<MavenPublication>("UL4") {
		from(components["java"])
	}
	repositories {
		maven {
			url = uri(if (project.version.toString().endsWith("SNAPSHOT")) snapshotRepository else releaseRepository)
			name = "LLNexus"
			credentials {
				username = System.getProperty("llnexusUsername")
				password = System.getProperty("llnexusPassword")
			}
		}
	}
}

tasks.withType<Javadoc> {
	(options as StandardJavadocDocletOptions).addStringOption("Xdoclint:none", "-quiet")
}

task("execute_ul4", JavaExec::class) {
	classpath = sourceSets["main"].runtimeClasspath
	standardInput = System.`in`

	mainClass.set("com.livinglogic.ul4.Tester")
}

task("execute_ul4on", JavaExec::class) {
	classpath = sourceSets["main"].runtimeClasspath
	standardInput = System.`in`

	mainClass.set("com.livinglogic.ul4on.Tester")
}

task("copyDependencies", Copy::class) {
	configurations.compileClasspath.get()
		.filter { it.extension == "jar" }
		.forEach { from(it.absolutePath).into("$buildDir/dependencies") }
}

tasks.withType<JavaCompile> {
	options.encoding = "UTF-8"
	options.compilerArgs.addAll(listOf("-Xlint:deprecation"/*, "-Xlint:unchecked"*/))
}

tasks.named("sourcesJar") {
	dependsOn(":generateGrammarSource")
}

tasks.register("createVersionTxt") {
	doLast {
		val version = findProperty("version") // returns "unspecified" if no version is set
		val dir = "src/main/resources/com/livinglogic/ul4"
		mkdir(dir)
		file("${dir}/version.txt").writeText("${version}")
	}
}

tasks.named("compileJava") {
	dependsOn(":createVersionTxt")
}
