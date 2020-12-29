package com.keygle.cnbetax.utils

import java.math.BigInteger
import java.security.MessageDigest

object Tools {
    fun md5(input:String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }

    /**
     * 获取 Sid 小于 endSid 文章列表
     * @param endSid 文章Sid
     * @return 文章列表url
     */
    fun getArticleListUrl(endSid: String?): String? {
        val sb = StringBuilder()
        sb.append("app_key=10000")
        sb.append("&end_sid=").append(endSid)
        sb.append("&format=json")
        sb.append("&method=Article.Lists")
        sb.append("&timestamp=").append(System.currentTimeMillis())
        sb.append("&v=2.8.5")
        val signed: String = md5("$sb&mpuffgvbvbttn3Rc")
        sb.append("&sign=").append(signed)
        sb.insert(0, "https://api.cnbeta.com/capi?")
        return sb.toString()
    }

    /**
     * 获取 对应 sid 的文章
     * @param endSid 文章id
     * @return 文章 url
     */
    fun getDetailUrl(sid: String?): String? {
        val sb = StringBuilder()
        sb.append("app_key=10000")
        sb.append("&format=json")
        sb.append("&method=Article.NewsContent")
        sb.append("&sid=").append(sid)
        sb.append("&timestamp=").append(System.currentTimeMillis())
        sb.append("&v=2.8.5")
        val signed: String = md5("$sb&mpuffgvbvbttn3Rc")
        sb.append("&sign=").append(signed)
        sb.insert(0, "https://api.cnbeta.com/capi?")
        return sb.toString()
    }
}