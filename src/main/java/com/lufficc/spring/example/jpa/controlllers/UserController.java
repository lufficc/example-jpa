package com.lufficc.spring.example.jpa.controlllers;

import com.lufficc.spring.example.jpa.Search;
import com.lufficc.spring.example.jpa.SearchAnn;
import com.lufficc.spring.example.jpa.models.QPost;
import com.lufficc.spring.example.jpa.models.QUser;
import com.lufficc.spring.example.jpa.models.User;
import com.lufficc.spring.example.jpa.repositories.UserRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Constant;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
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
    public Object index(Search search) {
        QUser user = QUser.user;
        QPost post = QPost.post;
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        NumberExpression<Integer> cases = new CaseBuilder()
                .when(post.category.eq("java")).then(1)
                .otherwise(0);

        Object o;
        BooleanBuilder builder = new BooleanBuilder();

        Path<User> person = Expressions.path(User.class, "person");
        Path<String> personFirstName = Expressions.path(String.class, person, "firstName");
        Constant<String> constant = (Constant<String>) Expressions.constant("P");
        Expressions.predicate(Ops.STARTS_WITH, personFirstName, constant);

        PathBuilder<User> entityPath = new PathBuilder<>(User.class, "entity");
        entityPath.get("s", String.class);
        entityPath.getMap("map", String.class, String.class).get("key");
        user.name.lower().when("").then(1).when("").then(1);

        o = queryFactory
                .from(user)
                .leftJoin(user.posts, post)
                .groupBy(post.category)
                .select(user.id, user.name, post.category, post.category.count())
                .groupBy(user.id)
                .fetch()
                .stream()
                .map(((Tuple tuple) -> {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("id", tuple.get(user.id));
                    map.put("name", tuple.get(user.name));
                    map.put("category", tuple.get(2, String.class));
                    map.put("java_post_count", tuple.get(3, Long.class));
                    return map;
                }))
                .collect(Collectors.toList());
        QuerydslPredicate querydslPredicate;
        return o;
    }


    @GetMapping("all")
    public Object all(@QuerydslPredicate(root = User.class) Predicate predicate, @SearchAnn(User.class) Search search) {
        return userRepository.findAll(search);
    }
}
