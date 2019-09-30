package com.feelschaotic.upload

import org.gradle.api.Plugin
import org.gradle.api.Project

class UploadPatchPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        println '---创建_uploadPatch Task'
        def patchUploadDebug = project.task('_uploadPatch', type: UploadPatchTask)
        patchUploadDebug.group = 'upload'
        patchUploadDebug.description = 'Upload an test Patch file'
    }

}