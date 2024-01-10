package mainproject.stocksite.domain.news.service;

import lombok.RequiredArgsConstructor;
import mainproject.stocksite.domain.news.dto.NewsDto;
import mainproject.stocksite.global.config.OpenApiSecretInfo;
import mainproject.stocksite.global.exception.BusinessLogicException;
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

import static mainproject.stocksite.global.exception.ExceptionCode.OPEN_API_SERVER_ERROR;

@Service
@Transactional
@RequiredArgsConstructor
public class NewsService {
    private static final String NAVER_DEFAULT_URL = "https://openapi.naver.com/v1/search/news";
    
    private final OpenApiSecretInfo openApiSecretInfo;
    private final RestTemplate restTemplate;
    
    public NewsDto.Response searchStockNews(
            String search,
            int count,
            int start,
            String sort) {
        
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
        
        return response.getBody();
    }
    
    private HttpHeaders getBaseHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", openApiSecretInfo.getNaverClientId());
        headers.set("X-Naver-Client-Secret", openApiSecretInfo.getNaverClientSecret());
        headers.set("Content-Type", "application/json");
        return headers;
    }
}
