package com.geomatys.crsservice.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.web.header.writers.CrossOriginEmbedderPolicyHeaderWriter.CrossOriginEmbedderPolicy.REQUIRE_CORP;
import static org.springframework.security.web.header.writers.CrossOriginOpenerPolicyHeaderWriter.CrossOriginOpenerPolicy.SAME_ORIGIN;

@Configuration
public class Security {

    @Bean
    public SecurityFilterChain securityDeactivated(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(
                        authorize ->
                                authorize.anyRequest().permitAll()
                )
                .csrf(csrf -> csrf.disable())
                // Force HTTP response headers for better performance measurement in browser.
                // For details, see https://stackoverflow.com/a/65959796/2678097
                .headers(
                        headers ->
                                headers.crossOriginOpenerPolicy(coop -> coop.policy(SAME_ORIGIN))
                                       .crossOriginEmbedderPolicy(coep -> coep.policy(REQUIRE_CORP))
                )
                .build();
    }
}
