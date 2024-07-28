package ru.litres.publish.samsung

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import ru.litres.publish.samsung.exception.NotFoundRequiredField
import ru.litres.publish.samsung.usecase.PublishBuildUseCase

abstract class SamsungPublishTask : DefaultTask() {
    @get:Internal("Directory mapped to a useful set of files later")
    abstract val artifactDir: DirectoryProperty

    @get:Input
    abstract val privateKey: Property<String>

    @get:Input
    abstract val serviceAccountId: Property<String>

    @get:Input
    abstract var publishSetting: PublishSetting

    @get:Input
    abstract var debugSetting: DebugSetting

    @Suppress("ThrowsCount")
    @TaskAction
    fun publish() {
        val key = privateKey.orNull ?: throw NotFoundRequiredField("privateKey")
        val serviceId = serviceAccountId.orNull ?: throw NotFoundRequiredField("serviceAccountId")
        val folderWithApk = artifactDir.orNull ?: throw NotFoundRequiredField("artifactDir")
        if (publishSetting.contentId == null) throw NotFoundRequiredField("contentId")
        PublishBuildUseCase(debugSetting).invoke(serviceId, key, folderWithApk, publishSetting)
    }
}
