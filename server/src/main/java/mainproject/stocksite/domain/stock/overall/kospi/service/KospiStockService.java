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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    
    private final KospiStockMapper kospiStockMapper;
    private final KospiStockListRepository kospiStockListRepository;
    private final KospiStockIndexRepository kospiStockIndexRepository;
    
    public List<KospiStockDto.IndexResponse> getKospiStockIndices() {
        List<KospiStockIndex> kospiStockIndices = kospiStockIndexRepository.findAll();
        verifyExistsData(kospiStockIndices);
        
        log.info("KOSPI-stocks/index");
        
        return kospiStockMapper.kospiStockIndicesToResponseDtos(kospiStockIndices);
    }
    
    
    public List<KospiStockDto.ListResponse> getKospiStockLists() {
        List<KospiStockList> kospiStockLists = kospiStockListRepository.findAll();
        verifyExistsData(kospiStockLists);
        
        log.info("KOSPI-stocks/list");
        
        return kospiStockMapper.kospiStockListsToResponseDtos(kospiStockLists);
    }
    
    private <T> void verifyExistsData(List<T> data) {
        if (data.isEmpty()) {
            throw new BusinessLogicException(ExceptionCode.CANNOT_FOUND_STOCK_DATA);
        }
    }
}
