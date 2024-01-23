package mainproject.stocksite.domain.stock.overall.kospi.controller;

import lombok.RequiredArgsConstructor;
import mainproject.stocksite.domain.stock.overall.kospi.dto.KospiStockDto;
import mainproject.stocksite.domain.stock.overall.kospi.service.KospiStockService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * PackageName: mainproject.stocksite.domain.stock.overall.kospi.controller
 * FileName: KospiStockController
 * Author: bangjaeyoung
 * Date: 2024-01-14
 * Description: KOSPI 전체 주식정보 조회 API 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/KOSPI-stocks")
public class KospiStockController {
    
    private final KospiStockService kospiStockService;
    
    // index: 주가지수시세
    @GetMapping("/index")
    public ResponseEntity<List<KospiStockDto.Index>> getIndicesOfKospi() {
        List<KospiStockDto.Index> response = kospiStockService.getKospiStockIndices();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    // list: 주식시세
    @GetMapping("/list")
    public ResponseEntity<List<KospiStockDto.List>> getListsOfKospi() {
        List<KospiStockDto.List> response = kospiStockService.getKospiStockLists();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
