# Liquibase + Spring Boot: Настройка и написание миграций баз данных

В этом гайде мы расскажем, как подключить и настроить Liquibase в Spring Boot приложении, сгенерировать скрипты инициализации и миграции схемы БД, а также дополнить уже существующие changelog файлы новыми скриптами миграции вместе с Amplicode!

[![Смотреть на Youtube](images/youtube-thumbnail.png)](https://www.youtube.com/watch?v=blAaNt_XHAs)

**Примерное время прочтения: 20 минут.**

## Введение
В процессе разработки приложения нам время от времени приходится вносить изменения в JPA модель, что влечет за собой эволюцию схемы БД.

Без использования системы контроля версий существует риск потери данных и возникновения сложностей при совместной работе нескольких разработчиков над проектом. На сегодняшний день наиболее распространенной системой контроля версий является git, который отлично подходит для отслеживания изменений во всех файлах проекта.

Однако, помимо кода, не менее важно версионировать и базу данных, чтобы избежать потери данных при изменениях и иметь возможность отката к предыдущим версиям схемы БД в случае неудачного обновления или ошибки в процессе разработки.

Для эффективного управления версиями базы данных существуют специализированные решения, такие как Liquibase.

Данный гайд посвящен использованию Liquibase в Spring Boot приложениях с помощью Amplicode для наиболее удобного и эффективного управления базами данных.

Для прохождения этого гайда можно перейти в [репозиторий с гайдами](https://github.com/Amplicode/amplicode-tutorials) и выполнить шаги, описанные в [инструкции](https://github.com/Amplicode/amplicode-tutorials/blob/main/liquibase-and-spring-boot/README.md), чтобы настроить проект..

## Список задач, рассматриваемых в данном гайде
1. Подключение и настройка Liquibase
2. Расширение JPA модели и написание скриптов миграции БД
3. Валидация JPA модели и БД

## Обзор приложения

Прежде чем приступать к решению поставленных задач, необходимо изучить структуру используемого приложения `BlogApplicaion` с помощью панели Amplicode Explorer.

Панель выглядит следующим образом:

![application-in-amplicode-explorer.png](images/application-in-amplicode-explorer.png)

Чтобы проанализировать приложение в контексте используемых фреймворков и библиотек с помощью панели Amplicode Explorer, нажмите на значок стрелочки рядом с именем проекта, чтобы развернуть его. Здесь мы можем узнать, какие модули подключены к нашему проекту, что из себя представляет слой данных, какие эндпонты доступны, а также какие файлы для развертывания приложений уже есть в проекте.

![application-info-expanded.png](images/application-info-expanded.png)

Исходя из знаний Amplicode о нашем проекте, мы можем сделать следующие выводы:
1. Для удобной работы с персистентным слоем в приложении используется модуль Spring Data JPA (1)
2. В качестве СУБД используется PostgreSQL (2).
3. Мы можем более детально ознакомиться с моделью данных нашего приложения.
  * В качестве базовой для всех сущности используется mapped superclass `BaseEntity` (3.1).
  * Сущности `Post` и `User` связаны отношением "многие к одному" (3.2).
4. Наконец, мы можем посмотреть на `docker compose` файлы и их элементы (4).

![conclusions.png](images/conclusions.png)

## Подключение и настройка Liquibase к приложению

Первая задача — это подключение и настройка Liquibase к Spring Boot приложению с уже разработанной ранее JPA моделью. В процессе решения этой задачи, помимо добавления необходимой зависимости и настройки файла `application.properties`, нам также потребуется создать скрипт инициализации базы данных.

Для взаимодействия с PostgreSQL было бы удобно иметь также сервис pgAdmin. Amplicode предлагает pgAdmin в списке рекомендуемых сервисов, так как видит сервис PostgreSQL в текущем файле. Чтобы воспользоваться этой возможностью, нажмите на иконку в виде лампочки и выберите **Add pgAdmin service** в появившемся меню:

![docker-compose-pgadmin-suggested.png](images/docker-compose-pgadmin-suggested.png)

Откроется всплывающий диалог **Add pgAdmin to Docker Compose**. Чтобы воспользоваться предоставленной Amplicode возможностью для настройки автоматического подключения PostgreSQL к pgAdmin:

* Отметьте чекбокс **Configure DB server connections**

![configure-db-server-connections.png](images/configure-db-server-connections.png)
* Оставьте значения всех остальных параметров по умолчанию
* Нажмите **OK**.
  
Amplicode сгенерирует следующий код:

```
pgadmin:
image: dpage/pgadmin4:8.6
restart: always
ports:
- "5050:80"
volumes:
- pgadmin_data:/var/lib/pgadmin
- ./docker/pgadmin/servers.json:/pgadmin4/servers.json
- ./docker/pgadmin/pgpass:/pgadmin4/pgpass
environment:
PGADMIN_DEFAULT_EMAIL: admin@admin.com
PGADMIN_DEFAULT_PASSWORD: root
PGADMIN_CONFIG_SERVER_MODE: "False"
PGADMIN_CONFIG_MASTER_PASSWORD_REQUIRED: "False"
healthcheck:
test: wget --no-verbose --tries=1 --spider http://localhost:80/misc/ping || exit -1
interval: 10s
timeout: 5s
start_period: 10s
retries: 5
entrypoint: /bin/sh -c "chmod 600 /pgadmin4/pgpass; /entrypoint.sh;"
volumes:
pgadmin_data:
```

Теперь необходимо запустить все сервисы, чтобы посмотреть на актуальное состояние БД. Для этого необходимо нажать на иконку двойной стрелочки напротив слова `services` в файле `docker-compose.yaml`

![run-all-services.png](images/run-all-services.png)

После запуска Amplicode добавит inlay для удобного открытия адреса, связанного с сервисом, прямо из IntelliJ IDEA.

![inlay.png](images/inlay.png)

На данный момент база данных находится в том же состоянии, что и JPA модель. Переходим к подключению Liquibase к нашему приложению.

Amplicode Explorer позволяет добавить необходимые стартеры и библиотеки к проекту. Для этого необходимо:
* Щелкнуть правой кнопкой мыши по узлу **Configuration**
* Выбрать опцию **Add Configuration**

![add-configuration.png](images/add-configuration.png)
* В открывшемся окне выбрать **DB Migration Configuration**

![db-migration-configuration.png](images/db-migration-configuration.png)

Откроется диалоговое окно **DB Migration Settings**.

![db-migration-settings.png](images/db-migration-settings.png)

Далее необходимо сделать следующее:
* В открывшемся всплывающем диалоговом окне выбрать **Liquibase**
* Оставить название основного changelog файла, его формат и директорию, где он будет создан, как задано по умолчанию

![choose-liquibase.png](images/choose-liquibase.png)
* Отметить чекбоксы **Create init DB scripts** и **Run changelog-sync**.

![check-boxes.png](images/check-boxes.png)
Благодаря выбору первого чекбокса произойдет автоматическое перенаправление к окну генерации скрипта базы данных. Благодаря выбору второго чекбокса Amplicode выполнит команду Liquibase `changelog-sync`сразу после генерации скриптов инициализации базы данных. Эта команда позволит отметить скрипты как выполненные, при этом фактически их не выполняя, т.к. все, что будет в них описано, уже есть в базе данных.
* В качестве источника данных для генерации скрипта инициализации выберем базу данных. Для анализа ее структуры Amplicode потребуется подключение к базе данных.

![choose-db.png](images/choose-db.png)

* Выбрать опцию создания нового подключения.

![create-new-db-connection.png](images/create-new-db-connection.png)

(Здесь необходимо отметить, что Amplicode позволяет создать подключение к базе данных с нуля, либо отталкиваясь от информации, указанной для источника данных в приложении. Здесь следует выбрать второй вариант, так как источник данных в приложении уже настроен.)

Появится следующее диалоговое окно:
![detected-data-sources.png](images/detected-data-sources.png)

* Нажмите **Create DB Connection**. Теперь вы увидите следующее окно:

![db-connection-dialog.png](images/db-connection-dialog.png)
* Нажмите **Test Connection**, чтобы проверить подключение. После этого нажмите **OK**.
* Нажмите **OK** также в главном диалоговом окне.

* Amplicode добавит необходимые зависимости в `build.gradle` и свойства в `application.properties`, сгенерирует файл `db.changelog-master.xml` и начнет процесс генерации скрипта инициализации базы данных. В открывшемся окне **Change Preview** мы можем изменить расположение, тип и другие параметры файла, а также убедиться в корректности всего файла и его отдельных changeset'ов.

![change-preview-window.png](images/change-preview-window.png)

* Нажмите **OK**

По результатам выполнения Amplicode сохранит changelog по указанному пути и выполнит команду `changelog-sync`, т.к. ранее был отмечен соответствующий чекбокс.

По итогу выполненных действий команда будет успешно выполнена, системные таблицы Liquibase будут добавлены в базу данных, а таблица `databasechangelog` получит информацию о выполненных скриптах. Выполнение первой задачи на этом завершено.

## Модификация JPA модели

Приступим к модификации текущей JPA модели. Так как использование факса стало редкостью, следует убрать его из модели, удалив следующий (выделенный) код, а также связанные с ним геттер и сеттер:

![fax.png](images/fax.png)

Для обеспечения полноты данных сделаем атрибут `email` обязательным с помощью Amplicode Designer.

![email-mandatory.png](images/email-mandatory.png)

Также нам необходимо добавить новую сущность - `Comment` и установить отношение "многие ко многим" с сущностью `Post`.

Чтобы выполнить оба эти действия с помощью Amplicode Designer:
* Перейдите в сущность `Post` и откройте окно создания ассоциации двойным щелчком на пункте меню **Attributes** -> **Association** в палитре.

![new-association-attribute-window.png](images/new-association-attribute-window.png)
* Создайте новую сущность нажатием на кнопку плюс, укажите ей имя и выберите родительский класс.

![name-and-parent.png](images/name-and-parent.png)
* Измените кардинальность ассоциации на "многие ко многим" (1). Amplicode уже выбрал наиболее оптимальный тип для ассоциации "многие ко многим" — `Set`, но для полностью корректной работы `Set` с JPA сущностями также необходимо чтобы у неё были корректно переопределены методы `equals()` и `hashCode()` (2). Amplicode знает об этом, и предупреждает, что текущая реализация может быть неоптимальной в плане производительности и предложит сгенерировать реализацию методов `equals()` и `hashCode()`. Чтобы согласиться с этим предложением, нажмите соответствующую ссылку (3).

![many-to-many.png](images/many-to-many.png)
* Нажмите ОК, чтобы создать соответствующий класс `Comment`.

Amplicode сгенерирует следующий код для сущности:

```asciidoc
@Entity
@Table(name = "comment")
public class Comment extends BaseEntity {
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy
                ? ((HibernateProxy) o).getHibernateLazyInitializer()
                .getPersistentClass()
                : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer()
                .getPersistentClass()
                : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Comment comment = (Comment) o;
        return getId() != null && Objects.equals(getId(), comment.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer()
                .getPersistentClass()
                .hashCode()
                : getClass().hashCode();
    }
}
```

Теперь необходимо сгенерировать для сущности `Comment` новый базовый атрибут `text`, а также создать отношение "многие к одному" с сущностью `User`. Для этого:
* Установите курсор на имени класса `Comment`  в соответствующем файле
* Вызовите всплывающее окно **Generate**, нажав **Alt+Insert** для Windows/Linux или **⌘+N** для macOS.

![generate.png](images/generate.png)
* Выберите **Entity Attribute**
* Введите тип (**String**) и нажмите **Enter**

![entity-attribute.png](images/entity-attribute.png)
* Введите название атрибута **text**
  ![entity-attribute-name.png](images/entity-attribute-name.png)
* Нажмите **OK**
  
Amplicode сгенерирует код атрибута, а также getter и setter к нему:
```
java
@Column(name = "text")
private String text;

public String getText() {
return text;
}

public void setText(String text) {
this.text = text;
}
```
* Еще раз вызовите окно **Generate** и выберите **Entity Attribute**
* В окне типа атрибута введите `User`

![type-user.png](images/type-user.png)
* Задайте название атрибута (`author`) и сделайте его обязательным.

![author-mandatory.png](images/author-mandatory.png)
* Нажмите **OK**

Задача по модификации JPA модели выполнена. Следующий шаг — генерации скриптов миграции Liquibase.

## Генерация Liquibase скриптов миграции для синхронизации JPA модели и схемы БД

Для создания Liquibase скрипта миграции необходимо обратиться к панели Amplicode Explorer и в секции DB Versioning выбрать пункт **Liquibase Diff Changelog**.

В открывшемся окне следует убедиться в правильности выбранных persistence unit и подключения к базе данных.

![diff-window.png](images/diff-window.png)

С окном предпросмотра changelog файлов мы уже познакомились в процессе создания скрипта инициализации базы данных.

![changelog-preview.png](images/changelog-preview.png)

Стоит отметить, что в данном случае цветовая раскраска некоторых changeset отличается, например, скрипт на удаление колонки `fax` отмечен красным цветом, а скрипт на добавление `NotNull` ограничения — желтым.

![red-and-yellow.png](images/red-and-yellow.png)

Оба скрипта являются потенциально опасными ввиду возможной потери данных, поэтому Amplicode старается обратить на них максимальное внимание со стороны разработчика.

Разрабатывая JPA модель, мы решили две бизнес задачи: изменение сущности `User` и создание сущности `Comment`. Нам бы не хотелось складывать скрипты, относящиеся к разным бизнес задачам, в один файл. Amplicode позволяет разнести changeset скрипты в несколько changelog файлов, не покидая текущего окна.

Для этого необходимо:
* Выбрать скрипты, относящиеся к изменению пользователя (1), и в верхней панели выбрать действие переноса скриптов в новый changelog файл (2)

![select-scripts-user.png](images/select-scripts-user.png)

* Задать название каждому из changelog файлов
  ![changelog-file-names.png](images/changelog-file-names.png)
* Для файла модификации со скриптами таблицы `users` изменить путь к файлу (1), задать имя (2) и указать главный changelog (3), в который нужно будет включить текущий файл.

![changelog-names.png](images/changelog-names.png)

Также обратите внимание, что для скрипта добавления `NotNull` ограничения Amplicode хочет предложить некоторые улучшения и сигнализирует нам об этом иконкой лампочки. Так как мы добавляем `NotNull` ограничение, мы должны быть уверены в том, что в таблице уже нет `null` значений.

Amplicode позволяет нам улучшить существующий скрипт, указав значение, которое будет проставлено для всех `null` значений перед добавлением ограничения.

![default-value-for-null.png](images/default-value-for-null.png)

Перед сохранением changelog файлов можем еще раз убедиться в их корректности в окне предпросмотра.

![script-preview.png](images/script-preview.png)

Кстати, есть еще один скрипт, который мы упустили. Это скрипт удаления индекса из таблицы `Users`в секции **Ignored**.

![drop-index.png](images/drop-index.png)

Amplicode автоматически разместил этот скрипт в данную секцию, т.к. создание индекса может быть довольно дорогостоящей операцией, а его добавление на уровне JPA модели не является довольно распространенным. Поэтому в реальной жизни удалять его из базы данных приходится крайне редко.

![ignored-script.png](images/ignored-script.png)

Но в случае необходимости всегда можно удалить скрипт из списка игнорируемых, либо наоборот расширить список игнорируемых скриптов, выбрав и перетащив ненужные скрипты в секцию **Ignored** прямо в окне предпросмотра или сконфигурировав такой список заранее в настройках.

![unlist-ignored.png](images/unlist-ignored.png)

Оба changelog файла успешно сгенерированы и включены в наш основной файл `db.changelog-master.xml`.

![included-in-master.png](images/included-in-master.png)

Остается только выполнить скрипты и накатить изменения в базу данных. Конечно же, самым правильным подходом к применению скриптов миграции будет настройка Liquibase плагина для Maven или Gradle, т.к. он в любом случае понадобится во время настройки CI/CD pipeline. Но, чтобы не отвлекаться на эту задачу на данном этапе, в рамках данного гайда можно воспользоваться действием **Liquibase Update** в Amplicode и накатить скрипты без предварительной настройки плагина, отложив эту задачу на потом.

![liquibase-update.png](images/liquibase-update.png)

При выборе данного пункта из меню появится следующее выплывающее окно. Нажмите в нем кнопку **Update**.

![update-window.png](images/update-window.png)

Все скрипты выполнились успешно, а информация об их успешном выполнении была записана в таблицу `databasechangelog`.

![databasechangelog-table.png](images/databasechangelog-table.png)

## Добавление новых скриптов миграции в существующий файл

Модифицируя JPA модель, мы забыли сделать атрибут `text` для сущности `Comment` обязательным.

![text-not-mandatory.png](images/text-not-mandatory.png)

Исправить эту оплошность довольно легко.

Для этого, воспользуйтесь панелью Amplicode Designer и пометьте атрибут как обязательный:

![make-mandatory.png](images/make-mandatory.png)

Однако, засорять проект множеством Liquibase changelog файлов нежелательно. К тому же, данный скрипт относится именно к changelog файлу, содержащему скрипты, связанные с созданием таблицы `Comments`. Благодаря тому, что Liquibase исполняет и фиксирует исполнение именно changeset'ов, из которых состоит changelog файл, а не исполнение всего changelog файла целиком, мы можем дополнить существующие changelog файлы новыми changeset скриптами. Amplicode хорошо знает про подобную возможность и позволяет догенерировать необходимые скрипты в существующий файл.

Для этого:

* Откройте нужный нам changelog файл и обратитесь к меню **Generate** от IntelliJ IDEA и выберите пункт **Liquibase Diff Changes** от Amplicode.

![diff-changes.png](images/diff-changes.png)

* В открывшемся всплывающем окне нажмите **OK**.
  Новый changeset будет добавлен в существующий changelog файл.
```asciidoc
<changeSet id="1718798931904-1" author="georgii (generated)">
    <addNotNullConstraint columnDataType="VARCHAR(255)" columnName="text" tableName="comment" validate="true"/>
</changeSet>
```

* Накатите изменения (как показано выше) и проверьте таблицу `databasechangelog` через pgAdmin.

## Запуск Spring Boot приложения (анализ логов от Amplicode)

Хорошей практикой при разработке Spring Boot приложения и использованием системы версионирования баз данных является применение возможностей валидации соответствия JPA модели и схемы базы данных при помощи Hibernate. Путем использования свойства Hibernate `spring.jpa.hibernate.ddl-auto` со значением `validate` мы можем обеспечить соответствие JPA модели и схемы базы данных.

Внесите следующий код в файл `application-properties`:
```asciidoc
spring.jpa.hibernate.ddl-auto=validate
```
Чтобы сделать это быстро, начните печатать `ddl-auto` и Amplicode предложит вам соответствующее свойство. Достаточно будет выбрать его из выпадающего списка и нажать `Enter`.

![validate.png](images/validate.png)

В случае обнаружения несоответствия Spring Boot приложение не запустится и выдаст ошибку.

Запустите приложение, чтобы убедиться, что в логах действительно отсутствуют исключения.

![run-application.png](images/run-application.png)

Если приложение запущено без проблем, лог должен выглядеть примерно следующим образом:

![no-exceptions.png](images/no-exceptions.png)

Столь же важно отметить, что в случае несоответствия JPA модели и схемы базы данных Amplicode сообщил бы о проблеме и дал бы возможность решить ее прямо из stacktrace.

## Заключение

Подводя итог, все поставленные в данном гайде задачи успешно выполнены примерно за 20 минут.

Вы научились:
1. Подключать и настраивать Liquibase в проекте, написанном на Spring Boot с применением Amplicode
2. Расширять JPA модель и писать скрипты миграции БД
3. Валидировать JPA модель и БД

Вы можете установить Amplicode уже сейчас и применить полученные знания на практике.
