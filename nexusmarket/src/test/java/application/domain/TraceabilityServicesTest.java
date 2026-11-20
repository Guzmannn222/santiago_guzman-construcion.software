package application.domain;

import static org.junit.jupiter.api.Assertions.*;

import application.domain.exceptions.*;
import application.domain.models.*;
import application.domain.services.buyer.*;
import application.domain.services.cart.*;
import application.domain.services.inventory.*;
import application.domain.services.invoice.*;
import application.domain.services.order.*;
import application.domain.services.product.*;
import application.domain.services.refund.*;
import application.domain.services.report.*;
import application.domain.services.returns.*;
import application.domain.services.seller.*;
import application.domain.services.shipment.*;
import application.domain.services.user.*;
import application.domain.services.warehouse.*;
import application.domain.services.operation.*;
import application.domain.valueobjects.*;
import java.math.BigDecimal;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
class TraceabilityServicesTest {

    private Fixture fx;

    @BeforeEach
    void setUp() {
        fx = new Fixture();
    }

    @Test
    void everySuccessfulOperationIsRecordedInBothStores() {
        User admin = fx.admin();
        fx.traceability.execute(admin, OperationType.USER_LOGIN, EntityReference.of(EntityType.USER, admin.getId()),
                Map.of("k", "v"));
        assertEquals(1, fx.operations.findAll().size());
        assertEquals(1, fx.audits.size());
        AuditLog log = fx.audits.get(0);
        assertEquals(SystemRole.ADMINISTRATOR, log.getUserRole());
        assertEquals("v", log.getDetails().get("k"));
        assertSame(admin, fx.operations.findAll().get(0).getPerformedBy());
    }

    @Test
    void incompleteRecordsAreRejected() {
        RegisterOperationService registerOperation = new RegisterOperationService(fx.operationPort);
        RegisterAuditLogService registerAudit = new RegisterAuditLogService(fx.auditPort);
        assertThrows(InvalidOperationException.class, () -> registerOperation.execute(null));
        assertThrows(InvalidOperationException.class, () -> registerOperation.execute(new Operation()));
        assertThrows(InvalidAuditLogException.class, () -> registerAudit.execute(null));
        assertThrows(InvalidAuditLogException.class, () -> registerAudit.execute(new AuditLog()));
    }

    @Test
    void auditConsultationIsRestrictedToSupervisionRoles() {
        ConsultAuditLogsService audit = new ConsultAuditLogsService(fx.auditPort, fx.validateStatus, fx.validateRole);
        ConsultOperationsService ops = new ConsultOperationsService(fx.operationPort, fx.validateStatus, fx.validateRole);
        User admin = fx.admin();
        User supervisor = fx.supervisor();
        User buyer = fx.newBuyer().getUser();
        EntityReference ref = EntityReference.of(EntityType.USER, admin.getId());
        fx.traceability.execute(admin, OperationType.USER_LOGIN, ref, null);

        assertEquals(1, audit.consultByUser(supervisor, admin).size());
        assertEquals(1, audit.consultByEntity(admin, ref).size());
        assertEquals(1, audit.consultByOperationType(admin, OperationType.USER_LOGIN).size());
        assertEquals(1, ops.consultByUser(supervisor, admin).size());
        assertEquals(1, ops.consultByEntity(supervisor, ref).size());
        assertThrows(UnauthorizedOperationException.class, () -> audit.consultByUser(buyer, admin));
        assertThrows(UnauthorizedOperationException.class, () -> ops.consultByEntity(buyer, ref));
        assertThrows(InvalidAuditLogException.class, () -> audit.consultByEntity(admin, null));
        assertThrows(InvalidOperationException.class, () -> ops.consultByUser(admin, null));
    }

    @Test
    void failedBusinessActionsLeaveNoTrace() {
        User admin = fx.admin();
        BlockUserService block = new BlockUserService(fx.validateStatus, fx.validateRole, fx.traceability, fx.userPort);
        assertThrows(UnauthorizedOperationException.class, () -> block.block(admin, admin));
        assertTrue(fx.operations.findAll().isEmpty());
        assertTrue(fx.audits.isEmpty());
    }
}
