package com.keygle.cnbetax

import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.*

interface ApiClient {
    @GET("articles.json") fun getArticlesAsync(): Deferred<Response<List<ArticleList>>>
}