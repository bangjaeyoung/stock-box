package mainproject.stocksite.domain.stock.overall.kospi;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Entity(name = "KOSPI_STOCK_LIST")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KospiStockList {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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
    
    @Builder
    public KospiStockList(Long id,
                          String basDt,
                          String srtnCd,
                          String isinCd,
                          String itmsNm,
                          String mrktCtg,
                          String clpr,
                          String vs,
                          String fltRt,
                          String mkp,
                          String hipr,
                          String lopr,
                          String trqu,
                          String trPrc,
                          String lstgStCnt,
                          String mrktTotAmt
    ) {
        
        this.id = id;
        this.basDt = basDt;
        this.srtnCd = srtnCd;
        this.isinCd = isinCd;
        this.itmsNm = itmsNm;
        this.mrktCtg = mrktCtg;
        this.clpr = clpr;
        this.vs = vs;
        this.fltRt = fltRt;
        this.mkp = mkp;
        this.hipr = hipr;
        this.lopr = lopr;
        this.trqu = trqu;
        this.trPrc = trPrc;
        this.lstgStCnt = lstgStCnt;
        this.mrktTotAmt = mrktTotAmt;
    }
}
