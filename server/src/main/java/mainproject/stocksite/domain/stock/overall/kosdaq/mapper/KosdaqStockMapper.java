package mainproject.stocksite.domain.stock.overall.kosdaq.mapper;

import mainproject.stocksite.domain.stock.overall.kosdaq.dto.KosdaqStockDto;
import mainproject.stocksite.domain.stock.overall.kosdaq.entity.KosdaqStockIndex;
import mainproject.stocksite.domain.stock.overall.kosdaq.entity.KosdaqStockList;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * PackageName: mainproject.stocksite.domain.stock.overall.kosdaq.mapper
 * FileName: KosdaqStockMapper
 * Author: bangjaeyoung
 * Date: 2024-01-13
 * Description:
 */
@Mapper(componentModel = "spring")
public interface KosdaqStockMapper {
    
    List<KosdaqStockDto.IndicesResponse> KOSDAQStockIndicesToStockIndexResponseDtos(List<KosdaqStockIndex> kosdaqStockIndices);
    
    List<KosdaqStockDto.ListsResponse> KOSDAQStockListsToStockListsResponseDtos(List<KosdaqStockList> kosdaqStockLists);
}
