package application.adapters.persistence.mongodb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import application.adapters.persistence.mongodb.documents.AuditLogDocument;
import application.adapters.persistence.mongodb.mappers.AuditLogMongoMapper;
import application.domain.models.AuditLog;
import application.domain.models.User;
import application.domain.valueobjects.EntityReference;
import application.domain.valueobjects.EntityType;
import application.domain.valueobjects.OperationType;
import application.domain.valueobjects.SystemRole;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AuditLogMongoMapperTest {

    @Test
    @DisplayName("AuditLog is denormalized into the document and rebuilt")
    void roundTrip() {
        User actor = new User("seller", "hash", "Seller", "s@mail.com", "DOC1", SystemRole.SELLER);
        actor.setId(4L);
        Map<String, Object> details = new HashMap<>();
        details.put("price", "1000.00");
        AuditLog log = new AuditLog();
        log.setAuditId("abc");
        log.setOperationType(OperationType.PRODUCT_CREATION);
        log.setOperationDate(LocalDateTime.of(2026, 5, 6, 7, 8));
        log.setPerformedBy(actor);
        log.setUserRole(SystemRole.SELLER);
        log.setAffectedEntity(EntityReference.of(EntityType.PRODUCT, 9L));
        log.setDetails(details);

        AuditLogDocument doc = AuditLogMongoMapper.toDocument(log);
        assertEquals("PRODUCT_CREATION", doc.getOperationType());
        assertEquals((Object) 4L, doc.getPerformedByUserId());
        assertEquals("seller", doc.getPerformedByUsername());
        assertEquals("SELLER", doc.getPerformedByRole());
        assertEquals("PRODUCT", doc.getAffectedEntityType());
        assertEquals("9", doc.getAffectedEntityIdentifier());

        AuditLog back = AuditLogMongoMapper.toDomain(doc);
        assertEquals("abc", back.getAuditId());
        assertEquals(OperationType.PRODUCT_CREATION, back.getOperationType());
        assertEquals(SystemRole.SELLER, back.getUserRole());
        assertEquals((Object) 4L, back.getPerformedBy().getId());
        assertEquals(SystemRole.SELLER, back.getPerformedBy().getRole());
        assertEquals(EntityReference.of(EntityType.PRODUCT, 9L), back.getAffectedEntity());
        assertEquals("1000.00", back.getDetails().get("price"));
    }

    @Test
    @DisplayName("System audit entries without actor or entity map cleanly")
    void nullableParts() {
        AuditLog log = new AuditLog();
        log.setOperationType(OperationType.USER_LOGIN);
        AuditLog back = AuditLogMongoMapper.toDomain(AuditLogMongoMapper.toDocument(log));
        assertNull(back.getPerformedBy());
        assertNull(back.getAffectedEntity());
        assertNull(AuditLogMongoMapper.toDocument(null));
        assertNull(AuditLogMongoMapper.toDomain(null));
    }
}
