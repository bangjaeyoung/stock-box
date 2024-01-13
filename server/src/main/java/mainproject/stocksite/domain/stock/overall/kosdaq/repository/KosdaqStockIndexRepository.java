package mainproject.stocksite.domain.stock.overall.kosdaq.repository;

import mainproject.stocksite.domain.stock.overall.kosdaq.entity.KosdaqStockIndex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * PackageName: mainproject.stocksite.domain.stock.overall.kosdaq.repository
 * FileName: KosdaqStockIndexRepository
 * Author: bangjaeyoung
 * Date: 2024-01-13
 * Description:
 */
public interface KosdaqStockIndexRepository extends JpaRepository<KosdaqStockIndex, Long> {

//    @Query("SELECT COUNT(s.id) > 0 " +
//            "FROM KOSDAQ_STOCK_INDEX s " +
//            "WHERE s.basDt =:basDt")
//    boolean exists(@Param("basDt") String basDt);
}
