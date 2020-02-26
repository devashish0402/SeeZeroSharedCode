package com.seezero.clientapi.implementation

import com.seezero.clientapi.global.GlobalStorage
import com.seezero.clientapi.models.*
import java.io.InputStream
import java.io.OutputStream
import java.security.*
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.time.LocalDateTime
import java.util.*
import javax.crypto.Cipher


fun createTokenBank(): String {

    val global = GlobalStorage
    global.keybank.put("SELF-SELF", generateAsymkeyPair())   //store the key pair for self
    global.keybank.put("SELF-SERVER", generateAsymkeyPair())   //store the key self to server

    //todo -- needs to be wrapped in server's public key
    //  return global.keybank.get("C0SERVER")!!.getC0AsymKeyFromXml(false) //send public key only
    return ""
}

fun generateAsymkeyPair(size: Int = 4096): C0AsymKey {
    val kp = KeyPairGenerator.getInstance("RSA").run {
        initialize(size, SecureRandom.getInstanceStrong())
        genKeyPair()
    }
    return C0AsymKey(kp, true)
}

fun encryptAndSignStream(
    p_inStream: InputStream,
    p_outStream: OutputStream,
    p_public: C0AsymKey,
    p_private: C0AsymKey
): C0MetaData {
    //both keys passed should be from a single key pair.
    // if Alice send bob , her public key (Alice ->Bob)
    //Assume this encrypt will happen on Bob's side
    // Bob encrypts symmetrically and signs the hash with his private key ( Bob->Alice)
    val symKey = symmetricEncryptStream(p_inStream, p_outStream) //encrypt with ramdom Sym Key
    symKey.hash = ComputeSignature(symKey.hash!!, p_private)
    val metaData = C0MetaData()
    metaData.encryptedUTC = LocalDateTime.now()
    metaData.encryptionKeyId = p_public.id
    val symXmlBytes = Base64.getEncoder().encode(getXmlFromC0SymKey(symKey).toByteArray(Charsets.UTF_8))
    metaData.encryptedSymKey = asymmetricEncryptBytes(symXmlBytes, p_public)

    return metaData
}

fun encryptAndSignStream(p_inStream: InputStream, p_outStream: OutputStream, sLink: C0TrustedLink): C0MetaData {
    return encryptAndSignStream(p_inStream, p_outStream, sLink.theirKey!!, sLink.myKey)
}


fun decryptAndVerifyStream(
    p_inStream: InputStream,
    p_outStream: OutputStream,
    p_metaData: C0MetaData,
    p_sLink: C0TrustedLink
): Boolean {
    val privateKey = p_sLink.myKey
    val symKeyBytes = asymmetricDecryptBytes(p_metaData.encryptedSymKey, privateKey)
    val symKeyXml: String = Base64.getDecoder().decode(symKeyBytes).toString(Charsets.UTF_8)

    val symKey: C0SymKey = getSymKeyFromXml(symKeyXml)
    val decryptHash: C0Hash = symmetricDecryptStream(p_inStream, p_outStream, symKey)
    return if (!symKey.hash!!.signatureKeyId.equals(p_sLink.theirKey!!.id)) {
        false
    } else {
        VerifySignature(decryptHash, symKey.hash!!, p_sLink.theirKey!!)
    }
}

fun ComputeSignature(p_hash: C0Hash, p_private: C0AsymKey): C0Hash {

    return C0Hash(Signature.getInstance("SHA512withRSA").run {
        initSign(p_private.keyPair.private)
        update(p_hash.value)
        sign()

    }, p_hash.algo, p_private.id)
}

fun VerifySignature(p_hash: C0Hash, p_signature: C0Hash, p_public: C0AsymKey): Boolean {
    if (!p_hash.algo.equals(p_signature.algo) || !p_signature.signatureKeyId.equals(p_public.id)) {
        return false
    }
    return Signature.getInstance("SHA512withRSA").run {
        initVerify(p_public.keyPair.public)
        update(p_hash.value)
        verify(p_signature.value)
    }
}


fun loadPublicKey(publicBytes: ByteArray): PublicKey {
    return KeyFactory.getInstance("RSA").generatePublic(X509EncodedKeySpec(publicBytes))
}

fun loadPrivateKey(privateBytes: ByteArray): PrivateKey {
    return KeyFactory.getInstance("RSA").generatePrivate(PKCS8EncodedKeySpec(privateBytes))
}


fun asymmetricEncryptBytes(p_data: ByteArray, p_public: C0AsymKey): ByteArray {
    val maxsize: Int = 446 // (p_public.pubKey.encoded.size - 384) / 8 + 6
    val encryptedChunks = ArrayList<ByteArray>()
    var encryptedLen = 0
    var pos = 0
    while (pos < p_data.size) {
        val len = Math.min(p_data.size - pos, maxsize)

        val chunk = ByteArray(len)
        System.arraycopy(p_data, pos, chunk, 0, len)

        val encryptedChunk: ByteArray = encrypt(chunk, p_public.keyPair.public)
        encryptedChunks.add(encryptedChunk)
        encryptedLen += encryptedChunk.size
        pos += len
    }

    val encryptedData = ByteArray(encryptedLen)
    var chunkPos = 0

    for (chunk in encryptedChunks) {
        System.arraycopy(chunk, 0, encryptedData, chunkPos, chunk.size)
        chunkPos += chunk.size
    }
    return encryptedData

}

fun asymmetricDecryptBytes(p_EncryptedData: ByteArray, p_private: C0AsymKey): ByteArray {

    val maxsize: Int = (4096 / 8)
    val chunks = ArrayList<ByteArray>()
    var decryptedLen = 0
    var pos = 0
    while (pos < p_EncryptedData.size) {
        val len = Math.min(p_EncryptedData.size - pos, maxsize)
        val encryptedChunk = ByteArray(len)
        System.arraycopy(p_EncryptedData, pos, encryptedChunk, 0, len)
        val chunk: ByteArray = decrypt(encryptedChunk, p_private.keyPair.private)
        chunks.add(chunk)
        decryptedLen += chunk.size
        pos += len
    }
    val data = ByteArray(decryptedLen)
    var chunkPos = 0
    for (chunk in chunks) {
        System.arraycopy(chunk, 0, data, chunkPos, chunk.size)
        chunkPos += chunk.size
    }
    return data

}

fun encrypt(content: ByteArray, key: PublicKey): ByteArray {
    return Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding").run {
        init(Cipher.ENCRYPT_MODE, key)
        doFinal(content)
    }
}

fun decrypt(Encrypted: ByteArray, key: PrivateKey): ByteArray {
    return Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding").run {
        //"RSA/ECB/OAEPWithSHA-256AndMGF1Padding" or "RSA/ECB/PKCS1Padding"
        init(Cipher.DECRYPT_MODE, key)
        doFinal(Encrypted)
    }
}
