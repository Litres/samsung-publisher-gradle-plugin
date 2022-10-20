package ru.litres.publish.samsung

import org.gradle.api.Action
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import javax.inject.Inject

@Suppress("UnnecessaryAbstractClass")
abstract class SamsungPublishExtension @Inject constructor(
    objects: ObjectFactory
) {

    /**
     * Specify a directory where prebuilt artifacts such as APKs may be found.
     *
     */
    @get:Internal("Directory mapped to a useful set of files later")
    abstract val artifactDir: DirectoryProperty

    /**
     * Private key for generate jwt token from samsung store
     * https://developer.samsung.com/galaxy-store/galaxy-store-developer-api/create-an-access-token.html#Create-a-service-account
     */
    @get:Input
    abstract val privateKey: Property<String>

    /**
     * Service account id from samsung store
     * https://developer.samsung.com/galaxy-store/galaxy-store-developer-api/create-an-access-token.html#Create-a-service-account
     */
    @get:Input
    abstract val serviceAccountId: Property<String>

    /** Setting necessary to app publishing */
    val publishSetting: PublishSetting = objects.newInstance(PublishSetting::class.java)
    fun publishSetting(action: Action<PublishSetting>) {
        action.execute(publishSetting)
    }
}
