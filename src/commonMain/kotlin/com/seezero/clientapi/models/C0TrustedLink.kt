package com.seezero.clientapi.models

import com.seezero.clientapi.implementation.generateAsymkeyPair
import com.seezero.clientapi.implementation.loadXMLFromString
import org.w3c.dom.Node
import java.util.*
import javax.xml.xpath.XPath
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory


fun main() {
    var myKey = generateAsymkeyPair()
    var theirKey = generateAsymkeyPair()
    var linkUser = C0User("Bob", UUID.randomUUID().toString())
    var AliceSideLink = C0TrustedLink(linkUser, myKey, theirKey)
    val str = getXmlFromC0TrustedLink(AliceSideLink)
    println(str)
    val newLink = getC0TrustedLinkFromXml(str)
    val str2 = getXmlFromC0TrustedLink(newLink)
    println(str2)


}

class C0TrustedLink {
    lateinit var linkUser: C0User
    lateinit var myKey: C0AsymKey
    var isTheirKeyExists = false
    var theirKey: C0AsymKey? = null

    private constructor()

    constructor(p_linkUser: C0User, p_myKey: C0AsymKey, p_linkKey: C0AsymKey) {

        linkUser = p_linkUser
        myKey = p_myKey
        theirKey = p_linkKey
        isTheirKeyExists = true
    }


    constructor(p_linkUser: C0User, p_myKey: C0AsymKey) {

        linkUser = p_linkUser
        myKey = p_myKey
        theirKey = null
        isTheirKeyExists = false
    }


}


fun getXmlFromC0TrustedLink(link: C0TrustedLink): String {

    //To be done .... infomplete

    val sb = StringBuilder()
    sb.append("<C0TrustedLink>")
    sb.append("<LinkUser>" + getXmlFromC0User(link.linkUser) + "</LinkUser>")
    sb.append("<MyKey>")
    sb.append(getXmlFromC0AsymKey(link.myKey, true))
    sb.append("</MyKey>")
    if (link.isTheirKeyExists) {
        sb.append("<TheirKeyExists>" + "Y" + "</TheirKeyExists>")
     } else {
        sb.append("<TheirKeyExists>" + "N" + "</TheirKeyExists>")
    }


    if (link.isTheirKeyExists && link.theirKey != null) {
        sb.append(
            "<TheirKey>" +
                    getXmlFromC0AsymKey(link.theirKey!!, false /*their private key never stored*/)
                    + "</TheirKey>"
        )
    }
    sb.append("</C0TrustedLink>")
    return sb.toString()
}


fun getC0TrustedLinkFromXmlNode(tlNode: Node?): C0TrustedLink {

    val xPath: XPath = XPathFactory.newInstance().newXPath()
    val userNode = xPath.evaluate(".//LinkUser/C0User", tlNode, XPathConstants.NODE) as Node

    val userName = xPath.evaluate(".//UserName", userNode, XPathConstants.STRING) as String
    val userId = xPath.evaluate(".//UserId", userNode, XPathConstants.STRING) as String
    val user = C0User(userName, userId)
    val myKeyNode = xPath.compile(".//MyKey/C0AsymKey").evaluate(tlNode, XPathConstants.NODE) as Node
    val myAsymKey = getC0AsymKeyFromXmlNode(myKeyNode)
    val isTheirKeyExistsStr = xPath.compile("//TheirKeyExists").evaluate(tlNode, XPathConstants.STRING) as String
    val isTheirKeyExists = (isTheirKeyExistsStr.equals("Y"))
    if (isTheirKeyExists) {
        val theirKeyNode = xPath.compile(".//TheirKey").evaluate(tlNode, XPathConstants.NODE) as Node
        val theirAsymKey = getC0AsymKeyFromXmlNode(theirKeyNode)

        return C0TrustedLink(user, myAsymKey, theirAsymKey)

    } else {
        return C0TrustedLink(user, myAsymKey)
    }
}

fun getC0TrustedLinkFromXml(p_xml: String): C0TrustedLink {
    val doc = loadXMLFromString(p_xml)
    val xPath: XPath = XPathFactory.newInstance().newXPath()

    val tlNode = xPath.evaluate("/C0TrustedLink", doc, XPathConstants.NODE) as Node
    return getC0TrustedLinkFromXmlNode(tlNode)
}
