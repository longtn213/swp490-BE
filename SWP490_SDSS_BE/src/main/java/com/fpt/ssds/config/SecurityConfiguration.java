package com.fpt.ssds.config;

import com.fpt.ssds.security.jwt.*;
import com.fpt.ssds.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

import java.util.List;
import java.util.Map;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Import(SecurityProblemSupport.class)
public class SecurityConfiguration {
    @Configuration
    @Order(1)
    public class ApiSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {
        private final TokenProvider tokenProvider;

        private final MessageSource messageSource;

        private final CorsFilter corsFilter;

        private final SecurityProblemSupport problemSupport;

        private final HandlerExceptionResolver resolver;

        @Autowired
        PermissionService permissionService;

        public ApiSecurityConfigurationAdapter(CorsFilter corsFilter, SecurityProblemSupport problemSupport, TokenProvider tokenProvider, MessageSource messageSource, @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
            this.corsFilter = corsFilter;
            this.problemSupport = problemSupport;
            this.tokenProvider = tokenProvider;
            this.messageSource = messageSource;
            this.resolver = resolver;
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Override
        public void configure(WebSecurity web) {
            web.ignoring()
                .antMatchers(HttpMethod.OPTIONS, "/**")
                .antMatchers("/swagger-ui/index.html")
                .antMatchers("/test/**");
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {
            /*Map<String, List<String>> mapPermission = permissionService.getMapPermission();
            for (Map.Entry<String, List<String>> permission : mapPermission.entrySet()) {
                if (!permission.getValue().isEmpty()) {
                    String[] array = permission.getValue().toArray(new String[0]);
                    http.authorizeRequests().antMatchers(permission.getKey()).hasAnyAuthority(array);
                }
            }*/

            // @formatter:off
            http.csrf()
                .disable()
                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(problemSupport)
                .accessDeniedHandler(problemSupport)
                .and()
                .headers()
                .contentSecurityPolicy("default-src 'self'; frame-src 'self' data:; script-src 'self' 'unsafe-inline' 'unsafe-eval' https://storage.googleapis.com; style-src 'self' 'unsafe-inline'; img-src 'self' data:; font-src 'self' data:")
                .and()
                .referrerPolicy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
                .and()
                .featurePolicy("geolocation 'none'; midi 'none'; sync-xhr 'none'; microphone 'none'; camera 'none'; magnetometer 'none'; gyroscope 'none'; speaker 'none'; fullscreen 'self'; payment 'none'")
                .and()
                .frameOptions()
                .deny()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .requestMatchers()
                .antMatchers("/api/v1/user/profile", "/api/web/v1/user/profile", "/api/v1/group/**", "/api/v1/permission/**", "/api/v1/role/**")
                .and()
                .addFilterBefore(securityApiFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .anyRequest().authenticated();
            // @formatter:on
        }

        private ApiFilter securityApiFilter() {
            return new ApiFilter(tokenProvider, messageSource, resolver);
        }
    }

    @Configuration
    @Order(2)
    public class ApiDcSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {
        private final TokenProvider tokenProvider;

        private final MessageSource messageSource;

        private final CorsFilter corsFilter;

        private final SecurityProblemSupport problemSupport;

        private final HandlerExceptionResolver resolver;

        @Autowired
        PermissionService permissionService;

        public ApiDcSecurityConfigurationAdapter(CorsFilter corsFilter, SecurityProblemSupport problemSupport, TokenProvider tokenProvider, MessageSource messageSource, @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
            this.corsFilter = corsFilter;
            this.problemSupport = problemSupport;
            this.tokenProvider = tokenProvider;
            this.messageSource = messageSource;
            this.resolver = resolver;
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Override
        public void configure(WebSecurity web) {
            web.ignoring()
                .antMatchers(HttpMethod.OPTIONS, "/**")
                .antMatchers("/swagger-ui/index.html")
                .antMatchers("/test/**");
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {
//            Map<String, List<String>> mapPermission = permissionService.getMapPermission();
//            for (Map.Entry<String, List<String>> permission : mapPermission.entrySet()) {
//                if (!permission.getValue().isEmpty()) {
//                    String[] array = permission.getValue().toArray(new String[0]);
//                    http.authorizeRequests().antMatchers(permission.getKey()).hasAnyAuthority(array);
//                }
//            }

            // @formatter:off
            http.csrf()
                .disable()
                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(problemSupport)
                .accessDeniedHandler(problemSupport)
                .and()
                .headers()
                .contentSecurityPolicy("default-src 'self'; frame-src 'self' data:; script-src 'self' 'unsafe-inline' 'unsafe-eval' https://storage.googleapis.com; style-src 'self' 'unsafe-inline'; img-src 'self' data:; font-src 'self' data:")
                .and()
                .referrerPolicy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
                .and()
                .featurePolicy("geolocation 'none'; midi 'none'; sync-xhr 'none'; microphone 'none'; camera 'none'; magnetometer 'none'; gyroscope 'none'; speaker 'none'; fullscreen 'self'; payment 'none'")
                .and()
                .frameOptions()
                .deny()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .requestMatchers()
                .antMatchers("/api/**", "/work/**")
                .and()
                .addFilterBefore(securityApiFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/api/web/v1/user/create/**").permitAll()
                .antMatchers("/management/health/**").permitAll()
                .antMatchers("/management/info").permitAll()
                .antMatchers("/management/prometheus").permitAll()
                .anyRequest().authenticated();
            // @formatter:on
        }

        private ApiBranchFilter securityApiFilter() {
            return new ApiBranchFilter(tokenProvider, messageSource, resolver);
        }
    }

}
