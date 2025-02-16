# Rate Limiter Implementations

This project demonstrates different rate limiting algorithms implemented in Java. Each implementation provides a different approach to controlling request rates.



## Rate Limiter Algorithms

### 1. Token Bucket
- Maintains a bucket of tokens that refills at a constant rate
- Good for handling burst traffic while maintaining a long-term rate
- Parameters:
  - capacity: Maximum number of tokens
  - refillRate: Number of tokens added per second

### 2. Leaky Bucket
- Uses a queue with fixed capacity
- Processes requests at a constant rate
- Good for smoothing out traffic spikes
- Parameters:
  - capacity: Queue size
  - processingRate: Requests processed per second

### 3. Fixed Window Counter
- Counts requests in fixed time windows
- Resets counter at window boundaries
- Parameters:
  - windowSizeMs: Window size in milliseconds
  - maxRequests: Maximum requests per window

### 4. Sliding Window Log
- Tracks timestamp of each request
- Provides precise rate limiting
- Parameters:
  - windowSizeMs: Window size in milliseconds
  - maxRequests: Maximum requests per window

### 5. Sliding Window Counter
- Combines fixed window with previous window's rate
- Provides smooth rate limiting across windows
- Parameters:
  - windowSizeMs: Window size in milliseconds
  - maxRequests: Maximum requests per window



