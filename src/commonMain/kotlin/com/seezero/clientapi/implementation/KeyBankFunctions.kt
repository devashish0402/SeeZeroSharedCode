package com.seezero.clientapi.implementation

import com.seezero.clientapi.global.getDefaultPasswordHardener
import com.seezero.clientapi.models.*


lateinit var sessionSymKey: C0SymKey
lateinit var applicationSymKey: C0SymKey

lateinit var keyBank: C0KeyBank

private val keybankName =  "seezero.kbk"


fun getKeyBankUser(): C0User {
    return keyBank.user
}


fun createInitTokenBank(
    userName: String,
    userId: String,
    questionList: MutableList<String>,
    hashedPhraseList: MutableList<String>

): String {

    deleteFile(keybankName)
    sessionSymKey = generateRandomSymKey()

    keyBank = C0KeyBank(C0User(userName, userId), questionList)
    applicationSymKey =
        generateSymKeyFromPassword(keyBank.user.userName + getDefaultPasswordHardener(), C0Salt.getDefaultSalt())

    //Create a self/self key
    val selfUser = C0User("Self", "00000") //fixed id
    createSelfLink(selfUser)


//    //Create a self/server key
    val server = C0User("Server", "99999") //fixed id
    createMySideLink2(server)


    //encrypt and store encrypted keys for all 4 key phrases
    for (hashedPhrase in hashedPhraseList) {
        storeEncryptBytes(hashedPhrase)
    }

    //
    val linkDigest = C0LinkDigest(keyBank.user, getTrustedLinkFromKeyBank("Server").myKey)
    return getEncodedStringFromC0LinkDigest(linkDigest)

}


fun addTrustedLinkToKeyBank(trustedLink: C0TrustedLink)
{
    keyBank.trustedLinkMap.put(trustedLink.linkUser.userName,trustedLink)
    debug("Added keys for Link " + trustedLink.linkUser.userName)

    //Keybank changed, persist immediately
    storeEncryptBytestoFile(
        keybankName, symmetricEncryptBytes(
            getXmlFromC0KeyBank(keyBank).toByteArray(Charsets.UTF_8),
            sessionSymKey
        )
    )
}
fun listSortedTrustedLinks(): List<String> {
    val userList: ArrayList<String> = ArrayList<String>()
    val sortedMap = keyBank.trustedLinkMap.toSortedMap()
    for ((key) in sortedMap) {
        if (!key.equals("Server"))
            userList.add(key)
    }
    return userList
}

fun getTrustedLinkFromKeyBank(user: String): C0TrustedLink {
    debug("looking for user in keybank for " + user)
    return keyBank.trustedLinkMap.get(user)!!
}

fun keyBankgetQuestionList(): MutableList<String> {
    return keyBank.questionList
}

fun createSelfLink(user: C0User): C0TrustedLink {
    val ourAsyncKey = generateAsymkeyPair()
    val theirAsyncKey = ourAsyncKey
    val link = C0TrustedLink(user, ourAsyncKey, theirAsyncKey)
    addTrustedLinkToKeyBank(link)
    return link
}

fun createFullLink(user: C0User): C0TrustedLink {
    //can only be used in a demo
    println("Internal demo method being called !!! please check API documentation!!!!")
    val ourAsyncKey = generateAsymkeyPair()
    val theirAsyncKey = generateAsymkeyPair()
    val link = C0TrustedLink(user, ourAsyncKey, theirAsyncKey)
    addTrustedLinkToKeyBank(link)
    return link
}

fun createFullLink(user: C0User, ourAsyncKey: C0AsymKey, theirAsyncKey: C0AsymKey): C0TrustedLink {


    val link = C0TrustedLink(user, ourAsyncKey, theirAsyncKey)
    addTrustedLinkToKeyBank(link)
    return link

}


fun createMySideLink2(user: C0User): C0TrustedLink {
    val selfAsyncKey = generateAsymkeyPair()
    val link = C0TrustedLink(user, selfAsyncKey)
    addTrustedLinkToKeyBank(link)
    return link
}

fun getSessionSymmetricKey(): C0SymKey {
    return sessionSymKey
}

fun getApplicationSymmetricKey(): C0SymKey {
    return applicationSymKey
}


fun storeEncryptBytes(hashedPhrase: String) {
    val fileName = getAdler32HexString(hashedPhrase) + ".czk"

    storeEncryptBytestoFile(
        fileName,
        symmetricEncryptBytes(
            getXmlFromC0SymKey(sessionSymKey).toByteArray(Charsets.UTF_8),
            hashedPhrase,
            C0Salt.getDefaultSalt()
        )
    )
}


fun getAccessToKeyBank(hashedPhrase: String): Boolean {

    val fileName = getAdler32HexString(hashedPhrase) + ".czk"
    if (checkEncryptBytesFile(fileName)) {
        val xml =
            symmetricDecryptBytes(
                retrieveEncryptBytesFromFile(fileName),
                hashedPhrase,
                C0Salt.getDefaultSalt()
            ).toString(
                Charsets.UTF_8
            )
        sessionSymKey = getSymKeyFromXml(xml)
        debug("sym   key restored : " + getXmlFromC0SymKey(sessionSymKey))


        val xml2 = symmetricDecryptBytes(
            retrieveEncryptBytesFromFile("seezero.kbk"),
            sessionSymKey
        ).toString(Charsets.UTF_8)
        keyBank = getC0KeyBankFromXml(xml2)
        return true
    }
    return false
}




