package com.nvlad

import com.nvlad.tasks.CreateRelease
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.artifacts.configurations.DefaultConfiguration

class SentryPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
//        project.extensions.create('sentry', SentryPluginExtension)
//        project.convention.plugins.sentry = new SentryPluginConvention()
//        project.convention.plugins.sentry.defaults = new SentryPluginDefaultsConvention()

        def defaults = new SentryPluginExtensionDefaults()
//
////        LOG.info("Configure SentryCli.createRelease task")
////        project.tasks.add("createRelease", CreateRelease)
////        project.tasks.withType(CreateRelease)
        project.tasks.create("createRelease", CreateRelease).with {
//            group = "sentry"
//            convention.create("defaults", SentryPluginExtensionDefaults)
//            doLast {
////                conventionMapping.map(extension.defaults, SentryPluginExtensionDefaults)
                println "SentryPlugin::createRelease"
//                println extension.auth
//                println extension.defaults.url
//            }

            project.configure(project) {
                extensions.create("sentry", SentryPluginExtension, defaults)
            }
        }
    }
}
