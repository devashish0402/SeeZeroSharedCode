package com.seezero.clientapi.service

import com.seezero.clientapi.implementation.*
import com.seezero.clientapi.models.C0MetaData
import com.seezero.clientapi.models.C0User
import java.io.InputStream
import java.io.OutputStream

fun main() {

}

fun login(hashedPhrase: String): Boolean {
    return getAccessToKeyBank(hashedPhrase)
}

fun hashString(phrase: String): String {
    return getHashFromString(phrase)
}

fun createTokenBank(
    userName: String,
    userId: String,
    questionList: MutableList<String>,
    hashedPhraseList: MutableList<String>
): String {

    return createInitTokenBank(userName, userId, questionList, hashedPhraseList)
}

fun createNewLinkDigestForQRCode(user: C0User): String {

    return createNewTrustedLinkDigest(user)
}

fun scanQRSendRegisterLink(linkDigestText: String): String {
    return scanAndRegisterTrustedLink(linkDigestText)
}


fun acceptScannersRegisterDigest(registerDigest: String): Boolean {
    return acceptRegisterLinkDigest(registerDigest)

}


fun createDeviceLoginShareRequest(): String {

    return createLoginShareRequest()

}

fun scanAndShareKeyBank(linkDigestText: String): String {
    return scanAndShareKeyBankWithDevice(linkDigestText)
}


fun acceptHostKeyBank(registerDigest: String): Boolean {
    return acceptLoginHostKeyBank(registerDigest)
}

fun completeServerRegistration(registerDigest: String): Boolean {
    return completeServerRegisterLink(registerDigest)
}

fun listTrustedLinks(): List<String> {
    return listSortedTrustedLinks()
}

fun sendFiletoRepository(p_inStream: InputStream, p_outStream: OutputStream, otherUser: String): C0MetaData {

    val trustedLink = getTrustedLinkFromKeyBank(otherUser)
    if (trustedLink.isTheirKeyExists) {
        debug("Encryption keys  :  ours " + trustedLink.myKey.id + " theirs " + trustedLink.theirKey!!.id)
    } else {
        debug("Encryption keys  :  ours " + trustedLink.myKey.id)
    }

    return encryptAndSignStream(p_inStream, p_outStream, trustedLink)
}


fun downloadFileFromRepository(
    p_inStream: InputStream,
    p_outStream: OutputStream,
    metaData: C0MetaData,
    otherUser: String
): Boolean {

    val trustedLink = getTrustedLinkFromKeyBank(otherUser)
    if(trustedLink.isTheirKeyExists)
    {
        debug("Deccryption keys  :  ours " + trustedLink.myKey.id + " theirs " + trustedLink.theirKey!!.id)
    }
    else
    {
        debug("Deccryption keys  :  ours " + trustedLink.myKey.id)
    }
     return decryptAndVerifyStream(p_inStream, p_outStream, metaData, trustedLink)
}


fun getAllKeyBankUserNames(): ArrayList<String> {
    return getAllKeyBankUsers()
}

class ClientAPI
