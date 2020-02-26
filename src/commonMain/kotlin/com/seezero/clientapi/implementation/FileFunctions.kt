package com.seezero.clientapi.implementation

import com.seezero.clientapi.global.*
import com.seezero.clientapi.global.GlobalStorage.keyBankRootFolder
import java.io.*
import java.nio.file.Paths


fun main() {
    storeEncryptBytestoFile("hello", getDefaultPasswordSalt())
}


fun getDefaultRootFolderHandle() : String
{
    // create a File object for the   directory
    val rootDirectory = File(keyBankRootFolder)
    // have the object build the directory structure, if needed.
    if (rootDirectory.isDirectory()) {
         return keyBankRootFolder
    }
    if(rootDirectory.mkdirs()) {
        System.out.println("Keybank Root folder exists")
        return keyBankRootFolder
    }
    throw Exception("Unable to create root folder for keybank")
}

fun deleteFile(fileNme:String )
{
    File(getDefaultRootFolderHandle()+ fileNme).delete()
}

fun storeEncryptBytestoFile(fileNme:String, bytes:ByteArray)
{
    File(getDefaultRootFolderHandle()+ fileNme).writeBytes(bytes)
}

fun checkEncryptBytesFile(fileName:String):Boolean{
    return File(getDefaultRootFolderHandle()+fileName).exists()
}

fun retrieveEncryptBytesFromFile(fileName:String):ByteArray{
        return (File(getDefaultRootFolderHandle()+fileName).readBytes())
}


fun storeRawMagicKey(fileNme:String, key:String)
{
    File(getDefaultRootFolderHandle()+ fileNme).writeBytes(symmetricEncryptBytes(key.toByteArray(Charsets.UTF_8),
        getApplicationSymmetricKey()
    ))
}

fun checkRawMagicKeyFile(fileName:String):Boolean{
    return File(getDefaultRootFolderHandle()+fileName).exists()
}

fun getRawMagicKeyFromFile(fileName:String):String{
    val returnTxt = symmetricDecryptBytes( File(getDefaultRootFolderHandle()+fileName).readBytes(),
        getApplicationSymmetricKey()
    ).toString(Charsets.UTF_8)
    File(getDefaultRootFolderHandle()+fileName).delete()//get rid of the file
    return returnTxt
}
fun deleteRawMagicKeyFromFile(fileName:String):Boolean{
    return (File(getDefaultRootFolderHandle()+fileName).delete())
}

fun getAllKeyBankUsers():ArrayList<String>
{
    val fileList:ArrayList<String> = ArrayList<String>()

     File(getDefaultRootFolderHandle()).walk().forEach {
        var fileName :String = it.name
        if(fileName.endsWith(".kbk",true)
                    && !fileName.equals("server.kbk",true)
                    && !fileName.equals("self.kbk",true)
            )
        {
            fileList.add(fileName.substringBefore(".kbk"))
        }
    }
    return fileList
}