package mainproject.stocksite.domain.stock.overall.kospi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mainproject.stocksite.domain.stock.overall.kospi.dto.KospiStockDto;
import mainproject.stocksite.domain.stock.overall.kospi.entity.KospiStockIndex;
import mainproject.stocksite.domain.stock.overall.kospi.entity.KospiStockList;
import mainproject.stocksite.domain.stock.overall.kospi.mapper.KospiStockMapper;
import mainproject.stocksite.domain.stock.overall.kospi.repository.KospiStockIndexRepository;
import mainproject.stocksite.domain.stock.overall.kospi.repository.KospiStockListRepository;
import mainproject.stocksite.global.exception.BusinessLogicException;
import mainproject.stocksite.global.exception.ExceptionCode;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * PackageName: mainproject.stocksite.domain.stock.overall.kospi.service
 * FileName: KospiStockService
 * Author: bangjaeyoung
 * Date: 2024-01-14
 * Description: KOSPI 주식 데이터 비즈니스 로직 + 캐시 체크 로직
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KospiStockService {
    public static final String KOSPI_STOCK_INDEX_CACHE_KEY = "KOSPIStockIndices: ";
    public static final String KOSPI_STOCK_LIST_CACHE_KEY = "KOSPIStockLists: ";
    
    private final KospiStockIndexRepository kospiStockIndexRepository;
    private final KospiStockListRepository kospiStockListRepository;
    private final RedisTemplate<String, List<KospiStockDto.IndexResponse>> indexRedisTemplate;
    private final RedisTemplate<String, List<KospiStockDto.ListResponse>> listRedisTemplate;
    private final KospiStockMapper kospiStockMapper;
    
    public List<KospiStockDto.IndexResponse> getKospiStockIndices() {
        if (Boolean.TRUE.equals(indexRedisTemplate.hasKey(KOSPI_STOCK_INDEX_CACHE_KEY))) {
            return indexRedisTemplate.opsForValue().get(KOSPI_STOCK_INDEX_CACHE_KEY);
        }
        
        List<KospiStockIndex> kospiStockIndices = kospiStockIndexRepository.findAll();
        verifyExistsData(kospiStockIndices);
        
        List<KospiStockDto.IndexResponse> indexResponses = kospiStockMapper.kospiStockIndicesToResponseDtos(kospiStockIndices);
        indexRedisTemplate.opsForValue().set(KOSPI_STOCK_INDEX_CACHE_KEY, indexResponses, 24, TimeUnit.HOURS);
        
        return indexResponses;
    }
    
    
    public List<KospiStockDto.ListResponse> getKospiStockLists() {
        if (Boolean.TRUE.equals(listRedisTemplate.hasKey(KOSPI_STOCK_LIST_CACHE_KEY))) {
            return listRedisTemplate.opsForValue().get(KOSPI_STOCK_LIST_CACHE_KEY);
        }
        
        List<KospiStockList> kospiStockLists = kospiStockListRepository.findAll();
        verifyExistsData(kospiStockLists);
        
        List<KospiStockDto.ListResponse> listResponses = kospiStockMapper.kospiStockListsToResponseDtos(kospiStockLists);
        listRedisTemplate.opsForValue().set(KOSPI_STOCK_LIST_CACHE_KEY, listResponses, 24, TimeUnit.HOURS);
        
        return listResponses;
    }
    
    private <T> void verifyExistsData(List<T> data) {
        if (data.isEmpty()) {
            throw new BusinessLogicException(ExceptionCode.CANNOT_FOUND_STOCK_DATA);
        }
    }
}
