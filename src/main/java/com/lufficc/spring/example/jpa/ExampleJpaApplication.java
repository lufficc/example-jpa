package com.lufficc.spring.example.jpa;

import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.lufficc.spring.example.jpa.models.Category;
import com.lufficc.spring.example.jpa.models.Post;
import com.lufficc.spring.example.jpa.models.Tag;
import com.lufficc.spring.example.jpa.models.User;
import com.lufficc.spring.example.jpa.repositories.CategoryRepository;
import com.lufficc.spring.example.jpa.repositories.PostRepository;
import com.lufficc.spring.example.jpa.repositories.TagRepository;
import com.lufficc.spring.example.jpa.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableJpaAuditing
public class ExampleJpaApplication {
	private final UserRepository userRepository;
	private final PostRepository postRepository;
	private final CategoryRepository categoryRepository;
	private final TagRepository tagRepository;

	@Autowired
	public ExampleJpaApplication(UserRepository userRepository, PostRepository postRepository, CategoryRepository categoryRepository, TagRepository tagRepository) {
		this.userRepository = userRepository;
		this.postRepository = postRepository;
		this.categoryRepository = categoryRepository;
		this.tagRepository = tagRepository;
	}

	public static void main(String[] args) {
		SpringApplication.run(ExampleJpaApplication.class, args);
	}

	@PostConstruct
	public void init() {
		Category category1 = new Category();
		category1.setName("Java");

		Category category2 = new Category();
		category2.setName("Python");

		Category category3 = new Category();
		category3.setName("PHP");

		categoryRepository.save(Arrays.asList(category1, category2, category3));

		for (int i = 0; i < 10; i++) {
			Tag tag = new Tag();
			tag.setName("Tag" + i);
			tagRepository.save(tag);
		}

		feedFakeData("Lufficc", "lufficc@qq.com", "chengdu");
		feedFakeData("Allen", "allen@qq.com", "beijing");
		feedFakeData("Mike", "mike@qq.com", "shanghai");
		feedFakeData("Lucy", "lucy@qq.com", "guangzhou");
	}

	@Bean
	public Jackson2ObjectMapperBuilder objectMapperBuilder() {
		Jackson2ObjectMapperBuilder mapperBuilder = new Jackson2ObjectMapperBuilder();
		mapperBuilder.dateFormat(new ISO8601DateFormat());
		return mapperBuilder;
	}

	private void feedFakeData(String name, String email, String address) {
		List<Category> categories = categoryRepository.findAll();
		List<Tag> tags = tagRepository.findAll();
		Random rand = new Random();
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
			post.setCategory(categories.get(rand.nextInt(categories.size())));
			post.setTags(
					rand.ints(rand.nextInt(tags.size()), 0, tags.size())
							.mapToObj(tags::get)
							.collect(Collectors.toSet())
			);
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
