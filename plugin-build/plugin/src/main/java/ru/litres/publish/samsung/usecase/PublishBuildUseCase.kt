package ru.litres.publish.samsung.usecase

import org.gradle.api.file.Directory
import ru.litres.publish.samsung.DebugSetting
import ru.litres.publish.samsung.NetworkSetting
import ru.litres.publish.samsung.PublishSetting
import ru.litres.publish.samsung.exception.UploadApkException
import ru.litres.publish.samsung.network.NetworkClient
import ru.litres.publish.samsung.repository.GenerateTokenRepository
import ru.litres.publish.samsung.repository.UpdateAppRepository
import ru.litres.publish.samsung.utils.API_BASE_URL
import ru.litres.publish.samsung.utils.HEADER_SERVICE_ACCOUNT_ID
import ru.litres.publish.samsung.utils.JwtGenerator
import ru.litres.publish.samsung.utils.UPLOAD_API_BASE_URL
import ru.litres.publish.samsung.utils.createProxy
import java.io.File

class PublishBuildUseCase(
    private val debugSetting: DebugSetting,
    private val networkSetting: NetworkSetting = NetworkSetting(),
    private val networkClient: NetworkClient = createApiClient(networkSetting),
    private val uploadNetworkClient: NetworkClient = createUploadClient(networkSetting),
    private val jwtGenerator: JwtGenerator = JwtGenerator(),
) {
    operator fun invoke(
        serviceId: String,
        privateKey: String,
        artifactDir: Directory,
        publishSetting: PublishSetting,
    ) {
        val apk = artifactDir.findApkFile()
        println("------ Found apk -------")
        println(apk.absolutePath)
        println("------ Found apk -------")
        networkClient.appendCommonHeaders(mapOf(HEADER_SERVICE_ACCOUNT_ID to serviceId))
        uploadNetworkClient.appendCommonHeaders(mapOf(HEADER_SERVICE_ACCOUNT_ID to serviceId))

        val generateTokenRepository = GenerateTokenRepository(networkClient, jwtGenerator)
        val updateAppRepository =
            UpdateAppRepository(debugSetting, networkSetting, networkClient, uploadNetworkClient)

        val accessToken = generateTokenRepository.getAccessToken(privateKey, serviceId)
        networkClient.setBearerAuth(accessToken)
        uploadNetworkClient.setBearerAuth(accessToken)

        updateAppRepository.update(apk, publishSetting)
        println("-------- Success updated apk ----------")

        if (publishSetting.submitReview) {
            updateAppRepository.submitReview(publishSetting)
            println("-------- Success submit review ----------")
        }
    }

    // if dry mode - return fake file
    private fun Directory.findApkFile(): File {
        return asFileTree.find { it.extension == "apk" }
            ?: if (debugSetting.dryMode) {
                File("/")
            } else {
                throw UploadApkException("Apk file not found in folder \"${this.asFile.absolutePath}\"")
            }
    }

    companion object {
        private fun createApiClient(networkSetting: NetworkSetting) =
            NetworkClient(
                baseUrl = API_BASE_URL,
                connectTimeoutMs = networkSetting.connectTimeoutMs,
                readTimeoutMs = networkSetting.readTimeoutMs,
                proxy = createProxy(networkSetting.proxy),
            )

        private fun createUploadClient(networkSetting: NetworkSetting) =
            NetworkClient(
                baseUrl = UPLOAD_API_BASE_URL,
                connectTimeoutMs = networkSetting.connectTimeoutMs,
                readTimeoutMs = networkSetting.uploadReadTimeoutMs,
                proxy = createProxy(networkSetting.proxy),
            )
    }
}
