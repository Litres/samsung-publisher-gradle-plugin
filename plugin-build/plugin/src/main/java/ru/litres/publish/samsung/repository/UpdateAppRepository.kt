package ru.litres.publish.samsung.repository

import com.github.kittinunf.fuel.core.FileDataPart
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.serialization.kotlinxDeserializerOf
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import ru.litres.publish.samsung.PublishSetting
import ru.litres.publish.samsung.exception.UploadApkException
import ru.litres.publish.samsung.models.update.ApkFile
import ru.litres.publish.samsung.models.update.UpdateDataRequest
import ru.litres.publish.samsung.models.update.UpdateDataResponse
import ru.litres.publish.samsung.models.upload.UploadResponse
import ru.litres.publish.samsung.models.upload.session.UploadSessionResponse
import ru.litres.publish.samsung.network.NetworkClient
import java.io.File
import kotlin.math.roundToInt

class UpdateAppRepository(
    private val networkClient: NetworkClient,
    private val uploadNetworkClient: NetworkClient
) {

    fun update(apk: File, publishSetting: PublishSetting): Boolean {
        val sessionId = getUploadSessionId()
        val fileKey = uploadApk(sessionId, apk)
        return updateApplication(fileKey, publishSetting)
    }

    private fun getUploadSessionId(): String {
        val sessionResult = networkClient.post(CREATE_UPLOAD_SESSION)
            .responseObject<UploadSessionResponse>(kotlinxDeserializerOf())

        return sessionResult.third.get().sessionId
            ?: throw UploadApkException("Field \"sessionId\" not found in \"/seller/createUploadSessionId\"")
    }

    private fun uploadApk(sessionId: String, file: File): String {
        var prevProgress = 0
        val uploadResult = uploadNetworkClient.upload(UPLOAD_APK, listOf(SESSION_ID_FIELD to sessionId))
            .add { FileDataPart(file, name = "file") }
            .progress { readBytes, totalBytes ->
                val progress = (readBytes.toFloat() / totalBytes.toFloat() * PERCENT_MULTIPLIER).roundToInt()
                if (progress != prevProgress) {
                    val readyMb = readBytes / KB_DIVIDER
                    val totalMb = totalBytes / KB_DIVIDER
                    println("Uploaded ${readyMb}kb / ${totalMb}kb ($progress %)")
                }
                prevProgress = progress
            }
            .responseObject<UploadResponse>(kotlinxDeserializerOf())

        val uploadResponse = uploadResult.third.fold(
            success = { it },
            failure = { error ->
                println(error)
                println(error.errorData.decodeToString())
                null
            }
        )

        if (uploadResponse?.errorMsg != null) throw UploadApkException(uploadResponse.errorMsg)
        val key = uploadResponse?.fileKey
        if (key.isNullOrBlank()) throw UploadApkException("Field \"fileKey\" not found in \"/galaxyapi/fileUpload\"")
        return key
    }

    private fun updateApplication(fileKey: String, publishSetting: PublishSetting): Boolean {
        val contentId = publishSetting.contentId ?: return false
        val paid = if (publishSetting.paid) "Y" else "N"
        val gms = if (publishSetting.hasGoogleService) "Y" else "N"

        val data = UpdateDataRequest(
            contentId,
            listOf(ApkFile(fileKey, gms)),
            publishSetting.defaultLanguageCode,
            paid
        )
        val json = Json.encodeToJsonElement(data)

        val updateResult = networkClient.post(UPDATE_APPLICATION)
            .jsonBody(json.jsonObject.toString())
            .responseObject<UpdateDataResponse>(kotlinxDeserializerOf())

        val updateResponse = updateResult.third.fold(
            success = { it },
            failure = {
                println(it.errorData.decodeToString())
                null
            }
        )
        if (updateResponse?.errorMsg != null) throw UploadApkException(updateResponse.errorMsg)

        return updateResponse?.contentStatus == SUCCESS_UPDATE_APK_RESULT
    }

    companion object {
        private const val CREATE_UPLOAD_SESSION = "/seller/createUploadSessionId"
        private const val UPLOAD_APK = "/galaxyapi/fileUpload"
        private const val UPDATE_APPLICATION = "/seller/contentUpdate"

        private const val SESSION_ID_FIELD = "sessionId"
        private const val SUCCESS_UPDATE_APK_RESULT = "REGISTERING"

        private const val KB_DIVIDER = 1024
        private const val PERCENT_MULTIPLIER = 100
    }
}
