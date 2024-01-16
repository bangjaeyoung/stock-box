package mainproject.stocksite.domain.stock.overall.kosdaq.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * PackageName: mainproject.stocksite.domain.stock.overall.kosdaq.dto
 * FileName: KosdaqStockDto
 * Author: bangjaeyoung
 * Date: 2024-01-13
 * Description:
 */
public class KosdaqStockDto {
    
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IndexResponse {
        private String basDt;
        private String idxNm;
        private String idxCsf;
        private String epyItmsCnt;
        private String clpr;
        private String vs;
        private String fltRt;
        private String mkp;
        private String hipr;
        private String lopr;
        private String trqu;
        private String trPrc;
        private String lstgMrktTotAmt;
        private String lsYrEdVsFltRg;
        private String lsYrEdVsFltRt;
        private String yrWRcrdHgst;
        private String yrWRcrdHgstDt;
        private String yrWRcrdLwst;
        private String yrWRcrdLwstDt;
        private String basPntm;
        private String basIdx;
    }
    
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ListResponse {
        private String basDt;
        private String srtnCd;
        private String isinCd;
        private String itmsNm;
        private String mrktCtg;
        private String clpr;
        private String vs;
        private String fltRt;
        private String mkp;
        private String hipr;
        private String lopr;
        private String trqu;
        private String trPrc;
        private String lstgStCnt;
        private String mrktTotAmt;
    }
}
