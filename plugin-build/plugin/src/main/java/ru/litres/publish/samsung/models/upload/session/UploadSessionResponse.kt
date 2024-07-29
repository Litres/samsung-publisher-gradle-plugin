package ru.litres.publish.samsung.models.upload.session

import kotlinx.serialization.Serializable

@Serializable
data class UploadSessionResponse(
    val url: String?,
    val sessionId: String?,
)
