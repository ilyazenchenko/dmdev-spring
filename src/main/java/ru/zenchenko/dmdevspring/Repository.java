package ru.zenchenko.dmdevspring;

import jakarta.annotation.PostConstruct;

public class Repository {

    @InjectBean
    private ConnectionPool connectionPool;

    @PostConstruct
    public void init() {
        System.out.println("ConnectionPool: " + connectionPool);
    }

}
