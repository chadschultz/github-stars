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
                    //TODO: improve? https://stackoverflow.com/questions/22490057/android-okhttp-with-basic-authentication
                    return response.request().newBuilder().header("Authorization", "bearer 4108af97e88d5ad95ca24024319f7482555f8143").build();
                }

            }).build()
            return ApolloClient.builder().serverUrl(BASE_URL).okHttpClient(okHttpClient).build()
        }
    }

}