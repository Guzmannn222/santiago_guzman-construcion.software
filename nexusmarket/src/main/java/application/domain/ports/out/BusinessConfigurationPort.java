package application.domain.ports.out;

import application.domain.models.Inventory;
import java.math.BigDecimal;

/** Output port: business-level configuration (externalized in application.properties). */
public interface BusinessConfigurationPort {

    /** Default tax rate (0..1) applied when an invoice is generated without an explicit one (CU-10). */
    BigDecimal getDefaultTaxRate();
    /** Stock threshold, inclusive, at or below which an Inventory record is LOW_STOCK (D6). */
    int getLowStockThreshold();
}
