# Nivel 4 — Código

```mermaid
classDiagram
  class BuyerPort
  class BuyerUseCaseImpl
  class CreateOrderService
  class OrderRepositoryPort
  class OrderJpaAdapter
  BuyerUseCaseImpl ..|> BuyerPort
  BuyerUseCaseImpl --> CreateOrderService
  CreateOrderService --> OrderRepositoryPort
  OrderJpaAdapter ..|> OrderRepositoryPort
```
