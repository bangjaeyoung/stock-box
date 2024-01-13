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
import java.util.stream.Collectors;

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
        List<KospiStockIndex> foundIndices = kospiStockIndexRepository.findAll();
        verifyExistsData(foundIndices);
        
        String theMostRecentBasDt = foundIndices.get(0).getBasDt();
        List<KospiStockIndex> theMostRecentStockIndices = foundIndices.stream()
                .filter(e -> e.getBasDt().equals(theMostRecentBasDt))
                .collect(Collectors.toList());
        
        log.info("KOSPI-stocks/index");
        
        return kospiStockMapper.kospiStockIndicesToResponseDtos(theMostRecentStockIndices);
    }
    
    public List<KospiStockDto.ListResponse> getKospiStockLists() {
        List<KospiStockList> foundLists = kospiStockListRepository.findAll();
        verifyExistsData(foundLists);
        
        String theMostRecentBasDt = foundLists.get(0).getBasDt();
        List<KospiStockList> theMostRecentStockLists = foundLists.stream()
                .filter(e -> e.getBasDt().equals(theMostRecentBasDt))
                .collect(Collectors.toList());
        
        log.info("KOSPI-stocks/list");
        
        return kospiStockMapper.kospiStockListsToResponseDtos(theMostRecentStockLists);
    }
    
    private <T> void verifyExistsData(List<T> data) {
        if (data.isEmpty()) {
            throw new BusinessLogicException(ExceptionCode.CANNOT_FOUND_STOCK_DATA);
        }
    }
}
