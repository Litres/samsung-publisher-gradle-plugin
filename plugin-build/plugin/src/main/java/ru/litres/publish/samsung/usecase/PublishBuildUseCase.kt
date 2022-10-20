package ru.litres.publish.samsung.usecase

import org.gradle.api.file.Directory
import ru.litres.publish.samsung.PublishSetting
import ru.litres.publish.samsung.exception.UploadApkException
import ru.litres.publish.samsung.network.NetworkClient
import ru.litres.publish.samsung.repository.GenerateTokenRepository
import ru.litres.publish.samsung.repository.UpdateAppRepository
import ru.litres.publish.samsung.utils.API_BASE_URL
import ru.litres.publish.samsung.utils.HEADER_SERVICE_ACCOUNT_ID
import ru.litres.publish.samsung.utils.JwtGenerator
import ru.litres.publish.samsung.utils.UPLOAD_API_BASE_URL
import java.io.File

class PublishBuildUseCase(
    private val networkClient: NetworkClient = NetworkClient(API_BASE_URL),
    private val uploadNetworkClient: NetworkClient = NetworkClient(UPLOAD_API_BASE_URL),
    private val jwtGenerator: JwtGenerator = JwtGenerator()
) {
    operator fun invoke(
        serviceId: String,
        privateKey: String,
        artifactDir: Directory,
        publishSetting: PublishSetting
    ) {
        val apk = artifactDir.findApkFile()
        println("------ Found apk -------")
        println(apk.absolutePath)
        println("------ Found apk -------")
        networkClient.appendCommonHeaders(mapOf(HEADER_SERVICE_ACCOUNT_ID to serviceId))
        uploadNetworkClient.appendCommonHeaders(mapOf(HEADER_SERVICE_ACCOUNT_ID to serviceId))

        val generateTokenRepository = GenerateTokenRepository(networkClient, jwtGenerator)
        val updateAppRepository = UpdateAppRepository(networkClient, uploadNetworkClient)

        val accessToken = generateTokenRepository.getAccessToken(privateKey, serviceId)
        networkClient.setBearerAuth(accessToken)
        uploadNetworkClient.setBearerAuth(accessToken)

        val success = updateAppRepository.update(apk, publishSetting)

        if (success) {
            println("-------- Success updated apk ----------")
        } else {
            println("-------- Error while updating apk ----------")
        }
    }

    private fun Directory.findApkFile(): File {
        return this.asFileTree.find { it.extension == "apk" }
            ?: throw UploadApkException("Apk file not found in folder \"${this.asFile.absolutePath}\"")
    }
}
