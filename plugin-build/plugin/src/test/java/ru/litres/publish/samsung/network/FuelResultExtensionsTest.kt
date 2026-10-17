package ru.litres.publish.samsung.network

import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.HttpException
import com.github.kittinunf.fuel.core.Response
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.URL

private const val ENDPOINT = "/galaxyapi/fileUpload"
private val URL_UNDER_TEST = URL("https://seller.samsungapps.com/galaxyapi/fileUpload")

class FuelResultExtensionsTest {
    private fun transportError(cause: Throwable) = FuelError.wrap(cause, Response(URL_UNDER_TEST))

    private fun httpError(statusCode: Int) =
        FuelError.wrap(
            HttpException(statusCode, "message"),
            Response(URL_UNDER_TEST, statusCode = statusCode),
        )

    @Test
    fun `a connect timeout names the endpoint, the cause and where to look`() {
        val message = transportError(SocketTimeoutException("Connect timed out")).asPublishException(ENDPOINT).message!!

        assertTrue(message.contains(ENDPOINT))
        assertTrue(message.contains(URL_UNDER_TEST.toString()))
        assertTrue(message.contains("Connect timed out"))
        assertTrue(message.contains("egress"))
    }

    @Test
    fun `the original fuel error is kept as the cause`() {
        val error = transportError(SocketTimeoutException("Connect timed out"))

        assertEquals(error, error.asPublishException(ENDPOINT).cause)
    }

    @Test
    fun `an http failure reports the status instead of the egress hint`() {
        val message = httpError(413).asPublishException(ENDPOINT).message!!

        assertTrue(message.contains("HTTP 413"))
        assertFalse(message.contains("egress"))
    }

    @Test
    fun `transport failures are retryable`() {
        assertTrue(transportError(SocketTimeoutException("Connect timed out")).isRetryable)
        assertTrue(transportError(ConnectException("Connection refused")).isRetryable)
    }

    @Test
    fun `server side and throttling answers are retryable`() {
        assertTrue(httpError(500).isRetryable)
        assertTrue(httpError(503).isRetryable)
        assertTrue(httpError(408).isRetryable)
        assertTrue(httpError(429).isRetryable)
    }

    @Test
    fun `a rejected request is not retryable`() {
        assertFalse(httpError(400).isRetryable)
        assertFalse(httpError(401).isRetryable)
        assertFalse(httpError(413).isRetryable)
    }

    @Test
    fun `an interrupted build is not retried`() {
        assertFalse(transportError(InterruptedException("cancelled")).isRetryable)
    }
}
