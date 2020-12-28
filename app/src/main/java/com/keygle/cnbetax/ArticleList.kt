package com.keygle.cnbetax

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class ArticleListResponse(
    var code: Int,
    var msg: String,
    var status: String,
    var result: MutableList<ArticleList>
)

@JsonClass(generateAdapter = true)
data class ArticleList(
        var sid: String,
        var title: String,
        var pubtime: String,
        var summary: String,
        var topic: String,
        var counter: String,
        var comments: String,
        var ratings: String,
        var score: String,
        var ratings_story: String,
        var score_story: String,
        var topic_logo: String,
        var thumb: String
)
