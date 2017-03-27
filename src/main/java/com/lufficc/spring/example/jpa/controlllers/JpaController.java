package com.lufficc.spring.example.jpa.controlllers;

import com.lufficc.spring.example.jpa.models.QCategory;
import com.lufficc.spring.example.jpa.models.QPost;
import com.lufficc.spring.example.jpa.models.QUser;
import com.lufficc.spring.example.jpa.models.User;
import com.lufficc.spring.example.jpa.repositories.UserRepository;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by lufficc
 * When 2017/2/20
 */
@RestController
public class JpaController {
	private final Logger logger = Logger.getLogger(JpaController.class);

	@PersistenceContext
	private EntityManager entityManager;

	private final UserRepository userRepository;
	private JPAQueryFactory queryFactory;

	@Autowired
	public JpaController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@PostConstruct
	public void init() {
		queryFactory = new JPAQueryFactory(entityManager);
	}

	@GetMapping("/users/emails")
	public Object userEmails() {
		QUser user = QUser.user;
		QPost post = QPost.post;
		userRepository.findAll(user.name.eq("lufifcc"));
		userRepository.findAll(
				user.email.endsWith("@gmail.com")
						.and(user.name.startsWith("lu"))
						.and(user.id.in(Arrays.asList(520L, 1314L, 1L, 2L, 12L)))
		);

		userRepository.count(
				user.email.endsWith("@outlook.com")
						.and(user.name.containsIgnoreCase("a"))
		);

		userRepository.findAll(
				user.email.endsWith("@outlook.com")
						.and(user.posts.size().goe(5))
		);

		userRepository.findAll(
				user.posts.size().goe(JPAExpressions.select(user.posts.size().max()).from(user))
		);

		return queryFactory.selectFrom(user)
				.select(user.email)
				.fetch();
	}

	@GetMapping("users/names-emails")
	public Object userNamesEmails() {
		QUser user = QUser.user;
		return queryFactory.selectFrom(user)
				.select(user.email, user.name)
				.fetch()
				.stream()
				.map(tuple -> {
					Map<String, String> map = new LinkedHashMap<>();
					map.put("name", tuple.get(user.name));
					map.put("email", tuple.get(user.email));
					return map;
				}).collect(Collectors.toList());
	}


	@GetMapping("users")
	public Object users(@QuerydslPredicate(root = User.class) Predicate predicate) {
		return userRepository.findAll(predicate);
	}

	@GetMapping("users/{user}/posts")
	public Object posts(@PathVariable User user) {
		return user.getPosts();
	}

	@GetMapping("users/posts-count")
	public Object postCount() {
		QUser user = QUser.user;
		QPost post = QPost.post;
		return queryFactory.selectFrom(user)
				.leftJoin(user.posts, post)
				.select(user.id, user.name, post.count())
				.groupBy(user.id)
				.fetch()
				.stream()
				.map(tuple -> {
					Map<String, Object> map = new LinkedHashMap<>();
					map.put("id", tuple.get(user.id));
					map.put("name", tuple.get(user.name));
					map.put("posts_count", tuple.get(post.count()));
					return map;
				}).collect(Collectors.toList());
	}

	@GetMapping("users/category-count")
	public Object postCategoryMax() {
		QUser user = QUser.user;
		QPost post = QPost.post;
		QCategory category = QCategory.category;
		NumberExpression<Integer> java = post.category
				.name.lower().when("java").then(1).otherwise(0);
		NumberExpression<Integer> python = post.category
				.name.lower().when("python").then(1).otherwise(0);
		return queryFactory.selectFrom(user)
				.leftJoin(user.posts, post)
				.groupBy(user.id)
				.select(user.name, user.id, java.count(), python.count())
				.orderBy(user.name.desc())
				.fetch()
				.stream()
				.map(tuple -> {
					Map<String, Object> map = new LinkedHashMap<>();
					map.put("username", tuple.get(user.name));
					map.put("java_count", tuple.get(java.count()));
					map.put("python_count", tuple.get(python.count()));
					return map;
				}).collect(Collectors.toList());
	}
}
