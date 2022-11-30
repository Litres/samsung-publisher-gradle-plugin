package ru.litres.publish.samsung

import org.gradle.api.Plugin
import org.gradle.api.Project

const val EXTENSION_NAME = "samsungPublishConfig"
const val TASK_NAME = "samsungPublish"
const val GROUP = "publishing"

class SamsungPublishPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create(EXTENSION_NAME, SamsungPublishExtension::class.java)

        project.tasks.register(TASK_NAME, SamsungPublishTask::class.java) {
            it.group = GROUP
            it.artifactDir.set(extension.artifactDir)
            it.privateKey.set(extension.privateKey)
            it.serviceAccountId.set(extension.serviceAccountId)
            it.publishSetting = extension.publishSetting
            it.debugSetting = extension.debugSetting
        }
    }
}
