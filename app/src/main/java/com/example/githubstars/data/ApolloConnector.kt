package com.example.githubstars.data

import com.apollographql.apollo.ApolloClient
import com.example.githubstars.BuildConfig
import com.example.githubstars.util.SensitiveValues
import okhttp3.*


class ApolloConnector {

    companion object {
        private const val BASE_URL = "https://api.github.com/graphql"

        fun setupApollo(): ApolloClient {
            val okHttpClient = OkHttpClient.Builder().authenticator { _, response ->
                val accessToken = SensitiveValues.decrypt(BuildConfig.GITHUB_ACCESS_TOKEN)
                //TODO: more secure way to handle authentication
                //TODO: improve? https://stackoverflow.com/questions/22490057/android-okhttp-with-basic-authentication
                response.request().newBuilder().header("Authorization", "bearer $accessToken").build()
            }.build()
            return ApolloClient.builder().serverUrl(BASE_URL).okHttpClient(okHttpClient).build()
        }
    }

}
