package com.niton.gradle.plugins.javalib

import org.gradle.api.BuildCancelledException
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.jvm.tasks.Jar
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension

class ModulePlugin implements Plugin<Project> {
    static Set<String> subPlugins = [
            'java-library',
            'maven-publish',
            'jacoco'
    ]

    @Override
    void apply(Project p) {
        applySubPlugins(p)

        ModuleSettings settings = p.extensions.create("module", ModuleSettings)
        ArtifactorySettings publisher = settings.extensions.create("publishing", ArtifactorySettings)

        p.afterEvaluate { Project pro ->
            pro.java {
                sourceCompatibility = settings.javaVersion.orElse(JavaVersion.VERSION_11).get()
                targetCompatibility = settings.javaVersion.orElse(JavaVersion.VERSION_11).get()
                withJavadocJar()
                withSourcesJar()
            }
            pro.publishing {
                publications {
                    lib(MavenPublication) {
                        if (!settings.group.isPresent() && pro.group == null)
                            throw new BuildCancelledException("module.group or project.group needs to be set!")
                        if (!settings.version.isPresent() && pro.version == null)
                            throw new BuildCancelledException("module.version or project.version needs to be set!")
                        groupId = settings.group.orElse(pro.group.toString()).get()
                        artifactId = settings.name.orElse(pro.name).get()
                        version = settings.version.orElse(pro.version.toString()).get()
                        from pro.components.java
                    }
                }

                repositories {
                    maven {
                        name = "Artifactory"
                        credentials {
                            if (!publisher.getUsername().isPresent())
                                throw new BuildCancelledException("module.publishing.username needs to be set!")
                            username = publisher.getUsername()
                            def pwdFile = publisher.getTokenFile().get()
                            if (pwdFile.exists())
                                password = pwdFile.readLines().get(0)
                            else
                                password = System.getenv(publisher.getTokenEnvironmentVariable().get())
                        }
                        url = new URL(publisher.getRepo().get())
                    }
                }
            }
            pro.tasks.withType(Javadoc) {
                failOnError = false
            }
        }


        p.tasks.create("testJar", Jar.class) {
            from p.sourceSets.test.output
        }


        p.jacocoTestReport {
            dependsOn p.test // tests are required to run before generating the report
            reports {
                xml.enabled true
                csv.enabled false
                html.destination p.file("${p.buildDir}/jacocoHtml")
            }
        }
        p.jacoco {
            toolVersion = "0.8.5"
        }
        p.test {
            // Use junit platform for unit tests
            useJUnitPlatform()
            finalizedBy p.tasks.jacocoTestReport // report is always generated after tests run
            jacoco {
                enabled = true
                dumpOnExit = true
                classDumpDir = null
                output = JacocoTaskExtension.Output.FILE
            }
        }

        p.repositories {
            mavenCentral()
        }
    }

    static void applySubPlugins(Project p) {
        subPlugins.forEach(p.plugins::apply)
    }
}
