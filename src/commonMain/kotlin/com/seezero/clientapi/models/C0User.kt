package com.seezero.clientapi.models

import com.seezero.clientapi.implementation.loadXMLFromString
import jdk.internal.org.xml.sax.InputSource
import org.w3c.dom.Document
import javax.xml.xpath.XPath
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.StringReader

fun main() {
    val user = C0User("Shankar", "957141fc-6478-4697-a54a-4990b48a07e6Z")
    println(getXmlFromC0User(user))
    val newUser = getC0UserfromXml(getXmlFromC0User(user))
    println(getXmlFromC0User(newUser))

}

class C0User {

      lateinit var userName: String
      lateinit var userId: String

    constructor(p_user: String, p_userId: String) {
        userName = p_user
        userId = p_userId
    }

}


fun  getXmlFromC0User(c0User:C0User): String {
    return "<C0User><UserName>" + c0User.userName + "</UserName><UserId>" + c0User.userId + "</UserId></C0User>"
}

fun toByteArray(c0User:C0User): ByteArray {
    return getXmlFromC0User(c0User).toByteArray(Charsets.UTF_8)
}

fun getC0UserfromXmlNode(userNode: Node?): C0User {

    val xPath: XPath = XPathFactory.newInstance().newXPath()
    val userName = xPath.evaluate("//UserName", userNode, XPathConstants.STRING) as String
    val userId = xPath.evaluate("//UserId", userNode, XPathConstants.STRING) as String

    return C0User(userName,userId)
}


public fun getC0UserfromXml(p_xml: String): C0User {
    val doc = loadXMLFromString(p_xml)
    doc?.documentElement?.normalize()
    val xPath: XPath = XPathFactory.newInstance().newXPath()
    val userNode = xPath.evaluate(".//C0User", doc, XPathConstants.NODE) as Node

    return getC0UserfromXmlNode(userNode)

}
