package com.lufficc.spring.example.jpa;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lufficc
 * When 2017/2/23
 */
public class SearchMethodArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.getParameterType().equals(Search.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        String q = nativeWebRequest.getParameter("q");
        SearchAnn searchAnn = methodParameter.getParameterAnnotation(SearchAnn.class);
        Search search = new Search(searchAnn.value());
        if (!org.springframework.util.StringUtils.isEmpty(q)) {
            Pattern pattern = Pattern.compile("(\\w+?)(:|<|>)(\\w+?),");
            Matcher matcher = pattern.matcher(q + ",");
            while (matcher.find()) {
                search.with(matcher.group(1), matcher.group(2), matcher.group(3));
            }
        }
        return search;
    }
}
