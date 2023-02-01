plugins {
	`java-library`
	`maven-publish`
	`antlr`
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

task("copyDependencies", Copy::class) {
	configurations.compileClasspath.get()
		.filter { it.extension == "jar" }
		.forEach { from(it.absolutePath).into("$buildDir/dependencies") }
}

tasks.withType<JavaCompile> {
	options.encoding = "UTF-8"
	// only possible with jdk >= 9   options.compilerArgs.addAll(listOf("--release", "1_8"))
}

tasks.named("sourcesJar") {
	dependsOn(":generateGrammarSource")
}

