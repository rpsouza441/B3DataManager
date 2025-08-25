package br.dev.rodrigopinheiro.B3DataManager.presentation.security;


import br.dev.rodrigopinheiro.B3DataManager.application.service.UserCustomService;
import com.vaadin.flow.spring.security.VaadinWebSecurity;

import com.vaadin.hilla.route.RouteUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends VaadinWebSecurity {

    private final RouteUtil routeUtil;

    private final UserDetailsService userDetailsService;

    public SecurityConfig(RouteUtil routeUtil, UserDetailsService userDetailsService) {
        this.routeUtil = routeUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Set default security policy that permits Hilla internal requests and
        // denies all other
        http.authorizeHttpRequests(auth ->
        {
            auth.requestMatchers(HttpMethod.GET, "/images/*.png").permitAll();
            auth.requestMatchers(HttpMethod.GET, "/line-awesome/svg/*.svg").permitAll();
            auth.requestMatchers(HttpMethod.GET, "/@fontsource/roboto-condensed/*.css").permitAll();
        });
        http.authorizeHttpRequests(registry -> registry.requestMatchers(
                routeUtil::isRouteAllowed).permitAll());
        http.headers(configurer -> configurer.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));
        super.configure(http);        // use a custom login view and redirect to root on logout
        setLoginView(http, "/login", "/");
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
