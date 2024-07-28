package ru.litres.publish.samsung.models.update

import kotlinx.serialization.Serializable

@Serializable
data class UpdateDataResponse(
    val contentId: String?,
    val contentStatus: String?,
    val httpStatus: String?,
    val errorCode: String?,
    val errorMsg: String?,
)
