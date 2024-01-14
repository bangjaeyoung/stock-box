package mainproject.stocksite.domain.stock.overall.kosdaq.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mainproject.stocksite.domain.stock.overall.cache.CacheService;
import mainproject.stocksite.domain.stock.overall.kosdaq.dto.KosdaqStockDto;
import mainproject.stocksite.domain.stock.overall.kosdaq.entity.KosdaqStockIndex;
import mainproject.stocksite.domain.stock.overall.kosdaq.entity.KosdaqStockList;
import mainproject.stocksite.domain.stock.overall.kosdaq.mapper.KosdaqStockMapper;
import mainproject.stocksite.domain.stock.overall.kosdaq.repository.KosdaqStockIndexRepository;
import mainproject.stocksite.domain.stock.overall.kosdaq.repository.KosdaqStockListRepository;
import mainproject.stocksite.global.exception.BusinessLogicException;
import mainproject.stocksite.global.exception.ExceptionCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static mainproject.stocksite.domain.stock.overall.cache.CacheService.KOSDAQ_STOCK_INDICES_CACHE_KEY;
import static mainproject.stocksite.domain.stock.overall.cache.CacheService.KOSDAQ_STOCK_LISTS_CACHE_KEY;

/**
 * PackageName: mainproject.stocksite.domain.stock.overall.kosdaq.service
 * FileName: KosdaqStockService
 * Author: bangjaeyoung
 * Date: 2024-01-13
 * Description:
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KosdaqStockService {
    
    private final KosdaqStockIndexRepository kosdaqStockIndexRepository;
    private final KosdaqStockListRepository kosdaqStockListRepository;
    private final CacheService cacheService;
    private final KosdaqStockMapper kosdaqStockMapper;
    
    public List<KosdaqStockDto.IndexResponse> getKosdaqStockIndices() {
        if (cacheService.hasCacheValueByKey(KOSDAQ_STOCK_INDICES_CACHE_KEY)) {
            return (List<KosdaqStockDto.IndexResponse>) cacheService.getCacheValueByKey(KOSDAQ_STOCK_INDICES_CACHE_KEY);
        }
        
        List<KosdaqStockIndex> kosdaqStockIndices = kosdaqStockIndexRepository.findAll();
        verifyExistsData(kosdaqStockIndices);
        
        List<KosdaqStockDto.IndexResponse> indexResponses = kosdaqStockMapper.kosdaqStockIndicesToResponseDtos(kosdaqStockIndices);
        cacheService.saveCacheValue(KOSDAQ_STOCK_INDICES_CACHE_KEY, indexResponses);
        
        return indexResponses;
    }
    
    public List<KosdaqStockDto.ListResponse> getKosdaqStockLists() {
        if (cacheService.hasCacheValueByKey(KOSDAQ_STOCK_LISTS_CACHE_KEY)) {
            return (List<KosdaqStockDto.ListResponse>) cacheService.getCacheValueByKey(KOSDAQ_STOCK_LISTS_CACHE_KEY);
        }
        
        List<KosdaqStockList> kosdaqStockLists = kosdaqStockListRepository.findAll();
        verifyExistsData(kosdaqStockLists);
        
        List<KosdaqStockDto.ListResponse> listResponses = kosdaqStockMapper.kosdaqStockListsToResponseDtos(kosdaqStockLists);
        cacheService.saveCacheValue(KOSDAQ_STOCK_LISTS_CACHE_KEY, listResponses);
        
        return listResponses;
    }
    
    private <T> void verifyExistsData(List<T> data) {
        if (data.isEmpty()) {
            throw new BusinessLogicException(ExceptionCode.CANNOT_FOUND_STOCK_DATA);
        }
    }
}
