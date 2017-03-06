package com.lufficc.spring.example.jpa;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.StringPath;
import org.springframework.data.jpa.repository.support.QueryDslRepositorySupport;
import org.springframework.data.web.querydsl.QuerydslPredicateArgumentResolver;

import javax.annotation.Nullable;


/**
 * Created by lufficc
 * When 2017/2/23
 */
public class Search implements Predicate {
    private BooleanBuilder booleanBuilder = new BooleanBuilder();
    private Class<?> entityType;

    public Search(Class<?> entityType) {
        this.entityType = entityType;
    }

    public Search with(String key, String operator, String value) {
        System.out.println(String.format("%s %s %s", key, operator, value));
        Predicate predicate = new SearchItem(key, operator, value).toPredicate();
        if (predicate != null) {
            booleanBuilder.and(predicate);
        }
        return this;
    }

    @Override
    public Predicate not() {
        return booleanBuilder.not();
    }

    @Nullable
    @Override
    public <R, C> R accept(Visitor<R, C> visitor, @Nullable C c) {
        return booleanBuilder.accept(visitor, c);
    }

    @Override
    public Class<? extends Boolean> getType() {
        return booleanBuilder.getType();
    }

    private class SearchItem {
        private final String key;
        private final String operator;
        private final String value;

        private SearchItem(String key, String operator, String value) {
            this.key = key == null ? "" : key;
            this.operator = operator == null ? "" : key;
            this.value = value == null ? "" : key;
        }

        private Predicate toPredicate() {
            String simpleName = entityType.getSimpleName().toLowerCase();
            PathBuilder<?> entityPath = new PathBuilder<>(entityType, simpleName);

            if (isNumeric(value)) {
                NumberPath<Integer> path = entityPath.getNumber(key, Integer.class);
                int intValue = Integer.parseInt(value);
                if (":".equals(operator)) {
                    return path.eq(intValue);
                } else if (">".equals(operator)) {
                    return path.goe(intValue);
                } else if ("<".equals(operator)) {
                    return path.loe(intValue);
                }
            } else {
                StringPath path = entityPath.getString(key);
                if (":".equals(operator)) {
                    return path.containsIgnoreCase(value);
                }
            }
            QuerydslPredicateArgumentResolver querydslPredicateArgumentResolver;
            QueryDslRepositorySupport support;
            Predicate predicate;
            return null;
        }

        private boolean isNumeric(String str) {
            for (char c : str.toCharArray()) {
                if (!Character.isDigit(c)) return false;
            }
            return true;
        }

    }
}
