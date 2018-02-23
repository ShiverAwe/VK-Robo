import java.awt.Desktop
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.*


fun with(appId: String, accessToken: String): Application {
    return Application(appId, accessToken)
}

class Application(
        private val appId: String = "",
        private val accessToken: String = "dfd4356905a55263116b891f55f74ba8f40fbf6b74c5b292bbd7e1768279860ba12560f2c835143b02a7"
) {

    private val APP_ID = appId
    private val EXPIRES_IN = "86400"
    private val USER_ID = "135383817"
    private val API_VERSION = "5.21"
    private val DISPLAY = "page"
    private val REDIRECT_URI = "https://oauth.vk.com/blank.html"
    private val PERMISSIONS = "photos,messages"

    init {
        if (accessToken.isEmpty()) {
            auth(appId)
            throw Error("Need access token")
        }
    }

    private val AUTH_URL = ("https://oauth.vk.com/authorize"
            + "?client_id=${APP_ID}"
            + "&scope=${PERMISSIONS}"
            + "&redirect_uri=${REDIRECT_URI}"
            + "&display=${DISPLAY}"
            + "&v=${API_VERSION}"
            + "&response_type=token")

    private val API_REQUEST = ("https://api.vk.com/method/{METHOD_NAME}"
            + "?{PARAMETERS}"
            + "&access_token={ACCESS_TOKEN}"
            + "&v=" + API_VERSION)

    private fun auth(appId: String, scope: String, redirectUri: String) = ("https://oauth.vk.com/authorize"
            + "?client_id=${appId}"
            + "&scope=${scope}"
            + "&redirect_uri=${redirectUri}"
            + "&display=${DISPLAY}"
            + "&v=${API_VERSION}"
            + "&response_type=token")

    private fun auth(appId: String) {
        val reqUrl = AUTH_URL
        Desktop.getDesktop().browse(URL(reqUrl).toURI())
    }

    fun getDialogs(): String {
        return invokeApi("messages.getDialogs", null)
    }

    fun getHistory(userId: String, offset: Int, count: Int, rev: Boolean): String {
        return invokeApi("messages.getHistory", Params.create()
                .add("user_id", userId)
                .add("offset", offset.toString())
                .add("count", count.toString())
                .add("rev", if (rev) "1" else "0"))
    }

    fun getAlbums(userId: String): String {
        return invokeApi("photos.getAlbums", Params.create()
                .add("owner_id", userId)
                .add("photo_sizes", "1")
                .add("thumb_src", "1"))
    }

    private fun invokeApi(method: String, params: Params?): String {
        val parameters = params?.build() ?: ""
        val reqUrl = API_REQUEST
                .replace("{METHOD_NAME}", method)
                .replace("{ACCESS_TOKEN}", accessToken)
                .replace("{PARAMETERS}&", parameters)
        return invokeApi(reqUrl)
    }

    private fun invokeApi(requestUrl: String): String {
        val result = StringBuilder()
        val url = URL(requestUrl)
        url.openStream().use { `is` ->
            val reader = BufferedReader(InputStreamReader(`is`, StandardCharsets.UTF_8))
            reader.lines().forEach {
                result.append(it)
            }
        }
        return result.toString()
    }

    private class Params private constructor() {

        private val params: HashMap<String, String> = HashMap()

        fun add(key: String, value: String): Params {
            params.put(key, value)
            return this
        }

        fun build(): String {
            if (params.isEmpty()) return ""
            val result = StringBuilder()
            params.keys.stream().forEach { key ->
                result.append("${key}=${params[key]}&");
            }
            return result.toString()
        }

        companion object {
            fun create(): Params {
                return Params()
            }
        }
    }
}