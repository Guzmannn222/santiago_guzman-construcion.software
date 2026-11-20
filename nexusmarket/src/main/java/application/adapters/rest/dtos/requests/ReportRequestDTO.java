package application.adapters.rest.dtos.requests;

import java.util.Map;
import lombok.Data;

@Data
public class ReportRequestDTO {

    private Map<String, Object> filters;
}
