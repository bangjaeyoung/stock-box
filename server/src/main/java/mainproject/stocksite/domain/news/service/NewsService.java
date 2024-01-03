package mainproject.stocksite.domain.news.service;

import lombok.RequiredArgsConstructor;
import mainproject.stocksite.domain.news.dto.NewsDto;
import mainproject.stocksite.global.config.OpenApiSecretInfo;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class NewsService {
    private static final String NAVER_DEFAULT_URL = "https://openapi.naver.com/v1/search/";
    
    private final OpenApiSecretInfo openApiSecretInfo;
    private final RestTemplate restTemplate;
    
    private HttpHeaders baseHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", openApiSecretInfo.getNaverClientId());
        headers.set("X-Naver-Client-Secret", openApiSecretInfo.getNaverClientSecret());
        
        return headers;
    }
    
    public NewsDto.Response searchStockNews(
            String search,
            int count,
            int start,
            String sort) {
        
        HttpHeaders requestHeaders = baseHeaders();
        requestHeaders.set("Content-Type", "application/json");
        
        HttpEntity<String> requestMessage = new HttpEntity<>(requestHeaders);
        String url = NAVER_DEFAULT_URL + "news";
        
        UriComponents uriBuilder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("query", search)
                .queryParam("display", count)
                .queryParam("start", start)
                .queryParam("sort", sort)
                .build();
        
        ResponseEntity<NewsDto.Response> response = restTemplate.exchange(
                uriBuilder.toString(),
                HttpMethod.GET,
                requestMessage,
                NewsDto.Response.class
        );
        
        return response.getBody();
    }
}
