package com.wallstreetcn.autotrack;


import com.android.build.gradle.AppExtension;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class AutoTrackPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.afterEvaluate(project1 -> project1.getExtensions().getByType(AppExtension.class)
                .registerTransform(new NewAutoTackTransform(project1)));
    }

}