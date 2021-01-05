package com.kronos.doubletap;

import com.android.build.gradle.AppExtension;
import com.android.build.gradle.AppPlugin;
import com.android.build.gradle.LibraryExtension;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class DoubleTapPlugin implements Plugin<Project> {

    private static final String EXT_NAME = "doubleTab";

    @Override
    public void apply(Project project) {
        boolean isApp = project.getPlugins().hasPlugin(AppPlugin.class);
        project.getExtensions().create(EXT_NAME, DoubleTabConfig.class);
        project.afterEvaluate(project1 -> {
            DoubleTabConfig config = (DoubleTabConfig) project1.getExtensions().findByName(EXT_NAME);
            if (config == null) {
                config = new DoubleTabConfig();
            }
            config.transform();
        });
        if (isApp) {
            AppExtension appExtension = project.getExtensions().getByType(AppExtension.class);
            appExtension.registerTransform(new DoubleTapAppTransform());

            return;
        }
        if (project.getPlugins().hasPlugin("com.android.library")) {
            LibraryExtension libraryExtension = project.getExtensions().getByType(LibraryExtension.class);
            libraryExtension.registerTransform(new DoubleTapLibraryTransform());
        }
    }
}
