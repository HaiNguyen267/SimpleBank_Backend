package hainguyen.tech.SimpleBank.config;

import hainguyen.tech.SimpleBank.security.JwtProvider;
import hainguyen.tech.SimpleBank.security.jwtFilters.JwtAuthorizationFilter;
import hainguyen.tech.SimpleBank.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private CustomUserDetailsService customerDetailsService;


    @Autowired
    private JwtProvider jwtProvider;

    @Override
    public void configure(HttpSecurity http) throws Exception {

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        JwtAuthorizationFilter jwtAuthorizationFilter
                = new JwtAuthorizationFilter(customerDetailsService, jwtProvider, authenticationManagerBean());
        http.addFilterAfter(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);


        http
                .cors()
                .and()
                .csrf().disable()
                .authorizeRequests(authz ->
                        authz
                                .antMatchers("/signup", "/login" ,"/hello", "/oauthlogin", "/userinfo", "/verify", "/accountname").permitAll()
                                .antMatchers("/deposit", "/withdraw", "/transfer", "/users").hasAuthority("ROLE_USER")
                                .antMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                                .anyRequest().authenticated()
                )
                .formLogin()
                .loginPage("/signin");

    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }





}
