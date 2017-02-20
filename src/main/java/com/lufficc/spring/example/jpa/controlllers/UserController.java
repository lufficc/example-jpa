package com.lufficc.spring.example.jpa.controlllers;

import com.lufficc.spring.example.jpa.models.QPost;
import com.lufficc.spring.example.jpa.models.QUser;
import com.lufficc.spring.example.jpa.repositories.UserRepository;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by lufficc
 * When 2017/2/20
 */
@RestController
@RequestMapping("user")
public class UserController {
    private final Logger logger = Logger.getLogger(UserController.class);

    @PersistenceContext
    private EntityManager entityManager;

    private final UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("")
    public Object index() {
        QUser user = QUser.user;
        QPost post = QPost.post;
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        NumberExpression<Integer> cases = new CaseBuilder()
                .when(post.category.eq("java")).then(1)
                .otherwise(0);

        Object o;
        /*o = queryFactory.from(post)
                .select()
                .groupBy(post.user.id);*/


        o = queryFactory
                .from(user)
                .leftJoin(user.posts, post)
                .groupBy(post.category)
                .select(user.id, user.name, post.category, post.category.count())
                .groupBy(user.id)
                .fetch()
                .stream()
                .map((tuple -> {
                    logger.info(tuple.size());
                    logger.info(tuple.toString());
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("id", tuple.get(user.id));
                    map.put("name", tuple.get(user.name));
                    /*map.put("all_post_count", tuple.get(3, Long.class));*/
                    map.put("category", tuple.get(2, String.class));
                    map.put("java_post_count", tuple.get(3, Long.class));
                    return map;
                }))
                .collect(Collectors.toList());
        return o;
    }

    @GetMapping("all")
    public Object all() {
        return userRepository.findAll();
    }
}
