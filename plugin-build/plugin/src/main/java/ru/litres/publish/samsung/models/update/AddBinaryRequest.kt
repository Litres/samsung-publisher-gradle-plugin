package ru.litres.publish.samsung.models.update

import kotlinx.serialization.Serializable

@Serializable
data class AddBinaryRequest(
    val contentId: String,
    val filekey: String,
    val gms: String,
)
