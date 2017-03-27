package com.lufficc.spring.example.jpa.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;

/**
 * Created by lufficc on 3/27/2017.
 */
@Entity
@Table
public class Category extends BaseModel {
	private String name;

	@JsonIgnore
	@OneToMany(mappedBy = "category", targetEntity = Post.class)
	private Set<Post> posts;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Post> getPosts() {
		return posts;
	}

	public void setPosts(Set<Post> posts) {
		this.posts = posts;
	}
}
