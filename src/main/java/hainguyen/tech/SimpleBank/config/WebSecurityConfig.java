package hainguyen.tech.SimpleBank.config;

import hainguyen.tech.SimpleBank.security.JwtProvider;
import hainguyen.tech.SimpleBank.security.jwtFilters.JwtAuthenticationFilter;
import hainguyen.tech.SimpleBank.security.jwtFilters.JwtAuthorizationFilter;
import hainguyen.tech.SimpleBank.service.AppUserService;
import hainguyen.tech.SimpleBank.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

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

        JwtAuthenticationFilter jwtAuthenticationFilter
                = new JwtAuthenticationFilter(authenticationManagerBean(), jwtProvider);

        jwtAuthenticationFilter.setFilterProcessesUrl("/login1");
        http.addFilter(jwtAuthenticationFilter);

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
                                .anyRequest().authenticated()
                )
                .formLogin()
                .loginPage("/signin");
//                .and()
////                .oauth2Client()
////                .and()
//                .oauth2Login()
//                .userInfoEndpoint()
//                .userService(customOAuth2UserService)
//                .and()
//                .successHandler(new AuthenticationSuccessHandler() {
//                    @Override
//                    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//                        Object principal = authentication.getPrincipal();
//                        OAuth2User customOAuth2User = (OAuth2User) principal;
//                        System.out.println(customOAuth2User.toString());
//                        String email = customOAuth2User.getAttribute("email");
//                        System.out.println("User email: " + email);
//
//                        createAppUserIfNotExist(email, customOAuth2User);
//
//
//                    }
//
//                    private void createAppUserIfNotExist(String email, OAuth2User customOAuth2User) {
//
//                        boolean exist = appUserService.checkIfEmailExist(email);
//                        if (!exist) {
//                            AppUser appUser = appUserService.createNewAppUser(
//                                    email,
//                                    customOAuth2User.getAttribute("name"),
//                                    null,
//                                    null,
//                                    GOOGLE
//                            );
//
//                            appUserService.save(appUser);
//                        }
//                    }
//                })
//                .failureHandler(new AuthenticationFailureHandler() {
//                    @Override
//                    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
//                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized!");
//                    }
//                });
//

    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(customerDetailsService);
//    }
//}

}
