package temp

import java.util.*

object Configuration {

    val userToken: String
        get() = properties.getProperty("user_access_token")

    val userId: String
        get() = properties.getProperty("user_id")

    val appId: String
        get() = properties.getProperty("app_id")

    val appSecret: String
        get() = properties.getProperty("app_client_secret")

    val appRedirectUri: String
        get() = properties.getProperty("app_redirect_uri")

    private val properties: Properties = Properties()

    init {
        this.javaClass.getResourceAsStream("secret.properties").use {
            properties.load(it)
        }
    }

}