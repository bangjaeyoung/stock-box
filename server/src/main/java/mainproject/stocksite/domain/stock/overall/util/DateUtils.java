package mainproject.stocksite.domain.stock.overall.config;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * PackageName: mainproject.stocksite.domain.stock.overall.config
 * FileName: DateConfig
 * Author: bangjaeyoung
 * Date: 2024-01-12
 * Description: Open API 서버로부터 현재부터 5일전까지의 데이터를 가져오기 위해, 현재부터 5일전까지의 날짜를 구하기 위한 클래스
 */
@Getter
@Component
public class DateConfig {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    
    public static String getFiveDaysAgoToNow() {
        LocalDate fiveDaysAgo = LocalDate.now().minusDays(5);
        return DATE_FORMATTER.format(fiveDaysAgo);
    }
}
