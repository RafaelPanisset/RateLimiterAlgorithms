package com.ratelimiter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimiters {
	
	public static class TokenBucket {
		private final long capacity;
		private final double refillrate;
		private double currentTokens;
		private long lastRefillTimestamp;
		
		public TokenBucket(long capacity, double refillrate) {
			this.capacity = capacity;
			this.refillrate = refillrate;
			this.currentTokens = capacity;
			this.lastRefillTimestamp = System.currentTimeMillis();
		}
		
        public synchronized boolean tryConsume() {
        	refill();
        	if (currentTokens >= 1) {
        		currentTokens--;
        		return true;
        	}
        	return false;
        }
        
        private void refill() {
        	long now = System.currentTimeMillis();
        	double tokensToAdd = (now - lastRefillTimestamp) * refillrate / 1000;
        	currentTokens = Math.min(capacity, currentTokens + tokensToAdd);
        	lastRefillTimestamp = now;
        }
	}
	public static class LeakyBucket {
		private final Queue<Long> bucket;
        private final long capacity;
        private final long processingRate;
        private long lastProcessedTime;
       
        public LeakyBucket(long capacity, long processingRate) {
            this.bucket = new LinkedList<>();
            this.capacity = capacity;
            this.processingRate = processingRate;
            this.lastProcessedTime = System.currentTimeMillis();
        }
        
        public synchronized boolean tryConsume() {
            processRequests();
            
            if (bucket.size() < capacity) {
                bucket.offer(System.currentTimeMillis());
                return true;
            }
            
            return false;
        }
        private void processRequests() {
            long now = System.currentTimeMillis();
            long timeElapsed = now - lastProcessedTime;
            long requestsToProcess = timeElapsed * processingRate / 1000;
            
            while (requestsToProcess > 0 && !bucket.isEmpty()) {
                bucket.poll();
                requestsToProcess--;
            }
            
            lastProcessedTime = now;
        }
		
	}
    public static class FixedWindowCounter {
    	  private final long windowSizeMs;
          private final long maxRequests;
          private long currentWindow;
          private AtomicInteger counter;
          
          public FixedWindowCounter(long windowSizeMs, long maxRequests) {
              this.windowSizeMs = windowSizeMs;
              this.maxRequests = maxRequests;
              this.currentWindow = System.currentTimeMillis() / windowSizeMs;
              this.counter = new AtomicInteger(0);
          }
          public boolean tryConsume() {
              long currentTimeWindow = System.currentTimeMillis() / windowSizeMs;
              
              if (currentTimeWindow > currentWindow) {
                  currentWindow = currentTimeWindow;
                  counter = new AtomicInteger(0);
              }
              
              return counter.incrementAndGet() <= maxRequests;
          }
          
    }
    public static class SlidingWindowLog {
        private final Queue<Long> log;
        private final long windowSizeMs;
        private final long maxRequests;
        
        public SlidingWindowLog(long windowSizeMs, long maxRequests) {
            this.log = new LinkedList<>();
            this.windowSizeMs = windowSizeMs;
            this.maxRequests = maxRequests;
        }
        
        public synchronized boolean tryConsume() {
            long currentTime = System.currentTimeMillis();
            long windowStart = currentTime - windowSizeMs;
            
            while (!log.isEmpty() && log.peek() <= windowStart) {
                log.poll();
            }
            
            if (log.size() < maxRequests) {
                log.offer(currentTime);
                return true;
            }
            
            return false;
        }
    }
    public static class SlidingWindowCounter {
        private final Map<Long, Integer> windowCounts;
        private final long windowSizeMs;
        private final long maxRequests;

        public SlidingWindowCounter(long windowSizeMs, long maxRequests) {
            this.windowCounts = new ConcurrentHashMap<>();
            this.windowSizeMs = windowSizeMs;
            this.maxRequests = maxRequests;
        }

        public boolean tryConsume() {
            long currentTime = System.currentTimeMillis();
            long currentWindow = currentTime / windowSizeMs;
            double overlap = (currentTime % windowSizeMs) / (double) windowSizeMs;
            
            // Clean up old windows
            windowCounts.keySet().removeIf(window -> window < currentWindow - 1);
            
            // Calculate current request count
            int previousCount = windowCounts.getOrDefault(currentWindow - 1, 0);
            int currentCount = windowCounts.getOrDefault(currentWindow, 0);
            double weightedCount = previousCount * (1 - overlap) + currentCount;
            
            if (weightedCount < maxRequests) {
                windowCounts.merge(currentWindow, 1, Integer::sum);
                return true;
            }
            
            return false;
        }
    }

    
}
