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
        name = project.name
        description = project.description
        url = "https://github.com/$gitHubUserName/${project.name}"

        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }

        developers {
            developer {
                id = gitHubUserName
                name = gitHubUserName
                url = "https://github.com/$gitHubUserName/"
            }
        }

        scm {
            connection = "scm:git:git://github.com/$gitHubUserName/${project.name}.git"
            developerConnection = "scm:git:ssh://github.com/$gitHubUserName/${project.name}.git"
            url = "https://github.com/$gitHubUserName/${project.name}/"
        }
    }
}
