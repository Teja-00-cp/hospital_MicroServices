
// package com.example.order.ratefilter;
// import org.springframework.boot.web.servlet.FilterRegistrationBean;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;

// @Configuration
// public class FilterConfig {

//     @Bean
//     public FilterRegistrationBean<RateLimitFilter> rateLimitFilterRegistration() {
//         FilterRegistrationBean<RateLimitFilter> registrationBean = new FilterRegistrationBean<>();
//         registrationBean.setFilter(new RateLimitFilter()); 
//         registrationBean.addUrlPatterns("/order/*");
//         registrationBean.setOrder(1);
//         return registrationBean;
//     }
// }
