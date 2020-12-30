package com.keygle.cnbetax.bean

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class DetailResponse(
    var code: Int,
    var msg: String,
    var status: String,
    var result: Detail
)

@JsonClass(generateAdapter = true)
data class Detail(
        var sid: String,
        var title: String,
        var time: String,
        var source: String,
        var thumb: String,
        var counter: String,
        var comments: String,
        var hometext: String,
        var bodytext: String
)
