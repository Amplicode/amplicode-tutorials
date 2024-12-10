GET("http://localhost:8080/rest/owners/{id}") {
    pathParams("id", "1")
}

POST("http://localhost:8080/rest/owners") {
    header("Content-Type", "application/json")
    body(
        """
            {
                "firstName": "John",
                "lastName": "Doe",
                "address": "Spring Avenue",
                "city": "Spring Ville",
                "telephone": "+1 234 567 89 00"
            }
        """.trimIndent()
    )
}
