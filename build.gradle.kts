plugins {
    java
    idea
    jacoco
    `jvm-test-suite`
    `jacoco-report-aggregation`
    id("org.springframework.boot") version "3.3.3"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "pl.codehouse.restaurant"
version = "0.0.1-SNAPSHOT"
val junitVersion = "5.11.2"
val junitPlatformVersion = "1.11.2"
val cucumberVersion = "7.11.1"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

idea {
    module {
        testSources.from(file("src/integrationTest/java"), file("src/test/java"))
        testResources.from(file("src/integrationTest/resources"), file("src/test/resources"))
    }
}

dependencies {
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("org.postgresql:r2dbc-postgresql")

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")

    implementation("org.springframework.kafka:spring-kafka")
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    implementation("org.apache.commons:commons-lang3:3.17.0")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
}

testing {
    suites {
        withType<JvmTestSuite> {
            useJUnitJupiter(junitVersion)
            dependencies {
                implementation("io.projectreactor:reactor-test")

                implementation("org.springframework.boot:spring-boot-starter-test")
                implementation("org.springframework.boot:spring-boot-starter-webflux")
                implementation("org.springframework.boot:spring-boot-starter-data-jpa")
                implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")

                implementation("org.junit.jupiter:junit-jupiter:$junitVersion")
                implementation("org.junit.platform:junit-platform-suite:$junitPlatformVersion")

                implementation("io.cucumber:cucumber-java:$cucumberVersion")
                implementation("io.cucumber:cucumber-junit:$cucumberVersion")
                implementation("io.cucumber:cucumber-spring:$cucumberVersion")
                implementation("io.cucumber:cucumber-junit-platform-engine:$cucumberVersion")

                runtimeOnly("org.junit.platform:junit-platform-launcher:$junitPlatformVersion")
            }
        }

        val integrationTest by registering(JvmTestSuite::class) {
            testType.set(TestSuiteType.INTEGRATION_TEST)
            sources {
                java {
                    setSrcDirs(listOf("src/integrationTest/java", "src/test/java"))
                }
                resources {
                    setSrcDirs(listOf("src/integrationTest/resources", "src/test/resources"))
                }
            }
            targets {
                all {
                    testTask.configure {
                        shouldRunAfter(test)
                    }
                }
            }
            dependencies {
                implementation(project())
                implementation(project.dependencies.platform("org.springframework.boot:spring-boot-dependencies:3.3.3"))

                implementation("org.flywaydb:flyway-core")
                implementation("org.flywaydb:flyway-database-postgresql")

                implementation("org.springframework.boot:spring-boot-testcontainers")

                implementation("org.testcontainers:junit-jupiter")
                implementation("org.testcontainers:kafka")
                implementation("org.testcontainers:postgresql")
                implementation("org.testcontainers:r2dbc")

                implementation("io.rest-assured:rest-assured:5.5.0")
                implementation("io.rest-assured:json-path:5.5.0")
                implementation("io.rest-assured:json-schema-validator:5.5.0")
                implementation("io.rest-assured:spring-web-test-client:5.5.0")
            }
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    systemProperty("cucumber.junit-platform.naming-strategy", "long")
    finalizedBy(tasks.jacocoTestReport)

    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.named<Test>("integrationTest") {
    useJUnitPlatform()
    systemProperty("cucumber.junit-platform.naming-strategy", "long")
    finalizedBy(tasks.jacocoTestReport)
}

tasks.named("check") {
    dependsOn(testing.suites.named("test"))
    dependsOn(testing.suites.named("integrationTest"))
}

tasks.jacocoTestReport {
    reports {
        xml.required = true
        csv.required = true
        html.outputLocation = layout.buildDirectory.dir("jacocoHtml")
    }
}

// Run test suites in parallel
tasks.register("parallelTests") {
    dependsOn(tasks.named("test"), tasks.named("integrationTest"))
    doFirst {
        println("Running all test suites in parallel")
    }
}

tasks.withType<Test>().configureEach {
    maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).takeIf { it > 0 } ?: 1
    setForkEvery(100)
    reports.html.required.set(true)
}
