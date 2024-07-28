package ru.litres.publish.samsung.models.access

import kotlinx.serialization.Serializable

@Serializable
data class AccessTokenResponse(
    val ok: Boolean,
    val createdItem: AccessToken?,
)
