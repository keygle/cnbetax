package com.keygle.cnbetax

import kotlinx.serialization.Serializable

@Serializable
data class Article(
    val title: String,
    val description: String,
    val createdAt: String,
    val updatedAt: String,
    val viewCount: Int,
    val replyCount: Int
) {
    private companion object {
        const val MAX_STR_LENGTH = 15
    }

    override fun toString(): String {
        return "Article(title='${title.take(MAX_STR_LENGTH)}', " +
                "description='${description.take(MAX_STR_LENGTH)}', " +
                "createdAt='$createdAt', " +
                "updatedAt='$updatedAt', " +
                "viewCount='$viewCount', " +
                "replyCount=$replyCount)"
    }
}

@Serializable
data class Articles(
    val articles: List<Article>,
    val count: Int
)