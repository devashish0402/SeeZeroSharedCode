package com.seezero.clientapi.models

import com.seezero.clientapi.implementation.loadXMLFromString
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.util.*
import javax.xml.xpath.XPath
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory
import kotlin.collections.HashMap
import kotlin.collections.component1
import kotlin.collections.component2

class C0KeyBank {

    lateinit var user: C0User
    var questionList: MutableList<String> = ArrayList()
    var trustedLinkMap: HashMap<String, C0TrustedLink> = HashMap<String, C0TrustedLink>()
    lateinit var xferSymKey: C0SymKey
    constructor()
    constructor(theUser: C0User, theQuestionList: MutableList<String>) {
        user = theUser
        questionList = theQuestionList
    }

}

fun getXmlFromC0KeyBank(keyBank: C0KeyBank): String {
    val sb = StringBuilder()
    sb.append("<C0KeyBank>")


    sb.append(
        "<CurrentUser>" +
                getXmlFromC0User(keyBank.user) +
                "</CurrentUser>"
    )
    sb.append("<Questions>")
    for (question in keyBank.questionList) {
        sb.append("<Question>" + question +  "</Question>")
    }
    sb.append("</Questions>")
    sb.append("<TrustedLinkList>")
     for ((key, value) in keyBank.trustedLinkMap) {

        sb.append(getXmlFromC0TrustedLink(value))
    }
    sb.append("</TrustedLinkList>")
    sb.append("</C0KeyBank>")


    return sb.toString()
}


fun getC0KeyBankFromXmlNode(kbNode: Node?): C0KeyBank {
    val newKeyBank = C0KeyBank()
    val xPath: XPath = XPathFactory.newInstance().newXPath()
    val userNode = xPath.evaluate("//CurrentUser/C0User", kbNode, XPathConstants.NODE) as Node
    newKeyBank.user = getC0UserfromXmlNode(userNode)
    val qNodes = xPath.evaluate("//Questions/Question", kbNode, XPathConstants.NODESET) as NodeList

    for (i in 0 until qNodes.length) {
        newKeyBank.questionList.add(qNodes.item(i).textContent)
    }


    val tNodes = xPath.evaluate("//TrustedLinkList/C0TrustedLink", kbNode, XPathConstants.NODESET) as NodeList

    for (i in 0 until tNodes.length) {
        val myNode = tNodes.item(i)
        val trustedLink = getC0TrustedLinkFromXmlNode(myNode)
        newKeyBank.trustedLinkMap.put(trustedLink.linkUser.userName, trustedLink)
    }
    return newKeyBank

}

fun getC0KeyBankFromXml(p_xml: String): C0KeyBank {
    val doc = loadXMLFromString(p_xml)
    doc.documentElement?.normalize()
    val xPath: XPath = XPathFactory.newInstance().newXPath()
    val kbNode = xPath.evaluate("/C0KeyBank", doc, XPathConstants.NODE) as Node

    return getC0KeyBankFromXmlNode(kbNode)

}
