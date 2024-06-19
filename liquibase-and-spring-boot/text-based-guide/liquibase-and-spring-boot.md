# Liquibase + Spring Boot: Настройка и написание миграций баз данных

**Изучите, как подключить в проект и использовать Liquibase для написания миграционных скриптов баз данных в приложении, написанном на Spring Boot с использованием Amplicode.**

![youtube-thumbnail.png](https://amplicode-strapi-ru.demo.haulmont.com/uploads/youtube_thumbnail_82fa8979fe.png)(https://www.youtube.com/watch?v=blAaNt_XHAs)
**Смотреть на YouTube:** https://www.youtube.com/watch?v=blAaNt_XHAs

**Примерное время прочтения: 20 минут.**

## Введение
В процессе разработки приложения нам время от времени приходится вносить изменения в JPA модель, что влечет за собой эволюцию схемы БД.

Без использования системы контроля версий существует риск потери данных и возникновения сложностей при совместной работе нескольких разработчиков над проектом. На сегодняшний день наиболее распространенной системой контроля версий является git, который отлично подходит для отслеживания изменений во всех файлах проекта.

Однако, помимо кода, не менее важно версионировать и базу данных, чтобы избежать потери данных при изменениях и иметь возможность отката к предыдущим версиям схемы БД в случае неудачного обновления или ошибки в процессе разработки.

Для эффективного управления версиями базы данных существуют специализированные решения, такие как Liquibase.

Данный гайд посвящен использованию Liquibase в Spring Boot приложениях с помощью Amplicode для наиболее удобного и эффективного управления базами данных.

Для прохождения этого гайда можно перейти в репозиторий с гайдами (https://github.com/Amplicode/amplicode-tutorials) и выполнить шаги, описанные в инструкции по адресу https://github.com/Amplicode/amplicode-tutorials/blob/main/liquibase-and-spring-boot/README.md.

## Список задач, рассматриваемых в данном гайде
1. Подключение и настройка Liquibase
2. Расширение JPA модели и написание скриптов миграции БД
3. Валидация JPA модели и БД

## Обзор приложения

Прежде чем приступать к решению поставленных задач, необходимо изучить структуру используемого приложения `BlogApplicaion` с помощью панели Amplicode Explorer.

Панель выглядит следующим образом:

![application-in-amplicode-explorer.png](https://amplicode-strapi-ru.demo.haulmont.com/uploads/application_in_amplicode_explorer_0a34c06dca.png)

Чтобы проанализировать приложение в контексте используемых фреймворков и библиотек с помощью панели Amplicode Explorer, нажмите на значок стрелочки рядом с именем проекта, чтобы развернуть его. Здесь мы можем узнать, какие модули подключены к нашему проекту, что из себя представляет слой данных, какие эндпонты доступны, а также какие файлы для развертывания приложений уже есть в проекте.

![application-info-expanded.png](https://amplicode-strapi-ru.demo.haulmont.com/uploads/application_info_expanded_e90a33fec8.png)
Вы можете продолжать нажимать на стрелочки рядом с различными разделами панели, чтобы получить больше информации:

![application-info-further-expanded.png](https://amplicode-strapi-ru.demo.haulmont.com/uploads/application_info_further_expanded_bf7e93f7f8.png)

Исходя из знаний Amplicode о нашем проекте, мы можем сделать следующие выводы:
1. Для удобной работы с персистентным слоем в приложении используется модуль Spring Data JPA:

![spring-data-jpa.png](https://amplicode-strapi-ru.demo.haulmont.com/uploads/spring_data_jpa_2f8bfdc973.png)
2. В качестве СУБД используется PostgreSQL.
3. Мы можем более детально ознакомиться с моделью данных нашего приложения.
  * В качестве базовой для всех сущности используется mapped superclass `BaseEntity`.
  * Сущности `Post` и `User` связаны отношением "многие к одному".

![persistence.png](https://amplicode-strapi-ru.demo.haulmont.com/uploads/persistence_70c625ea21.png)
4. Наконец, мы можем посмотреть на `docker compose` файлы и их элементы.

![docker-compose.png](https://amplicode-strapi-ru.demo.haulmont.com/uploads/docker_compose_e9b38de7af.png)

## Подключение и настройка Liquibase к приложению

Первая задача — это подключение и настройка Liquibase к Spring Boot приложению с уже разработанной ранее JPA моделью. В процессе решения этой задачи, помимо добавления необходимой зависимости и настройки файла `application.properties`, нам также потребуется создать скрипт инициализации базы данных.

Для взаимодействия с PostgreSQL было бы удобно иметь также сервис pgAdmin. Amplicode предлагает pgAdmin в списке рекомендуемых сервисов, так как видит сервис PostgreSQL в текущем файле. Чтобы воспользоваться этой возможностью, нажмите на иконку в виде лампочки и выберите **Add pgAdmin service** в появившемся меню:

![docker-compose-pgadmin-suggested.png](https://amplicode-strapi-ru.demo.haulmont.com/uploads/docker_compose_pgadmin_suggested_d5ea2a5985.png)

Откроется всплывающий диалог **Add pgAdmin to Docker Compose**. Чтобы воспользоваться предоставленной Amplicode возможностью для настройки автоматического подключения PostgreSQL к pgAdmin:

* Отметьте чекбокс **Configure DB server connections**

![configure-db-server-connections.png](https://amplicode-strapi-ru.demo.haulmont.com/uploads/configure_db_server_connections_52cb41a933.png)
* Оставьте значения всех остальных параметров по умолчанию
* Нажмите **OK**.

Теперь необходимо запустить все сервисы, чтобы посмотреть на актуальное состояние БД. Для этого необходимо нажать на иконку двойной стрелочки напротив слова `services` в файле `docker-compose.yaml`

![run-all-services.png](https://amplicode-strapi-ru.demo.haulmont.com/uploads/run_all_services_7cbc82a610.png)

После запуска Amplicode добавит inlay для удобного открытия адреса, связанного с сервисом, прямо из IntelliJ IDEA.

![inlay.png](https://amplicode-strapi-ru.demo.haulmont.com/uploads/inlay_14a3effa74.png)

На данный момент база данных находится в том же состоянии, что и JPA модель. Переходим к подключению Liquibase к нашему приложению.

Amplicode Explorer позволяет добавить необходимые стартеры и библиотеки к проекту. Для этого необходимо:
* Щелкнуть правой кнопкой мыши по узлу **Configuration**
* Выбрать опцию **Add Configuration**

![add-configuration.png](https://amplicode-strapi-ru.demo.haulmont.com/uploads/add_configuration_c291cfb5e5.png)
* В открывшемся окне выбрать **DB Migration Configuration**

![db-migration-configuration.png](https://amplicode-strapi-ru.demo.haulmont.com/uploads/db_migration_configuration_9a91cd4f41.png)
Откроется диалоговое окно **DB Migration Settings**.

![db-migration-settings.png](https://amplicode-strapi-ru.demo.haulmont.com/uploads/db_migration_settings_a4fb6d3ceb.png)

Далее необходимо сделать следующее:
* В открывшемся всплывающем диалоговом окне выбрать **Liquibase**
* Оставить название основного changelog файла, его формат и директорию, где он будет создан, как задано по умолчанию

![choose-liquibase.png](https://amplicode-strapi-ru.demo.haulmont.com/uploads/choose_liquibase_82350f69ad.png)
* Отметить чекбоксы **Create init DB scripts** и **Run changelog-sync**.

![check-boxes.png](https://amplicode-strapi-ru.demo.haulmont.com/uploads/check_boxes_8a62e183fd.png)
Благодаря выбору первого чекбокса произойдет автоматическое перенаправление к окну генерации скрипта базы данных. Благодаря выбору второго чекбокса Amplicode выполнит команду Liquibase `changelog-sync`сразу после генерации скриптов инициализации базы данных. Эта команда позволит отметить скрипты как выполненные, при этом фактически их не выполняя, т.к. все, что будет в них описано, уже есть в базе данных.
* В качестве источника данных для генерации скрипта инициализации выберем базу данных. Для анализа ее структуры Amplicode потребуется подключение к базе данных.

![choose-db.png](https://amplicode-strapi-ru.demo.haulmont.com/uploads/choose_db_e6d41278a2.png)
* Выбрать опцию создания нового подключения.

![create-new-db-connection.png](https://amplicode-strapi-ru.demo.haulmont.com/uploads/create_new_db_connection_9102412777.png)
(Здесь необходимо отметить, что Amplicode позволяет создать подключение к базе данных с нуля, либо отталкиваясь от информации, указанной для источника данных в приложении. Здесь следует выбрать второй вариант, так как источник данных в приложении уже настроен.)

Появится следующее диалоговое окно:
![detected-data-sources.png](https://amplicode-strapi-ru.demo.haulmont.com/uploads/detected_data_sources_c365485661.png)

* Нажмите **Create DB Connection**. Теперь вы увидите следующее окно:

![db-connection-dialog.png](https://amplicode-strapi-ru.demo.haulmont.com/uploads/db_connection_dialog_7e06e82a21.png)
* Нажмите **Test Connection**, чтобы проверить подключение. После этого нажмите **OK**.
* Нажмите **OK** также в главном диалоговом окне.

* Amplicode добавит необходимые зависимости в `build.gradle` и свойства в `application.properties`, сгенерирует файл `db.changelog-master.xml` и начнет процесс генерации скрипта инициализации базы данных. В открывшемся окне **Change Preview** мы можем изменить расположение, тип и другие параметры файла, а также убедиться в корректности всего файла и его отдельных changeset'ов.

![change-preview-window.png](https://amplicode-strapi-ru.demo.haulmont.com/uploads/change_preview_window_02d2095db9.png)

* Нажмите **OK**

По результатам выполнения Amplicode сохранит changelog по указанному пути и выполнит команду `changelog-sync`, т.к. ранее был отмечен соответствующий чекбокс.

По итогу выполненных действий команда будет успешно выполнена, системные таблицы Liquibase будут добавлены в базу данных, а таблица `databasechangelog` получит информацию о выполненных скриптах. Выполнение первой задачи на этом завершено.

## Модификация JPA модели

Приступим к модификации текущей JPA модели. Так как использование факса стало редкостью, следует убрать его из модели, удалив следующий (выделенный) код, а также связанные с ним геттер и сеттер:

![fax.png](https://amplicode-strapi-ru.demo.haulmont.com/uploads/fax_9b31668585.png)

Для обеспечения полноты данных сделаем атрибут `email` обязательным с помощью Amplicode Designer.

![email-mandatory.png](https://amplicode-strapi-ru.demo.haulmont.com/uploads/email_mandatory_1f14d090b8.png)

Также нам необходимо добавить новую сущность - `Comment` и установить отношение "многие ко многим" с сущностью `Post`.

Чтобы выполнить оба эти действия с помощью Amplicode Designer:
* Откройте окно создания ассоциации двойным щелчком на пункте меню **Attributes** -> **Association** в палитре

![new-association-attribute-window.png](https://amplicode-strapi-ru.demo.haulmont.com/uploads/new_association_attribute_window_550379a360.png)
* Создайте новую сущность нажатием на кнопку плюс, укажите ей имя и выберите родительский класс.

![name-and-parent.png](https://amplicode-strapi-ru.demo.haulmont.com/uploads/name_and_parent_2820991420.png)
* Измените кардинальность ассоциации на "многие ко многим". Amplicode уже выбрал наиболее оптимальный тип для ассоциации "многие ко многим" — `Set`, но для полностью корректной работы `Set` с JPA сущностями также необходимо чтобы у неё были корректно переопределены методы `equals()` и `hashCode()`. Amplicode знает об этом, и предупреждает, что текущая реализация может быть неоптимальной в плане производительности и предложит сгенерировать реализацию методов `equals()` и `hashCode()`. Чтобы согласиться с этим предложением, нажмите соответствующую ссылку.

![many-to-many.png](https://amplicode-strapi-ru.demo.haulmont.com/uploads/many_to_many_05a7b7d650.png)
* Нажмите ОК, чтобы создать соответствующий класс `Comment`.

Теперь необходимо сгенерировать для сущности `Comment` новый базовый атрибут `text`, а также создать отношение "многие к одному" с сущностью `User`. Для этого:
* Установите курсор на имени класса `Comment`  в соответствующем файле
* Вызовите всплывающее окно **Generate**, нажав **Alt+Insert** для Windows/Linux или **⌘+N** для macOS.

![generate.png](https://amplicode-strapi-ru.demo.haulmont.com/uploads/generate_9d70a4a335.png)
* Выберите **Entity Attribute**
* Введите тип (**String**) и имя (**text**) для атрибута

![entity-attribute.png](https://amplicode-strapi-ru.demo.haulmont.com/uploads/entity_attribute_28852c610f.png)
* Нажмите **OK**
* Еще раз вызовите окно **Generate**
* Выберите **Entity Attribute**
* В окне типа атрибута выберите `Persist` и введите имя типа `User`

![type-user.png](https://amplicode-strapi-ru.demo.haulmont.com/uploads/type_user_6406278c0c.png)
* Задайте название атрибута (`author`) и сделайте его обязательным.

![author-mandatory.png](https://amplicode-strapi-ru.demo.haulmont.com/uploads/author_mandatory_0098eeb4d7.png)
* Нажмите **OK**

Задача по модификации JPA модели выполнена. Следующий шаг — генерации скриптов миграции Liquibase.

## Генерация Liquibase скриптов миграции для синхронизации JPA модели и схемы БД

Для создания Liquibase скрипта миграции необходимо обратиться к панели Amplicode Explorer и в секции DB Versioning выбрать пункт **Liquibase Diff Changelog**.

В открывшемся окне следует убедиться в правильности выбранных persistence unit и подключения к базе данных.

![diff-window.png](https://amplicode-strapi-ru.demo.haulmont.com/uploads/diff_window_43500d0f40.png)

С окном предпросмотра changelog файлов мы уже познакомились в процессе создания скрипта инициализации базы данных.

![changelog-preview.png](https://amplicode-strapi-ru.demo.haulmont.com/uploads/changelog_preview_44f37dc531.png)

Стоит отметить, что в данном случае цветовая раскраска некоторых changeset отличается, например, скрипт на удаление колонки `fax` отмечен красным цветом, а скрипт на добавление `NotNull` ограничения — желтым.

![red-and-yellow.png](https://amplicode-strapi-ru.demo.haulmont.com/uploads/red_and_yellow_dacae9b531.png)

Оба скрипта являются потенциально опасными ввиду возможной потери данных, поэтому Amplicode старается обратить на них максимальное внимание со стороны разработчика.

Разрабатывая JPA модель, мы решили две бизнес задачи: изменение сущности `User` и создание сущности `Comment`. Нам бы не хотелось складывать скрипты, относящиеся к разным бизнес задачам, в один файл. Amplicode позволяет разнести changeset скрипты в несколько changelog файлов, не покидая текущего окна.

Для этого необходимо:
* Выбрать скрипты, относящиеся к изменению пользователя

![select-scripts-user.png](https://amplicode-strapi-ru.demo.haulmont.com/uploads/select_scripts_user_0cb44e3e09.png)
* В верхней панели выбрать действие переноса скриптов в новый changelog файл
* Задать название каждому из changelog файлов
* Для файла модификации со скриптами таблицы `users` изменить путь к файлу и указать главный changelog, в который нужно будет включить текущий файл.

![changelog-names.png](https://amplicode-strapi-ru.demo.haulmont.com/uploads/changelog_names_e3cd8cd9a3.png)

Также обратите внимание, что для скрипта добавления `NotNull` ограничения Amplicode хочет предложить некоторые улучшения и сигнализирует нам об этом иконкой лампочки. Так как мы добавляем `NotNull` ограничение, мы должны быть уверены в том, что в таблице уже нет `null` значений.

Amplicode позволяет нам улучшить существующий скрипт, указав значение, которое будет проставлено для всех `null` значений перед добавлением ограничения.

![default-value-for-null.png](https://amplicode-strapi-ru.demo.haulmont.com/uploads/default_value_for_null_d7ab379998.png)

Перед сохранением changelog файлов можем еще раз убедиться в их корректности в окне предпросмотра.

![script-preview.png](https://amplicode-strapi-ru.demo.haulmont.com/uploads/script_preview_6ae6e30655.png)

Кстати, есть еще один скрипт, который мы упустили. Это скрипт удаления индекса из таблицы `Users`в секции **Ignored**.

![drop-index.png](https://amplicode-strapi-ru.demo.haulmont.com/uploads/drop_index_57c731ff5c.png)

Amplicode автоматически разместил этот скрипт в данную секцию, т.к. создание индекса может быть довольно дорогостоящей операцией, а его добавление на уровне JPA модели не является довольно распространенным. Поэтому в реальной жизни удалять его из базы данных приходится крайне редко.

![ignored-script.png](https://amplicode-strapi-ru.demo.haulmont.com/uploads/ignored_script_5cf21bb1ad.png)

Но в случае необходимости всегда можно удалить скрипт из списка игнорируемых, либо наоборот расширить список игнорируемых скриптов, выбрав и перетащив ненужные скрипты в секцию **Ignored** прямо в окне предпросмотра или сконфигурировав такой список заранее в настройках.

Оба changelog файла успешно сгенерированы и включены в наш основной файл `db.changelog-master.xml`.

Остается только выполнить скрипты и накатить изменения в базу данных. Конечно же, самым правильным подходом к применению скриптов миграции будет настройка Liquibase плагина для Maven или Gradle, т.к. он в любом случае понадобится во время настройки CI/CD pipeline. Но, чтобы не отвлекаться на эту задачу на данном этапе, в рамках данного гайда можно воспользоваться действием **Liquibase Update** в Amplicode и накатить скрипты без предварительной настройки плагина, отложив эту задачу на потом.

![liquibase-update.png](https://amplicode-strapi-ru.demo.haulmont.com/uploads/liquibase_update_0a77a48b78.png)

При выборе данного пункта из меню появится следующее выплывающее окно. Нажмите в нам кнопку **Update**.

![update-window.png](https://amplicode-strapi-ru.demo.haulmont.com/uploads/update_window_9ce3ddcbd5.png)

Все скрипты выполнились успешно, а информация об их успешном выполнении была записана в таблицу `databasechangelog`.

![databasechangelog-table.png](https://amplicode-strapi-ru.demo.haulmont.com/uploads/databasechangelog_table_e8506892c3.png)

## Добавление новых скриптов миграции в существующий файл

Модифицируя JPA модель, мы забыли сделать атрибут `text` для сущности `Comment` обязательным.

![text-not-mandatory.png](https://amplicode-strapi-ru.demo.haulmont.com/uploads/text_not_mandatory_de20d5af82.png)

Исправить эту оплошность довольно легко.

Для этого, воспользуйтесь панелью Amplicode Designer и пометьте атрибут как обязательный:

![make-mandatory.png](https://amplicode-strapi-ru.demo.haulmont.com/uploads/make_mandatory_a83d3bb2eb.png)

Однако, засорять проект множеством Liquibase changelog файлов нежелательно. К тому же, данный скрипт относится именно к changelog файлу, содержащему скрипты, связанные с созданием таблицы `Comments`. Благодаря тому, что Liquibase исполняет и фиксирует исполнение именно changeset'ов, из которых состоит changelog файл, а не исполнение всего changelog файла целиком, мы можем дополнить существующие changelog файлы новыми changeset скриптами. Amplicode хорошо знает про подобную возможность и позволяет догенерировать необходимые скрипты в существующий файл.

Для этого:

* Откройте нужный нам changelog файл и обратитесь к меню **Generate** от IntelliJ IDEA и выберите пункт **Liquibase Diff Changes** от Amplicode.

![diff-changes.png](https://amplicode-strapi-ru.demo.haulmont.com/uploads/diff_changes_aae2e3101a.png)

* В открывшемся всплывающем окне нажмите **OK**.
  Новый changeset будет добавлен в существующий changelog файл.

* Накатите изменения (как показано выше) и проверьте таблицу `databasechangelog` через pgAdmin.

## Запуск Spring Boot приложения (анализ логов от Amplicode)

Хорошей практикой при разработке Spring Boot приложения и использованием системы версионирования баз данных является применение возможностей валидации соответствия JPA модели и схемы базы данных при помощи Hibernate. Путем использования свойства Hibernate `spring.jpa.hibernate.ddl-auto` со значением `validate` мы можем обеспечить соответствие JPA модели и схемы базы данных.

Внесите следующий код в файл `application-properties`:
```asciidoc
spring.jpa.hibernate.ddl-auto=validate
```
Чтобы сделать это быстро, начните печатать `ddl-auto` и Amplicode предложит вам соответствующее свойство. Достаточно будет выбрать его из выпадающего списка и нажать `Enter`.

![validate.png](https://amplicode-strapi-ru.demo.haulmont.com/uploads/validate_9819f01114.png)

В случае обнаружения несоответствия Spring Boot приложение не запустится и выдаст ошибку.

Запустите приложение, чтобы убедиться, что в логах действительно отсутствуют исключения.

![run-application.png](https://amplicode-strapi-ru.demo.haulmont.com/uploads/run_application_74da9ddc00.png)

Если приложение запущено без проблем, лог должен выглядеть примерно следующим образом:

![no-exceptions.png](https://amplicode-strapi-ru.demo.haulmont.com/uploads/no_exceptions_96d43ddb73.png)

Столь же важно отметить, что в случае несоответствия JPA модели и схемы базы данных Amplicode сообщил бы о проблеме и дал бы возможность решить ее прямо из stacktrace.

## Заключение

Подводя итог, все поставленные в данном гайде задачи успешно выполнены примерно за 20 минут.

Вы научились:
1. Подключать и настраивать Liquibase в проекте, написанном на Spring Boot с применением Amplicode
2. Расширять JPA модель и писать скрипты миграции БД
3. Валидировать JPA модель и БД

Вы можете установить Amplicode уже сейчас и применить полученные знания на практике.
