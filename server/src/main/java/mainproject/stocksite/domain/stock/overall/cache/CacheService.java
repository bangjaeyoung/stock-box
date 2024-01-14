package mainproject.stocksite.domain.stock.overall.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    
    private final RedisTemplate<String, ?> redisTemplate;
    
    public void deleteCacheByKey(String cacheKey) {
        redisTemplate.delete(cacheKey);
    }
}
