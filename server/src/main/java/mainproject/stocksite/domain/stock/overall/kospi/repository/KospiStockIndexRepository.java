package mainproject.stocksite.domain.stock.overall.kospi.repository;

import mainproject.stocksite.domain.stock.overall.kospi.entity.KospiStockIndex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * PackageName: mainproject.stocksite.domain.stock.overall.kospi.repository
 * FileName: KospiStockIndexRepository
 * Author: bangjaeyoung
 * Date: 2024-01-14
 * Description:
 */
public interface KospiStockIndexRepository extends JpaRepository<KospiStockIndex, Long> {

//    @Query("SELECT COUNT(s.id) > 0 " +
//            "FROM KOSPI_STOCK_INDEX s " +
//            "WHERE s.basDt =:basDt")
//    boolean exists(@Param("basDt") String basDt);
}
