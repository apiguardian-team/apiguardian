import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

plugins {
	id("java")
	id("eclipse")
	id("idea")
	id("maven-publish")
	id("signing")
	id("biz.aQute.bnd.builder") version "5.3.0"
	id("net.nemerosa.versioning") version "2.14.0"
	id("org.ajoberstar.git-publish") version "3.0.0"
	id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
}

val buildTimeAndDate = OffsetDateTime.now()
val buildDate = DateTimeFormatter.ISO_LOCAL_DATE.format(buildTimeAndDate)
val buildTime = DateTimeFormatter.ofPattern("HH:mm:ss.SSSZ").format(buildTimeAndDate)
val builtByValue = project.findProperty("builtBy") ?: project.property("defaultBuiltBy")

val isSnapshot = project.version.toString().contains("SNAPSHOT")
val docsVersion = if (isSnapshot) "snapshot" else project.version
val docsDir = File(buildDir, "ghpages-docs")
val replaceCurrentDocs = project.hasProperty("replaceCurrentDocs")

description = "@API Guardian"
val moduleName = "org.apiguardian.api"

repositories {
	mavenCentral()
}

java {
	withJavadocJar()
	withSourcesJar()
}

val moduleSourceDir = file("src/module/java")

tasks {
	compileJava {
		options.release.set(6)
	}

	val compileModule by registering(JavaCompile::class) {
		source(moduleSourceDir)
		destinationDir = file("$buildDir/classes/java/modules")
		classpath = files(compileJava.map { it.classpath })
		inputs.property("moduleName", moduleName)
		inputs.property("moduleVersion", project.version)
		options.release.set(9)
		options.compilerArgs = listOf(
			"--module-version", project.version as String,
			"--module-source-path", moduleSourceDir.toString(),
			"--patch-module", "$moduleName=${sourceSets.main.get().allJava.srcDirs.joinToString(":")}",
			"--module", moduleName
		)
	}

	jar {
		fun normalizeVersion(versionLiteral: String): String {
			val regex = Regex("(\\d+\\.\\d+\\.\\d+).*")
			val match = regex.matchEntire(versionLiteral)
			require(match != null) {
				"Version '$versionLiteral' does not match version pattern, e.g. 1.0.0-QUALIFIER"
			}
			return match.groupValues[1]
		}
		manifest {
			attributes(
				"Created-By" to "${System.getProperty("java.version")} (${System.getProperty("java.vendor")} ${System.getProperty("java.vm.version")})",
				"Built-By" to builtByValue,
				"Build-Date" to buildDate,
				"Build-Time" to buildTime,
				"Build-Revision" to versioning.info.commit,
				"Specification-Title" to project.name,
				"Specification-Version" to normalizeVersion(project.version.toString()),
				"Specification-Vendor" to "apiguardian.org",
				"Implementation-Title" to project.name,
				"Implementation-Version" to project.version,
				"Implementation-Vendor" to "apiguardian.org",
				"Bundle-Name" to project.name,
				"Bundle-Description" to project.description,
				"Bundle-DocURL" to "https://github.com/apiguardian-team/apiguardian",
				"Bundle-Vendor" to "apiguardian.org",
				"-exportcontents" to "org.apiguardian.api",
				"Bundle-SymbolicName" to moduleName
			)
		}
		from(files(compileModule.map { "${it.destinationDir}/${moduleName}" })) {
			include("module-info.class")
		}
	}

	javadoc {
		(options as StandardJavadocDocletOptions).apply {
			memberLevel = JavadocMemberLevel.PROTECTED
			isAuthor = true
			header = "@API Guardian"
			addStringOption("Xdoclint:html,syntax,reference", "-quiet")
			links("https://docs.oracle.com/en/java/javase/11/docs/api/")
		}
	}

	named<Jar>("sourcesJar") {
		from("${moduleSourceDir}/${moduleName}") {
			include("module-info.java")
		}
	}

	named<Jar>("javadocJar") {
		from(javadoc.map { File(it.destinationDir, "element-list") }) {
			// For compatibility with older tools, e.g. NetBeans 11
			rename { "package-list" }
		}
	}

	withType<Jar>().configureEach {
		from(rootDir) {
			include("LICENSE")
			into("META-INF")
		}
	}

	val prepareDocsForUploadToGhPages by registering(Copy::class) {
		dependsOn(javadoc)
		outputs.dir(docsDir)

		from("$buildDir/docs") {
			include("javadoc/**")
		}
		from("$buildDir/docs/javadoc") {
			// For compatibility with pre JDK 10 versions of the Javadoc tool
			include("element-list")
			rename { "api/package-list" }
		}
		into("${docsDir}/${docsVersion}")
		filesMatching("javadoc/**") {
			path = path.replace("javadoc/", "api/")
		}
		includeEmptyDirs = false
	}

	val createCurrentDocsFolder by registering(Copy::class) {
		dependsOn(prepareDocsForUploadToGhPages)
		enabled = replaceCurrentDocs
		outputs.dir("${docsDir}/current")

		from("${docsDir}/${docsVersion}")
		into("${docsDir}/current")
	}

	gitPublishCommit {
		dependsOn(prepareDocsForUploadToGhPages, createCurrentDocsFolder)
	}
}

if (!isSnapshot) {
	signing {
		sign(publishing.publications)
	}
}

nexusPublishing {
	packageGroup.set(group.toString())
	repositories {
		sonatype()
	}
}

publishing {
	publications {
		create<MavenPublication>("maven") {
			from(components["java"])
			pom {
				name.set("${project.group}:${project.name}")
				description.set("@API Guardian")
				url.set("https://github.com/apiguardian-team/apiguardian")
				scm {
					connection.set("scm:git:git://github.com/apiguardian-team/apiguardian.git")
					developerConnection.set("scm:git:git://github.com/apiguardian-team/apiguardian.git")
					url.set("https://github.com/apiguardian-team/apiguardian")
				}
				licenses {
					license {
						name.set("The Apache License, Version 2.0")
						url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
					}
				}
				developers {
					developer {
						id.set("apiguardian")
						name.set("@API Guardian Team")
						email.set("team@apiguardian.org")
					}
				}
			}
		}
	}
}

gitPublish {
	repoUri.set("https://github.com/apiguardian-team/apiguardian.git")
	branch.set("gh-pages")

	contents {
		from(docsDir)
		into("docs")
	}

	preserve {
		include("**/*")
		exclude("docs/$docsVersion/**")
		if (replaceCurrentDocs) {
			exclude("docs/current/**")
		}
	}
}
