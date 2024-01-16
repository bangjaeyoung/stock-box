package mainproject.stocksite.domain.news.controller;

import lombok.RequiredArgsConstructor;
import mainproject.stocksite.domain.news.dto.NewsDto;
import mainproject.stocksite.domain.news.service.NewsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/stock-news")
public class NewsController {
    
    private final NewsService newsService;
    
    @GetMapping
    public ResponseEntity<NewsDto.Response> getStockNews(
            @RequestParam String search,
            @RequestParam @Min(1) @Max(100) int count,
            @RequestParam @Min(1) @Max(100) int start,
            @RequestParam String sort) {
        
        NewsDto.Response response = newsService.searchStockNews(search, count, start, sort);
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
