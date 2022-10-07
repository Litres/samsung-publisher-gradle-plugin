package ru.litres.publish.samsung.usecase

import com.github.kittinunf.fuel.core.interceptors.LogRequestInterceptor
import com.github.kittinunf.fuel.core.interceptors.LogResponseInterceptor
import org.gradle.api.file.Directory
import ru.litres.publish.samsung.network.NetworkClient
import ru.litres.publish.samsung.repository.GenerateTokenRepository
import ru.litres.publish.samsung.repository.UpdateAppRepository
import ru.litres.publish.samsung.utils.API_BASE_URL
import ru.litres.publish.samsung.utils.HEADER_SERVICE_ACCOUNT_ID
import ru.litres.publish.samsung.utils.JwtGenerator
import ru.litres.publish.samsung.utils.UPLOAD_API_BASE_URL

class PublishBuildUseCase(
    private val networkClient: NetworkClient = NetworkClient(API_BASE_URL).apply {
        addRequestInterceptor(LogRequestInterceptor)
        addResponseInterceptor(LogResponseInterceptor)
    },
    private val uploadNetworkClient: NetworkClient = NetworkClient(UPLOAD_API_BASE_URL).apply {
        addRequestInterceptor(LogRequestInterceptor)
        addResponseInterceptor(LogResponseInterceptor)
    },
    private val jwtGenerator: JwtGenerator = JwtGenerator()
) {

    suspend operator fun invoke(
        serviceId: String,
        privateKey: String,
        contentId: String,
        artifactDir: Directory
    ) {
        networkClient.appendCommonHeaders(mapOf(HEADER_SERVICE_ACCOUNT_ID to serviceId))
        uploadNetworkClient.appendCommonHeaders(mapOf(HEADER_SERVICE_ACCOUNT_ID to serviceId))

        val generateTokenRepository = GenerateTokenRepository(networkClient, jwtGenerator)
        val updateAppRepository = UpdateAppRepository(networkClient, uploadNetworkClient)

        val accessToken = generateTokenRepository.getAccessToken(privateKey, serviceId)
        networkClient.setBearerAuth(accessToken)
        uploadNetworkClient.setBearerAuth(accessToken)

        val success = updateAppRepository.update(contentId, artifactDir)

        if (success) {
            println("-------- Success updated apk ----------")
        } else {
            println("-------- Error while updating apk ----------")
        }
    }
}
