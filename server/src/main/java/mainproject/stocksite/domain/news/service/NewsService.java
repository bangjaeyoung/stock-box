package mainproject.stocksite.domain.news.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mainproject.stocksite.domain.news.dto.NewsDto;
import mainproject.stocksite.global.config.OpenApiSecretInfo;
import mainproject.stocksite.global.exception.BusinessLogicException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static mainproject.stocksite.global.exception.ExceptionCode.OPEN_API_SERVER_ERROR;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NewsService {
    private static final String NAVER_DEFAULT_URL = "https://openapi.naver.com/v1/search/news";
    
    private final RedisTemplate<String, NewsDto.Response> redisTemplate;
    private final OpenApiSecretInfo openApiSecretInfo;
    private final RestTemplate restTemplate;
    
    public NewsDto.Response searchStockNews(
            String search,
            int count,
            int start,
            String sort) {
        
        String cacheKey = "stockNews: " + search;
        NewsDto.Response cacheValue = redisTemplate.opsForValue().get(cacheKey);
        
        // 캐시 히트일 경우, 저장된 캐시 데이터 반환
        if (cacheValue != null) {
            log.info("Redis cache existed!");
            return cacheValue;
        }
        
        log.info("Redis cache doesn't existed!");
        
        HttpEntity<String> requestMessage = new HttpEntity<>(getBaseHeaders());
        
        UriComponents uriBuilder = UriComponentsBuilder.fromHttpUrl(NAVER_DEFAULT_URL)
                .queryParam("query", search)
                .queryParam("display", count)
                .queryParam("start", start)
                .queryParam("sort", sort)
                .build();
        
        ResponseEntity<NewsDto.Response> response;
        try {
            response = restTemplate.exchange(
                    uriBuilder.toString(),
                    HttpMethod.GET,
                    requestMessage,
                    NewsDto.Response.class
            );
        } catch (HttpClientErrorException | HttpServerErrorException openApiServerError) {
            throw new BusinessLogicException(OPEN_API_SERVER_ERROR);
        }
        
        // 캐시 미스일 경우, 캐시 데이터 저장
        NewsDto.Response newsResponse = verifiedNewsResponse(response);
        redisTemplate.opsForValue()
                .set(
                        cacheKey,
                        newsResponse,
                        5,
                        TimeUnit.MINUTES
                );
        
        return newsResponse;
    }
    
    private HttpHeaders getBaseHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", openApiSecretInfo.getNaverClientId());
        headers.set("X-Naver-Client-Secret", openApiSecretInfo.getNaverClientSecret());
        headers.set("Content-Type", "application/json");
        return headers;
    }
    
    private NewsDto.Response verifiedNewsResponse(ResponseEntity<NewsDto.Response> response) {
        return Optional.ofNullable(response.getBody())
                .orElseThrow(() -> new BusinessLogicException(OPEN_API_SERVER_ERROR));
    }
}
