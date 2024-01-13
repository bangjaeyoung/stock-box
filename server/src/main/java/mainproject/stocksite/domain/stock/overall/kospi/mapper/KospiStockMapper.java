package mainproject.stocksite.domain.stock.overall.kospi.mapper;

import mainproject.stocksite.domain.stock.overall.kospi.dto.KospiStockDto;
import mainproject.stocksite.domain.stock.overall.kospi.entity.KospiStockIndex;
import mainproject.stocksite.domain.stock.overall.kospi.entity.KospiStockList;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * PackageName: mainproject.stocksite.domain.stock.overall.kospi.mapper
 * FileName: KospiStockMapper
 * Author: bangjaeyoung
 * Date: 2024-01-14
 * Description:
 */
@Mapper(componentModel = "spring")
public interface KospiStockMapper {
    
    List<KospiStockDto.IndexResponse> kospiStockIndicesToResponseDtos(List<KospiStockIndex> kospiStockIndices);
    
    List<KospiStockDto.ListResponse> kospiStockListsToResponseDtos(List<KospiStockList> kospiStockLists);
}
