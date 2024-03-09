package com.fpt.ssds.security.jwt;


import org.springframework.context.MessageSource;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

public class ApiConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final TokenProvider tokenProvider;

    private MessageSource messageSource;

    private final HandlerExceptionResolver resolver;

    public ApiConfigurer(TokenProvider tokenProvider, MessageSource messageSource, HandlerExceptionResolver resolver) {
        this.tokenProvider = tokenProvider;
        this.messageSource = messageSource;
        this.resolver = resolver;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        ApiFilter customFilter = new ApiFilter(tokenProvider, messageSource, resolver);
        http.requestMatchers()
            .antMatchers("/api/**", "/work/**", "/ext/**")
            .and()
            .addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
