package ru.litres.publish.samsung.repository

import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.coroutines.awaitObjectResponse
import com.github.kittinunf.fuel.serialization.kotlinxDeserializerOf
import ru.litres.publish.samsung.exception.ReceiveAccessTokenException
import ru.litres.publish.samsung.models.access.AccessTokenResponse
import ru.litres.publish.samsung.network.NetworkClient
import ru.litres.publish.samsung.utils.JwtGenerator

class GenerateTokenRepository(
    private val networkClient: NetworkClient,
    private val jwtGenerator: JwtGenerator
) {

    suspend fun getAccessToken(privateKey: String, serviceAccountId: String): String {
        val jwtToken = jwtGenerator.generate(privateKey, serviceAccountId)
        println("------ JWT generated -------")
        val accessResult = networkClient.post(ACCESS_TOKEN)
            .authentication()
            .bearer(jwtToken)
            .awaitObjectResponse<AccessTokenResponse>(kotlinxDeserializerOf())
        val accessResponse = accessResult.third

        return accessResponse.createdItem?.accessToken
            ?: throw ReceiveAccessTokenException("Not found access token")
    }

    companion object {
        private const val ACCESS_TOKEN = "/auth/accessToken"
    }
}
