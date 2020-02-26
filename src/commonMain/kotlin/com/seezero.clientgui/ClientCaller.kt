/*
package com.seezero.clientgui

import com.seezero.clientapi.implementation.*
import com.seezero.clientapi.models.C0LinkDigest
import com.seezero.clientapi.models.C0User
import com.seezero.clientapi.models.getEncodedStringFromC0LinkDigest
import com.seezero.clientapi.service.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*
import kotlin.jvm.JvmMultifileClass


fun main() {
//
//

    //
    // call to hash  Strings for pass phrases etc

    info("API # 1: Call to Hash Phrases!")

    val str1 = "You See Zero!"
    val result1 = hashString(str1)
    info("original " + str1 + " hashed " +result1)


    //upon successful registration with verification of users phone/email/getting the server's user id
    //call this method
    info("API # 2: Create Token Bank")

    val question1 = "What is your favorite movie"
    val question2 = "What is your dogs best buddy"
    val question3 = "What is your college chant"
    val questionsList: MutableList<String> = ArrayList()
    questionsList.add(question1)
    questionsList.add(question2)
    questionsList.add(question3)

    val hashedPhrase1 = hashString("Pulp Fiction")
    val hashedPhrase2 = hashString("Chuckles")
    val hashedPhrase3 = hashString("Go Green Go White")
    val hashedBioPhrase = hashString("some stored bio string")
    val phraseList: MutableList<String> = ArrayList()
    phraseList.add(hashedPhrase1)
    phraseList.add(hashedPhrase2)
    phraseList.add(hashedPhrase3)
    phraseList.add(hashedBioPhrase)

    // this returned registration digest will need to be sent to the server - contains client keys
    val registerDigest = createTokenBank(
        "Shankar",  //registered user name
        "12345", //user id from the server
        questionsList,
        phraseList
    )

    info("API # 4: Complete Server Registration")

    //Server will return a registration digest, that chould be passed back to the API
    //creating fake one here - not to be called from the client
    val linkDigest = C0LinkDigest(C0User("Server", "99999"), generateAsymkeyPair())
    val linkDigestStr = getEncodedStringFromC0LinkDigest(linkDigest)
    //end fake setup
    completeServerRegistration(linkDigestStr)


    info("API # 5: Login with Phrase")


    if (login(hashString("Go Blue")))//this will fail
        info("Login Successful with Go Blue")
    else
        info("Login UnSuccessful with Go Blue")


    if (login(hashedPhrase3)) //should succeed
        info("Login Successful  Go Green Go White")
    else
        info("Login UnSuccessful  - Go Green Go White")




    // set up code to create keys for both sides here in the keybank
    //these calls should never be made directly from the client
    //shown here just to show functionality
    val receiverAsymKey = generateAsymkeyPair()  //fake link setup - short circuited
    val senderAsymKey = generateAsymkeyPair()  //fake link setup - short circuited
    createFullLink(C0User("Sastry","12345"), senderAsymKey,receiverAsymKey)  //method for demo only do not call in actual client!!!
    createFullLink(C0User("Murali","67890"),receiverAsymKey,senderAsymKey) //method for demo only do not call in actual client!!!
    //end example specific code

    info("API # 6: file send example")

    //example here shows a local file encrypted and written to a local file
    //in case of SeeZero. local file will be read for send
    // output stream will be pointed to a web site to accept the stream
    //The stream will return a file identifier, that will be tied to the meta data
    //that will be sent in a subsequent call. The meta data will be stored in the server db , as is
    //it is required to verify and decrypt the file

    //SendFile
    val sendFileIn = File("c:/temp/Sync.docx")
    val sendFileOut = File("c:/temp/Sync.crpt") // this should be a output stream to the server
    val inStream1 = FileInputStream(sendFileIn)
    val outStream1 = FileOutputStream(sendFileOut)

    val metaData1 = sendFiletoRepository(inStream1, outStream1, "Sastry")
    inStream1.close()
    outStream1.close()
    metaData1.encryptedBy = getKeyBankUser().userName //sender user name
    metaData1.contentSize = sendFileIn.length() //original file length

    //   client should have a call to send meta data to server here!!!
    // sendMetadatatoServer(metaData1) //Sanganan implementation

    info("API # 7: file download example")


    val downloadFileIn = File("c:/temp/Sync.crpt")
    val downloadFileOut = File("c:/temp/Sync2.docx") //write back to a differnt file
    val inStream2 = FileInputStream(downloadFileIn)
    val outStream2 = FileOutputStream(downloadFileOut)

    //step 1 not shown here  is to get meta data for file from

    // getMetadataFromServer(File name/id) //Sanganan implementation

    if(downloadFileFromRepository(inStream2,outStream2,metaData1,"Murali" ))
    {
        info("File decrypt successful")
    }
    else
    {
        info("File decrypt un-successful")

    }
    inStream1.close()
    outStream1.close()


    info("API # 8: get User Questionlist")
    val questionList = keyBankgetQuestionList()
    for (i in questionList.indices) {
        info("Question " + i + "  " +questionList[i])
    }

    info("API # 9: get list of trusted links")
    val linkList  = listTrustedLinks()
    for (i in linkList.indices) {
        info("user " + i + "  " +linkList[i])
    }


    //following api shows full flow of trusted link setup

    info("API # 10: get String for QR code to display ")
    //Bobs phone shows qr code. set user to bob
    val hardcodeUser = C0User("Bob", "67890") // never make this call in Sanganan code
    //done here to switch users artificially
    val qrScanSource = createNewLinkDigestForQRCode(hardcodeUser)
    info(qrScanSource)


    info("API # 11: scan QR code and accept")
    //Decrypt scan of QR code and package our keys with  public code from QR code
    val regDigest = scanQRSendRegisterLink(qrScanSource)

    info("API # 12: finalize trusted link  on side that showed qr code ..get keys from scanner")
    //bob receives alices key, consttucts complte key bank, sends her his creds
    if (acceptScannersRegisterDigest(regDigest))
        debug("Trusted Link successfully established on both sides")


//    //KeyBank transfer

    info("API #  13 :request login( with temp keys) from secondary device .. ")

    val deviceQrScanSource = createDeviceLoginShareRequest()

    info("API #  14 :send primary keys to secondary device .. ")

    val loginShareDigest = scanAndShareKeyBank(deviceQrScanSource)


    info("API #  15 :accept keyBank from Primary ")

    if(acceptHostKeyBank(loginShareDigest))
    {
        debug("Keybank transferred successfully")
    }

}*/
