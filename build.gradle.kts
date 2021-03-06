plugins {
    id("java")
    id("idea")
    id("maven-publish")
    id("java-gradle-plugin")
    id("com.gradle.plugin-publish") version "0.12.0"
    id("io.freefair.lombok") version "5.1.0"
}

group = "uk.co.mulecode"
version = "1.0.1.R6"

repositories {
    mavenCentral()
}

dependencies {

    annotationProcessor("org.projectlombok:lombok:1.18.12")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.12")

    implementation("org.slf4j:slf4j-api:1.7.30")
    implementation("org.slf4j:slf4j-simple:1.7.30")

    implementation("org.apache.maven:maven-artifact:3.6.3")
    implementation("org.eclipse.jgit:org.eclipse.jgit:5.8.0.202006091008-r")
    implementation("org.apache.commons:commons-collections4:4.0")
    implementation("org.apache.commons:commons-lang3:3.9")
    implementation("org.json:json:20180813")
    implementation("com.jayway.jsonpath:json-path:2.4.0")
    implementation("commons-io:commons-io:2.6")

    testImplementation("junit", "junit", "4.12")
    testImplementation("org.hamcrest:hamcrest-all:1.3")
    testImplementation("com.jayway.jsonpath:json-path-assert:2.4.0")
    testImplementation("org.mockito:mockito-core:3.3.3")
    testImplementation("org.assertj:assertj-core:3.16.1")
}

tasks.wrapper {
    gradleVersion = "6.6.1"
    distributionType = Wrapper.DistributionType.ALL
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_11
}

pluginBundle {
    website = "https://github.com/mulecode/artifact-versioning"
    vcsUrl = "https://github.com/mulecode/artifact-versioning"
    tags = listOf("artifact", "version", "semantic", "plugins")
}

gradlePlugin {
    plugins {
        create("fileTemplate") {
            id = "uk.co.mulecode.artifact-versioning"
            displayName = "Artifact Semantic Versioning"
            description = "Simple Semantic versioning."
            implementationClass = "uk.co.mulecode.versioning.plugin.VersionPlugin"
        }
    }
}
