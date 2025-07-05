val host: String by env

useCase("create products") {
    val products = listOf(
        "Apple" to 1.2,
        "Banana" to 0.8,
        "Cherry" to 2.5,
        "Date" to 3.0,
        "Elderberry" to 4.0
    )

    for ((name, price) in products) {
        POST("$host/products") {
            header("Content-Type", "application/json")
            body(
                """
        {
            "name": "$name",
            "price": $price
        }
        """.trimIndent()
            )
        }
    }
}

useCase("create cities") {
    val cities = listOf(
        "Samara",
        "Moscow",
        "Saint-Petersburg",
        "London",
        "New-York",
    )

    for (name in cities) {
        POST("$host/cities") {
            header("Content-Type", "application/json")
            body(
                """
        {
            "name": "$name"
        }
        """.trimIndent()
            )
        }
    }
}


useCase("create customers") {
    val customers = listOf("Alex", "John", "Peter", "Ilia", "Simon", "Robert", "David")

    for (customer in customers) {
        POST("$host/customers") {
            header("Content-Type", "application/json")
            body(
                """
        {
            "name": "$customer",
            "email": "$customer@gmail.com"
        }
        """.trimIndent()
            )
        }
    }

}
