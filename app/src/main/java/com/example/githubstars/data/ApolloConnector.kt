package com.example.githubstars.data

import com.apollographql.apollo.ApolloClient
import okhttp3.*


class ApolloConnector {

    //TODO

    companion object {
        private val BASE_URL = "https://api.github.com/graphql"

        fun setupApollo(): ApolloClient {
            val okHttpClient = OkHttpClient.Builder().authenticator(object: Authenticator {
                override fun authenticate(route: Route?, response: Response): Request? {
                    //TODO: more secure way to handle authentication
                    //TODO: improve? https://stackoverflow.com/questions/22490057/android-okhttp-with-basic-authentication
                    return response.request().newBuilder().header("Authorization", "bearer 6416cb9cd8dd27fcdc87c5646e91c39c6f3c7783").build()
                }

            }).build()
            return ApolloClient.builder().serverUrl(BASE_URL).okHttpClient(okHttpClient).build()
        }
    }

}