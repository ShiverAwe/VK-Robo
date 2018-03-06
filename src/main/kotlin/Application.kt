import com.vk.api.sdk.client.VkApiClient
import com.vk.api.sdk.client.actors.UserActor
import com.vk.api.sdk.httpclient.HttpTransportClient
import com.vk.api.sdk.objects.users.UserXtrCounters
import com.vk.api.sdk.queries.users.UserField
import java.awt.Desktop
import java.net.URL
import java.util.HashMap

class Application {
    val APP_ID = 6383627
    val CLIENT_SECRET = "ZbOMwURj5cZ6P612exgc"
    //val code = "644ef1146078f41b37"
    val EXPIRES_IN = "86400"
    val USER_ID = 135383817
    val API_VERSION = "5.21"
    val DISPLAY = "page"
    val REDIRECT_URI = "https://oauth.vk.com/blank.html"
    val PERMISSIONS = "photos,messages"
    val RESPONSE_TYPE = "code"

    fun main(code: String) {
        val transportClient = HttpTransportClient.getInstance()
        val vk = VkApiClient(transportClient)
        val authResponse = vk.oauth()
                .userAuthorizationCodeFlow(APP_ID, CLIENT_SECRET, REDIRECT_URI, code)
                .execute()

        val actor = UserActor(authResponse.userId, authResponse.accessToken)

        val userok: MutableList<UserXtrCounters> = vk.users().get(actor)
                .userIds("19383739")
                .fields(UserField.ABOUT,
                        UserField.CITY,
                        UserField.LAST_SEEN)
                .execute()

        println(userok)

//        val getResponse = vk.wall().get(actor)
//                .ownerId(USER_ID)
//                .count(100)
//                .offset(5)
////            .filter("owner")
//                .execute()
//
//        println(getResponse)
    }

    fun auth() {
        val AUTH_URL = ("https://oauth.vk.com/authorize"
                + "?client_id=${APP_ID}"
                + "&scope=${PERMISSIONS}"
                + "&redirect_uri=${REDIRECT_URI}"
                + "&display=${DISPLAY}"
                + "&v=${API_VERSION}"
                + "&response_type=${RESPONSE_TYPE}")

        Desktop.getDesktop().browse(URL(AUTH_URL).toURI())
    }

    fun getProperties(query: String): Map<String, String> {
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
}