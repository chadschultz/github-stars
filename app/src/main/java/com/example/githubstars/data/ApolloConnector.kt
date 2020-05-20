package com.example.githubstars.data

import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.example.githubstars.BuildConfig
import com.example.githubstars.util.SensitiveValues
import okhttp3.*


class ApolloConnector {

    //TODO

    companion object {
        private val BASE_URL = "https://api.github.com/graphql"

        fun setupApollo(): ApolloClient {
            val okHttpClient = OkHttpClient.Builder().authenticator(object: Authenticator {
                override fun authenticate(route: Route?, response: Response): Request? {
                    val accessToken = SensitiveValues.decrypt(BuildConfig.GITHUB_ACCESS_TOKEN)
                    //TODO: more secure way to handle authentication
                    //TODO: improve? https://stackoverflow.com/questions/22490057/android-okhttp-with-basic-authentication
                    Log.e("xxx", "accessToken: $accessToken")
                    val bearer = "bearer $accessToken"
                    val testString = "whatever"
                    Log.e("xxx", "bearer: $bearer")

//                    return response.request().newBuilder().header("Authorization", "bearer $accessToken").build()
//                    return response.request().newBuilder().header("Authorization", "bearer 00ca2c4b049988648e92850ab77cc4bd0f84d115").build()
                    return response.request().newBuilder().header("Authorization", bearer).build()
                }

            }).build()
            return ApolloClient.builder().serverUrl(BASE_URL).okHttpClient(okHttpClient).build()
        }
    }

}
