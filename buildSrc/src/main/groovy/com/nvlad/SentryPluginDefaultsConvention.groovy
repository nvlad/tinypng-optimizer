package com.nvlad

import org.gradle.api.internal.plugins.DefaultConvention

class SentryPluginDefaultsConvention extends DefaultConvention {
    String url = null
    String org = null
    String project = null
}
