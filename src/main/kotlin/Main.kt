import java.util.*


fun main(args: Array<String>) {
    val app = Application()
    //app.auth()
    val sc = Scanner(System.`in`)
    val properties: Map<String, String> = app.getProperties(sc.nextLine())
    val code: String = properties["code"] ?: "null"
    //println(code)
    app.main(code)
}
