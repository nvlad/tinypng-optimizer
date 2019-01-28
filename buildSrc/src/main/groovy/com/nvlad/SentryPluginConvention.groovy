package com.nvlad

import org.gradle.api.internal.plugins.DefaultConvention

class SentryPluginConvention {
    SentryPluginExtensionAuth auth
    def defaults

    def sentry(Closure closure) {
        closure.delegate = this
        closure()
    }
}
