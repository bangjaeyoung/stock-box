package mainproject.stocksite.domain.stock.overall.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

/**
 * PackageName: mainproject.stocksite.domain.stock.overall.cache
 * FileName: CacheService
 * Author: bangjaeyoung
 * Date: 2024-01-14
 * Description:
 */
@Service
@Transactional
@RequiredArgsConstructor
public class CacheService {
    public static final String KOSDAQ_STOCK_INDICES_CACHE_KEY = "KOSDAQStockIndices: ";
    public static final String KOSDAQ_STOCK_LISTS_CACHE_KEY = "KOSDAQStockLists: ";
    public static final String KOSPI_STOCK_INDICES_CACHE_KEY = "KOSPIStockIndices: ";
    public static final String KOSPI_STOCK_LISTS_CACHE_KEY = "KOSPIStockLists: ";
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    public Object getCacheValueByKey(String cacheKey) {
        return redisTemplate.opsForValue().get(cacheKey);
    }
    
    public boolean hasCacheValueByKey(String cacheKey) {
        return getCacheValueByKey(cacheKey) != null;
    }
    
    public void deleteCacheByKey(String cacheKey) {
        redisTemplate.delete(cacheKey);
    }
    
    public <T> void saveCacheValue(String cacheKey, T cacheValue) {
        redisTemplate.opsForValue()
                .set(
                        cacheKey,
                        cacheValue,
                        24,
                        TimeUnit.HOURS
                );
    }
}
