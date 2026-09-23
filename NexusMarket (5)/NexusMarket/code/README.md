# NexusMarket -- Java Implementation (`code/`)

Java 17 / Maven implementation of the NexusMarket domain model described in [`../SDD`](../SDD).
All source code, comments and identifiers are written in **English**.

## Package structure

```
src/main/java/com/nexusmarket/
├── model/       Domain entities (17 classes: User, Buyer, Seller, Warehouse, Product,
│                Variant, Inventory, InventoryMovement, Cart, CartItem, Order, OrderItem,
│                Invoice, Shipment, Return, Refund, Report)
├── enums/       Roles, types and statuses (10 enumerations)
├── repository/  In-memory persistence layer (generic Repository<T, ID> + implementations)
├── service/     Business rules and validations (one service per domain area)
├── controller/  Coordination layer (NexusMarketFacade) used by Main
└── util/        Domain exceptions and shared utilities
```

## Build and run

Requires Java 17+ and Maven.

```bash
cd code
mvn compile      # compile
mvn test         # run unit tests
mvn package      # build the .jar
java -cp target/nexusmarket.jar com.nexusmarket.Main
```

`Main` runs a short end-to-end demo (seller onboarding, product publishing, inventory load,
cart, order, invoice, shipment and delivery) that exercises every layer of the architecture.

## Service catalog (53 use cases)

The `service` package exposes **53 individual services**, each mapping 1:1 to a use case and
restricted at runtime to the role(s) authorized to execute it (enforced by
`util.AccessControl`, which throws `AccessDeniedException` otherwise). The full table — service,
method, and allowed role(s) — lives in
[`src/main/java/com/nexusmarket/service/SERVICES.md`](src/main/java/com/nexusmarket/service/SERVICES.md).

## Design notes

- Inventory and order-status rules (no negative stock, no modifying a finished order) are
  enforced inside the domain classes themselves and re-validated in the `service` layer, per
  SDD section 16.2 and 31.
- Persistence is intentionally in-memory (`InMemoryRepository`) since the academic delivery does
  not require a real database; swapping in a JDBC/JPA implementation only requires implementing
  the `Repository<T, ID>` interface.
- `NexusMarketFacade` wires repositories and services together, playing the role of the
  `controller` layer while no menu/API is required (see SDD section 3.2).
