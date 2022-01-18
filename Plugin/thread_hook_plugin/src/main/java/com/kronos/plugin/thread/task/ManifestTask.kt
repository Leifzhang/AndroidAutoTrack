package com.kronos.plugin.thread.task

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

/**
 *
 *  @Author LiABao
 *  @Since 2022/1/18
 *
 */
abstract class ManifestTask : DefaultTask() {
    // 输入
    @get:InputFile
    abstract val mergedManifest: RegularFileProperty
    // 输出
    @get:OutputFile
    abstract val updatedManifest: RegularFileProperty

    @TaskAction
    fun taskAction() {
        val file = mergedManifest.get().asFile.inputStream()
        val steam = updatedManifest.get().asFile.outputStream()
        steam.use {
            it.write(file.readBytes())
        }
    }
}