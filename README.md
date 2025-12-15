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

![img.png](img.png)

- Он добавит необходимые бины для аннотаций и др., а именно:

![img_1.png](img_1.png)

> `BeanFactoryPostProcessor` уже знаем, а вот `BeanPostProcessor` - новый этап `Lifecycle`