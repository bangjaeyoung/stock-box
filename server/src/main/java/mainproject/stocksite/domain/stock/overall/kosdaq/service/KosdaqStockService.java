package mainproject.stocksite.domain.stock.overall.kosdaq.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mainproject.stocksite.domain.stock.overall.kosdaq.cache.updater.KosdaqStockIndexUpdater;
import mainproject.stocksite.domain.stock.overall.kosdaq.cache.updater.KosdaqStockListUpdater;
import mainproject.stocksite.domain.stock.overall.kosdaq.dto.KosdaqStockDto;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static mainproject.stocksite.domain.stock.overall.kosdaq.cache.updater.KosdaqStockIndexUpdater.KOSDAQ_STOCK_INDEX_CACHE_KEY;
import static mainproject.stocksite.domain.stock.overall.kosdaq.cache.updater.KosdaqStockListUpdater.KOSDAQ_STOCK_LIST_CACHE_KEY;

/**
 * PackageName: mainproject.stocksite.domain.stock.overall.kosdaq.service
 * FileName: KosdaqStockService
 * Author: bangjaeyoung
 * Date: 2024-01-13
 * Description: KOSDAQ 주식 데이터 캐시 체크 로직 / 캐시 미스 시, 캐시 데이터 저장, Open API 호출 및 응답 데이터 반환
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KosdaqStockService {
    
    private final RedisTemplate<String, List<KosdaqStockDto.Index>> indexRedisTemplate;
    private final RedisTemplate<String, List<KosdaqStockDto.List>> listRedisTemplate;
    private final KosdaqStockIndexUpdater kosdaqStockIndexUpdater;
    private final KosdaqStockListUpdater kosdaqStockListUpdater;
    
    public List<KosdaqStockDto.Index> getKosdaqStockIndices() {
        if (Boolean.TRUE.equals(indexRedisTemplate.hasKey(KOSDAQ_STOCK_INDEX_CACHE_KEY))) {
            return indexRedisTemplate.opsForValue().get(KOSDAQ_STOCK_INDEX_CACHE_KEY);
        }
        
        return kosdaqStockIndexUpdater.updateKosdaqStockIndices();
    }
    
    public List<KosdaqStockDto.List> getKosdaqStockLists() {
        if (Boolean.TRUE.equals(listRedisTemplate.hasKey(KOSDAQ_STOCK_LIST_CACHE_KEY))) {
            return listRedisTemplate.opsForValue().get(KOSDAQ_STOCK_LIST_CACHE_KEY);
        }
        
        return kosdaqStockListUpdater.updateKosdaqStockLists();
    }
}
