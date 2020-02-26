package com.seezero.clientapi.models

import java.io.File




class C0Paths {
    private fun getApplicationDir(): String {
       // var envVar: String = System.getenv("varname") ?: "default_value"
        val dir ="C:/ProgramData/C0Protocol"
        if (!File(dir).isDirectory) {
            File(dir).mkdirs()
        }
        return dir
    }

    public fun getRootKeyFile(p_username: String): String {
        return getApplicationDir() + "/" + p_username + ".a"
    }

    public fun getKeyBundleFile(p_username: String): String {
        return getApplicationDir() + "/" + p_username + ".b"
    }
}