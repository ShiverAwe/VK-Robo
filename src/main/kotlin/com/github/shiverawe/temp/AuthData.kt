package com.github.shiverawe.temp

import com.vk.api.sdk.client.actors.UserActor
import java.awt.Desktop
import java.net.URL
import java.util.*

object AuthData {

    val API_VERSION = "5.21"
    val DISPLAY = "page"
    val PERMISSIONS = "friends,status"

    fun getActor(code: String): UserActor {
        val authResponse = Requests.vk.oauth()
                .userAuthorizationCodeFlow(Configuration.appId.toInt(), Configuration.appSecret, Configuration.appRedirectUri, code)
                .execute()
        val actor = UserActor(authResponse.userId, authResponse.accessToken)
        return actor
    }

    fun getActor(): UserActor {
        return UserActor(Configuration.userId.toInt(), Configuration.userToken)
    }

    fun getCode(): String {
        val sc = Scanner(System.`in`)
        openAuthPage("code")
        val authUrl = sc.nextLine()
        val properties = getProperties(authUrl)
        val code = properties["code"] ?: "null"
        return code
    }

    fun getToken(): String {
        val sc = Scanner(System.`in`)
        openAuthPage("token")
        val authUrl = sc.nextLine()
        val properties: Map<String, String> = getProperties(authUrl)
        val code: String = properties["auth_token"] ?: "null"
        return code
    }

    private fun getProperties(query: String): Map<String, String> {
        val params: List<String> = query.trim().split("[&#]".toRegex())
        println(params)
        val map = HashMap<String, String>()
        for ((index, param) in params.withIndex()) {
            if (index == 0) continue
            val name = param.split("=")[0]
            val value = param.split("=")[1]
            map[name] = value
        }
        return map
    }


    /**
     * This method opens a URL to get user access code.
     */
    private fun openAuthPage(responseType: String) {
        val reqUrl = authUrl(responseType)
        println(reqUrl)
        Desktop.getDesktop().browse(URL(reqUrl).toURI())
    }

    /**
     * This method returns a URL to get user access code.
     */
    private fun authUrl(responseType: String) = ("https://oauth.vk.com/authorize"
            + "?client_id=${Configuration.appId}"
            + "&scope=${PERMISSIONS}"
            + "&redirect_uri=${Configuration.appRedirectUri}"
            + "&display=${DISPLAY}"
            + "&v=${API_VERSION}"
            + "&response_type=${responseType}")

}