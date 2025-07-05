import java.io.File

GET("http://localhost:8080/products") {

}

PATCH("http://localhost:8080/products") {
    queryParam("ids", "1,2,3")
    header("Content-Type", "application/json")
    body(
        """
        {
          "price": 199.99
        }
        """.trimIndent()
    )
}

PATCH("http://localhost:8080/products/{id}") {
    pathParam("id", "2")
    multipart {
        part (name = "body", contentType = "application/json") {
            body(
                """
                    {
                        "price":100.00
                    }
                """.trimIndent()
            )
        }
        file(
            name = "image",
            fileName = "banana.jpg",
            File("banana.jpg")
        )
    }

}


GET("http://localhost:8080/products/{id}") {
    pathParam("id", 2)
}
