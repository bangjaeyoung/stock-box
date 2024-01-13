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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
    
    private final KosdaqStockMapper kosdaqStockMapper;
    private final KosdaqStockListRepository kosdaqStockListRepository;
    private final KosdaqStockIndexRepository kosdaqStockIndexRepository;
    
    public List<KosdaqStockDto.IndexResponse> getKosdaqStockIndices() {
        List<KosdaqStockIndex> foundIndices = kosdaqStockIndexRepository.findAll();
        verifyExistsData(foundIndices);
        
        String theMostRecentBasDt = foundIndices.get(0).getBasDt();
        List<KosdaqStockIndex> theMostRecentStockIndices = foundIndices.stream()
                .filter(e -> e.getBasDt().equals(theMostRecentBasDt))
                .collect(Collectors.toList());
        
        log.info("KOSDAQ-stocks/index");
        
        return kosdaqStockMapper.kosdaqStockIndicesToResponseDtos(theMostRecentStockIndices);
    }
    
    public List<KosdaqStockDto.ListResponse> getKosdaqStockLists() {
        List<KosdaqStockList> foundLists = kosdaqStockListRepository.findAll();
        verifyExistsData(foundLists);
        
        String theMostRecentBasDt = foundLists.get(0).getBasDt();
        List<KosdaqStockList> theMostRecentStockLists = foundLists.stream()
                .filter(e -> e.getBasDt().equals(theMostRecentBasDt))
                .collect(Collectors.toList());
        
        log.info("KOSDAQ-stocks/list");
        
        return kosdaqStockMapper.kosdaqStockListsToResponseDtos(theMostRecentStockLists);
    }
    
    private <T> void verifyExistsData(List<T> data) {
        if (data.isEmpty()) {
            throw new BusinessLogicException(ExceptionCode.CANNOT_FOUND_STOCK_DATA);
        }
    }
}
