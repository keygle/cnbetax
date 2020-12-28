package com.keygle.cnbetax.network

import com.keygle.cnbetax.bean.ArticleListResponse
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.*


interface ApiClient {
    @GET fun getArticlesAsync(@Url url: String?): Deferred<Response<ArticleListResponse>>
}