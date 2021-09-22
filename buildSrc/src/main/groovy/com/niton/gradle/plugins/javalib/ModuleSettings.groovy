package com.niton.gradle.plugins.javalib

import org.gradle.api.JavaVersion
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.provider.Property

abstract class ModuleSettings implements ExtensionAware {
    abstract Property<JavaVersion> getJavaVersion();

    abstract Property<String> getVersion();

    abstract Property<String> getName();

    abstract Property<String> getGroup();

    ModuleSettings() {
        javaVersion.convention(JavaVersion.VERSION_11)
    }


}
