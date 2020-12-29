package com.keygle.cnbetax.utils

import android.text.TextUtils


/**
 * Created by didik
 * Created time 2017/2/9
 * Description:
 */
object HtmlUtil {
    private const val filter = "<p.*?>" //"<strong>", "</strong>"
    fun htmlFilter(htmlText: String): String {
        var htmlTxt:String = htmlText
        if (TextUtils.isEmpty(htmlTxt)) {
            return htmlTxt
        }
        htmlTxt = htmlTxt.replace(filter.toRegex(), "")
        htmlTxt = htmlTxt.replace("<br/></p>|<br></p>|</p>".toRegex(), "<br>")
        htmlTxt = htmlTxt.trim { it <= ' ' }
        if (htmlTxt.endsWith("<br>")) {
            htmlTxt = htmlTxt.substring(0, htmlTxt.length - 4)
        }
        return htmlTxt
    }
}
