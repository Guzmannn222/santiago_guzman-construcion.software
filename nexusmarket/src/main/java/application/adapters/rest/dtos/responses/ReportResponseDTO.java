package application.adapters.rest.dtos.responses;

import java.util.Map;
import lombok.Data;

@Data
public class ReportResponseDTO {

    private String type;

    private Map<String, Object> filters;

    private Map<String, Object> data;

    private String generationDate;
}
