
// package com.example.payment.ratefilter;

// import io.github.bucket4j.*;
// import jakarta.servlet.*;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;

// import java.io.IOException;
// import java.time.Duration;
// import java.util.Map;
// import java.util.concurrent.ConcurrentHashMap;

// public class RateLimitFilter implements Filter {

//     private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

//     @Override
//     public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//             throws IOException, ServletException {

//         HttpServletRequest httpRequest = (HttpServletRequest) request;
//         HttpServletResponse httpResponse = (HttpServletResponse) response;

//         String ip = httpRequest.getRemoteAddr();
//         Bucket bucket = cache.computeIfAbsent(ip, this::newBucket);

//         if (bucket.tryConsume(1)) {
//             chain.doFilter(request, response);
//         } else {
//             httpResponse.setStatus(429);
//             httpResponse.getWriter().write("Too Many Requests - Try again later");
//         }
//     }

//     private Bucket newBucket(String key) {
//         Refill refill = Refill.greedy(7, Duration.ofMinutes(1)); // 7 requests per minute
//         Bandwidth limit = Bandwidth.classic(7, refill);
//         return Bucket.builder().addLimit(limit).build();
//     }
// }
