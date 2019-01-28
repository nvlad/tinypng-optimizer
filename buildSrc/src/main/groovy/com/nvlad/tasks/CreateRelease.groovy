package com.nvlad.tasks


import com.nvlad.SentryPluginExtension
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class CreateRelease extends DefaultTask {
    private SentryPluginExtension extension = project.extensions.findByName("sentry") as SentryPluginExtension

//    private SentryPluginConvention options = project.convention.plugins.sentry as SentryPluginConvention

    CreateRelease() {
        group = "sentry"
    }

    @TaskAction
    void run() {
//        def extension = project.extensions.findByName("sentry")
        println "CreateRelease::run"
//        println extension.auth.token
        println this.properties
    }
}
