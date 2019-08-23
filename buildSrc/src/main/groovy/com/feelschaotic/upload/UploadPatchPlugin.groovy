package com.feelschaotic.upload

import org.gradle.api.Plugin
import org.gradle.api.Project

class UploadPatchPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        println '---创建_uploadPatchDebug Task'
        def patchUploadDebug = project.task('_uploadPatchDebug', type: UploadPatchTaskDebug)
        patchUploadDebug.group = 'upload'
        patchUploadDebug.description = 'Upload an test Patch file'

        println '---创建_uploadPatchRelease Task'
        def patchUploadRelease = project.task('_uploadPatchRelease', type: UploadPatchTaskRelease)
        patchUploadRelease.group = 'upload'
        patchUploadRelease.description = 'Upload an Patch file'
    }

}