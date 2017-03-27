package com.lufficc.spring.example.jpa.repositories;

import com.lufficc.spring.example.jpa.models.Post;
import com.lufficc.spring.example.jpa.models.QPost;
import com.querydsl.core.types.dsl.StringExpression;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.stereotype.Repository;

/**
 * Created by lufficc
 * When 2017/2/20
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Long>, QueryDslPredicateExecutor<Post>, QuerydslBinderCustomizer<QPost> {
    default void customize(QuerydslBindings bindings, QPost post) {
        bindings.bind(post.title).first(StringExpression::containsIgnoreCase);
    }
}
