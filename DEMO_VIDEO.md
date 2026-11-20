# Guion de la demostración (video)

Todo se ejecuta en la **terminal** (PowerShell), contra la API que corre en Docker. Después de **cada operación** se hace su `GET` de verificación. Cada bloque se pega por separado.

> Convención: `$tok` es el token del rol con el que se hace cada llamada. Un token por rol = una "sesión" por rol.

---

## 0. Arranque limpio

En la terminal 1 (carpeta que contiene `docker-compose.yml`):

```
docker compose down -v
docker compose up --build nexusmarket-app
```

Espera la línea `Seeding finished`. Abre la terminal 2 (con `+`), en la misma carpeta, y pega las funciones:

```
function Api($method, $path, $body, $tok) {
  $h = @{}
  if ($tok) { $h.Authorization = "Bearer $tok" }
  $uri = "http://localhost:8080/api/v1$path"
  try {
    if ($body) { $r = Invoke-RestMethod -Method $method -Uri $uri -Headers $h -ContentType "application/json" -Body $body }
    else { $r = Invoke-RestMethod -Method $method -Uri $uri -Headers $h }
    if ($r -and $r.PSObject.Properties['value'] -and $r.PSObject.Properties['Count']) { $r.value } else { $r }
  } catch { Write-Host $_.ErrorDetails.Message -ForegroundColor Red }
}
function Login($u) { (Api Post "/auth/login" ('{"username":"' + $u + '","password":"Password123*"}')).token }
function Ver($path, $tok) { Api Get $path $null $tok | ConvertTo-Json -Depth 6 }
function Codigo($method, $path, $body, $tok) {
  $h = @{}
  if ($tok) { $h.Authorization = "Bearer $tok" }
  try { Invoke-RestMethod -Method $method -Uri "http://localhost:8080/api/v1$path" -Headers $h -ContentType "application/json" -Body $body | Out-Null; 200 }
  catch { [int]$_.Exception.Response.StatusCode }
}
```

Roles de prueba que crea el seeder (contraseña `Password123*`): `admin`, `supervisor`, `logistics`, `seller`, `buyer`.

```
$adm = Login "admin"
$sup = Login "supervisor"
$log = Login "logistics"
```

---

## 1. Creación de compradores y productos

> El enunciado habla de crear productos "a cada comprador". En el modelo del proyecto los productos los crean los **vendedores**, así que se crean 3 compradores y 3 vendedores, y cada vendedor crea un producto físico y uno digital.

### 1.1 Tres compradores (registro público)

```
Api Post "/auth/register" '{"username":"comprador1","password":"Password123*","identityDocument":"7000001","name":"Comprador Uno","email":"comprador1@nexus.com","primaryAddress":"Calle 1 # 1-1, Bogota"}'
Api Post "/auth/register" '{"username":"comprador2","password":"Password123*","identityDocument":"7000002","name":"Comprador Dos","email":"comprador2@nexus.com","primaryAddress":"Calle 2 # 2-2, Medellin"}'
Api Post "/auth/register" '{"username":"comprador3","password":"Password123*","identityDocument":"7000003","name":"Comprador Tres","email":"comprador3@nexus.com","primaryAddress":"Calle 3 # 3-3, Cali"}'
```

**GET de verificación** (el administrador lista los usuarios):

```
Api Get "/administrator/users" $null $adm | Format-Table id, username, role, status
```

### 1.2 Tres vendedores

El vendedor `seller` ya existe. El administrador crea dos más:

```
Api Post "/administrator/sellers" '{"username":"seller2","password":"Password123*","identityDocument":"9000002","name":"Vendedor Dos","email":"seller2@nexus.com","businessInfo":"Accesorios","firstWarehouseLocation":"Bodega vendedor 2"}' $adm
Api Post "/administrator/sellers" '{"username":"seller3","password":"Password123*","identityDocument":"9000003","name":"Vendedor Tres","email":"seller3@nexus.com","businessInfo":"Cargadores","firstWarehouseLocation":"Bodega vendedor 3"}' $adm
```

**GET:**

```
Ver "/administrator/sellers" $adm
```

### 1.3 Cada vendedor crea un producto físico y uno digital, y los publica

```
$t1 = Login "seller"
$f1 = Api Post "/seller/products" '{"name":"Teclado fisico","description":"Teclado mecanico","price":100000,"currency":"COP","type":"PHYSICAL"}' $t1
$d1 = Api Post "/seller/products" '{"name":"Curso digital 1","description":"Curso en linea","price":30000,"currency":"COP","type":"DIGITAL"}' $t1
Api Post "/seller/products/$($f1.id)/publication" $null $t1 | Out-Null
Api Post "/seller/products/$($d1.id)/publication" $null $t1 | Out-Null
```
```
$t2 = Login "seller2"
$f2 = Api Post "/seller/products" '{"name":"Mouse fisico","description":"Mouse inalambrico","price":80000,"currency":"COP","type":"PHYSICAL"}' $t2
$d2 = Api Post "/seller/products" '{"name":"Ebook digital 2","description":"Libro electronico","price":25000,"currency":"COP","type":"DIGITAL"}' $t2
Api Post "/seller/products/$($f2.id)/publication" $null $t2 | Out-Null
Api Post "/seller/products/$($d2.id)/publication" $null $t2 | Out-Null
```
```
$t3 = Login "seller3"
$f3 = Api Post "/seller/products" '{"name":"Cargador fisico","description":"Cargador rapido","price":60000,"currency":"COP","type":"PHYSICAL"}' $t3
$d3 = Api Post "/seller/products" '{"name":"Licencia digital 3","description":"Licencia de software","price":20000,"currency":"COP","type":"DIGITAL"}' $t3
Api Post "/seller/products/$($f3.id)/publication" $null $t3 | Out-Null
Api Post "/seller/products/$($d3.id)/publication" $null $t3 | Out-Null
```

**GET** (cada vendedor ve solo lo suyo):

```
Api Get "/seller/products" $null $t1 | Format-Table id, name, type, status
Api Get "/seller/products" $null $t2 | Format-Table id, name, type, status
Api Get "/seller/products" $null $t3 | Format-Table id, name, type, status
```

### 1.4 Inventario de los productos físicos en una bodega compartida

Los productos físicos de vendedores distintos deben estar en la **misma bodega** para poder comprarse en una sola orden. La crea el operador logístico (tipo `MARKETPLACE`, sin dueño) y carga 5 unidades de cada físico:

```
$bodega = Api Post "/logistics-operator/warehouses" '{"location":"Bodega central","type":"MARKETPLACE"}' $log
foreach ($p in @($f1,$f2,$f3)) { Api Post "/logistics-operator/inventories" ('{"productId":' + $p.id + ',"warehouseId":' + $bodega.id + ',"initialStock":5}') $log | Out-Null }
```

**GET:**

```
Api Get "/logistics-operator/inventories" $null $log | Format-Table id, productId, warehouseId, stock, status
```

---

## 2. Gestión del carrito (comprador1)

```
$c1 = Login "comprador1"
```

### 2.1 Cuatro productos: 2 físicos y 2 digitales

```
Api Post "/buyer/cart/items" ('{"productId":' + $f1.id + ',"quantity":1}') $c1 | Out-Null
Api Post "/buyer/cart/items" ('{"productId":' + $f2.id + ',"quantity":2}') $c1 | Out-Null
Api Post "/buyer/cart/items" ('{"productId":' + $d1.id + ',"quantity":1}') $c1 | Out-Null
Api Post "/buyer/cart/items" ('{"productId":' + $d3.id + ',"quantity":1}') $c1 | Out-Null
```

**GET:**

```
Ver "/buyer/cart" $c1
```
Debe mostrar 4 líneas (cada una con su `sellerId`) y total de **310.000 COP** (100.000 + 2×80.000 + 30.000 + 20.000).

### 2.2 Cantidad superior al inventario (debe bloquearse)

Hay 5 unidades del Teclado. Se intenta agregar 99 más:

```
Codigo Post "/buyer/cart/items" ('{"productId":' + $f1.id + ',"quantity":99}') $c1
Api Post "/buyer/cart/items" ('{"productId":' + $f1.id + ',"quantity":99}') $c1
```
El primero devuelve **409**; el segundo muestra el mensaje en rojo (`Not enough stock ... only 5 unit(s) are available`). También se bloquea al cambiar la cantidad:

```
Codigo Put "/buyer/cart/items/$($f1.id)" '{"quantity":99}' $c1
```

**GET** (el carrito no cambió):

```
Ver "/buyer/cart" $c1
```

---

## 3. Compra y envío

### 3.1 Orden y pago (comprador1)

```
$orden = Api Post "/buyer/orders" '{}' $c1
Api Post "/buyer/orders/$($orden.id)/payment" $null $c1 | Out-Null
```

**GET:**

```
Ver "/buyer/orders/$($orden.id)" $c1
Ver "/buyer/cart" $c1
Api Get "/logistics-operator/inventories" $null $log | Format-Table id, productId, stock, status
```
La orden queda `PAID`, el carrito vacío y el stock de Teclado y Mouse bajó (4 y 3).

### 3.2 Factura (administrador, opcional)

```
Api Post "/administrator/orders/$($orden.id)/invoice" '{}' $adm
Ver "/buyer/invoices" $c1
```

### 3.3 Envío (operador logístico)

```
$envio = Api Post "/logistics-operator/orders/$($orden.id)/shipments" '{"address":"Calle 1 # 1-1, Bogota"}' $log
Api Get "/logistics-operator/shipments" $null $log | Format-Table id, orderId, status
Api Post "/logistics-operator/shipments/$($envio.id)/preparation" $null $log
Api Post "/logistics-operator/shipments/$($envio.id)/dispatch" $null $log
Api Put "/logistics-operator/shipments/$($envio.id)/status" '{"status":"IN_TRANSIT"}' $log
Api Post "/logistics-operator/shipments/$($envio.id)/delivery" $null $log
```

**GET** (antes de cambiar de rol):

```
Api Get "/logistics-operator/shipments" $null $log | Format-Table id, orderId, status, dispatchDate, deliveryDate
Ver "/buyer/shipments" $c1
Api Get "/buyer/orders/$($orden.id)" $null $c1 | Select-Object id, status, total
```
El envío queda `DELIVERED` y la orden `FINISHED`.

---

## 4. Devolución y reembolso de un producto físico de un vendedor

Se devuelve **1 unidad del Mouse**, que pertenece a `seller2`.

### 4.1 El comprador solicita la devolución

```
$dev = Api Post "/buyer/orders/$($orden.id)/returns" ('{"reason":"Mouse defectuoso","items":[{"productId":' + $f2.id + ',"quantity":1}]}') $c1
Ver "/buyer/returns" $c1
```
La devolución muestra solo el Mouse, con su `sellerId`.

### 4.2 El vendedor correcto la aprueba

```
Api Post "/seller/returns/$($dev.id)/approval" $null $t2
Api Get "/seller/returns" $null $t2 | ConvertTo-Json -Depth 5
```

### 4.3 El operador logístico la completa (el stock del Mouse vuelve en 1)

```
Api Post "/logistics-operator/returns/$($dev.id)/completion" $null $log
Api Get "/logistics-operator/inventories" $null $log | Format-Table id, productId, stock, status
```

### 4.4 Reembolso (vendedor dueño del producto)

```
$ref = Api Post "/seller/returns/$($dev.id)/refund" '{"amount":80000,"currency":"COP"}' $t2
Api Post "/seller/refunds/$($ref.id)/processing" $null $t2
Api Post "/seller/refunds/$($ref.id)/completion" $null $t2
Api Get "/seller/refunds" $null $t2 | ConvertTo-Json -Depth 4
Ver "/buyer/refunds" $c1
```

### 4.5 Los otros tres productos no cambian

```
Ver "/buyer/orders/$($orden.id)" $c1
Api Get "/logistics-operator/inventories" $null $log | Format-Table id, productId, stock, status
```
La orden sigue con sus 4 líneas y su estado. El stock del Teclado (4) no cambió; el del Mouse quedó en 4 (3 + 1 devuelta). Los digitales no tienen inventario.

---

## 5. Reportes de los vendedores

Cada vendedor consulta **sus** reportes (ventas, inventario, devoluciones y reembolsos). Se muestra solo la parte `data`:

```
foreach ($par in @(@("seller1",$t1), @("seller2",$t2), @("seller3",$t3))) {
  Write-Host "===== $($par[0]) - VENTAS ====="
  (Api Get "/seller/reports/sales" $null $par[1]).data | ConvertTo-Json -Depth 8
}
```
```
foreach ($par in @(@("seller1",$t1), @("seller2",$t2), @("seller3",$t3))) {
  Write-Host "===== $($par[0]) - INVENTARIO ====="
  (Api Get "/seller/reports/inventory" $null $par[1]).data | ConvertTo-Json -Depth 8
}
```
```
foreach ($par in @(@("seller1",$t1), @("seller2",$t2), @("seller3",$t3))) {
  Write-Host "===== $($par[0]) - DEVOLUCIONES Y REEMBOLSOS ====="
  (Api Get "/seller/reports/returns" $null $par[1]).data | ConvertTo-Json -Depth 8
}
```

El administrador y el supervisor pueden consultar los de cualquier vendedor con `?sellerId=`:

```
$vend = Api Get "/administrator/sellers" $null $adm
(Api Get "/administrator/reports/sales?sellerId=$($vend[1].id)" $null $adm).data | ConvertTo-Json -Depth 8
(Api Get "/supervisor/reports/returns?sellerId=$($vend[1].id)" $null $sup).data | ConvertTo-Json -Depth 8
```

Lo esperado en `seller2`: ventas de 2 unidades del Mouse (160.000), inventario con movimientos `INBOUND` 5, `RESERVATION` 2 y `RETURN` 1, devolución de 1 unidad (80.000) con el reembolso `COMPLETED` y `totalRefunded` 80.000.

Guarda los reportes **antes** del reinicio para compararlos después:

```
$sellers = @{ s1 = $t1; s2 = $t2; s3 = $t3 }
foreach ($k in $sellers.Keys) {
  foreach ($r in "sales","inventory","returns") {
    (Api Get "/seller/reports/$r" $null $sellers[$k]).data | ConvertTo-Json -Depth 8 | Out-File "antes-$k-$r.json" -Encoding utf8
  }
}
```

---

## 6. Seguridad por rol (intentos que deben bloquearse)

Códigos esperados: **401** = sin sesión, **403** = sin permiso.

```
# Sin token
Codigo Get "/buyer/cart" $null $null                                            # 401
Codigo Get "/seller/reports/sales" $null $null                                  # 401

# El comprador intenta operaciones de otros roles
Codigo Post "/logistics-operator/orders/$($orden.id)/shipments" '{"address":"x"}' $c1   # 403
Codigo Post "/administrator/sellers" '{}' $c1                                   # 403
Codigo Get "/seller/products" $null $c1                                         # 403
Codigo Get "/seller/reports/sales" $null $c1                                    # 403
Codigo Get "/supervisor/reports/sales" $null $c1                                # 403

# El vendedor intenta operaciones de otros roles
Codigo Get "/buyer/cart" $null $t1                                              # 403
Codigo Post "/logistics-operator/warehouses" '{"location":"x","type":"MARKETPLACE"}' $t1   # 403
Codigo Get "/administrator/users" $null $t1                                     # 403

# El operador logístico y el supervisor
Codigo Post "/seller/products" '{"name":"x","price":1,"currency":"COP","type":"PHYSICAL"}' $log   # 403
Codigo Post "/buyer/orders" '{}' $sup                                           # 403
Codigo Post "/logistics-operator/orders/$($orden.id)/shipments" '{"address":"x"}' $sup   # 403
```

Operaciones que el rol correcto intenta sobre datos de **otro** usuario:

```
$otro = Login "comprador2"
Codigo Get "/buyer/orders/$($orden.id)" $null $otro                             # 403 o 404 (no es su orden)
Codigo Post "/buyer/orders/$($orden.id)/payment" $null $otro                    # 403 o 404
Codigo Post "/buyer/orders/$($orden.id)/returns" '{"reason":"x"}' $otro         # 403 o 404

# Un vendedor ajeno al producto devuelto no puede decidir sobre la devolución
Codigo Post "/seller/returns/$($dev.id)/approval" $null $t1                     # 403
Codigo Post "/seller/returns/$($dev.id)/refund" '{"amount":1,"currency":"COP"}' $t3   # 403

# Un vendedor no puede ver los reportes de otro
Codigo Get "/seller/reports/sales" $null $t3                                    # 200 (el suyo)
Api Get "/seller/reports/sales" $null $t3 | Select-Object -ExpandProperty data | Select-Object sellerId   # su propio id
```

Reglas de negocio que también se bloquean:

```
# Reembolso mayor al valor devuelto
Codigo Post "/administrator/returns/$($dev.id)/refund" '{"amount":999999,"currency":"COP"}' $adm   # 400/409

# Devolver un producto que no está en la orden
Codigo Post "/buyer/orders/$($orden.id)/returns" ('{"reason":"x","items":[{"productId":' + $f3.id + ',"quantity":1}]}') $c1   # 400
```

---

## 7. Verificación final: reinicio y persistencia

1. Reinicio completo (**sin** `-v`, para conservar los datos). En la terminal 1: `Ctrl+C`, y luego:

```
docker compose down
docker compose up nexusmarket-app
```

2. Espera `Seeding finished`. En la terminal 2 inicia sesión otra vez con cada rol (los tokens anteriores ya no sirven):

```
$t1 = Login "seller"; $t2 = Login "seller2"; $t3 = Login "seller3"
$adm = Login "admin"; $sup = Login "supervisor"
```

3. Vuelve a consultar los reportes y compáralos con los de antes:

```
$sellers = @{ s1 = $t1; s2 = $t2; s3 = $t3 }
foreach ($k in $sellers.Keys) {
  foreach ($r in "sales","inventory","returns") {
    (Api Get "/seller/reports/$r" $null $sellers[$k]).data | ConvertTo-Json -Depth 8 | Out-File "despues-$k-$r.json" -Encoding utf8
    $dif = Compare-Object (Get-Content "antes-$k-$r.json") (Get-Content "despues-$k-$r.json")
    if ($dif) { Write-Host "DIFERENCIA en $k / $r" -ForegroundColor Red } else { Write-Host "IGUAL: $k / $r" -ForegroundColor Green }
  }
}
```

Los nueve reportes deben salir `IGUAL`. Se comparan solo los datos (`data`); la fecha de generación del reporte cambia por definición.

4. Verificación extra de lo guardado en las bases:

```
docker compose exec mysql-db mysql -uroot -proot_password nexusmarket_db -e "SELECT id,status,total_amount FROM orders; SELECT id,order_id,status FROM returns; SELECT * FROM return_items; SELECT id,return_id,amount_amount,status FROM refunds; SELECT id,product_id,warehouse_id,stock FROM inventories;"
docker compose exec mongo-db mongosh audit_db --quiet --eval "db.audit_logs.countDocuments()"
```
