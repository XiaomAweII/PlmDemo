@Component
@Order(1)
public class UrlDebounceFilter implements Filter {
    
    private final RedisTemplate<String, String> redisTemplate;
    private final DebounceProperties debounceProperties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public UrlDebounceFilter(RedisTemplate<String, String> redisTemplate, 
                           DebounceProperties debounceProperties) {
        this.redisTemplate = redisTemplate;
        this.debounceProperties = debounceProperties;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestUri = httpRequest.getRequestURI();
        DebounceProperties.UrlConfig urlConfig = getMatchedUrlConfig(requestUri);

        if (urlConfig == null || !urlConfig.isEnabled()) {
            chain.doFilter(request, response);
            return;
        }

        String userId = httpRequest.getHeader("userId");
        String redisKey = String.format("debounce:%s:%s", requestUri, userId);

        boolean locked = false;
        try {
            locked = redisTemplate.opsForValue().setIfAbsent(redisKey, "1", 
                    urlConfig.getTime(), TimeUnit.MILLISECONDS);

            if (!locked) {
                Result<?> result = Result.error(urlConfig.getMessage());
                httpResponse.setContentType("application/json;charset=UTF-8");
                httpResponse.getWriter().write(JsonUtil.toJson(result));
                return;
            }

            chain.doFilter(request, response);
        } finally {
            if (locked) {
                redisTemplate.delete(redisKey);
            }
        }
    }

    private DebounceProperties.UrlConfig getMatchedUrlConfig(String requestUri) {
        return debounceProperties.getUrls().entrySet().stream()
                .filter(entry -> pathMatcher.match(entry.getKey(), requestUri))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(null);
    }
}