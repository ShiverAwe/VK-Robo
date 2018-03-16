package temp

import com.vk.api.sdk.client.actors.UserActor
import java.awt.Desktop
import java.net.URL
import java.util.*

object AuthData {
    val APP_ID = 6383627
    val CLIENT_SECRET = "ZbOMwURj5cZ6P612exgc"
    val REDIRECT_URI = "https://oauth.vk.com/blank.html"
    val API_VERSION = "5.21"
    val DISPLAY = "page"
    val PERMISSIONS = "photos,messages"
    val RESPONSE_TYPE = "code"

    fun getActor(): UserActor {
        val sc = Scanner(System.`in`)
        openAuthPage()
        val authUrl = sc.nextLine()
        val properties: Map<String, String> = getProperties(authUrl)
        val code: String = properties["code"] ?: "null"

        val authResponse = Requests.vk.oauth()
                .userAuthorizationCodeFlow(APP_ID, CLIENT_SECRET, REDIRECT_URI, code)
                .execute()

        val actor = UserActor(authResponse.userId, authResponse.accessToken)

        return actor
    }


    private fun getProperties(query: String): Map<String, String> {
        val params: List<String> = query.trim().split("[&#]".toRegex())
        println(params)
        val map: HashMap<String, String> = HashMap()
        for ((index, param) in params.withIndex()) {
            if (index == 0) continue
            val name = param.split("=")[0]
            val value = param.split("=")[1]
            map[name] = value
        }
        return map
    }


    private val AUTH_URL = ("https://oauth.vk.com/authorize"
            + "?client_id=${APP_ID}"
            + "&scope=${PERMISSIONS}"
            + "&redirect_uri=${REDIRECT_URI}"
            + "&display=${DISPLAY}"
            + "&v=${API_VERSION}"
            + "&response_type=${RESPONSE_TYPE}")

    private fun openAuthPage() {
        val reqUrl = AUTH_URL
        Desktop.getDesktop().browse(URL(reqUrl).toURI())
    }

}