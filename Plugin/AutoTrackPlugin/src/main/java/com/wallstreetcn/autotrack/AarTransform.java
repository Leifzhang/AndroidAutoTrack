package com.wallstreetcn.autotrack;

import com.android.build.gradle.internal.dependency.ExtractProGuardRulesTransform;
import com.android.build.gradle.internal.dependency.GenericTransformParameters;
import com.android.build.gradle.internal.publishing.AndroidArtifacts;
import com.android.build.gradle.internal.tasks.AarMetadataTask;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.gradle.api.artifacts.transform.InputArtifact;
import org.gradle.api.artifacts.transform.TransformAction;
import org.gradle.api.artifacts.transform.TransformOutputs;
import org.gradle.api.file.FileSystemLocation;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Classpath;
import org.gradle.api.tasks.Input;

import java.io.File;

/**
 * @Author LiABao
 * @Since 2021/9/18
 */
public abstract class AarTransform {} /*implements TransformAction<AarTransform.Parameters> {

    public interface Parameters extends GenericTransformParameters {
        @Input
        Property<AndroidArtifacts.ArtifactType> getTargetType();

        @Input
        Property<Boolean> getSharedLibSupport();
    }

    @Classpath
    @InputArtifact
    public abstract Provider<FileSystemLocation> getInputArtifact();

    @NonNull
    public static AndroidArtifacts.ArtifactType[] getTransformTargets() {
        return new AndroidArtifacts.ArtifactType[] {
                // For CLASSES, this transform is ues for runtime, and AarCompileClassesTransform is
                // used for compile
                AndroidArtifacts.ArtifactType.SHARED_CLASSES,
                AndroidArtifacts.ArtifactType.JAVA_RES,
                AndroidArtifacts.ArtifactType.SHARED_JAVA_RES,
                AndroidArtifacts.ArtifactType.PROCESSED_JAR,
                AndroidArtifacts.ArtifactType.MANIFEST,
                AndroidArtifacts.ArtifactType.ANDROID_RES,
                AndroidArtifacts.ArtifactType.ASSETS,
                AndroidArtifacts.ArtifactType.SHARED_ASSETS,
                AndroidArtifacts.ArtifactType.JNI,
                AndroidArtifacts.ArtifactType.SHARED_JNI,
                AndroidArtifacts.ArtifactType.AIDL,
                AndroidArtifacts.ArtifactType.RENDERSCRIPT,
                AndroidArtifacts.ArtifactType.UNFILTERED_PROGUARD_RULES,
                AndroidArtifacts.ArtifactType.LINT,
                AndroidArtifacts.ArtifactType.ANNOTATIONS,
                AndroidArtifacts.ArtifactType.PUBLIC_RES,
                AndroidArtifacts.ArtifactType.COMPILE_SYMBOL_LIST,
                AndroidArtifacts.ArtifactType.DATA_BINDING_ARTIFACT,
                AndroidArtifacts.ArtifactType.DATA_BINDING_BASE_CLASS_LOG_ARTIFACT,
                AndroidArtifacts.ArtifactType.RES_STATIC_LIBRARY,
                AndroidArtifacts.ArtifactType.RES_SHARED_STATIC_LIBRARY,
                AndroidArtifacts.ArtifactType.PREFAB_PACKAGE,
                AndroidArtifacts.ArtifactType.AAR_METADATA,
                AndroidArtifacts.ArtifactType.ART_PROFILE,
        };
    }

    @Override
    public void transform(@NonNull TransformOutputs transformOutputs) {
        File input = getInputArtifact().get().getAsFile();
        AndroidArtifacts.ArtifactType targetType = getParameters().getTargetType().get();
        switch (targetType) {
            case CLASSES_JAR:
            case JAVA_RES:
            case PROCESSED_JAR:
                // even though resources are supposed to only be in the main jar of the AAR, this
                // is not necessarily enforced by all build systems generating AAR so it's safer to
                // read all jars from the manifest.
                // For shared libraries, these are provided via SHARED_CLASSES and SHARED_JAVA_RES.
                if (!isShared(input)) {
                    AarTransformUtil.getJars(input).forEach(transformOutputs::file);
                }
                break;
            case SHARED_CLASSES:
            case SHARED_JAVA_RES:
                if (isShared(input)) {
                    AarTransformUtil.getJars(input).forEach(transformOutputs::file);
                }
                break;
            case LINT:
                outputIfExists(FileUtils.join(input, FD_JARS, FN_LINT_JAR), transformOutputs);
                break;
            case MANIFEST:
                // Return both the manifest and the extra snippet for the shared library.
                outputIfExists(new File(input, FN_ANDROID_MANIFEST_XML), transformOutputs);
                if (isShared(input)) {
                    outputIfExists(
                            new File(input, FN_SHARED_LIBRARY_ANDROID_MANIFEST_XML),
                            transformOutputs);
                }
                break;
            case ANDROID_RES:
                outputIfExists(new File(input, FD_RES), transformOutputs);
                break;
            case ASSETS:
                outputIfExists(new File(input, FD_ASSETS), transformOutputs);
                break;
            case JNI:
                outputIfExists(new File(input, FD_JNI), transformOutputs);
                break;
            case AIDL:
                outputIfExists(new File(input, FD_AIDL), transformOutputs);
                break;
            case RENDERSCRIPT:
                outputIfExists(new File(input, FD_RENDERSCRIPT), transformOutputs);
                break;
            case UNFILTERED_PROGUARD_RULES:
                if (!ExtractProGuardRulesTransform.performTransform(
                        FileUtils.join(input, FD_JARS, FN_CLASSES_JAR), transformOutputs, false)) {
                    outputIfExists(new File(input, FN_PROGUARD_TXT), transformOutputs);
                }
                break;
            case ANNOTATIONS:
                outputIfExists(new File(input, FN_ANNOTATIONS_ZIP), transformOutputs);
                break;
            case PUBLIC_RES:
                outputIfExists(new File(input, FN_PUBLIC_TXT), transformOutputs);
                break;
            case COMPILE_SYMBOL_LIST:
                outputIfExists(new File(input, FN_RESOURCE_TEXT), transformOutputs);
                break;
            case RES_STATIC_LIBRARY:
                if (!isShared(input)) {
                    outputIfExists(new File(input, FN_RESOURCE_STATIC_LIBRARY), transformOutputs);
                }
                break;
            case RES_SHARED_STATIC_LIBRARY:
                if (isShared(input)) {
                    outputIfExists(
                            new File(input, SdkConstants.FN_RESOURCE_SHARED_STATIC_LIBRARY),
                            transformOutputs);
                }
                break;
            case DATA_BINDING_ARTIFACT:
                outputIfExists(
                        new File(input, DataBindingBuilder.DATA_BINDING_ROOT_FOLDER_IN_AAR),
                        transformOutputs);
                break;
            case DATA_BINDING_BASE_CLASS_LOG_ARTIFACT:
                outputIfExists(
                        new File(
                                input,
                                DataBindingBuilder.DATA_BINDING_CLASS_LOG_ROOT_FOLDER_IN_AAR),
                        transformOutputs);
                break;
            case PREFAB_PACKAGE:
                outputIfExists(new File(input, FD_PREFAB_PACKAGE), transformOutputs);
                break;
            case AAR_METADATA:
                outputIfExists(
                        FileUtils.join(input, AarMetadataTask.AAR_METADATA_ENTRY_PATH.split("/")),
                        transformOutputs);
                break;
            case ART_PROFILE:
                outputIfExists(
                        FileUtils.join(
                                input,
                                SdkConstants.FN_ART_PROFILE),
                        transformOutputs);
                break;
            default:
                throw new RuntimeException("Unsupported type in AarTransform: " + targetType);
        }
    }

    private boolean isShared(@NonNull File explodedAar) {
        return getParameters().getSharedLibSupport().get()
                && new File(explodedAar, FN_SHARED_LIBRARY_ANDROID_MANIFEST_XML).exists();
    }

    private static void outputIfExists(@NonNull File file, @NonNull TransformOutputs outputs) {
        if (file.isDirectory()) {
            outputs.dir(file);
        } else if (file.isFile()) {
            outputs.file(file);
        }
    }
}
*/