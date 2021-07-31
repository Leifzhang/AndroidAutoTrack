package com.kronos.plugin.base.constant

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.QualifiedContent.DefaultContentType
import org.gradle.internal.impldep.com.google.common.collect.ImmutableSet

/**
 * @Author LiABao
 * @Since 2021/7/31
 */
object TransformManager {
    val EMPTY_SCOPES: Set<QualifiedContent.ScopeType> = ImmutableSet.of()

    val CONTENT_CLASS: Set<QualifiedContent.ContentType> = ImmutableSet.of<QualifiedContent.ContentType>(DefaultContentType.CLASSES)
    val CONTENT_JARS: Set<QualifiedContent.ContentType> = ImmutableSet.of<QualifiedContent.ContentType>(DefaultContentType.CLASSES, DefaultContentType.RESOURCES)
    val CONTENT_RESOURCES: Set<QualifiedContent.ContentType> = ImmutableSet.of<QualifiedContent.ContentType>(DefaultContentType.RESOURCES)
    val PROJECT_ONLY: Set<QualifiedContent.ScopeType> = ImmutableSet.of<QualifiedContent.ScopeType>(QualifiedContent.Scope.PROJECT)
    val SCOPE_FULL_PROJECT: Set<QualifiedContent.ScopeType> = ImmutableSet.of<QualifiedContent.ScopeType>(QualifiedContent.Scope.PROJECT, QualifiedContent.Scope.SUB_PROJECTS, QualifiedContent.Scope.EXTERNAL_LIBRARIES)


}