import org.hamcrest.CoreMatchers.`is`
import java.util.*

val login: String by env
val password: String by env
val token: String = Base64.getEncoder().encodeToString("$login:$password".toByteArray())

val host: String by env

GET("$host/rest/owners/{id}") {
    header("Authorization", "Basic $token")
    pathParams("id", 1)
}

GET("$host/rest/owners") {
    header("Authorization", "Basic $token")
    queryParam("firstNameStarts", "J")
}

val id by variable<Long>()

POST("$host/rest/owners") {
    header("Authorization", "Basic $token")
    header("Content-Type", "application/json")
    body(
        """
        {
            "firstName": "Johny",
            "lastName": "Dow-vie",
            "address": "Spring Avenue",
            "city": "London",
            "telephone": null
        }
    """.trimIndent()
    )
} extract {
    id.set(body().jsonPath().getLong("id"))
}

GET("$host/rest/owners/{id}") {
    header("Authorization", "Basic $token")
    pathParams("id", id.get()!!)
} assert {
    statusCode(200)
    body("firstName", `is`("Johny"))
    body("lastName", `is`("Dow-vie"))
}
