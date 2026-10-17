package ru.litres.publish.samsung.network

import com.github.kittinunf.fuel.core.FoldableRequestInterceptor
import com.github.kittinunf.fuel.core.FoldableResponseInterceptor
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.fuel.core.Method
import com.github.kittinunf.fuel.core.Parameters
import com.github.kittinunf.fuel.core.interceptors.LogRequestInterceptor
import com.github.kittinunf.fuel.core.interceptors.LogResponseInterceptor
import java.net.Proxy

class NetworkClient(
    baseUrl: String,
    connectTimeoutMs: Int = DEFAULT_CONNECT_TIMEOUT_MS,
    readTimeoutMs: Int = DEFAULT_READ_TIMEOUT_MS,
    proxy: Proxy? = null,
) {
    private var authToken: String? = null

    private val fuelManager =
        FuelManager().apply {
            basePath = baseUrl
            this.timeoutInMillisecond = connectTimeoutMs
            this.timeoutReadInMillisecond = readTimeoutMs
            this.proxy = proxy
        }

    init {
        addRequestInterceptor(LogRequestInterceptor)
        addResponseInterceptor(LogResponseInterceptor)
    }

    fun get(
        path: String,
        parameters: Parameters? = null,
    ) = fuelManager.get(path, parameters)

    fun post(
        path: String,
        parameters: Parameters? = null,
    ) = fuelManager.post(path, parameters)

    fun upload(
        path: String,
        parameters: Parameters?,
    ) = fuelManager.upload(path, Method.POST, parameters)

    fun appendCommonHeaders(headers: Map<String, String>) {
        val prevHeaders = fuelManager.baseHeaders?.toMutableMap() ?: mutableMapOf()
        prevHeaders.putAll(headers)
        fuelManager.baseHeaders = prevHeaders
    }

    fun setBearerAuth(token: String) {
        authToken = token
        val headers = fuelManager.baseHeaders?.toMutableMap() ?: mutableMapOf()
        headers[Headers.AUTHORIZATION] = "Bearer $authToken"
        fuelManager.baseHeaders = headers
    }

    fun addRequestInterceptor(requestInterceptor: FoldableRequestInterceptor) {
        fuelManager.addRequestInterceptor(requestInterceptor)
    }

    fun addResponseInterceptor(responseInterceptor: FoldableResponseInterceptor) {
        fuelManager.addResponseInterceptor(responseInterceptor)
    }

    companion object {
        /**
         * Timeout of the TCP connect phase only.
         * A connection that is not established within this window will not be established at all,
         * so a short value turns an unreachable host into a fast failure instead of a long hang.
         */
        const val DEFAULT_CONNECT_TIMEOUT_MS = 20_000

        /** Response timeout for the small json calls of the publish api. */
        const val DEFAULT_READ_TIMEOUT_MS = 120_000

        /**
         * Response timeout for "/galaxyapi/fileUpload".
         * Galaxy Store accepts the whole binary and scans it before answering,
         * so a multi hundred megabyte apk needs far more than the default.
         */
        const val UPLOAD_READ_TIMEOUT_MS = 900_000
    }
}
