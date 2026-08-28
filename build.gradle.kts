plugins {
    `java-library`
    id("com.vanniktech.maven.publish") version "0.37.0"
    signing
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.jspecify:jspecify:1.0.0")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

signing {
    useGpgCmd()
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

    test {
        useJUnitPlatform()
    }
}

val gitHubUserName = "${property("github-user-name")}"

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
