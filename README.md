# Spring dmdev

# 1 Введение

## 1.1 Введение
- Библиотека - набор доп методов
- Фреймворк - каркас приложения
- Spring удобный

## 1.3 `Dependency Injection. Inverison of control`
- **`Dependencies`** (зависимости) – другие объекты, с которыми работает текущий объект и использует их функциональность
- **`IoC` (Inversion of Control) - инверсия управления** - принцип программирования, при котором управление программой передается фреймворку, а не программисту
- **`DI` (dependency injection)** – внедрение зависимостей – одна из реализаций IoC, посредством которой созданием объекта и внедрением его зависимостей занимается другой объект (фреймворк)
- **`DI`** внедряет зависимости через: конструктор, статический метод, свойства

## 1.4 `IoC Container`
- Это объект, который занимается созданием других объектов и внедрением в них зависимостей
- `Bean` – объект со всеми необходимыми зависимостями, который **был создан** `IoC Container` (`Controller, Service, Repository`)
- Кроме `Bean`, в `Spring` приложении есть `POJO - dto, entity`. Не содержат логики
- `IoC Container` реализует `BeanFactory, ApplicationContext`
- Есть разные `IoC` контейнеры. Для xml-конфигурации один, (`ClassPathXmlApplicationContext`), для других - другие
- Бины в контексте хранятся как `Map<String, Object>`, то есть id String(!). генерится по разному, как например скажем
![img.png](imgs/p2/img.png)

# 2 XML-based Configuration
## 2.1 XML-based Configuration
- Методы `BeanFactory`
![img_1.png](imgs/p2/img_1.png)

- Используется в основном реализация `ApplicationContext`
- Для xml - `ClassPathApplicationContext`, передаем в него адрес файла с бинами xml
- получить бин: `context.getBean(class)`, `context.getBean(String id/alias)` (вернет `object`), `context.getBean(id, class)` - конкретный бин класса
- если не указать `id`, в мапе бинов `ioc` `id` будет `имя класса#номер`
- если не указать `id/name` и сделать `getBean(class)`, то будет `exception`
- вызывается конструктор без параметров (рефлексия)
![img_3.png](imgs/p2/img_3.png)

## 2.2 Constructor injection
- Можно указать name - имя аргумента, тип - для перегруженных конструкторов

![img_4.png](imgs/p2/img_4.png)

## 2.3 Factory Method Injection
- Чтобы ссылаться на бин при создании бинов, нужно указывать не value а ref
- вместо конструкторов при создании бина можно использовать Фабричный метод:

![img_5.png](imgs/p2/img_5.png)

## 2.4 Property Injection
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

## 2.5 Bean Scopes
![img_8.png](imgs/p2/img_8.png)

- основные (не считая `web`): `singleton` и `prototype`
- для `prototype` `IoC` при каждом запросе прогоняет полностью жизненный цикл бина и **выплевывает** его, не запоминая

## 2.6 Lifecycle Callbacks
- Можно добавлять `PostContruct/PreDestroy` методы, **лучше использовать аннотации**
![img_9.png](imgs/p2/img_9.png)

> PreDestroy методы вызываются при закрытии контекста
> 
> ! У Контекста (= `IoC-контейнера`) на прототипы нет ссылки! он ее выплюнул, у прототипов PreDestroy не вызываются

## 2.7 properties файлы
- источник данных для приложения. благодаря им не нужно каждый раз перекомпилить

![img_10.png](imgs/p2/img_10.png)
- можно использовать в xml ))

## 2.8 BeanFactoryPostProcessor
- Всякие el-выражения для properties и т п рассчитывает BeanFactoryPostProcessor

![img_11.png](imgs/p2/img_11.png)

## 2.9 Custom BeanFactoryPostProcessor
- Можно реализовать свои BeanFactoryPostProcessor. Для этого реализовать в них BeanFactoryPostProcessor
- Можно настраивать порядос помощью implements Ordered getOrder или implements PriorityOrdered

# 3 Annotation-based configuration

## 3.1 Annotation-based Configuration
- В xml можно добавить

![img.png](imgs/p3/img.png)

- Он добавит необходимые бины для аннотаций и др., а именно:

![img_1.png](imgs/p3/img_1.png)

> `BeanFactoryPostProcessor` уже знаем, а вот `BeanPostProcessor` - новый этап `Lifecycle`

## 3.2 BeanPostProcessor

- `BeanPostProcessor` - это особенные бины, которые участвуют в жизненном цикле бинов и занимаются их конфигураций (наподобие `BeanFacotryPostProcessor`, только для Beans, а не `Bean Definitions`).
- ! `BeanFactoryPostProcessor` вызывается для контекста 1 раз, а `BeanPostProcessor` - для каждого бина

![img_2.png](imgs/p3/img_2.png)

- `Aware` интерфейсы - помогают инжектить в бин-пост процессоры, например, контекст. Имеют сеттеры, нужно их реализовать
- Порядок инициализации в `контексте` (= `IoC` контейнере):
1. `AwareBeanPostProcessors` – чтобы инжектить зависимости в сами пост процессоры
2. `BeanPostProcessors` – чтобы обрабатывать бины
3. `Beans`

## 3.3 Свой BeanPostProcessor

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

## 3.4 Свой BeanPostProcessor 2

- Есть методы:

```java
     Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException; // до колбека PostConstruct
// и
     Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException; // после колбека PostConstruct
```

> и если мы привносим какую-то сквозную функциональность с помощью Proxy, то нужно это делать в `postProcessAfterInitialization`, так как прокси вернет другой класс и могут быть ошибки

## 3.5 @Autowired, @Resource, @Value
> `@Resource` то же самое что `@Autowired`, просто чтобы поддерживать спецификацию `JavaEE`, лучше использовать `@Autowired`
> 
> `@Autowired` можно в поле, сеттере, конструкторе
> 
> Если несколько бинов - использовать `@Qualifier(beanId)` **или** **просто назвать поле/аргумент сеттера как** `id` бина
> 
> `@Value(${spring.el})` - подставить из пропертей

## 3.6 Classpath scanning
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

## 3.7 Bean Definition Readers
- Они читают xml/аннотации и строят контекст

![img_3.png](imgs/p3/img_3.png)


## 3.8 TypeFilters

<img src="imgs/p3/img_8.png" width="750">
<img src="imgs/p3/img_4.png" width="750">

Так Spring понимает, какие классы - бины. По умолчанию - `annotation`. То есть `@Component`, и т п. 
Можно настраивать в аннотации `@ComponentScan`: например `regex` - все, оканчивающиеся на Bean и т п.

## 3.9 @Scope

В аннотациях scope можно указывать в аннотации `@Scope: "prototype", "singleton"`

## 3.10 JSR 250, JSR 330

Для совместимости Spring поддерживает аннотации JSR:

![img_9.png](imgs/p3/img_9.png)

# 4. Java-based Configuration

## 4.1 Java-based Configuration

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

## 4.2 @Import & @ImportResource
В классе `@Configuration` можно указывать xml файлы с помощью `@ImportResource` (не используется) 
и другие классы-Configuration с помощью `@Import`, чтобы комбинировать конфигурации

## 4.3 @Bean. Часть 1
- Название метода - id бина
- Чтобы указать конкретный id бина при внеднерии, либо назвать аргумент/поле как id, либо 
- `@Qualifier("id")`
- Аргументы `@Bean`:
  - `autowireCandidate`: использовать ли при внедрении
  - `initMethod`, `destroyMethod` - аналоги `PostConstruct` и `PreDestroy`
- Можно при `@Bean` указывать `@Scope`

## 4.4 @Bean. Часть 2
- Можно внедрять бины прямо как вызов метода, как в `userRepository3`:

![img_1.png](imgs/p4-5/img_1.png)

> У pool3 не указан **`scope`**, т. е. он синглтон, и **`pool3()`** будет возвращать
> **один и тот же бин!** 

## 4.5 Profiles

Аннотацию `@Profile` можно ставить над `@Bean` или над `@Component`. В ней указывать профили.
- Поддерживает логические выражения:

![img_2.png](imgs/p4-5/img_2.png)

# 5. EventListeners

## 5.1 EventListeners. 1,2

В Spring можно делать слушатели событий:
![img_3.png](imgs/p4-5/img_3.png)

![img_4.png](imgs/p4-5/img_4.png)

![img_5.png](imgs/p4-5/img_5.png)

# 6. Spring Boot

## 6.1 Spring Boot. Введение

Если добавлять какие-то зависимости/модули в `Spring`, их нужно конфигурировать. 
`Spring Boot` все конфигурации содержит из коробки: в каждое `Spring Boot` приложение транзитивно тянется
```xml
org.springframework.book:spring-boot-autoconfigure
```

Спринг бут делает за нас это:
![img.png](imgs/p6/img.png)

![img_1.png](imgs/p6/img_1.png)

![img_2.png](imgs/p6/img_2.png)

Бины, автоматически настроенные `Spring Boot`, можно переопределить

## 6.2 @Conditional

- Можно создавать бины/конфигурации по условиям: аннотация `@Conditional` (`@Profile` использует ее под капотом)
- Передаем в нее Class, который реализует интерфейс Condition и его метод `matches(ConditionContext, AnnotatedTypeMetadata)`

В `Spring Boot` есть свои аннотации:

![img_3.png](imgs/p6/img_3.png)

## 6.3 Spring Boot. Настройка проекта

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

## 6.4 `@SpringBootApplication`

- Все классы и пакеты должны лежать на том же уровне/ниже класса `@SpringBootApplication`
- `SpringBootApplication.run` возвращает контекст
- `@SpringBootApplication` содержит:
  - `@SpringBootConfiguration` (Configuration, но одна на класс)
  - `@ComponentScan`
  - `@PropertySource` подтягивается тоже, сканируется application properties/yaml
  - `@EnableAutoConfiguration` – автоматически подтягиваются автоконфигурации 
из `spring-boot-autoconfigure`, но подтягиваются по условиям `@Conditional`,
могут добавлять какие-то свои бины (**то есть Spring Boot может добавлять какие-то бины**)

## 6.5 Lombok
- Вместо всех gradle зависимостей по типу preprocessor, можно подключить gradle plugin:

![img_4.png](imgs/p6/img_4.png)

Аннотации:
- `@Cleanup`: аналог try-with-resources но более гибкая
- `@NonNull`: на аргументе метода будет проверять и кидать `NPE` если передан `null` с именем аргумента
- `@SneakyThrows`: может быть полезна в лямбдах

> Как с помощью `lombok` сделать конструктор для `final`-полей `@Value`?
> 1. Делам lombok.config:
![img_5.png](imgs/p6/img_5.png)
> 2. Ставим над final полем аннотацию `@Value`/Qualifier и т п
> 3. Пишем @RequiredArgsConstructor и т п
> 4. Все, lombok сам в конструктор поставит аннотации

## 6.6 Properties

Можно сделать файл spring.properties, он для более low-level
пропертей, его проперти можно получить через SpringProperties.method

Вообще, есть 14 вариантов задания пропертей: 
![img_6.png](imgs/p6/img_6.png)

> Каждый из последущих имеет более высокий приоритет, чем предыдущий

> `VM options` задаются через `-D`, `Program arguments` через `--`
![img_7.png](imgs/p6/img_7.png)


## 6.7 yaml
В целом он удобнее, в том числе работа со списками, объектами:
![img_9.png](imgs/p6/img_9.png)

## 6.8 `@ConfigurationProperties`

> Можно маппить проперти в объекты/record:

![img_10.png](imgs/p6/img_10.png)

![img_11.png](imgs/p6/img_11.png)