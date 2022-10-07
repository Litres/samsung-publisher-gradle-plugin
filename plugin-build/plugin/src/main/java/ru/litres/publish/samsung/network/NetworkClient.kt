package ru.litres.publish.samsung.network

import com.github.kittinunf.fuel.core.FoldableRequestInterceptor
import com.github.kittinunf.fuel.core.FoldableResponseInterceptor
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.fuel.core.Method
import com.github.kittinunf.fuel.core.Parameters

class NetworkClient(
    baseUrl: String
) {

    private var authToken: String? = null

    private val fuelManager = FuelManager().apply {
        basePath = baseUrl
    }

    fun get(
        path: String,
        parameters: Parameters? = null
    ) = fuelManager.get(path, parameters)

    fun post(
        path: String,
        parameters: Parameters? = null
    ) = fuelManager.post(path, parameters)

    fun upload(
        path: String,
        parameters: Parameters?
    ) = fuelManager.upload(path, Method.POST, parameters)

    fun setCommonHeaders(headers: Map<String, String>?) {
        fuelManager.baseHeaders = headers
        authToken?.let { setBearerAuth(it) }
    }

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
}
