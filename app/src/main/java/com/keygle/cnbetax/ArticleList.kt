package com.keygle.cnbetax

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class ArticleList(
    var title: String,
    var created: String
)
