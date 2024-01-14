package mainproject.stocksite.domain.stock.overall.kospi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mainproject.stocksite.domain.stock.overall.cache.CacheService;
import mainproject.stocksite.domain.stock.overall.kospi.dto.KospiStockDto;
import mainproject.stocksite.domain.stock.overall.kospi.entity.KospiStockIndex;
import mainproject.stocksite.domain.stock.overall.kospi.entity.KospiStockList;
import mainproject.stocksite.domain.stock.overall.kospi.mapper.KospiStockMapper;
import mainproject.stocksite.domain.stock.overall.kospi.repository.KospiStockIndexRepository;
import mainproject.stocksite.domain.stock.overall.kospi.repository.KospiStockListRepository;
import mainproject.stocksite.global.exception.BusinessLogicException;
import mainproject.stocksite.global.exception.ExceptionCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static mainproject.stocksite.domain.stock.overall.cache.CacheService.KOSPI_STOCK_INDICES_CACHE_KEY;
import static mainproject.stocksite.domain.stock.overall.cache.CacheService.KOSPI_STOCK_LISTS_CACHE_KEY;

/**
 * PackageName: mainproject.stocksite.domain.stock.overall.kospi.service
 * FileName: KospiStockService
 * Author: bangjaeyoung
 * Date: 2024-01-14
 * Description:
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KospiStockService {
    
    private final KospiStockIndexRepository kospiStockIndexRepository;
    private final KospiStockListRepository kospiStockListRepository;
    private final CacheService cacheService;
    private final KospiStockMapper kospiStockMapper;
    
    public List<KospiStockDto.IndexResponse> getKospiStockIndices() {
        if (cacheService.hasCacheValueByKey(KOSPI_STOCK_INDICES_CACHE_KEY)) {
            return (List<KospiStockDto.IndexResponse>) cacheService.getCacheValueByKey(KOSPI_STOCK_INDICES_CACHE_KEY);
        }
        
        List<KospiStockIndex> kospiStockIndices = kospiStockIndexRepository.findAll();
        verifyExistsData(kospiStockIndices);
        
        List<KospiStockDto.IndexResponse> indexResponses = kospiStockMapper.kospiStockIndicesToResponseDtos(kospiStockIndices);
        cacheService.saveCacheValue(KOSPI_STOCK_INDICES_CACHE_KEY, indexResponses);
        
        return indexResponses;
    }
    
    
    public List<KospiStockDto.ListResponse> getKospiStockLists() {
        if (cacheService.hasCacheValueByKey(KOSPI_STOCK_LISTS_CACHE_KEY)) {
            return (List<KospiStockDto.ListResponse>) cacheService.getCacheValueByKey(KOSPI_STOCK_LISTS_CACHE_KEY);
        }
        
        List<KospiStockList> kospiStockLists = kospiStockListRepository.findAll();
        verifyExistsData(kospiStockLists);
        
        List<KospiStockDto.ListResponse> listResponses = kospiStockMapper.kospiStockListsToResponseDtos(kospiStockLists);
        cacheService.saveCacheValue(KOSPI_STOCK_LISTS_CACHE_KEY, listResponses);
        
        return listResponses;
    }
    
    private <T> void verifyExistsData(List<T> data) {
        if (data.isEmpty()) {
            throw new BusinessLogicException(ExceptionCode.CANNOT_FOUND_STOCK_DATA);
        }
    }
}
