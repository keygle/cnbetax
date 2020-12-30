package com.keygle.cnbetax.network

import com.keygle.cnbetax.bean.ArticleListResponse
import com.keygle.cnbetax.bean.CommentListResponse
import com.keygle.cnbetax.bean.DetailResponse
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.*


interface ApiClient {
    @GET fun getArticlesAsync(@Url url: String?): Deferred<Response<ArticleListResponse>>
    @GET fun getCommentsAsync(@Url url: String?): Deferred<Response<CommentListResponse>>
    @GET fun getArticleDetailAsync(@Url url: String?): Deferred<Response<DetailResponse>>
}