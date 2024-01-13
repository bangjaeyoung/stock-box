package mainproject.stocksite.domain.stock.overall.kosdaq.repository;

import mainproject.stocksite.domain.stock.overall.kosdaq.entity.KosdaqStockList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * PackageName: mainproject.stocksite.domain.stock.overall.kosdaq.repository
 * FileName: KosdaqStockListRepository
 * Author: bangjaeyoung
 * Date: 2024-01-14
 * Description:
 */
public interface KosdaqStockListRepository extends JpaRepository<KosdaqStockList, Long> {

//    @Query("SELECT COUNT(s.id) > 0 " +
//            "FROM KOSDAQ_STOCK_LIST s " +
//            "WHERE s.basDt =:basDt")
//    boolean exists(@Param("basDt") String basDt);
}
