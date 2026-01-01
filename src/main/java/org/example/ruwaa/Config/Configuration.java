package org.example.ruwaa.Config;

import lombok.RequiredArgsConstructor;
import org.example.ruwaa.Config.JWT.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@org.springframework.context.annotation.Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class Configuration
{

    private final JwtFilter jwtFilter;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/signup/customer",
                                "/api/v1/auth/signup/expert",
                                "/api/v1/auth/login",
                                "/api/v1/auth/admin",
                                "/api/v1/payment/thanks",
                                "/api/v1/chat/create",
                                "/api/v1/message/display-chat/{chat_id}",
                                "/api/v1/message/send/{chat_id}",
                                "/api/v1/post/free-feed",
                                "/api/v1/post/work-feed",
                                "/api/v1/card/**",
                                "/api/v1/category/get",
                                "/api/v1/category/get/{name}").permitAll()


                        .requestMatchers("/api/v1/review/finished",
                                "/api/v1/review/unfinished",
                                "/api/v1/post/add/learning",
                                "/api/v1/review/reject-review/{review_id}",
                                "/api/v1/review/submit/{reviewId}",
                                "/api/v1/expert/discount/{discountPercentage}/{date}",
                                "/api/v1/review/get-reviews-requests",
                                "/api/v1/review/accept-review/{reviewId}",
                                "/api/v1/review/reject-all-reviews",
                                "/api/v1/review/get-pending-review",
                                "/api/v1/post/update/learning/{postId}",
                                "/api/v1/expert/available").hasAuthority("EXPERT")
                                

                        .requestMatchers("/api/v1/expert/most-active/category/{category}",
                                "/api/v1/expert/get-high-rated-by-category/{category}",
                                "/api/v1/post/create",
                                "/api/v1/review/request-review/{postId}/{expertId}",
                                "/api/v1/subscription/subscribe/month",
                                "/api/v1/subscription/subscribe/3month",
                                "/api/v1/subscription/subscribe/6month",
                                "/api/v1/card/add",
                                "/api/v1/post/add/work",
                                "/api/v1/post/improve-attachment/{post_id}",
                                "/api/v1/expert/get-expert-by-category/{category}",
                                "/api/v1/expert/get-high-rated-by-category/{category}",
                                "/api/v1/review/get-send-requests",
                                "/api/v1/chat/open/{review_id}",
                                "/api/v1/review/get-send-requests"
                                ,"/api/v1/expert/available","/api/v1/expert/busy",
                                "/api/v1/review/get-send-requests",
                                "/api/v1/review/get-completed-reviews/{postId}",
                                "/api/v1/post/update/work/{postId}",
                                "/api/v1/post/review/{post_id}").hasAuthority("CUSTOMER")





                        .requestMatchers("/api/v1/auth/me",
                                "/api/v1/post/my-posts",
                                "/api/v1/auth/me",
                                "/api/v1/post/view/learning/{post_id}",
                                "/api/v1/auth/update-customer",
                                "/api/v1/auth/update-expert",
                                "/api/v1/chat/close/{id}",
                                "/api/v1/expert/most-active/category/{category}",
                                "/api/v1/post/subscription-feed",
                                "/api/v1/message/send/{chat_id}",
                                "/api/v1/message/display-chat/{chat_id}",
                                "/api/v1/post/delete/{postId}",
                                "/api/v1/post/public/{postId}",
                                "/api/v1/post/private/{postId}").hasAnyAuthority("CUSTOMER", "EXPERT")

                        .requestMatchers("/api/v1/category/create",
                                "/api/v1/category/update/{id}",
                                "/api/v1/category/delete/{id}",
                                "/api/v1/chat/get",
                                "/api/v1/customer/get","/api/v1/expert/subscription-earning/**"
                               ,"/api/v1/expert/activate","/api/v1/expert/reject"
                                ,"/api/v1/auth/get-all","/api/v1/expert/get-all",
                                         "/api/v1/customer/get",
                                          "/api/v1/expert/subscription-earning/**",
                                "/api/v1/post/get-all"
                        ).hasAuthority("ADMIN")


                        .anyRequest().authenticated())

                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
