# User and Authentication Services

Login, logout, registro de usuarios, cambio de contraseña/rol/estado y consulta de usuarios.

Patrón común: validar actor activo → validar rol → validar datos / recargar entidades → regla de negocio → persistir → registrar operación y auditoría.

## LoginService

Input Port: authenticate a user and issue a token (unknown user and wrong password are indistinguishable).

- Puerto: `LoginUseCase`
- Firma: `AuthenticationResult login(User credentials)`

## LogoutService

Input Port: invalidate every token issued to the user.

- Puerto: `LogoutUseCase`
- Firma: `void logout(User actor)`

## RegisterUserService

Input Port: CU-01 register a user (role ADMINISTRATOR). The new user carries username, plain password, name, email, identity document and role.

- Puerto: `RegisterUserUseCase`
- Firma: `User register(User actor, User newUser)`

## RegisterBuyerAccountService

Input Port: CU-02 public self-registration: creates a BUYER user, its buyer profile and its cart.

- Puerto: `RegisterBuyerAccountUseCase`
- Firma: `Buyer registerBuyerAccount(User newUser, String primaryAddress)`

## ChangeUserPasswordService

Input Port: change the password of the acting user (invalidates previous tokens).

- Puerto: `ChangeUserPasswordUseCase`
- Firma: `void changePassword(User actor, String currentPassword, String newPassword)`

## ChangeUserRoleService

Input Port: CU-01 change the role of a user (role ADMINISTRATOR).

- Puerto: `ChangeUserRoleUseCase`
- Firma: `void changeRole(User actor, User target, SystemRole newRole)`

## BlockUserService

Input Port: CU-01 block a user (role ADMINISTRATOR).

- Puerto: `BlockUserUseCase`
- Firma: `void block(User actor, User target)`

## ActivateUserService

Input Port: CU-01 activate a user (role ADMINISTRATOR).

- Puerto: `ActivateUserUseCase`
- Firma: `void activate(User actor, User target)`

## ConsultUserService

Input Port: CU-01 consult users. A user can consult itself; ADMINISTRATOR can consult any user.

- Puerto: `ConsultUserUseCase`
- Firma: `User consultUser(User actor, User target)`

