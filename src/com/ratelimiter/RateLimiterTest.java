package com.ratelimiter;

public class RateLimiterTest {
    public static void main(String[] args) throws InterruptedException {
        testTokenBucket();
        testLeakyBucket();
        testFixedWindowCounter();
        testSlidingWindowLog();
        testSlidingWindowCounter();
    }
    
    private static void testTokenBucket() throws InterruptedException {
        System.out.println("\n=== Testing Token Bucket ===");
        RateLimiters.TokenBucket limiter = new RateLimiters.TokenBucket(5, 2); // 5 tokens, 2 tokens/second
        
        // Try to consume 7 tokens (should allow only 5)
        for (int i = 1; i <= 7; i++) {
            boolean allowed = limiter.tryConsume();
            System.out.println("Request " + i + ": " + (allowed ? "Allowed" : "Blocked"));
        }
        
        // Wait for refill
        System.out.println("Waiting 2 seconds for token refill...");
        Thread.sleep(2000);
        
        // Try again
        boolean allowed = limiter.tryConsume();
        System.out.println("After wait: " + (allowed ? "Allowed" : "Blocked"));
    }
    
    private static void testLeakyBucket() throws InterruptedException {
        System.out.println("\n=== Testing Leaky Bucket ===");
        RateLimiters.LeakyBucket limiter = new RateLimiters.LeakyBucket(3, 2); // Queue size 3, 2 requests/second
        
        // Try to add 5 requests (should only accept 3)
        for (int i = 1; i <= 5; i++) {
            boolean allowed = limiter.tryConsume();
            System.out.println("Request " + i + ": " + (allowed ? "Allowed" : "Blocked"));
        }
        
        // Wait for processing
        System.out.println("Waiting 2 seconds for request processing...");
        Thread.sleep(2000);
        
        // Try again
        boolean allowed = limiter.tryConsume();
        System.out.println("After wait: " + (allowed ? "Allowed" : "Blocked"));
    }
    
    private static void testFixedWindowCounter() throws InterruptedException {
        System.out.println("\n=== Testing Fixed Window Counter ===");
        RateLimiters.FixedWindowCounter limiter = new RateLimiters.FixedWindowCounter(1000, 3); // 1 second window, 3 requests
        
        // Try 4 requests in quick succession
        for (int i = 1; i <= 4; i++) {
            boolean allowed = limiter.tryConsume();
            System.out.println("Request " + i + ": " + (allowed ? "Allowed" : "Blocked"));
        }
        
        // Wait for new window
        System.out.println("Waiting 1 second for new window...");
        Thread.sleep(1000);
        
        // Try again
        boolean allowed = limiter.tryConsume();
        System.out.println("After wait: " + (allowed ? "Allowed" : "Blocked"));
    }
    
    private static void testSlidingWindowLog() throws InterruptedException {
        System.out.println("\n=== Testing Sliding Window Log ===");
        RateLimiters.SlidingWindowLog limiter = new RateLimiters.SlidingWindowLog(1000, 3); // 1 second window, 3 requests
        
        // Try 4 requests in quick succession
        for (int i = 1; i <= 4; i++) {
            boolean allowed = limiter.tryConsume();
            System.out.println("Request " + i + ": " + (allowed ? "Allowed" : "Blocked"));
        }
        
        // Wait for partial window
        System.out.println("Waiting 500ms...");
        Thread.sleep(500);
        
        // Try again
        boolean allowed = limiter.tryConsume();
        System.out.println("After wait: " + (allowed ? "Allowed" : "Blocked"));
    }
    
    private static void testSlidingWindowCounter() throws InterruptedException {
        System.out.println("\n=== Testing Sliding Window Counter ===");
        RateLimiters.SlidingWindowCounter limiter = new RateLimiters.SlidingWindowCounter(1000, 3); // 1 second window, 3 requests
        
        // Try 4 requests in quick succession
        for (int i = 1; i <= 4; i++) {
            boolean allowed = limiter.tryConsume();
            System.out.println("Request " + i + ": " + (allowed ? "Allowed" : "Blocked"));
        }
        
        // Wait for partial window
        System.out.println("Waiting 500ms...");
        Thread.sleep(500);
        
        // Try again
        boolean allowed = limiter.tryConsume();
        System.out.println("After wait: " + (allowed ? "Allowed" : "Blocked"));
    }
}