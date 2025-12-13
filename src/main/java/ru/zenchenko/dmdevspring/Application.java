package ru.zenchenko.dmdevspring;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Application {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext("applicationContext.xml");
        context.getBean(MyClass.class);
        System.out.println("hello world");
    }
}

class MyClass {

}
