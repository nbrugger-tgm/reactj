package com.niton.gradle.plugins.javalib

import org.gradle.api.provider.Property

abstract class ArtifactorySettings {
    abstract Property<String> getUsername();

    abstract Property<File> getTokenFile();

    abstract Property<String> getTokenEnvironmentVariable();

    abstract Property<String> getRepo();

    ArtifactorySettings() {
        tokenEnvironmentVariable.convention("ARTIFACTORY_API_KEY")
        tokenFile.convention(new File("TOKENS"))
    }

}