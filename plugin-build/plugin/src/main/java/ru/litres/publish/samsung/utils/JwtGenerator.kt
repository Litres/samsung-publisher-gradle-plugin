package ru.litres.publish.samsung.utils

import com.github.kittinunf.fuel.util.decodeBase64
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import ru.litres.publish.samsung.utils.rsa.DerParser
import java.io.BufferedReader
import java.io.IOException
import java.io.StringReader
import java.security.KeyFactory
import java.security.spec.RSAPrivateCrtKeySpec
import javax.management.openmbean.InvalidKeyException
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class JwtGenerator {

    fun generate(privateKey: String, serviceAccountId: String): String {
        val bas64ByteArrayKey = privateKey.getBase64ByteFromPrivateKeyString()
            ?: throw InvalidKeyException("Provided private key is invalid")

        val keySpec = getRSAKeySpec(bas64ByteArrayKey)
        val kf = KeyFactory.getInstance("RSA")
        val key = kf.generatePrivate(keySpec)

        val currentTime = System.currentTimeMillis().toDuration(DurationUnit.MILLISECONDS)
        val expTime = currentTime.plus(TIME_TOKEN_ALIVE.toDuration(DurationUnit.MINUTES))

        return Jwts.builder()
            .signWith(key, SignatureAlgorithm.RS256)
            .claim(CLAIM_ISS, serviceAccountId)
            .claim(CLAIM_SCOPES, arrayOf(PUBLISHING_SCOPE))
            .claim(CLAIM_IAT, currentTime.inWholeSeconds)
            .claim(CLAIM_EXP, expTime.inWholeSeconds)
            .compact()
    }

    private fun String.getBase64ByteFromPrivateKeyString(): ByteArray? {
        val pkcs8Lines = StringBuilder()
        val rdr = BufferedReader(StringReader(this))
        var line: String?
        while (rdr.readLine().also { line = it } != null) {
            pkcs8Lines.append(line)
        }

        // Remove the "BEGIN" and "END" lines, as well as any whitespace
        var pkcs8Pem = pkcs8Lines.toString()
        pkcs8Pem = pkcs8Pem.replace("-----BEGIN RSA PRIVATE KEY-----", "")
        pkcs8Pem = pkcs8Pem.replace("-----END RSA PRIVATE KEY-----", "")
        pkcs8Pem = pkcs8Pem.replace("\\s+".toRegex(), "")

        // Base64 decode the result
        return pkcs8Pem.decodeBase64()
    }

    @Throws(IOException::class)
    private fun getRSAKeySpec(keyBytes: ByteArray): RSAPrivateCrtKeySpec? {
        var parser = DerParser(keyBytes)
        val sequence = parser.read()
        if (sequence.type != DerParser.SEQUENCE) throw IOException("Invalid DER: not a sequence") // $NON-NLS-1$

        // Parse inside the sequence
        parser = sequence.parser
        parser.read() // Skip version
        val modulus = parser.read().integer
        val publicExp = parser.read().integer
        val privateExp = parser.read().integer
        val prime1 = parser.read().integer
        val prime2 = parser.read().integer
        val exp1 = parser.read().integer
        val exp2 = parser.read().integer
        val crtCoef = parser.read().integer
        return RSAPrivateCrtKeySpec(
            modulus,
            publicExp,
            privateExp,
            prime1,
            prime2,
            exp1,
            exp2,
            crtCoef
        )
    }

    companion object {
        private const val PUBLISHING_SCOPE = "publishing"

        // service-account-id
        private const val CLAIM_ISS = "iss"
        private const val CLAIM_SCOPES = "scopes"

        // current time in second
        private const val CLAIM_IAT = "iat"

        // expiration time in second
        private const val CLAIM_EXP = "exp"

        // min
        private const val TIME_TOKEN_ALIVE = 15
    }
}
