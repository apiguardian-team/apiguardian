import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

plugins {
	id("java")
	id("eclipse")
	id("idea")
	id("maven-publish")
	id("signing")
	id("biz.aQute.bnd.builder") version "7.1.0"
	id("net.nemerosa.versioning") version "3.1.0"
	id("org.ajoberstar.git-publish") version "5.1.1"
	id("com.gradleup.nmcp") version "0.1.2"
}

val buildTimeAndDate: OffsetDateTime = OffsetDateTime.now()
val buildDate: String = DateTimeFormatter.ISO_LOCAL_DATE.format(buildTimeAndDate)
val buildTime: String = DateTimeFormatter.ofPattern("HH:mm:ss.SSSZ").format(buildTimeAndDate)
val builtByValue = project.findProperty("builtBy") ?: project.property("defaultBuiltBy")

val isSnapshot = project.version.toString().contains("SNAPSHOT")
val docsVersion = if (isSnapshot) "snapshot" else project.version.toString()
val docsDir = layout.buildDirectory.dir("ghpages-docs")
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
		options.release = 6
		javaCompiler = project.javaToolchains.compilerFor {
			languageVersion.set(JavaLanguageVersion.of(11))
		}
	}

	val compileModule by registering(JavaCompile::class) {
		source(moduleSourceDir)
		destinationDirectory = layout.buildDirectory.dir("classes/java/modules")
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
		from(compileModule.flatMap { it.destinationDirectory }.map { it.dir(moduleName) }) {
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
			include("COPYRIGHT")
			into("META-INF")
		}
	}

	val prepareDocsForUploadToGhPages by registering(Copy::class) {
		dependsOn(javadoc)
		outputs.dir(docsDir)

		from(layout.buildDirectory.dir("docs")) {
			include("javadoc/**")
		}
		from(layout.buildDirectory.dir("docs/javadoc")) {
			// For compatibility with pre JDK 10 versions of the Javadoc tool
			include("element-list")
			rename { "api/package-list" }
		}
		into(docsDir.map { it.dir(docsVersion) })
		filesMatching("javadoc/**") {
			path = path.replace("javadoc/", "api/")
		}
		includeEmptyDirs = false
	}

	val createCurrentDocsFolder by registering(Copy::class) {
		dependsOn(prepareDocsForUploadToGhPages)
		enabled = replaceCurrentDocs
		outputs.dir("${docsDir}/current")

		from(docsDir.map { it.dir(docsVersion) })
		into(docsDir.map { it.dir("current") })
	}

	gitPublishCopy {
		dependsOn(prepareDocsForUploadToGhPages, createCurrentDocsFolder)
	}
}

if (!isSnapshot) {
	signing {
		useGpgCmd()
		sign(publishing.publications)
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
						url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
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
	repositories {
		maven {
			name = "mavenCentralSnapshots"
			url = uri("https://central.sonatype.com/repository/maven-snapshots/")
			credentials {
				username = providers.gradleProperty("mavenCentralUsername").orNull
				password = providers.gradleProperty("mavenCentralPassword").orNull
			}
		}
	}
}

nmcp {
	centralPortal {
		username = providers.gradleProperty("mavenCentralUsername")
		password = providers.gradleProperty("mavenCentralPassword")
		publishingType = providers.gradleProperty("mavenCentralPublishingType").orElse("USER_MANAGED")
	}
}

gitPublish {
	repoUri = "https://github.com/apiguardian-team/apiguardian.git"
	referenceRepoUri = projectDir.toURI().toString()

	branch = "gh-pages"

	username = providers.environmentVariable("GIT_USERNAME")
	password = providers.environmentVariable("GIT_PASSWORD")

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
