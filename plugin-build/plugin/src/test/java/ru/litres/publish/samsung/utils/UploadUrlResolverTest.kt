package ru.litres.publish.samsung.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.litres.publish.samsung.exception.UploadApkException

private const val FALLBACK = "/galaxyapi/fileUpload"
private const val SESSION_URL = "https://seller.samsungapps.com/galaxyapi/fileUpload"

class UploadUrlResolverTest {
    @Test
    fun `the url from the session is used when there is no override`() {
        assertEquals(SESSION_URL, resolveUploadUrl(override = null, sessionUrl = SESSION_URL, fallbackPath = FALLBACK))
    }

    @Test
    fun `an override wins over the session url`() {
        val resolved = resolveUploadUrl(override = "https://mirror.example.com/upload", SESSION_URL, FALLBACK)

        assertEquals("https://mirror.example.com/upload", resolved)
    }

    @Test
    fun `an override is not checked against the store host`() {
        val resolved = resolveUploadUrl(override = "http://10.0.0.1:8080/upload", sessionUrl = null, FALLBACK)

        assertEquals("http://10.0.0.1:8080/upload", resolved)
    }

    @Test
    fun `a blank override does not shadow the session url`() {
        assertEquals(SESSION_URL, resolveUploadUrl(override = "   ", sessionUrl = SESSION_URL, fallbackPath = FALLBACK))
    }

    @Test
    fun `a session without a url falls back to the known path`() {
        assertEquals(FALLBACK, resolveUploadUrl(override = null, sessionUrl = null, fallbackPath = FALLBACK))
        assertEquals(FALLBACK, resolveUploadUrl(override = null, sessionUrl = "", fallbackPath = FALLBACK))
    }

    @Test
    fun `any samsungapps subdomain is accepted`() {
        val url = "https://seller-eu.samsungapps.com/galaxyapi/fileUpload"

        assertEquals(url, resolveUploadUrl(override = null, sessionUrl = url, fallbackPath = FALLBACK))
    }

    @Test
    fun `a session url outside the store is refused instead of followed`() {
        val error =
            assertThrows(UploadApkException::class.java) {
                resolveUploadUrl(override = null, sessionUrl = "https://evil.example.com/upload", FALLBACK)
            }

        assertTrue(error.message!!.contains("https://evil.example.com/upload"))
        assertTrue(error.message!!.contains("networkSetting.uploadUrl"))
    }

    @Test
    fun `a host that merely ends with the store name is refused`() {
        assertThrows(UploadApkException::class.java) {
            resolveUploadUrl(override = null, sessionUrl = "https://samsungapps.com.evil.example/upload", FALLBACK)
        }
    }

    @Test
    fun `an unparsable session url is refused`() {
        assertThrows(UploadApkException::class.java) {
            resolveUploadUrl(override = null, sessionUrl = "not a url", fallbackPath = FALLBACK)
        }
    }
}
