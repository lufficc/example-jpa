package com.lufficc.spring.example.jpa;

import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.lufficc.spring.example.jpa.models.Post;
import com.lufficc.spring.example.jpa.models.User;
import com.lufficc.spring.example.jpa.repositories.PostRepository;
import com.lufficc.spring.example.jpa.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.UUID;

@SpringBootApplication
@EnableJpaAuditing
public class ExampleJpaApplication {
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Autowired
    public ExampleJpaApplication(UserRepository userRepository, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(ExampleJpaApplication.class, args);
    }

    @PostConstruct
    public void init() {
        feedFakeData("Lufficc", "lufficc@qq.com", "chengdu");
        feedFakeData("Allen", "allen@qq.com", "beijing");
        feedFakeData("Mike", "mike@qq.com", "shanghai");
        feedFakeData("Lucy", "lucy@qq.com", "guangzhou");
    }

    @Bean
    public Jackson2ObjectMapperBuilder objectMapperBuilder(){
        Jackson2ObjectMapperBuilder mapperBuilder = new Jackson2ObjectMapperBuilder();
        mapperBuilder.dateFormat(new ISO8601DateFormat());
        return mapperBuilder;
    }

    private void feedFakeData(String name, String email, String address) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setAddress(address);
        userRepository.save(user);
        Random random = new Random(System.currentTimeMillis());
        int count = random.nextInt(5) + 5;
        for (int i = 0; i < count; i++) {
            Post post = new Post();
            post.setTitle("title" + i + 1);
            post.setCreatedAt(randomDate());
            post.setContent(UUID.randomUUID().toString());
            post.setCategory(random.nextBoolean() ? "java" : "python");
            post.setUser(user);
            postRepository.save(post);
        }
    }

    public static Date randomDate() {
        GregorianCalendar gc = new GregorianCalendar();
        int year = randBetween(2015, 2017);
        gc.set(GregorianCalendar.YEAR, year);
        int dayOfYear = randBetween(1, gc.getActualMaximum(GregorianCalendar.DAY_OF_YEAR));
        gc.set(GregorianCalendar.DAY_OF_YEAR, dayOfYear);
        return gc.getTime();
    }

    private static int randBetween(int start, int end) {
        return start + (int) Math.round(Math.random() * (end - start));
    }
}
