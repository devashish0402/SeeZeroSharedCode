package com.seezero.clientapi.implementation

import com.seezero.clientapi.global.getDefaultPasswordHardener
import com.seezero.clientapi.global.getDefaultPasswordSalt
import java.security.MessageDigest
import java.security.SecureRandom
import java.security.spec.KeySpec
import java.util.*
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec


fun byteArrayOfInts(vararg ints: Int) = ByteArray(ints.size) { pos -> ints[pos].toByte() }

fun getHashFromString(input: String): String {
    //make function case insensitive
    return oneWayHashBytes(input.toUpperCase() + getDefaultPasswordHardener())
}

fun oneWayHashBytes(phrase: String): String {
    val hardenedPhrase = phrase + getDefaultPasswordHardener()
    val factory2 = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
    val spec: KeySpec = PBEKeySpec(
        hardenedPhrase.toCharArray(), getDefaultPasswordSalt(), DEFAULT_HASH_ITERATIONS, 256
    )  //pas iv+ key length in bits

    val theSecret = factory2.generateSecret(spec).encoded
    return Base64.getEncoder().encodeToString(theSecret)

}


fun createRandomHash(): String {
    val random = SecureRandom()
    var randomStr = ByteArray(16)
    random.nextBytes(randomStr)
    return Base64.getEncoder().encodeToString(randomStr)
}

fun getDigest(p_value: ByteArray?, p_algo: String): String {
    val md = MessageDigest.getInstance(p_algo)
    val digest = md.digest(p_value)
    return digest.fold("", { str, it -> str + "%02x".format(it) })
}


fun adler32sum(phrase: String): Int {
    val MOD_ADLER = 65521
    var a = 1
    var b = 0

    val byteArray = (phrase + getDefaultPasswordHardener()).toByteArray()
    for (byte in byteArray) {
        a = (byte + a) % MOD_ADLER
        b = (b + a) % MOD_ADLER
    }

    // note: Int is 32 bits, which this requires
    return b * 65536 + a   // or (b << 16) + a
}

fun getAdler32HexString(s: String): String {
    return Integer.toHexString(adler32sum(s))
}