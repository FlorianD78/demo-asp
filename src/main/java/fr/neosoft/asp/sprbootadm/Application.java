package fr.neosoft.asp.sprbootadm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import de.codecentric.boot.admin.server.config.AdminServerProperties;
import de.codecentric.boot.admin.server.config.EnableAdminServer;
import de.codecentric.boot.admin.server.domain.entities.InstanceRepository;
import de.codecentric.boot.admin.server.notify.LoggingNotifier;

@Configuration
@EnableAutoConfiguration
@EnableAdminServer
public class Application {

    private final String adminContextPath;

    public Application(final AdminServerProperties adminServerProperties) {
        this.adminContextPath = adminServerProperties.getContextPath();
    }

    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    @Profile("default")
    public SecurityWebFilterChain securityWebFilterChainPermitAll(final ServerHttpSecurity http) {
        return http
                .authorizeExchange()
                .anyExchange()
                .permitAll()//
                .and()
                .csrf()
                .disable()//
                .build();
    }

    @Bean
    @Profile("secure")
    public SecurityWebFilterChain securityWebFilterChainSecure(final ServerHttpSecurity http) {
        // @formatter:off
        return http.authorizeExchange()
                .pathMatchers(this.adminContextPath + "/assets/**").permitAll()
                .pathMatchers(this.adminContextPath + "/login").permitAll()
                .anyExchange().authenticated()
                .and()
            .formLogin().loginPage(this.adminContextPath + "/login").and()
            .logout().logoutUrl(this.adminContextPath + "/logout").and()
            .httpBasic().and()
            .csrf().disable()
            .build();
        // @formatter:on
    }

    @Bean
    public LoggingNotifier loggerNotifier(final InstanceRepository repository) {
        return new LoggingNotifier(repository);
    }
}
