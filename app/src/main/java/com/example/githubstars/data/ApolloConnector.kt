package com.example.githubstars.data

import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.example.githubstars.BuildConfig
import com.example.githubstars.util.SensitiveValues
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class ApolloConnector {

    companion object {
        private const val BASE_URL = "https://api.github.com/graphql"

        fun setupApollo(): ApolloClient {
            val okHttpClient = OkHttpClient.Builder().authenticator(object : Authenticator {
                override fun authenticate(route: Route?, response: Response): Request? {
                    if (response.request.header("Authorization") != null) {
                        Log.w("xxx", "We've already attempted to authenticate. Giving up on authentication.")
                        return null
                    }
                    //TODO: Timber
                    Log.d("xxx", "Authenticating for response: $response")
                    Log.d("xxx", "Challenges: ${response.challenges()}")
                    val accessToken = SensitiveValues.decrypt(BuildConfig.GITHUB_ACCESS_TOKEN)
                    return response.request.newBuilder().header("Authorization", "bearer $accessToken").build()
                }
            }).build()
            return ApolloClient.builder().serverUrl(BASE_URL).okHttpClient(okHttpClient).build()
        }
    }

}
