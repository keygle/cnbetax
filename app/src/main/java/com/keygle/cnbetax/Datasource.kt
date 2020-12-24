package com.keygle.cnbetax

import android.content.Context

class Datasource(val context: Context) {
    fun getArticleList(): Array<String> {

        return context.resources.getStringArray()
    }
}