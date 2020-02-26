package com.seezero.clientapi.implementation

import com.seezero.clientapi.global.getDefaultPasswordSalt
import com.seezero.clientapi.models.C0Hash
import com.seezero.clientapi.models.C0Salt
import com.seezero.clientapi.models.C0SymKey
import java.io.*
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.SecureRandom
import java.security.spec.KeySpec
import java.util.*
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

val DEFAULT_LENGTH_BITS = 256
val DEFAULT_HASH_ITERATIONS = 10000

fun symmetricEncryptStream(
    p_inStream: InputStream,
    p_outStream: OutputStream?,
    p_symKey: C0SymKey? = null
): C0SymKey {
    var theKey = p_symKey
    if (p_symKey == null) {
        theKey =  generateRandomSymKey()
    }
    val keySpec = SecretKeySpec(theKey?.key, "AES")
    val ivSpec = IvParameterSpec(theKey?.iv)
    val sha256Digest = MessageDigest.getInstance("SHA-256")
    val aes = Cipher.getInstance("AES/CBC/PKCS5PADDING") //("AES")
    aes.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
    val buffer = ByteArray(theKey!!.key.size)
    var total = 0
    val out = CipherOutputStream(p_outStream, aes)
    var len = p_inStream.read(buffer)

    while (len > 0) {
        sha256Digest.update(buffer.sliceArray(IntRange(0,len-1)))
        out.write(buffer,0,len)
        out.flush()
        total += len
         len = p_inStream.read(buffer)
    }
    //commenting out this close will make the process miss the final padding
    out.close()
    val computedHash = sha256Digest.digest()

    theKey.hash = C0Hash(computedHash, "SHA-256")
    return theKey
}

fun symmetricDecryptStream(
    p_inStream: InputStream,
    p_outStream: OutputStream,
    p_symKey: C0SymKey? = null
): C0Hash {
    var theKey = p_symKey
    if (p_symKey == null) {
        theKey = generateRandomSymKey()
    }
  //  println("At Decrypt sym key used to decrypt:" + getXmlFromSymKey(theKey!!))

    val sha256Digest = MessageDigest.getInstance("SHA-256")

    val keySpec = SecretKeySpec(theKey?.key, "AES")
    val ivSpec = IvParameterSpec(theKey?.iv)
    val aes = Cipher.getInstance("AES/CBC/PKCS5PADDING") //("AES")
    aes.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
    val buffer = ByteArray(theKey!!.key.size)
    var total = 0
    val inFile = CipherInputStream(p_inStream, aes)
    var len = inFile.read(buffer)

    while (len > 0) {
        sha256Digest.update(buffer.sliceArray(IntRange(0,len-1)))
        buffer.sliceArray(IntRange(0,len-1)).toString()
        p_outStream.write(buffer,0,len)
        total += len
        len = inFile.read(buffer)
    }
    val computedHash = sha256Digest.digest()
    val c0Hash = C0Hash(computedHash, "SHA-256")

    return c0Hash
}

fun symmetricEncryptBytes(
    p_value: ByteArray,
    p_password: String,
    p_salt: C0Salt
): ByteArray {

    return symmetricEncryptBytes(p_value, generateSymKeyFromPassword(p_password, p_salt))

}


fun symmetricEncryptBytes(
    p_value: ByteArray,
    p_key: C0SymKey
): ByteArray {

    val inp = ByteArrayInputStream(p_value)
    val out = ByteArrayOutputStream()

    symmetricEncryptStream(inp, out, p_key)
    out.flush()
    return out.toByteArray()
}

fun symmetricDecryptBytes(
    p_value: ByteArray?,
    p_password: String,
    p_salt: C0Salt
): ByteArray {
    val inp = ByteArrayInputStream(p_value)
    val out = ByteArrayOutputStream()

    val symKey: C0SymKey = generateSymKeyFromPassword(p_password, p_salt)

    symmetricDecryptStream(inp, out, symKey)
    return out.toByteArray()
}

fun symmetricDecryptBytes(
    p_value: ByteArray?,
    p_symKey: C0SymKey
): ByteArray {
    val inp = ByteArrayInputStream(p_value)
    val out = ByteArrayOutputStream()
    symmetricDecryptStream(inp, out, p_symKey)
    return out.toByteArray()
}

fun symmetricEncryptString(p_value: String, p_password: String, p_salt: C0Salt): ByteArray {
    return symmetricEncryptBytes(p_value.toByteArray(StandardCharsets.UTF_8), p_password, p_salt)
}

fun symmetricDecryptString(p_value: String, p_password: String, p_salt: C0Salt): ByteArray {
    return symmetricDecryptBytes(p_value.toByteArray(StandardCharsets.UTF_8), p_password, p_salt)
}


fun symmetricEncryptFile(
    p_inPath: String,
    p_outPath: String,
    c0SymKey: C0SymKey? = null
): C0SymKey {

    val inStream1 = FileInputStream(p_inPath)
    val outStream1 = FileOutputStream(p_outPath)
    val returnSymKey = symmetricEncryptStream(inStream1, outStream1, c0SymKey)
    return returnSymKey
}


fun generateSymKeyFromPassword(
    p_password: String,
    p_salt: C0Salt,
    p_length: Int = 256,
    p_hashIterations: Int = DEFAULT_HASH_ITERATIONS
): C0SymKey {
    val symKey = C0SymKey()

    symKey.salt = p_salt
    val factory2 = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
    val keyBytesLength = p_length + 128/* length of ivy*/  //generate up front in java unlike c#
    val spec: KeySpec = PBEKeySpec(
        p_password.toCharArray(), getDefaultPasswordSalt(), p_hashIterations, keyBytesLength
    )  //pas iv+ key length in bits

    val secretKey2 = factory2.generateSecret(spec)
    val theSecret = secretKey2.encoded

    symKey.iv = Arrays.copyOfRange(theSecret, 0, 16)
    symKey.key = Arrays.copyOfRange(theSecret, 16, 48)
    symKey.hash = C0Hash(getDefaultPasswordSalt()) //set to random init value..
    return  symKey
}


fun generateRandomSymKey(
    p_length: Int = DEFAULT_LENGTH_BITS,
    p_hashIterations: Int = DEFAULT_HASH_ITERATIONS
): C0SymKey {


    val random = SecureRandom()
    val password = ByteArray(p_length)
    random.nextBytes(password)
    val salt = C0Salt()
    return generateSymKeyFromPassword(password.toString(), salt, p_length, p_hashIterations)
}

