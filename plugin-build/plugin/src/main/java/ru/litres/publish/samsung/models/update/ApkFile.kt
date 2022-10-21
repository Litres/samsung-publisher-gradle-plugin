package ru.litres.publish.samsung.models.update

import kotlinx.serialization.Serializable

@Serializable
data class ApkFile(
    val filekey: String,
    val gms: String
)
