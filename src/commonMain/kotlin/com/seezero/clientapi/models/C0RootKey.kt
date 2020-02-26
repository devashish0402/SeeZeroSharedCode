package com.seezero.clientapi.models


import com.seezero.clientapi.implementation.*
import org.w3c.dom.Document
import org.w3c.dom.Node
import java.io.File
import javax.xml.xpath.XPath
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory


class C0RootKey {

    private var username: String? = null
    private var keyFileName: String? = null
    lateinit var key: C0AsymKey

    constructor (p_username: String, p_password: String, p_create: Boolean) {
        username = p_username
        keyFileName = C0Paths().getRootKeyFile(p_username)
        if (File(keyFileName).isFile) {
            val encryptedData: ByteArray = File(keyFileName).readBytes()
            val data: ByteArray = symmetricDecryptBytes(encryptedData, p_password, C0Salt.getDefaultSalt())
            val rootKeyXml: String = data.toString(Charsets.UTF_8)
            println("rootkeyXml: "+ rootKeyXml)
            loadFromXml(rootKeyXml)
        } else if (p_create) {
            key =  generateAsymkeyPair()
            val rootKeyXml = toXml()
            println(rootKeyXml)
//        //    val crypt = C0Crypt()
//            val data = rootKeyXml.toByteArray(StandardCharsets.UTF_8)
//            val encryptedData: ByteArray =  crypt.symmetricEncryptBytes(data, p_password, C0Salt.getDefaultSalt())
//            File(keyFileName).writeBytes(encryptedData)
        } else {
            throw RuntimeException("Root key not found")
        }
    }


    fun fromXmlNode(p_node: Document?): Unit {

        p_node?.documentElement?.normalize()
        val xPath: XPath = XPathFactory.newInstance().newXPath()
        username = xPath.compile("C0RootKey/Username").evaluate(p_node, XPathConstants.STRING) as String
        val nodeList = xPath.compile("/C0RootKey/Key").evaluate(
            p_node, XPathConstants.NODE
        ) as Node
        var x = p_node!!.getUserData ("/C0RootKey/Key")
        val value =
            xPath.evaluate("/C0RootKey/Key/C0AsymKey", p_node, XPathConstants.NODE) as Node

        println(nodeList.nodeName)
        val keyXml = xPath.compile("C0RootKey/Key/C0AsymKey").evaluate(p_node, XPathConstants.NODE) as Node
        println("hello")
//        if (keyXml == null) {
//            throw Exception("Root Key's key is null!! Exiting!!")
//        }
        key =  getC0AsymKeyFromXmlNode( value.ownerDocument)
    }


    fun loadFromXml(p_xml: String): Unit {
        val doc = loadXMLFromString(p_xml)
        return fromXmlNode(doc)
    }


    fun toXml(): String {
        val sb = StringBuilder()
        sb.append("<C0RootKey>")
        sb.append("<Username>" + username + "</Username>")

        sb.append("<Key>" + getXmlFromC0AsymKey(key,key.hasPrivateKey()) + "</Key>")
        sb.append("</C0RootKey>")
        return sb.toString()
    }
}