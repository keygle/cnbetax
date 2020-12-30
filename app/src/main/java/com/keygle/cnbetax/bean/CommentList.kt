package com.keygle.cnbetax.bean


import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CommentListResponse (
    var code: Int,
    var msg: String,
    var status: String,
    var result: MutableList<CommentList>
)

@JsonClass(generateAdapter = true)
data class CommentList(
    var tid: String,
    var pid: String,
    var username: String,
    var content: String,
    var created_time: String,
    var support: String,
    var against: String,
)