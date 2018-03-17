package temp

import com.vk.api.sdk.client.actors.ServiceActor
import com.vk.api.sdk.client.actors.UserActor
import com.vk.api.sdk.httpclient.HttpTransportClient
import java.awt.Desktop
import java.net.URL
import java.util.*

object AuthData {
    val APP_ID = 6383627
    val CLIENT_SECRET = "ZbOMwURj5cZ6P612exgc"
    val SERVICE_TOKEN = "db52d295db52d295db52d295e6db33ba9eddb52db52d29581dd240242f23149b23daa6f"
    val REDIRECT_URI = "https://oauth.vk.com/blank.html"
    val API_VERSION = "5.21"
    val DISPLAY = "page"
    val PERMISSIONS = "friends,status"
    val RESPONSE_TYPE = "code"

    val transportClient = HttpTransportClient.getInstance()

    fun getActor(code: String): UserActor {
        val authResponse = Requests.vk.oauth()
                .userAuthorizationCodeFlow(APP_ID, CLIENT_SECRET, REDIRECT_URI, code)
                .execute()
        val actor = UserActor(authResponse.userId, authResponse.accessToken)
        return actor
    }

    fun getServiceActor(): ServiceActor {
        return ServiceActor(APP_ID, CLIENT_SECRET, SERVICE_TOKEN)
    }

    fun getCode(): String {
        val sc = Scanner(System.`in`)
        openAuthPage("code")
        val authUrl = sc.nextLine()
        val properties: Map<String, String> = getProperties(authUrl)
        val code: String = properties["code"] ?: "null"
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
        val map: HashMap<String, String> = HashMap()
        for ((index, param) in params.withIndex()) {
            if (index == 0) continue
            val name = param.split("=")[0]
            val value = param.split("=")[1]
            map[name] = value
        }
        return map
    }


    private fun authUrl(responseType: String) = ("https://oauth.vk.com/authorize"
            + "?client_id=${APP_ID}"
            + "&scope=${PERMISSIONS}"
            + "&redirect_uri=${REDIRECT_URI}"
            + "&display=${DISPLAY}"
            + "&v=${API_VERSION}"
            + "&response_type=${responseType}")

    private fun openAuthPage(responseType: String) {
        val reqUrl = authUrl(responseType)
        println(reqUrl)
        Desktop.getDesktop().browse(URL(reqUrl).toURI())
    }


}