package application.adapters.config;

import application.domain.ports.out.BusinessConfigurationPort;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Reads the business parameters externalized in application.properties. */
@Component
public class DefaultBusinessConfigurationAdapter implements BusinessConfigurationPort {

    private final BigDecimal defaultTaxRate;
    private final int lowStockThreshold;

    public DefaultBusinessConfigurationAdapter(
            @Value("${business.invoice.default-tax-rate:0.19}") BigDecimal defaultTaxRate,
            @Value("${business.inventory.low-stock-threshold:5}") int lowStockThreshold) {
        this.defaultTaxRate = defaultTaxRate;
        this.lowStockThreshold = lowStockThreshold;
    }

    @Override
    public BigDecimal getDefaultTaxRate() {
        return defaultTaxRate;
    }

    @Override
    public int getLowStockThreshold() {
        return lowStockThreshold;
    }
}
