package mainproject.stocksite.domain.stock.overall.kosdaq.controller;

import lombok.RequiredArgsConstructor;
import mainproject.stocksite.domain.stock.overall.kosdaq.dto.KosdaqStockDto;
import mainproject.stocksite.domain.stock.overall.kosdaq.service.KosdaqStockService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * PackageName: mainproject.stocksite.domain.stock.overall.kosdaq.controller
 * FileName: KosdaqStockController
 * Author: bangjaeyoung
 * Date: 2024-01-13
 * Description: KOSDAQ 전체 주식정보 조회 API 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/KOSDAQ-stocks")
public class KosdaqStockController {
    
    private final KosdaqStockService kosdaqStockService;
    
    // index: 주가지수시세
    @GetMapping("/index")
    public ResponseEntity<List<KosdaqStockDto.Index>> getIndicesOfKosdaq() {
        List<KosdaqStockDto.Index> response = kosdaqStockService.getKosdaqStockIndices();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    // list: 주식시세
    @GetMapping("/list")
    public ResponseEntity<List<KosdaqStockDto.List>> getListsOfKosdaq() {
        List<KosdaqStockDto.List> response = kosdaqStockService.getKosdaqStockLists();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
