package com.keygle.cnbetax.network

import com.keygle.cnbetax.bean.ArticleListResponse
import com.keygle.cnbetax.bean.DetailResponse
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.*


interface ApiClient {
    @GET fun getArticlesAsync(@Url url: String?): Deferred<Response<ArticleListResponse>>
    @GET fun getDetailAsync(@Url url: String?): Deferred<Response<DetailResponse>>
}