## Подготовка

1. Стартуем docker-compose
2. Стартуем приложение
3. Открываем [data.connekt.kts](data.connekt.kts)
4. Запускаем весь файл


## Демо

1. Показываем Эксплорер, показываем какие есть сущности и какие ресты
2. Открываем ProductController. Генрируем запрос для findAll. Запускаем, говорим, как просто это выглядит. Не забываем env предварительно выбрать.
3. Создаем запрос для create. Но делаем это теперь из дерева. Заполняем чем хотим. Но не запускаем.

    На этом этапе имеем что-то такое:

    ```kotlin
    GET("http://localhost:8080/products")

    POST("http://localhost:8080/products") {
        header("Content-Type", "application/json")
        body(
            """
            {
                "name": "Тыква",
                "price": 158.9
            }
            """.trimIndent()
        )
    }
    ```

4. Простые запросы делаются просто, это хорошо. Может быть теперь сделаем тесты? Пишем для последнего запроса ассерты. Не обязательно, все. Запускаем.

    ```kotlin
    POST("http://localhost:8080/products") {
        header("Content-Type", "application/json")
        body(
            """
            {
                "name": "Тыква",
                "price": 158.9
            }
            """.trimIndent()
        )
    } then {
        assertThat(
            jsonPath().readString("$.name")
        ).isEqualTo("Тыква")

        assertThat(
            jsonPath().read("$.price", Double::class.java)
        ).isEqualTo(158.9)

        assertThat(
            jsonPath().readLong("$.id")
        ).isNotNull()
    }
   ```

5. Перейдем к задачам по-сложнее. Попробуем создать новый заказ. Генерируем запрос для `POST /orders/`. Чтобы выполнить, нам нужно указать cityId и customerId. Есть два способа, получить эти самые id. Можно посмотреть через DBeaver, можно выбрать любой.
6. После заполнения, сохраняем значение в переменную. Для этого ее необходимо извлечь через jsonPath. Должно получиться так:
    ```kotlin
    val orderId by POST("http://localhost:8080/orders") {
        header("Content-Type", "application/json")
        body(
            """
            {
                "customerId": 1,
                "cityId": 1
            }
            """.trimIndent()
        )
    } then {
        jsonPath().readLong("$.id")
    }
   ```
   Теперь мы можем работать с этой переменной! Начнем добавлять продукты в коризну.

7. Открываем [OrderController](src/main/java/ru/amplicode/orders/rest/OrderController.java), addProduct эндпоинт. Генерируем для него запрос.
    ```kotlin
    POST("http://localhost:8080/orders/{orderId}/lines") {
        header("Content-Type", "application/json")

        pathParam("orderId", orderId)

        body(
            """
            {
                "productId": 5,
                "amount": 1
            }
            """.trimIndent()
        )
    }
    ```
    Запускаем, но упадет 409. На складе нет продуктов!

8. Смотрим на эндпоинт `POST /inventory/supply`. Можем даже сгенерить от него. НО. Нужно поставить конкретный продукт в конкретный город и так несколько раз для разных продуктов. Можно сразу все? Да, пишем useCase.
    ```kotlin
   useCase {
        data class City(val id: Long)
        val cities: List<City> by GET("$host/cities") then {
            assertThat(code).isEqualTo(200)

            jsonPath().readList("$", City::class.java)
        }

        val productIds: List<Long> by GET("$host/products") then {
            jsonPath().readList("$.content[*].id", Long::class.java)
        }

        for (city in cities) {
            for (productId in productIds) {
                POST("$host/inventory/supply") {
                    header("Content-Type", "application/json")
                    body(
                        """
            {
                "productId": $productId,
                "cityId": ${city.id},
                "amount": ${Random.nextInt(100, 1000)}
            }
            """.trimIndent()
                    )
                }
            }
        }
    }
   ```

9. Кайф, все работает! Но представим, что мы это все задеплоили. Хочется проверить на стенде. Заменяем `http://localhost:8080/` на host, а host объявляем в самом начале вот так:
    ```kotlin
   val host: String by env
   ```
