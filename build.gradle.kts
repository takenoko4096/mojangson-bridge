plugins {
    `java-library`
    id("com.vanniktech.maven.publish") version "0.36.0"
    signing
}

group = "io.github.takenoko4096"
version = "0.1.2"
description = "library of mojangson"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.jspecify:jspecify:1.0.0")
}

val signingKey: String by project
val signingPassword: String by project

signing {
    useInMemoryPgpKeys(signingKey.replace("\\n", "\n"), signingPassword)
}

tasks {
    withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
    }

    javadoc {
        title = "${project.name} ${project.version}"
        destinationDir = file("$projectDir/docs/${project.version}")
        options {
            encoding = Charsets.UTF_8.name()
        }
    }
}

val gitHubUserName = "takenoko4096"

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()

    pom {
        name.set(project.name)
        description.set(project.description)
        url.set("https://github.com/$gitHubUserName/${project.name}")

        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }

        developers {
            developer {
                id.set(gitHubUserName)
                name.set(gitHubUserName)
                url.set("https://github.com/$gitHubUserName/")
            }
        }

        scm {
            connection.set("scm:git:git://github.com/$gitHubUserName/${project.name}.git")
            developerConnection.set("scm:git:ssh://github.com/$gitHubUserName/${project.name}.git")
            url.set("https://github.com/$gitHubUserName/${project.name}/")
        }
    }
}
