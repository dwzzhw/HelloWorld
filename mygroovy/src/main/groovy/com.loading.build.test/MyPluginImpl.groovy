package com.loading.build.test
import org.gradle.api.Plugin
import org.gradle.api.Project

public class MyPluginImpl implements Plugin<Project> {
    void apply(Project project) {
        project.task('myGroovyTask') << {
            println "Hello, I am gradle plugin"
            println "Nice to meed you, loading! 123"
        }
    }
}


