package com.wallstreetcn.autotrack


import org.gradle.api.Plugin
import org.gradle.api.Project

class AutoTrackPlugin implements Plugin<Project> {

    public static final String EXT_NAME = 'autotrack'

    @Override
    void apply(Project project) {
        AutoTrackConfig config = project.extensions.create('autoConfig', AutoTrackConfig)
        project.android.registerTransform(new AutoTrackTransform(project, config))
    }
}