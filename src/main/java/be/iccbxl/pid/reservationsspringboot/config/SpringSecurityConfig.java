package be.iccbxl.pid.reservationsspringboot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder.BCryptVersion;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SpringSecurityConfig {
	@Bean
    public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(BCryptVersion.$2Y, 12);
    }
/*
    @Bean
    public AuthenticationManager authenticationManager(
            final AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
*/    
    @Bean
    public SecurityFilterChain configure(final HttpSecurity http) throws Exception {
        return http.cors(Customizer.withDefaults())
                //.csrf(Customizer.withDefaults())
        		.csrf(csrf -> csrf.disable())	//Désactiver la protection des formulaires
                
                .authorizeHttpRequests(auth -> {
                	auth.requestMatchers("/admin").hasRole("ADMIN");
                    auth.requestMatchers("/user").hasRole("MEMBER");
                    
                    //API
                    auth.requestMatchers("/api/public/**").permitAll(); // Endpoints publics
                    auth.requestMatchers("/api/admin/**").hasRole("ADMIN"); // Endpoints réservés aux administrateurs

                    auth.anyRequest().permitAll();
                })
                .httpBasic(Customizer.withDefaults()) // Permet l'authentification de base (utile pour les tests)
                .formLogin(form -> form
                    .loginPage("/login")
                    .usernameParameter("login")
                    .failureUrl("/login?loginError=true"))
                .logout(logout -> logout
                    .logoutSuccessUrl("/login?logoutSuccess=true")
                    .deleteCookies("JSESSIONID"))
                .exceptionHandling(exception -> exception
                    .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login?loginRequired=true")))
                .build();
    }
    
}
