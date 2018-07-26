package com.github.shiverawe.temp

import com.github.shiverawe.lib.PropertiesInstrumented

object Configuration : PropertiesInstrumented("/secret.properties") {

    val userToken: String = property("user_access_token")

    val userId: String = property("user_id")

    val appId: String = property("app_id")

    val appSecret: String = property("app_client_secret")

    val appRedirectUri: String = property("app_redirect_uri")

}
