package com.lufficc.spring.example.jpa.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by lufficc on 3/27/2017.
 */
@Entity
@Table
public class Tag extends BaseModel {
	private String name;

	@JsonIgnore
	@ManyToMany
	@JoinTable(name = "post_tag", joinColumns = @JoinColumn(name = "tag_id"), inverseJoinColumns = @JoinColumn(name = "post_id"))
	private Set<Post> posts = new HashSet<>();

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

