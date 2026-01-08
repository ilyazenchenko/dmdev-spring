# Spring dmdev

## 1 Введение

### 1.1 Введение
- Библиотека - набор доп методов
- Фреймворк - каркас приложения
- Spring удобный

### 1.3 Dependency Injection. Inverison of control
- **`Dependencies`** (зависимости) – другие объекты, с которыми работает текущий объект и использует их функциональность
- **`IoC` (Inversion of Control) - инверсия управления** - принцип программирования, при котором управление программой передается фреймворку, а не программисту
- **`DI` (dependency injection)** – внедрение зависимостей – одна из реализаций IoC, посредством которой созданием объекта и внедрением его зависимостей занимается другой объект (фреймворк)
- **`DI`** внедряет зависимости через: конструктор, статический метод, свойства

### 1.4 `IoC Container`
- Это объект, который занимается созданием других объектов и внедрением в них зависимостей
- `Bean` – объект со всеми необходимыми зависимостями, который **был создан** `IoC Container` (`Controller, Service, Repository`)
- Кроме `Bean`, в `Spring` приложении есть `POJO - dto, entity`. Не содержат логики
- `IoC Container` реализует `BeanFactory, ApplicationContext`
- Есть разные `IoC` контейнеры. Для xml-конфигурации один, (`ClassPathXmlApplicationContext`), для других - другие
- Бины в контексте хранятся как `Map<String, Object>`, то есть id String(!). генерится по разному, как например скажем
![img.png](imgs/p2/img.png)

## 2. XML-based Configuration
### 2.1 XML-based Configuration
- Методы `BeanFactory`
![img_1.png](imgs/p2/img_1.png)

- Используется в основном реализация `ApplicationContext`
- Для xml - `ClassPathApplicationContext`, передаем в него адрес файла с бинами xml
- получить бин: `context.getBean(class)`, `context.getBean(String id/alias)` (вернет `object`), `context.getBean(id, class)` - конкретный бин класса
- если не указать `id`, в мапе бинов `ioc` `id` будет `имя класса#номер`
- если не указать `id/name` и сделать `getBean(class)`, то будет `exception`
- вызывается конструктор без параметров (рефлексия)
![img_3.png](imgs/p2/img_3.png)

### 2.2 Constructor injection
- Можно указать name - имя аргумента, тип - для перегруженных конструкторов

![img_4.png](imgs/p2/img_4.png)

### 2.3 Factory Method Injection
- Чтобы ссылаться на бин при создании бинов, нужно указывать не value а ref
- вместо конструкторов при создании бина можно использовать Фабричный метод:

![img_5.png](imgs/p2/img_5.png)

### 2.4 Property Injection
- Можно внедрять с помощью сеттеров, **но есть минусы: поле должно быть не final и возоможны циклические зависимости**
- Вот как в xml:

![img_6.png](imgs/p2/img_6.png)

- Жизненный цикл бина:

![img_7.png](imgs/p2/img_7.png)

- Мы даем `definitions` - метаинфу
- `контейер`:
  - сортирует (сначала нужно создать те, у которых нет зависимостей, и потом уже их внедрять в последующие)
  - вызывает конструктор
  - сеттеры
  - создает

### 2.5 Bean Scopes
![img_8.png](imgs/p2/img_8.png)

- основные (не считая `web`): `singleton` и `prototype`
- для `prototype` `IoC` при каждом запросе прогоняет полностью жизненный цикл бина и **выплевывает** его, не запоминая

### 2.6 Lifecycle Callbacks
- Можно добавлять `PostContruct/PreDestroy` методы, **лучше использовать аннотации**
![img_9.png](imgs/p2/img_9.png)

> PreDestroy методы вызываются при закрытии контекста
> 
> ! У Контекста (= `IoC-контейнера`) на прототипы нет ссылки! он ее выплюнул, у прототипов PreDestroy не вызываются

### 2.7 properties файлы
- источник данных для приложения. благодаря им не нужно каждый раз перекомпилить

![img_10.png](imgs/p2/img_10.png)
- можно использовать в xml ))

### 2.8 BeanFactoryPostProcessor
- Всякие el-выражения для properties и т п рассчитывает BeanFactoryPostProcessor

![img_11.png](imgs/p2/img_11.png)

### 2.9 Custom BeanFactoryPostProcessor
- Можно реализовать свои BeanFactoryPostProcessor. Для этого реализовать в них BeanFactoryPostProcessor
- Можно настраивать порядос помощью implements Ordered getOrder или implements PriorityOrdered

## 3 Annotation-based configuration

### 3.1 Annotation-based Configuration
- В xml можно добавить

![img.png](imgs/p3/img.png)

- Он добавит необходимые бины для аннотаций и др., а именно:

![img_1.png](imgs/p3/img_1.png)

> `BeanFactoryPostProcessor` уже знаем, а вот `BeanPostProcessor` - новый этап `Lifecycle`

### 3.2 BeanPostProcessor

- `BeanPostProcessor` - это особенные бины, которые участвуют в жизненном цикле бинов и занимаются их конфигураций (наподобие `BeanFacotryPostProcessor`, только для Beans, а не `Bean Definitions`).
- ! `BeanFactoryPostProcessor` вызывается для контекста 1 раз, а `BeanPostProcessor` - для каждого бина

![img_2.png](imgs/p3/img_2.png)

- `Aware` интерфейсы - помогают инжектить в бин-пост процессоры, например, контекст. Имеют сеттеры, нужно их реализовать
- Порядок инициализации в `контексте` (= `IoC` контейнере):
1. `AwareBeanPostProcessors` – чтобы инжектить зависимости в сами пост процессоры
2. `BeanPostProcessors` – чтобы обрабатывать бины
3. `Beans`

### 3.3 Свой BeanPostProcessor

> 1 Сделали свою аннотацию `InjectBean`
> 
> 2 В `PostProcessor` берем все филды, и для этой аннотации ищем в контексте бин этого типа, инжектим, конец
> 
> 3 ! Внедряем в пост процессор `application context` с помощью `ApplicationContextAware` и `set` метода

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface InjectBean {
}

```
```java
public class Repository {

    @InjectBean
    private ConnectionPool connectionPool;

    @PostConstruct
    public void init() {
        System.out.println("ConnectionPool: " + connectionPool);
    }

}
```
```java
public class InjectBeanPostProcessor implements BeanPostProcessor, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Arrays.stream(bean.getClass().getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(InjectBean.class))
                .forEach(field -> {
                    field.setAccessible(true);
                    ReflectionUtils.setField(field, bean, applicationContext.getBean(field.getType()));
                });
        return bean;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
```

### 3.4 Свой BeanPostProcessor 2

- Есть методы:

```java
     Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException; // до колбека PostConstruct
// и
     Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException; // после колбека PostConstruct
```

> и если мы привносим какую-то сквозную функциональность с помощью Proxy, то нужно это делать в `postProcessAfterInitialization`, так как прокси вернет другой класс и могут быть ошибки

### 3.5 @Autowired, @Resource, @Value
> `@Resource` то же самое что `@Autowired`, просто чтобы поддерживать спецификацию `JavaEE`, лучше использовать `@Autowired`
> 
> `@Autowired` можно в поле, сеттере, конструкторе
> 
> Если несколько бинов - использовать `@Qualifier(beanId)` **или** **просто назвать поле/аргумент сеттера как** `id` бина
> 
> `@Value(${spring.el})` - подставить из пропертей

### 3.6 Classpath scanning
- В пакете stereotype есть аннотации `Component, Controller, Service, Repository`
- Последние 3 – просто наследуют `Component`, для логического разделения
- В `value Component` можно написать id бина `@Component("myBean1")`
- Если сделать как поле или аргумент конструктора `List<MyBean>`, то `Spring` вставит в него все бины

> Почему `Autowired` через конструктор - лучше
> 1. **Безопасность NPE:** Зависимости через конструктор гарантированно инициализированы при создании бина, их можно сделать `final`. `@Autowired` выкинет ошибку, если не найдет бина и required!=false, но в какой-то момент может быть `null`
> 2. **Порядок инициализации:** через конструктор зависимости доступны сразу после создания бина. `@Autowired` срабатывает после конструктора, что может вызвать проблемы
> 3. **Тестирование:** С конструктором можно легко создать объект в тестах без Spring-контекста. `@Autowired` требует настройки контекста или `ReflectionTestUtils`
> 4. **Явные зависимости:** Конструктор явно показывает все обязательные зависимости класса, и если их слишком много, и позволяет обнаружить циклические
> 5. **Spring recommendation:** Официальная документация `Spring` рекомендует `constructor injection`

### 3.7 Bean Definition Readers
- Они читают xml/аннотации и строят контекст

![img_3.png](imgs/p3/img_3.png)


### 3.8 TypeFilters

<img src="imgs/p3/img_8.png" width="750">
<img src="imgs/p3/img_4.png" width="750">

Так Spring понимает, какие классы - бины. По умолчанию - `annotation`. То есть `@Component`, и т п. 
Можно настраивать в аннотации `@ComponentScan`: например `regex` - все, оканчивающиеся на Bean и т п.

### 3.9 @Scope

В аннотациях scope можно указывать в аннотации `@Scope: "prototype", "singleton"`

### 3.10 JSR 250, JSR 330

Для совместимости Spring поддерживает аннотации JSR:

![img_9.png](imgs/p3/img_9.png)

## 4. Java-based Configuration

### 4.1 Java-based Configuration

Пример кастомных фильтров в аннотации:
![img.png](imgs/p4-5/img.png)

Создать `ApplicationConfiguration` контекст:
```java
ApplicationContext ctx = new AnnotationConfigApplicationContext(ApplicationConfiguration.class);
```

Что под капотом:
у `AnnotationConfigApplicationContext` есть поля `reader`, `scanner`, и в конструкторе для всех бинов 
вызывается метод `register()`

> Отличия `@Configuration` и `@ComponentScan`:
> - `@Configuration`: в текущем классе читать бины с аннотацией `@Bean`. В не-помеченных классах 
> Spring тоже сканирует, но `@Configuration` - в первую очередь, поэтому обычно пишут `@Bean` именно там
> - `@ComponentScan`: сканировать пакет на `@Component` – по умолчанию пакет класса с аннотацией 
> @ComponentScan и его подпакеты

### 4.2 @Import & @ImportResource
В классе `@Configuration` можно указывать xml файлы с помощью `@ImportResource` (не используется) 
и другие классы-Configuration с помощью `@Import`, чтобы комбинировать конфигурации

### 4.3 @Bean. Часть 1
- Название метода - id бина
- Чтобы указать конкретный id бина при внеднерии, либо назвать аргумент/поле как id, либо 
- `@Qualifier("id")`
- Аргументы `@Bean`:
  - `autowireCandidate`: использовать ли при внедрении
  - `initMethod`, `destroyMethod` - аналоги `PostConstruct` и `PreDestroy`
- Можно при `@Bean` указывать `@Scope`

### 4.4 @Bean. Часть 2
- Можно внедрять бины прямо как вызов метода, как в `userRepository3`:

![img_1.png](imgs/p4-5/img_1.png)

> У pool3 не указан **`scope`**, т. е. он синглтон, и **`pool3()`** будет возвращать
> **один и тот же бин!** 

### 4.5 Profiles

Аннотацию `@Profile` можно ставить над `@Bean` или над `@Component`. В ней указывать профили.
- Поддерживает логические выражения:

![img_2.png](imgs/p4-5/img_2.png)

## 5. EventListeners

### 5.1 EventListeners. 1,2

В Spring можно делать слушатели событий:
![img_3.png](imgs/p4-5/img_3.png)

![img_4.png](imgs/p4-5/img_4.png)

![img_5.png](imgs/p4-5/img_5.png)

## 6. Spring Boot

### 6.1 Spring Boot. Введение

Если добавлять какие-то зависимости/модули в `Spring`, их нужно конфигурировать. 
`Spring Boot` все конфигурации содержит из коробки: в каждое `Spring Boot` приложение транзитивно тянется
```xml
org.springframework.book:spring-boot-autoconfigure
```

Спринг бут делает за нас это:

<img alt="img.png" src="imgs/p6/img.png" width="800"/>

<img alt="img_1.png" src="imgs/p6/img_1.png" width="800"/>

<img alt="img_2.png" src="imgs/p6/img_2.png" width="800"/>

Бины, автоматически настроенные `Spring Boot`, можно переопределить

### 6.2 @Conditional

- Можно создавать бины/конфигурации по условиям: аннотация `@Conditional` (`@Profile` использует ее под капотом)
- Передаем в нее Class, который реализует интерфейс Condition и его метод `matches(ConditionContext, AnnotatedTypeMetadata)`

В `Spring Boot` есть свои аннотации:

<img alt="img_3.png" src="imgs/p6/img_3.png" width="800"/>

### 6.3 Spring Boot. Настройка проекта

- `Gradle`:
  - добавить plugin `spring boot`
  - добавить plugin `dependency management`
  - добавить dependency `spring-boot-starter`
  - версии не указываем, `spring boot` `dependency management` делает это за нас
- `Main class`:
  - `SpringBootApplication.run(class, args)`
  - `@SpringBootApplication`
- Можно сделать с `Spring initialzr`
- `Maven`:
  - У него есть `<parent> spring-boot-starter-parent`
  - В `parent` есть `spring-boot-dependencies`, и в нем **куча** версий зависимостей на все случаи жизни, совместимых с этой версией `spring boot`

С Spring Boot включены следующие зависимости:
<img alt="img_1.png" src="imgs/p7/img_1.png" width="800"/>
### 6.4 `@SpringBootApplication`

- Все классы и пакеты должны лежать на том же уровне/ниже класса `@SpringBootApplication`
- `SpringBootApplication.run` возвращает контекст
- `@SpringBootApplication` содержит:
  - `@SpringBootConfiguration` (Configuration, но одна на класс)
  - `@ComponentScan`
  - `@PropertySource` подтягивается тоже, сканируется application properties/yaml
  - `@EnableAutoConfiguration` – автоматически подтягиваются автоконфигурации 
из `spring-boot-autoconfigure`, но подтягиваются по условиям `@Conditional`,
могут добавлять какие-то свои бины (**то есть Spring Boot может добавлять какие-то бины**)

### 6.5 Lombok
- Вместо всех gradle зависимостей по типу preprocessor, можно подключить gradle plugin:

<img alt="img_4.png" src="imgs/p6/img_4.png" width="800"/>

Аннотации:
- `@Cleanup`: аналог try-with-resources но более гибкая
- `@NonNull`: на аргументе метода будет проверять и кидать `NPE` если передан `null` с именем аргумента
- `@SneakyThrows`: может быть полезна в лямбдах
- `@RequiredArgsConstructor`: конструктор для `@NonNull` и `final` полей

> Как с помощью `lombok` сделать конструктор для `final`-полей `@Value`?
> 1. Делам lombok.config:
> <img alt="img_5.png" src="imgs/p6/img_5.png" width="800"/>
> 2. Ставим над final полем аннотацию `@Value`/Qualifier и т п
> 3. Пишем @RequiredArgsConstructor и т п
> 4. Все, lombok сам в конструктор поставит аннотации

### 6.6 Properties

Можно сделать файл spring.properties, он для более low-level
пропертей, его проперти можно получить через SpringProperties.method

Вообще, есть 14 вариантов задания пропертей:
<img alt="img_6.png" src="imgs/p6/img_6.png" width="800"/>

> Каждый из последущих имеет более высокий приоритет, чем предыдущий

> `VM options` задаются через `-D`, `Program arguments` через `--`
<img alt="img_7.png" src="imgs/p6/img_7.png" width="800"/>


### 6.7 yaml
В целом он удобнее, в том числе работа со списками, объектами:
<img alt="img_9.png" src="imgs/p6/img_9.png" width="800"/>

### 6.8 `@ConfigurationProperties`

> Можно маппить проперти в объекты/record:

<img alt="img_10.png" src="imgs/p6/img_10.png" width="800"/>

<img alt="img_11.png" src="imgs/p6/img_11.png" width="800"/>

## 7. Logging Starter

### 7.1 Logging Starter

Для логов есть разные реализации, по умолчанию идет джавовская
`logback`, можно переопределить:

<img alt="img_2.png" src="imgs/p7/img_2.png" width="800"/>

<img alt="img_3.png" src="imgs/p7/img_3.png" width="800"/>

Используя аннотацию `@Slf4j`, вот что под капотом:

<img alt="img_4.png" src="imgs/p7/img_4.png" width="800"/>

т.е. то же самое

Логи можно настраивать в properties, причем для разных классов разное:

<img alt="img_6.png" src="imgs/p7/img_6.png" width="800"/>

### 7.2 Logback Configuration

Во всех логгерах 2 основных понятия: `appender` (куда выгружаем логи: файл, консоль, другой сервер итп)
и `logger` (пишет в аппендеры, напр `root`)

Можно более низкоуровнево настраивать логи: переопределить `xml` файл конфигурации.
В зависимости от используемой системы, например `logback`:

<img alt="img_7.png" src="imgs/p7/img_7.png" width="800"/>

## 8. Test Starter

### 8.1 Test Starter

Что входит в `starter-test`:

<img alt="img.png" src="imgs/p8/img.png" width="800"/>

Базовый тест:

<img alt="img_1.png" src="imgs/p8/img_1.png" width="800"/>

### 8.2 Spring Integration testing. P1

Для интеграционных тестов нужно как-то:
1. Внедрить зависимости
2. Кэшировать контекст, чтобы не поднимать каждый раз
3. Управлять транзакциями

Из чего состоит `Spring Integration testing`:

<img alt="img_2.png" src="imgs/p8/img_2.png" width="800"/>

Есть колбеки:

<img alt="img_3.png" src="imgs/p8/img_3.png" width="800"/>

`Test context framework`:

<img alt="img_8.png" src="imgs/p8/img_8.png" width="700"/>

### 8.3 Spring Integration testing. P2

> Под тесты лучше код не менять

```java
@ExtendWith(SpringExtention.class) // интеграция JUnit5 с Spring TestContext
@ContextConfiguration(SpringApplication.class) // класс где запускается SpringBoot приложение
```

Эти две аннотации можно заменить на `@SpringBootTest`:
<img alt="img_11.png" src="imgs/p8/img_11.png" width="800"/>
(`SpringBoot...Bootstraper` автоматически ищет класс `SpringBootApplication`)

Пример интеграционного теста:
```java
@SpringBootTest
//@ExtendWith(SpringExtension.class)
//@ContextConfiguration(classes = ApplicationRunner.class, initializers = ConfigDataApplicationO
  public class CompanyServiceIT {
    private static final Integer COMPANY_ID = 1;
    @Autowired // в тестах так можно
    private CompanyService companyService;
    @Test
    void findById() {
        var actualResult =  Optional<CompanyReadDto> = companyService.findById(COMPANY_ID);
        assertTrue(actualResult.isPresent());|
        var expectedResult = new CompanyReadDto (COMPANY_ID);
        actualResult.ifPresent(actual -> assertEquals(expectedResult, actual));
    }
}
```

> На каждый класс `@SpringBootTest` создается `TestContextManager` (см. 8.2), 
> `TestContext` содержит информацию о выполняемом тесте, а `TestExecutionListeners` 
> выполняют различную логику, например внедрение зависимостей

### 8.4 Spring Integration testing. P3

> Чтобы не дублировать везде `@ActiveProfiles` и `@SprintBootTest` можно сделать
свою аннотацию:
> 
> ![img_13.png](imgs/p8/img_13.png)

> Чтобы не писать над полями `@Autowired`, можно аналогично сделать их 
> `final` и добавить `@RequiredArgsConstructor`, но нужно будет добавить:
> - или 
> ![img_14.png](imgs/p8/img_14.png)
> - или в `test/resources/spring.properties`: 
> ![img_15.png](imgs/p8/img_15.png)

### 8.5 Context Caching

Если Spring видит изменение конфигурации контекста в разных, например, классах,
то он пересоздает контекст. Например, если на одном классе есть `@ActiveProfiles("test")`,
пересоздаст.

Если пометим какой-то бин как `@SpyBean` или `@MockBean`, то пересоздаст

Если хотим что-то переопределить для всего контекста, то делаем класс 
`@TestConfiguration` и 
<img alt="img_16.png" src="imgs/p8/img_16.png" width="400"/>

Помечать контекст "испорченным" можно с помощью аннотации `@DirtiesContext`
над классом или методом

## 9. Data JPA Starter

### 9.1 Data JPA Starter. Введение

У чистого `Hibernate` есть проблемы:
- нужно писать самому логику `dao`
- нет декларативного способа управления транзакциями
- сложная конфигурация

Как решает проблемы `Spring Data Jpa`:

<img alt="img.png" src="imgs/p9/img.png" width="700"/>

### 9.2 Data JPA Starter. Подключение

Что входит:

<img alt="img_2.png" src="imgs/p9/img_2.png" width="700"/>

> `Hikari` - самый производительный `ConnectionPool`

При подключении стартера, подключается `HibernateJpaAutoConfiguration`:

<img alt="img_3.png" src="imgs/p9/img_3.png" width="700"/>

Для `jpa` у `Spring` 2 основные "ветки" в `properties`:
- `spring.datasource`
- `spring.jpa`

### 9.3 Hibernate Entities

Спринг бут автоматом сканит `Entity` классы в субдиректориях `@SpringBootApplication`,
но если например лежит в другом пакете, можно указать аннотацию
`@EntityScan("путь")` 

## 10. Data JPA Transactions

### 10.1 Data JPA Transactions

При подключении `spring-data-jpa-starter`, добавляется класс `JpaBaseConfiguration`.
Можно управлять как-то вручную `TransactionTemplate`, но почти всегда декларативно`@Transactional`.
Можно ставить в тестах и в коде:

<img alt="img.png" src="imgs/p10/img.png" width="700"/>

Для работы с бд в тестах нужна `@Transactional`, иначе 
метод-тест не будет помнить значения и будет `LazyInitializationException`.

> По умолчанию после каждого теста происходит rollback (= аннотация `@Rollback`),
> но можно поставить `@Commit`, и будет не откат а коммит после каждого теста

### 10.2 TransactionAutoConfiguration
`@Transactional` можно писать и над классом, и над методом,
над классом имеет приоритет.

Когда пишем `@Transactional`, спринг оборачивает бин в прокси,
**а если делаем внутренний вызов, транзакция не откроется**: 

![img_1.png](imgs/p10/img_1.png)

Это фиксится либо внедрением контекста, либо бина самому себе, 
но это костыли, поэтому лучше не допускать такой логики.

> Что значит: вместо бина внедряется прокси?
> 
> Прокси в `Spring` (`CglibPoxy`) реализована через **наследование**, то есть
> прокси является `instance` бина, но оборачивает методы в какую-то
> доп логику, типа транзакций

### 10.3 @Transactional Settings

В такой логике **_по умолчанию_** _новая транзакция не откроется_:

![img_2.png](imgs/p10/img_2.png)

Можно это изменить с помощью настройки `@Transactional` - `propagation`.
Какие у `propagation` могут быть значения:
- `REQUIRED` (значение по умолчанию) - поддерживает текущую транзакцию, а если ее нет – открывает
- `SUPPORTS` - поддерживает текущую транзакцию, а если ее нет – не открывает
- `MANDATORY` - поддерживает текущую транзакцию, а если ее нет – бросает исключение
- `REQUIRES_NEW` - приостанавливает текущую транзакцию, если есть, и открывает новую 
транзакцию в новом соединении (могут быть deadlocks)
- `NOT_SUPPORTED` - приостанавливает текущую транзакцию, выполняется не в транзакции
- `NEVER` - если текущая транзакция есть, бросает исключение, выполняется не в транзакции
- `NESTED` - с savepoints

> В основном `propagation` = `REQUIRED` и `REQUIRES_NEW`

Какие еще есть значения в `@Transactional`:
- `isolation` - так же как в `sql`
- `timeout`
- `readonly` - если нет изменений, поставить для оптимизации, `default=false`
- `rollbackFor` - для каких исключений делать ролбек,
по умолчанию `RuntimeException` и `Error`
- `noRollbackFor` - для каких исключений не делать ролбек

> **`isolation` и `timeout` учитываются только в первой открытой транзакции,
> если в ней открываются еще, то не учитываются**

> Нужно ли ставить `@Transactional(readonly=true)` на read only методах?
> 
> Да:
> 1. Гарантия `read-only` режима
> 2. Единая транзакция для всех запросов в методе
> 3. Можно задать `isolation` при желании

### 10.4 Manual Transactions

В контексте лежит бин `TransactionTemplate`, он может с помощью
`execute` или `executeWithoutResult` запускать код в транзакции.
То есть не обязательно весь метод оборачивать.

> Чтобы не писать `@Transactional` над всеми классами тестов, можно
> ее поставить над своей аннотацией `@IT` или `@ITDB`

## 11. Data JPA Repositories

### 11.1 Repository
Базовый репозиторий:
```java
interface MyRepo extends Repository<T, ID> 
```
> Можно не ставить аннотацию, spring-jpa создаст бин т.к. extends `Repository`

Подключается класс `JpaRepositoriesAutoConfiguration`:
![img.png](imgs/p11/img.png)

> В `Spring Boot` аннотация @EnableJpaRepositories идет из коробки, включать не надо

### 11.2 RepositoryQuery

В `Spring Data JPA` базовый класс-запроса – `RepositoryQuery`.
Его реализации:

<img alt="img_1.png" src="imgs/p11/img_1.png" width="700"/>

- `NamedQuery` – аннотация
- `NativeJpaQuery` – нативный sql
- `SimpleJpaQuery` – HQL
- `PartTreeJpaQuery` - генерирует запросы по имени метода
- `StoredProcedureJpaQuery` - вызов процедур, _легаси_

Когда пишем просто имена методов в интерфейсе, используется прокси,
только dynamic, а не через наследование

Чтобы не писать в `Repository` популярные методы, типа `findById`, 
есть наследники, в которых уже много чего есть:
`Repository <- CrudRepository <- JpaRepository`,
в них много готовых методов

### 11.3 PartTreeJpaQuery

Делает из имен методов запросы. Под капотом - регулярки.
<img alt="img_2.png" src="imgs/p11/img_2.png" width="700"/>

Как можно делать:
<img alt="img_3.png" src="imgs/p11/img_3.png" width="700"/>

Все возможности - в доках ))

### 11.4 NamedQuery

Если весь запрос писать в имени метода, имя быстро увеличивается.
Как вариант решение - использовать NamedQuery. Это реализация 
`@NamedQuery` из `javax/hibernate`

Ставим аннотацию _над сущностью, пишем имя (формат `Entity.name`)
и сам запрос (`HQL`). 

<img alt="img_4.png" src="imgs/p11/img_4.png" width="700"/>

В репозитории также пишем метод с таким же именем как name. 

> `NamedQuery` имеет приоритет над `PartTreeJpaQuery`, то есть если
> есть `NamedQuery` с таким именем, то `PartTreeJpaQuery` не создастся

### 11.5 @Query
Аннотация работает с `SimpleJpaQuery` и `NativeJpaQuery`:

<img alt="img_5.png" src="imgs/p11/img_5.png" width="700"/>

> `Spring Data Jpa` (не `Hibernate`) позволяет писать проценты вокруг
> параметров, без доп знаков (картинка выше)

Удобно сразу достать необходимые сущности с помощью `join fetch`:

<img alt="img_6.png" src="imgs/p11/img_6.png" width="700"/>

> `@Query` имеет приоритет над `PartTreeJpaQuery` и `@NamedQuery`

> `HQL` запрос лучше, чем `native sql`, так как:
> 1. Теряем в производительности
> 2. Не можем использовать `join fetch`
> 3. Не можем использовать `EntityGraph`
### 11.6 @Modifying
По умолчанию `@Query` только для чтения!
Чтобы можно было изменять, нужно поставить аннотацию `@Modifying`:

<img alt="img_8.png" src="imgs/p11/img_8.png" width="700"/>

Есть свойста `clearAutomatically` и `flushAutomatically`:

> Так как `PersistenceContext` работает как кеш, он не увидит изменения
> после этого метода. Чтобы исправить, нужно поставить:
> 
> <img alt="img_9.png" src="imgs/p11/img_9.png" width="700"/>
> 
> После этого важно работать именно с _новыми сущностями_, 
> соединения старых закроются, при обращении к связанным
> сущностям будет `LazyInitializationException`

### 11.7 Special Parameters
```java
// Можно все писать с помощью PartTreeJpaQuery:
List<User> findTop3ByBirthDateBefore0rderByBirthDateDesc(LocalDate birthDate);
// Но это слишком объемно. Можно добавлять параметр Sort
List<User> findTop3ByBirlthDateBefore(LocalDate birthDate, Sort sort);
// Создание:
Sort sort = Sort.sort(User.class)
              .by(User::getFirstname)
              .and(sort.by(User::getLastname))
              .descending();

// Также можно делать аргумент Pageable (может содержать сортировку)
List<User> findAllBy(Pageable pageable);
// Создание: PageRequest.of(page, size, sort)
var pageable = PageRequest.of(0, 2, Sort.by("id")); 
```

### 11.8 Page & Slice

Для пагинации удобно использовать не `List`, а `Slice` и `Page` (`Slice <- Page`). 
`Page` лучше, содержит кол-во страниц (с помощью `count`).

Можно делать `while (page.hasNext()) { repo.findAll(page.nextPageable()) }`.

Чтобы понять кол-во страниц, используется `count`, в `@Query` его можно переопределять:

<img alt="img_11.png" src="imgs/p11/img_11.png" width="700"/>

### 11.9 @EntityGraph

С помощью `@EntityGraph` можно гибко регулировать `fetch eager/lazy`, но **Pageable
будет работать неправильно !!!**

<img alt="img_12.png" src="imgs/p11/img_12.png" width="700"/>

### 11.10 @Lock & @QueryHints

Рассмотрим 2 оставшиеся аннотации, которые позволяют не прибегать к работе с
`EntityManager`:
- `@QueryHints` - позволяет устанавливать доп параметры запроса
- `@Lock` - блокировки (подробнее на курсе по `Hibernate`)

<img alt="img_13.png" src="imgs/p11/img_13.png" width="700"/>

### 11.11 Projections
Можно сделать кастомный класс с полями:

<img alt="img_14.png" src="imgs/p11/img_14.png" width="548"/>

И писать запрос с ним:

<img alt="img_15.png" src="imgs/p11/img_15.png" width="580"/>

Можно даже писать под разные `projections` с дженериками

<img alt="img_16.png" src="imgs/p11/img_16.png" width="657"/>

Для более сложных вариантов можно делать с интерфейсом - реализовать геттеры: 

<img alt="img_17.png" src="imgs/p11/img_17.png" width="387"/>

Тогда нужно настроить поля с помощью `native query`:

<img alt="img_18.png" src="imgs/p11/img_18.png" width="590"/>

Для вычислимых полей можно использовать `@Value`:

<img alt="img_19.png" src="imgs/p11/img_19.png" width="541"/>

### 11.12 Custom Repository Implementation

Как сделать свой репозиторий с кастомной имплементацией какого то метода, 
но не плодить бины и обращаться к существующим `jpa репозиториям`?

Например, хотим все фильтры передавать в одном объекте и динамически добавлять в запрос:
1. Делаем объект-фильтр <br/>
![img_20.png](imgs/p11/img_20.png)
2. Делаем интерфейс с нужным методом <br/>
![img_21.png](imgs/p11/img_21.png)
3. Пишем реализацию. В нее можем добавлять бины, и тп. **Обязательно постфикс именно `Impl`** <br/>
![img_22.png](imgs/p11/img_22.png)
4. Экстендим существующий `jpa репо` новым <br/>
![img_23.png](imgs/p11/img_23.png)
5. Вуаля <br/>
![img_24.png](imgs/p11/img_24.png)

> Spring увидит постфикс и вызовет нужный метод из  Impl сам.
> Так как в `@EnableJpaRepositories` свойство:
> ![img_25.png](imgs/p11/img_25.png)

### 11.13 JPA Auditing

В спринге можно автоматизировать аудит сущностей.
1. Делаем абстрактный класс сущности, от которого будем наследовать наши сущности
2. Делаем поля для аудита, ставим аннотации (скрин):
3. Не забываем `@MappedSuperclass`, чтобы hiber видел в наследниках их
4. Добавляем `@EntityListeners(AuditingEntityListener.class)` <br/>
   <img alt="img_26.png" src="imgs/p11/img_26.png" width="600"/>
5. Ставим аннотацию `@EnableJpaAuditing`
6. И добавляем бин `AuditorAware<тип created/modified by>`  <br/>
   <img alt="img_27.png" src="imgs/p11/img_27.png" width="500"/>

### 11.14 Hibernate Envers

Можно отслеживать не только метаинфу, но и изменения самих полей. Для этого есть механизм `Hibernate Envers`
(подробнее про него в курсе про `Hibernate`). Для этого:
1. Подключить зависимость `spring-data-envers`
2. Сделать сущность `Revision` <br/>
   <img alt="img_28.png" src="imgs/p11/img_28.png" width="500"/>
3. Поставить `@Audited` над сущностью: <br/>
   <img alt="img_30.png" src="imgs/p11/img_30.png" width="500"/> <br/>
   <img alt="img_31.png" src="imgs/p11/img_31.png" width="450"/>
4. Создать таблицы
5. Аннотацию: <br/>
   <img alt="img_32.png" src="imgs/p11/img_32.png" width="500"/>

> Чтобы опять же не плодить репозитории, унаследуем наш репо от RevisionRepository<T, ID, RID>: <br/>
> <img alt="img_33.png" src="imgs/p11/img_33.png" width="500"/> <br>
> Теперь доступны методы: <br>
> <img alt="img_34.png" src="imgs/p11/img_34.png" width="700"/>

### 11.15 - Querydsl

Удобная библиотека, например для формирования динамических sql-запросов.

Когда корректно подключена, при компиляции формирует свои классы `Entity`: <br>
<img alt="img_35.png" src="imgs/p11/img_35.png" width="300"/>

Делаем наш класс для удобства:<br>
<img alt="img_36.png" src="imgs/p11/img_36.png" width="600"/>

Теперь можем удобно делать например динанические запросы (используется именно QUser):

<img alt="img_37.png" src="imgs/p11/img_37.png" width="600"/>

> Spring имеет поддержку Querydsl, можно имплементнуть интерфейс:
> <img alt="img_38.png" src="imgs/p11/img_38.png" width="500"/>
>
> И он предоставит возможности работы с Querydsl, например: <br>
> <img alt="img_39.png" src="imgs/p11/img_39.png" width="400"/>