package mainproject.stocksite.domain.stock.overall.kosdaq.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mainproject.stocksite.domain.stock.overall.kosdaq.dto.KosdaqStockDto;
import mainproject.stocksite.domain.stock.overall.kosdaq.entity.KosdaqStockIndex;
import mainproject.stocksite.domain.stock.overall.kosdaq.entity.KosdaqStockList;
import mainproject.stocksite.domain.stock.overall.kosdaq.mapper.KosdaqStockMapper;
import mainproject.stocksite.domain.stock.overall.kosdaq.repository.KosdaqStockIndexRepository;
import mainproject.stocksite.domain.stock.overall.kosdaq.repository.KosdaqStockListRepository;
import mainproject.stocksite.global.exception.BusinessLogicException;
import mainproject.stocksite.global.exception.ExceptionCode;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

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
    private final RedisTemplate<String, List<KosdaqStockDto.IndexResponse>> indexRedisTemplate;
    private final RedisTemplate<String, List<KosdaqStockDto.ListResponse>> listRedisTemplate;
    private final KosdaqStockMapper kosdaqStockMapper;
    
    public List<KosdaqStockDto.IndexResponse> getKosdaqStockIndices() {
        if (Boolean.TRUE.equals(indexRedisTemplate.hasKey("KOSDAQStockIndices: "))) {
            return indexRedisTemplate.opsForValue().get("KOSDAQStockIndices: ");
        }
        
        List<KosdaqStockIndex> kosdaqStockIndices = kosdaqStockIndexRepository.findAll();
        verifyExistsData(kosdaqStockIndices);
        
        List<KosdaqStockDto.IndexResponse> indexResponses = kosdaqStockMapper.kosdaqStockIndicesToResponseDtos(kosdaqStockIndices);
        indexRedisTemplate.opsForValue().set("KOSDAQStockIndices: ", indexResponses, 24, TimeUnit.HOURS);
        
        return indexResponses;
    }
    
    public List<KosdaqStockDto.ListResponse> getKosdaqStockLists() {
        if (Boolean.TRUE.equals(listRedisTemplate.hasKey("KOSDAQStockLists: "))) {
            return listRedisTemplate.opsForValue().get("KOSDAQStockLists: ");
        }
        
        List<KosdaqStockList> kosdaqStockLists = kosdaqStockListRepository.findAll();
        verifyExistsData(kosdaqStockLists);
        
        List<KosdaqStockDto.ListResponse> listResponses = kosdaqStockMapper.kosdaqStockListsToResponseDtos(kosdaqStockLists);
        listRedisTemplate.opsForValue().set("KOSDAQStockLists: ", listResponses, 24, TimeUnit.HOURS);
        
        return listResponses;
    }
    
    private <T> void verifyExistsData(List<T> data) {
        if (data.isEmpty()) {
            throw new BusinessLogicException(ExceptionCode.CANNOT_FOUND_STOCK_DATA);
        }
    }
}
