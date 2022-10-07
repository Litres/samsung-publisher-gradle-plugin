package ru.litres.publish.samsung.repository

import com.github.kittinunf.fuel.core.FileDataPart
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.coroutines.awaitObjectResponse
import com.github.kittinunf.fuel.serialization.kotlinxDeserializerOf
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonArray
import org.gradle.api.file.Directory
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

    suspend fun update(contentId: String, pathToFolderWithApk: Directory): Boolean {
        val sessionId = getUploadSessionId()
        val fileKey = uploadApk(sessionId, pathToFolderWithApk.findApkFile())
        return updateApplication(contentId, fileKey)
    }

    private fun Directory.findApkFile(): File {
        return this.asFileTree.find { it.extension == "apk" }
            ?: throw UploadApkException("Apk file not found in folder \"${this.asFile.absolutePath}\"")
    }

    private suspend fun getUploadSessionId(): String {
        val sessionResult = networkClient.post(CREATE_UPLOAD_SESSION)
            .awaitObjectResponse<UploadSessionResponse>(kotlinxDeserializerOf())

        return sessionResult.third.sessionId
            ?: throw UploadApkException("Field \"sessionId\" not found in \"/seller/createUploadSessionId\"")
    }

    private suspend fun uploadApk(sessionId: String, file: File): String {
        println("------ Found apk -------")
        println(file.absolutePath)
        println("------ Found apk -------")
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
            .awaitObjectResponse<UploadResponse>(kotlinxDeserializerOf())
        val uploadResponse = uploadResult.third

        if (uploadResponse.errorMsg != null) throw UploadApkException(uploadResponse.errorMsg)
        return uploadResult.third.fileKey
            ?: throw UploadApkException("Field \"fileKey\" not found in \"/galaxyapi/fileUpload\"")
    }

    private suspend fun updateApplication(contentId: String, fileKey: String): Boolean {
        val data = listOf(UpdateDataRequest(contentId, listOf(ApkFile(fileKey))))
        val json = Json.encodeToJsonElement(data)

        val updateResult = networkClient.post(UPDATE_APPLICATION)
            .jsonBody(json.jsonArray.toString())
            .awaitObjectResponse<UpdateDataResponse>(kotlinxDeserializerOf())

        val updateResponse = updateResult.third
        if (updateResponse.errorMsg != null) throw UploadApkException(updateResponse.errorMsg)

        return updateResponse.contentStatus == SUCCESS_UPDATE_APK_RESULT
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
