package com.github.graphql.api

import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.exception.ApolloException
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import java.util.concurrent.TimeUnit

/**
 *Its Repository class, it handles all api callss
 */
class DataRepository {

    fun getReposList(query: String, count: Long = 20, completion: (result: Pair<List<ListReposQuery.Edge>?, Error?>) -> Unit) {

        val queryCall = ListReposQuery
                .builder()
                .queryString(query)
                .itemsCount(count)
                .build()

        apolloClient.query(queryCall).enqueue(object : ApolloCall.Callback<ListReposQuery.Data>() {
            override fun onFailure(e: ApolloException) {
                completion(Pair(null, Error(e.message)))
            }

            override fun onResponse(response: com.apollographql.apollo.api.Response<ListReposQuery.Data>) {
                val errors = response.errors()
                if (!errors.isEmpty()) {
                    val message = errors[0]?.message() ?: ""
                    completion(Pair(null, Error(message)))
                } else {
                    completion(Pair(response.data()?.search()?.edges() ?: listOf(), null))
                }
            }

        })


    }

    fun getSearchDetail(owner: String, name: String, completion: (result: Pair<SearchDetailQuery.Data?, Error?>) -> Unit) {
        val queryCall = SearchDetailQuery
                .builder()
                .owner(owner)
                .name(name)
                .build()

        apolloClient.query(queryCall).enqueue(object : ApolloCall.Callback<SearchDetailQuery.Data>() {
            override fun onFailure(e: ApolloException) {
                completion(Pair(null, Error(e.message)))
            }

            override fun onResponse(response: com.apollographql.apollo.api.Response<SearchDetailQuery.Data>) {
                val errors = response.errors()
                if (!errors.isEmpty()) {
                    val message = errors[0]?.message() ?: ""
                    completion(Pair(null, Error(message)))
                } else {
                    completion(Pair(response.data(), null))
                }
            }

        })

    }

    companion object {

        private val GITHUB_GRAPHQL_ENDPOINT = "https://api.github.com/graphql"

        private val httpClient: OkHttpClient by lazy {
            OkHttpClient.Builder()
                    .writeTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .addNetworkInterceptor(NetworkInterceptor())
                    .build()
        }


        private val apolloClient: ApolloClient by lazy {
            ApolloClient.builder()
                    .serverUrl(GITHUB_GRAPHQL_ENDPOINT)
                    .okHttpClient(httpClient)
                    .build()
        }

        private class NetworkInterceptor : Interceptor {

            override fun intercept(chain: Interceptor.Chain?): Response {
                return chain!!.proceed(chain.request().newBuilder().header("Authorization", "Bearer d7680755d19fe0043a4876af73acd1b44b733645").build())
            }
        }

    }

}