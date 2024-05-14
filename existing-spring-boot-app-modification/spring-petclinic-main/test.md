UPDATE vets SET salary = floor(random() * (10000-5000+1) + 5000)::int;

#### Получение списка ветеренаров

```bash
curl -X GET -v http://localhost:8080/rest/vets | python3 -m 'json.tool'
```

#### Получение списка ветеренаров с фильтрацией по специальности

```bash
curl -X POST -v -H 'Content-Type: application/json' -d '["2"]' http://localhost:8080/rest/vets/by-speciality | python3 -m 'json.tool'
```
