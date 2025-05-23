package com.learningtohunt.web.server.config;

import com.learningtohunt.web.server.security.JwtUtil;
import com.learningtohunt.web.server.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ProjectSecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.cors(Customizer.withDefaults())
                .authorizeHttpRequests((requests) -> {
                    requests
                            // Home page & Angular files
                            .requestMatchers("/").permitAll()
                            .requestMatchers("/index.html").permitAll()
                            .requestMatchers("/hunting-guide", "/article/**", "/account/**", "/privacy").permitAll() // replicate in SpaController.forward()
                            .requestMatchers("/favicon.ico").permitAll()
                            .requestMatchers("/styles*.css").permitAll()
                            .requestMatchers("/chunk-*.js").permitAll()
                            .requestMatchers("/polyfills*.js", "/scripts*.js", "/main*.js").permitAll()
                            .requestMatchers("/assets/**").permitAll()
                            .requestMatchers("/esm2020/**").permitAll()
                            .requestMatchers("/fesm2015/**").permitAll()
                            .requestMatchers("/fesm2020/**").permitAll()
                            .requestMatchers("/lib/**").permitAll()
                            .requestMatchers("/themes/**").permitAll()
                            .requestMatchers("/kolkov-angular-editor.d.ts", "/package.json", "/public-api.d.ts").permitAll() // kolkov angular editor

                            // articles
                            .requestMatchers("/api/article/get/**").permitAll()

                            // user account
                            .requestMatchers("/api/account/login").permitAll()
                            .requestMatchers("/api/account/logout").permitAll()
                            .requestMatchers("/api/account/forgot-password").permitAll()
                            .requestMatchers("/api/account/forgot-password-reset").permitAll()
                            .requestMatchers("/api/account/register").permitAll()
                            .requestMatchers("/api/account/confirmation/**").permitAll()
                            .requestMatchers("/api/account/ping").authenticated()
                            .requestMatchers("/api/account/update").authenticated();
                })
                .sessionManagement(sessionCreationPolicy ->
                        sessionCreationPolicy.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(config -> config.ignoringRequestMatchers("/api/**"));

        http.addFilterBefore(jwtRequestFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**").allowedOrigins(System.getenv("L2H_CLIENT_URL"))
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")
                        .allowCredentials(true)
                        .allowedHeaders("*");
            }
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public JwtRequestFilter jwtRequestFilter() {
        return new JwtRequestFilter(jwtUtil, customUserDetailsService);
    }
}
