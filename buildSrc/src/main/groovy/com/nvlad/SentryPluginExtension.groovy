package com.nvlad

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.internal.NamedDomainObjectContainerConfigureDelegate
import org.gradle.util.Configurable
import org.gradle.util.ConfigureUtil


class SentryPluginExtensionAuth {
    String token = null
}

class SentryPluginExtensionDefaults implements NamedDomainObjectContainer<SentryPluginExtensionDefaults>, Configurable {
    String url
    String org
    String project

    @Override
    SentryPluginExtensionDefaults configure(Closure cl) {
        def delegate = new NamedDomainObjectContainerConfigureDelegate(cl, this);
        return cl.properties.get("defaults")
    }
}

class SentryPluginExtension {
//    NamedDomainObjectContainer<SentryPluginExtensionAuth> auth
    final Configurable<SentryPluginExtensionDefaults> defaults

    SentryPluginExtension(defaults) {
        this.defaults = defaults
    }
//
//    SentryPluginExtension(auth, defaults) {
//        println(auth)
//    }

    def defaults(Closure defaultsClosure) {
        println("SentryPluginExtension::defaults")
        defaults.configure(defaultsClosure)
    }
}
