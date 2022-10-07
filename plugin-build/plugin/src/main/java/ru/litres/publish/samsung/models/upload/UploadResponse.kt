package ru.litres.publish.samsung.models.upload

import kotlinx.serialization.Serializable

@Serializable
data class UploadResponse(
    val fileKey: String?,
    val fileName: String?,
    val fileSize: String?,
    val errorCode: String?,
    val errorMsg: String?
)
