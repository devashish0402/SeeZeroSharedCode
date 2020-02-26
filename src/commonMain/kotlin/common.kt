package com.jetbrains.handson.mpp.mobile

import com.seezero.clientapi.global.GlobalStorage
import com.seezero.clientapi.service.createTokenBank
import com.seezero.clientapi.service.hashString
import com.seezero.clientapi.service.listTrustedLinks
import com.seezero.clientapi.service.login

fun setKeyBankPath(keyBankPath: String) {
    GlobalStorage.setKeyBankLocation(keyBankPath)
}

fun getHashedPassPhrase(passphrase: String): String {
    return hashString(passphrase)
}

fun createTokenBankForUser(userName: String,
                           userId: String,
                           questionList: MutableList<String>,
                           hashedPhraseList: MutableList<String>) : String {
    return createTokenBank(userName, userId, questionList, hashedPhraseList)
}

fun loginUser(hashedPassPhrase: String) : Boolean {
    return login(hashedPassPhrase)
}

fun getTrustedLinks(): List<String> {
    return listTrustedLinks()
}

