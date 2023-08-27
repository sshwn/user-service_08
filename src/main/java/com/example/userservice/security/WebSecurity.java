package com.example.userservice.security;

import com.example.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.servlet.Filter;

@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {
    private UserService userService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private Environment env;

    @Autowired
    public WebSecurity(Environment env, UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.env = env;
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    // 권한
    @Override
    protected void configure(HttpSecurity http) throws Exception {  // 권한
        http.csrf().disable();
//        http.authorizeRequests().antMatchers("/users/**").permitAll();
        http.authorizeRequests().antMatchers("/**")    // 인증이 된 상태에서만 추가적인 행동이 가능하므로 제약조건 추가
                .hasIpAddress("192.168.219.103")    // IP
                .and()
                .addFilter(getAuthenticationFilter());


        http.headers().frameOptions().disable();    // frame로 구별된 h2-console 페이지가 정상적으로 나오게하려면 추가
    }

    private AuthenticationFilter getAuthenticationFilter() throws Exception {
        AuthenticationFilter authenticationFilter = new AuthenticationFilter();
        authenticationFilter.setAuthenticationManager(authenticationManager());

        return authenticationFilter;
    }

    // select pwd from users where email=?
    // db_pwd(encrypted) == input_pwd(encrypted)
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {  // 인증
        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);// 사용자가 전달을한 username password를 가지고 로그인 처리
        super.configure(auth);
    }
}
