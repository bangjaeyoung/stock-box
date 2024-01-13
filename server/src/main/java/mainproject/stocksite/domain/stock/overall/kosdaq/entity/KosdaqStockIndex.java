package mainproject.stocksite.domain.stock.overall.kosdaq.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * PackageName: mainproject.stocksite.domain.stock.overall.kosdaq.entity
 * FileName: KosdaqStockIndex
 * Author: bangjaeyoung
 * Date: 2024-01-13
 * Description: KOSDAQ 주가지수시세
 */
@Getter
@Entity(name = "KOSDAQ_STOCK_INDEX")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KosdaqStockIndex {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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
    
    @Builder
    public KosdaqStockIndex(Long id,
                            String basDt,
                            String idxNm,
                            String idxCsf,
                            String epyItmsCnt,
                            String clpr,
                            String vs,
                            String fltRt,
                            String mkp,
                            String hipr,
                            String lopr,
                            String trqu,
                            String trPrc,
                            String lstgMrktTotAmt,
                            String lsYrEdVsFltRg,
                            String lsYrEdVsFltRt,
                            String yrWRcrdHgst,
                            String yrWRcrdHgstDt,
                            String yrWRcrdLwst,
                            String yrWRcrdLwstDt,
                            String basPntm,
                            String basIdx
    ) {
        
        this.id = id;
        this.basDt = basDt;
        this.idxNm = idxNm;
        this.idxCsf = idxCsf;
        this.epyItmsCnt = epyItmsCnt;
        this.clpr = clpr;
        this.vs = vs;
        this.fltRt = fltRt;
        this.mkp = mkp;
        this.hipr = hipr;
        this.lopr = lopr;
        this.trqu = trqu;
        this.trPrc = trPrc;
        this.lstgMrktTotAmt = lstgMrktTotAmt;
        this.lsYrEdVsFltRg = lsYrEdVsFltRg;
        this.lsYrEdVsFltRt = lsYrEdVsFltRt;
        this.yrWRcrdHgst = yrWRcrdHgst;
        this.yrWRcrdHgstDt = yrWRcrdHgstDt;
        this.yrWRcrdLwst = yrWRcrdLwst;
        this.yrWRcrdLwstDt = yrWRcrdLwstDt;
        this.basPntm = basPntm;
        this.basIdx = basIdx;
    }
}
