package com.lufficc.spring.example.jpa;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

/**
 * Created by lufficc
 * When 2017/2/23
 */
@Configuration
@EnableWebMvc
public class WebConfigure extends WebMvcConfigurerAdapter{
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new SearchMethodArgumentResolver());
    }
}
