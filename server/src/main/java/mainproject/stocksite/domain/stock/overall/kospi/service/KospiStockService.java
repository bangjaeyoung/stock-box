package mainproject.stocksite.domain.stock.overall.kospi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mainproject.stocksite.domain.stock.overall.kospi.cache.updater.KospiStockIndexUpdater;
import mainproject.stocksite.domain.stock.overall.kospi.cache.updater.KospiStockListUpdater;
import mainproject.stocksite.domain.stock.overall.kospi.dto.KospiStockDto;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static mainproject.stocksite.domain.stock.overall.kospi.cache.updater.KospiStockIndexUpdater.KOSPI_STOCK_INDEX_CACHE_KEY;
import static mainproject.stocksite.domain.stock.overall.kospi.cache.updater.KospiStockListUpdater.KOSPI_STOCK_LIST_CACHE_KEY;

/**
 * PackageName: mainproject.stocksite.domain.stock.overall.kospi.service
 * FileName: KospiStockService
 * Author: bangjaeyoung
 * Date: 2024-01-14
 * Description: KOSPI 주식 데이터 캐시 체크 로직 / 캐시 미스 시, 캐시 데이터 저장, Open API 호출 및 응답 데이터 반환
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KospiStockService {
    
    private final RedisTemplate<String, List<KospiStockDto.Index>> indexRedisTemplate;
    private final RedisTemplate<String, List<KospiStockDto.List>> listRedisTemplate;
    private final KospiStockIndexUpdater kospiStockIndexUpdater;
    private final KospiStockListUpdater kospiStockListUpdater;
    
    public List<KospiStockDto.Index> getKospiStockIndices() {
        if (Boolean.TRUE.equals(indexRedisTemplate.hasKey(KOSPI_STOCK_INDEX_CACHE_KEY))) {
            return indexRedisTemplate.opsForValue().get(KOSPI_STOCK_INDEX_CACHE_KEY);
        }
        
        return kospiStockIndexUpdater.updateKospiStockIndices();
    }
    
    public List<KospiStockDto.List> getKospiStockLists() {
        if (Boolean.TRUE.equals(listRedisTemplate.hasKey(KOSPI_STOCK_LIST_CACHE_KEY))) {
            return listRedisTemplate.opsForValue().get(KOSPI_STOCK_LIST_CACHE_KEY);
        }
        
        return kospiStockListUpdater.updateKospiStockLists();
    }
}
