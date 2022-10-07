package ru.litres.publish.samsung.models.access

import kotlinx.serialization.Serializable

@Serializable
data class AccessToken(
    val accessToken: String?
)
