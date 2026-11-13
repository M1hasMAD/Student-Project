# 🚗 Car Store

**Online Car Dealership Platform**  
A full-stack web application for browsing cars, managing a shopping cart, placing orders, and administering a car store.

> **Project name:** ShopOfCars (`tienda.online`)  
> **Application name:** Car Store  
> **Version:** `0.0.1-SNAPSHOT`  
> **Author:** M1khail  
> **Framework:** Spring Boot 4.1.1 · Java 17 · Spring Data JPA · Thymeleaf · H2

---

## Table of Contents

- [Project Overview](#-project-overview)
- [Features](#-features)
- [Technology Stack](#-technology-stack)
- [Architecture](#-architecture)
- [Project Structure](#-project-structure)
- [Domain Model](#-domain-model)
- [Repositories and Services](#-repositories-and-services)
- [Application Flows and Routes](#-application-flows-and-routes)
- [Security](#-security)
- [Internationalization](#-internationalization)
- [Templates and User Interface](#-templates-and-user-interface)
- [Database Schema](#-database-schema)
- [API Overview](#-api-overview)
- [Configuration](#-configuration)
- [Build and Run](#-build-and-run)
- [Testing](#-testing)
- [Known Limitations and Technical Debt](#-known-limitations-and-technical-debt)
- [Recommended Improvements](#-recommended-improvements)
- [Development Workflow](#-development-workflow)
- [Key Files](#-key-files)
- [SQL Reference](#-sql-reference)

---

## 🚘 Project Overview

**Car Store** is a full-stack web application for an online car store, built with Spring Boot. It provides two main areas:

- **Public customer portal:** customers can browse the car catalog, search and paginate results, view car details, register and sign in, manage a shopping cart, and complete a multi-step checkout.
- **Administrative panel:** administrators can manage cars, categories, users, and orders, including order-status tracking and store statistics.

The application uses a layered architecture and a file-based H2 database for local development.

## ✨ Features

| Area | Functionality |
|---|---|
| Public portal | Browse cars, search and paginate listings, view car details |
| User accounts | Registration, login, profile, and order history |
| Shopping cart | Add, update, and remove cart items; calculate totals |
| Checkout | Four steps: shipping, payment details, delivery method, and order confirmation |
| Administration | Admin login and management of cars, categories, users, and orders |
| Order management | View orders, inspect order details, and update statuses |
| Internationalization | English (default), Spanish, and Russian |
| Image handling | Main cover image, additional car images, and generated thumbnails |

### Image Handling

- Main cover image and multiple additional images per car.
- JPEG and PNG image formats.
- Maximum uploaded image size: 20 MB per image.
- Automatically generated thumbnails (400 × 300).
- Images are stored in the database as binary data.

## 🧰 Technology Stack

| Category | Technology | Version / Notes |
|---|---|---|
| Core framework | Spring Boot | 4.1.1 (as recorded in the project documentation) |
| Programming language | Java | 17 |
| Build tool | Maven | 3.x, Maven Wrapper included |
| Database | H2 | File-based for local development |
| ORM / persistence | Spring Data JPA / Hibernate | JPA-based persistence |
| Template engine | Thymeleaf | 3.x |
| Security | Spring Security | Plus a custom admin interceptor |
| Validation | Hibernate Validator | Bean Validation |
| Image processing | Thumbnailator | 0.4.20 |
| JSON | Gson | 2.13.2 |
| Testing | Spring Boot Test, JUnit 5 | Basic context-load test documented |
| Front-end | HTML, CSS, JavaScript | Thymeleaf, jQuery, Mustache templates |

### Main Dependencies

The project documentation identifies these main dependencies:

- `spring-boot-starter-webmvc`
- `spring-boot-starter-data-jpa`
- `spring-boot-starter-thymeleaf`
- `spring-boot-starter-security`
- `spring-boot-starter-validation`
- H2 database and H2 console
- Gson `2.13.2`
- Thumbnailator `0.4.20`
- Spring Boot DevTools and test starters

The exact dependency versions are defined in [`pom.xml`](pom.xml).

## 🏗️ Architecture

The application follows a **layered architecture**:

```text
┌──────────────────────────────────────────────────────────────┐
│                     Presentation Layer                       │
│       Thymeleaf templates, HTML, CSS, JavaScript              │
├──────────────────────────────────────────────────────────────┤
│                      Controller Layer                        │
│ ControllerAdmin, ControllerPublic, ControllerCart,            │
│ ControllerCheckout, ControllerUser, ControllerAuth           │
├──────────────────────────────────────────────────────────────┤
│                        Service Layer                          │
│ ServiceCarsImplJPA, ServiceUsersImplJPA,                      │
│ ServiceOrderImplJPA, CartServiceImplJPA, ServiceSetUpImplJPA  │
├──────────────────────────────────────────────────────────────┤
│                      Repository Layer                         │
│ CarRepository, CategoryRepository, UserRepository,            │
│ OrderRepository, CartRepository, CartItemRepository,          │
│ CarImageRepository, OrderItemRepository                       │
├──────────────────────────────────────────────────────────────┤
│                       Domain Model                            │
│ Car, Category, User, Cart, CartItem, Order, OrderItem,        │
│ CarImage, Configuration                                       │
├──────────────────────────────────────────────────────────────┤
│                   H2 File-based Database                      │
│                 ./data/base_de_datos.mv.db                    │
└──────────────────────────────────────────────────────────────┘
```

### Design Principles

- **Interface-based services:** service interfaces are separated from JPA implementations to support testing and implementation changes.
- **Transactional services:** service implementations use transaction management; query methods can be read-only.
- **Bean Validation:** entities use validation annotations such as `@NotEmpty`, `@Size`, `@Min`, `@Max`, and `@Pattern`.
- **Custom exceptions:** domain-specific exceptions extend a common `BusinessException` base.
- **Admin route interception:** a custom `InterceptorAdmin` checks the administrator session for protected routes.
- **Open Session in View:** the documented configuration uses Spring Boot's default Open Session in View behavior.

## 📁 Project Structure

### Java Source

```text
src/main/java/com/mikhail/shop/cars/
├── Application.java
├── Config.java
├── SetupRunner.java
├── interceptores/
│   └── InterceptorAdmin.java
├── constantesSQL/
│   └── ConstantesSQL.java
├── controllers/
│   ├── ControllerAdmin.java
│   ├── ControllerPublic.java
│   ├── ControllerCart.java
│   ├── ControllerCheckout.java
│   ├── ControllerUser.java
│   └── ControllerAuth.java
├── dto/
│   ├── DeliveryMethod.java
│   ├── PaymentDetails.java
│   └── ShippingDetails.java
├── enums/
│   └── Orders.java
├── exception/
│   ├── BusinessException.java
│   ├── CarNotFoundException.java
│   ├── CategoryNotFoundException.java
│   ├── DuplicateCategoryException.java
│   └── EntityNotFoundException.java
├── interfaces/
│   ├── ServiceCars.java
│   ├── ServiceUsers.java
│   ├── ServiceOrder.java
│   ├── CartService.java
│   └── ServiceSetUp.java
├── model/
│   ├── Car.java
│   ├── Category.java
│   ├── User.java
│   ├── Cart.java
│   ├── CartItem.java
│   ├── Order.java
│   ├── OrderItem.java
│   ├── CarImage.java
│   └── Configuration.java
├── repository/
│   ├── CarRepository.java
│   ├── CategoryRepository.java
│   ├── UserRepository.java
│   ├── OrderRepository.java
│   ├── CartRepository.java
│   ├── CartItemRepository.java
│   ├── CarImageRepository.java
│   └── OrderItemRepository.java
├── serviciosImpl/
│   ├── ServiceCarsImplJPA.java
│   ├── ServiceUsersImplJPA.java
│   ├── ServiceOrderImplJPA.java
│   ├── CartServiceImplJPA.java
│   └── ServiceSetUpImplJPA.java
└── config/
    └── SecurityConfig.java
```

### Resources

```text
src/main/resources/
├── application.properties
├── messages.properties
├── messages_es.properties
├── messages_ru.properties
├── templates/
│   ├── index.html
│   ├── register.html
│   ├── login.html
│   ├── car_list.html
│   ├── car_detail.html
│   ├── cart.html
│   ├── checkout/
│   │   ├── check_out_1.html
│   │   ├── check_out_2.html
│   │   ├── check_out_3.html
│   │   └── check_out_4.html
│   ├── fragments/
│   │   └── general.html
│   └── admin/
│       ├── start_admin.html
│       ├── login_admin.html
│       ├── manage_cars.html
│       ├── manage_categories.html
│       ├── manage_users.html
│       ├── manage_orders.html
│       ├── order_details.html
│       ├── user_details.html
│       ├── edit_car.html
│       ├── edit_category.html
│       ├── edit_user.html
│       ├── manage_category.html
│       ├── manage_product.html
│       └── register_product_ok.html
├── static/
│   ├── css/
│   │   ├── estilos.css
│   │   ├── admin.css
│   │   └── checkout.css
│   ├── js/
│   │   └── admin-orders.js
│   └── plantillas_html/
└── test/
    └── resources/application.properties
```

> The tree above reflects the structure described in the project's technical documentation. If files have been renamed or moved in the current working tree, use the actual repository structure as the source of truth.

## 🧩 Domain Model

The application uses JPA entities to represent cars, users, shopping carts, orders, and related data.

### Car — `tabla_cars`

| Field | Type | Description |
|---|---|---|
| `id` | `Long` | Primary key |
| `title` | `String` | Car title; validated for length and allowed characters |
| `price` | `double` | Price in euros; documented range 1–99,999 |
| `brand` | `String` | Car brand |
| `description` | `String` | Optional description |
| `coverImage` | `byte[]` | Main image stored as a BLOB |
| `thumbnailImage` | `byte[]` | Generated 400 × 300 thumbnail |
| `category` | `Category` | Many-to-one category association |
| `images` | `List<CarImage>` | Additional images; one-to-many association |
| `uploadedPhoto` | `MultipartFile` | Transient upload field |
| `uploadedPhotos` | `List<MultipartFile>` | Transient multiple-upload field |

**Relationships:**

- `Car` → `Category`: `ManyToOne`.
- `Car` → `CarImage`: `OneToMany`, cascade and orphan removal documented.

### Category — `tabla_categories`

- `id`: primary key.
- `name`: category name, documented as unique without regard to case at the service layer.
- `description`: category description.
- `cars`: inverse one-to-many relationship with cars.

**Business rules:** category names are checked for duplicates in the service. Before deleting a category, associated cars have their category set to `null`.

### User — `TABLA_USERS`

- `id`: primary key.
- `email`: unique login email.
- `pass`: BCrypt-hashed user password.
- `firstName`, `lastName`, `phone`: user information.
- `role`: `USER` or `ADMIN`.
- `cart`: one-to-one relationship with the user's shopping cart.

### Cart and CartItem

**Cart** (`tabla_cart`):

- `id`: primary key.
- `user`: one-to-one association with a user.
- `items`: one-to-many association with cart items.

**CartItem** (`tabla_cart_items`):

- `id`: primary key.
- `cart`: many-to-one association with a cart.
- `car`: many-to-one association with a car.
- `quantity`: item quantity.

### Order and OrderItem

**Order** (`tabla_orders`):

- `id`: primary key.
- `user`: many-to-one association with a user.
- `status`: order status (`PENDING`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED`).
- `totalAmount`: order total.
- `shippingDetails`: name, address, province, city, phone, and postal code.
- `paymentDetails`: card type, cardholder, and billing address.
- `deliveryMethod`: `HOME_DELIVERY` or `STORE_PICKUP`.
- `createdAt`: creation date and time.
- `items`: one-to-many association with order items.

**OrderItem** (`tabla_order_items`):

- `id`: primary key.
- `order`: many-to-one association with an order.
- `car`: many-to-one association with a car.
- `quantity`: purchased quantity.
- `priceAtPurchase`: price snapshot at the time of purchase.

### CarImage and Configuration

**CarImage** (`tabla_car_images`): stores additional image bytes (`imageData`), MIME type (`contentType`), and a many-to-one relationship with a car.

**Configuration** (`tabla_config`): stores application settings as key/value records.

## 🗃️ Repositories and Services

All documented repositories extend `JpaRepository<T, Long>` and may provide custom queries.

### Repositories

| Repository | Responsibility |
|---|---|
| `CarRepository` | Car CRUD, lookup, and search queries |
| `CategoryRepository` | Category CRUD and case-insensitive name lookup |
| `UserRepository` | User persistence and email lookup |
| `OrderRepository` | Order persistence and order-history queries |
| `CartRepository` | Shopping cart lookup by user |
| `CartItemRepository` | Cart item persistence |
| `CarImageRepository` | Additional image lookup by car |
| `OrderItemRepository` | Order item persistence |

### Service Layer

#### `ServiceCars` → `ServiceCarsImplJPA`

**Car operations:**

- Register a car and process its main and additional images.
- Retrieve cars, including paginated and sorted results.
- Search cars with pagination and sorting.
- Retrieve a car by ID.
- Delete a car.
- Save car edits and optionally replace its image.

**Category operations:**

- Retrieve categories and individual categories.
- Create categories with duplicate-name validation.
- Update categories.
- Delete categories while detaching associated cars.

**Image operations:**

- Retrieve all additional images for a car.
- Retrieve an individual image.
- Validate image type and size.
- Generate a 400 × 300 JPEG thumbnail using Thumbnailator.

#### Other Services

| Service | Responsibility |
|---|---|
| `ServiceUsers` → `ServiceUsersImplJPA` | User registration, BCrypt password encoding, lookup, and admin CRUD |
| `ServiceOrder` → `ServiceOrderImplJPA` | Create orders from carts, retrieve orders, and update order status |
| `CartService` → `CartServiceImplJPA` | Create/retrieve carts, add/update/remove items, clear carts, and calculate totals |
| `ServiceSetUp` → `ServiceSetUpImplJPA` | Initial setup, including administrator and default categories |

## 🌐 Application Flows and Routes

### Public Portal

The project documentation describes these public pages and flows:

| Route | Method | Purpose |
|---|---|---|
| `/` | GET | Home page |
| `/vehicles` | GET | Car listing with search and pagination |
| `/car/{id}` | GET | Car details |
| `/search` | GET | Search using query parameters |
| `/register` | GET | User registration |
| `/login` | GET | User login |
| `/my-account` | GET | User profile |
| `/my-orders` | GET | User order history |

### Shopping Cart

| Route | Method | Purpose |
|---|---|---|
| `/cart` | GET | View cart |
| `/cart/add/{carId}` | POST | Add a car to the cart |
| `/cart/update/{itemId}` | POST | Update item quantity |
| `/cart/remove/{itemId}` | POST | Remove an item |

### Checkout — Four Steps

| Step | Documented route | Template | Information |
|---|---|---|---|
| 1 | `/checkout/step1` | `checkout/check_out_1` | Shipping details |
| 2 | `/checkout/step2` | `checkout/check_out_2` | Payment details |
| 3 | `/checkout/step3` | `checkout/check_out_3` | Delivery method |
| 4 | `/checkout/step4` | `checkout/check_out_4` | Order review and confirmation |

The documented checkout process collects shipping details, payment details, and a delivery method, then creates an order and clears the cart after confirmation.

### Admin Panel

All administrative routes are documented under `/admin/**` and are intended to require an administrator session.

| Area | Main routes |
|---|---|
| Dashboard | `/admin/startAdmin` |
| Cars | `/admin/manageCars`, `/admin/registerCar`, `/admin/saveCar`, `/admin/editCar`, `/admin/saveChangesOfCar`, `/admin/deleteCar` |
| Categories | `/admin/manageCategories`, `/admin/registerCategory`, `/admin/saveCategory`, `/admin/editCategory`, `/admin/saveChangesOfCategory`, `/admin/deleteCategory` |
| Users | `/admin/manageUsers`, `/admin/registerUser`, `/admin/saveUser`, `/admin/editUser`, `/admin/saveChangesOfUser`, `/admin/deleteUser`, `/admin/userDetails` |
| Orders | `/admin/manageOrders`, `/admin/orderDetails` |

The category functionality is consolidated into `ControllerAdmin` and `ServiceCars`; separate category controller and service classes were removed according to the source documentation.

> **Route verification note:** route lists above are transcribed from the supplied technical documentation. Before release, compare them with the current controller mappings and update this README if the application routes have changed.

## 🔐 Security

The technical documentation describes the following security mechanisms:

- `SecurityConfig.java` configures Spring Security.
- `InterceptorAdmin` intercepts `/admin/**` routes and checks the `adminLoggedIn` session attribute.
- The admin login handler validates the administrator password against the `admin.password` application property.
- User passwords are encoded with `BCryptPasswordEncoder`.
- The H2 console is enabled for local development.

### Important Security Considerations

The supplied technical documentation also records limitations that should be addressed before deploying the application publicly:

- Some routes are configured with permissive authorization rules; admin protection relies on the MVC interceptor.
- CSRF protection is ignored for several admin, checkout, and API routes.
- The admin password was documented as a plain-text application property. Do not commit real passwords or credentials to GitHub. Use an environment variable or external secret configuration instead.
- Login and registration rate limiting is not documented.

This README intentionally does **not** publish a default administrator password.

## 🌍 Internationalization

The application supports three languages:

| Language | Code | Resource file |
|---|---|---|
| English (default) | `en` | `messages.properties` |
| Spanish | `es` | `messages_es.properties` |
| Russian | `ru` | `messages_ru.properties` |

The documented configuration uses `SessionLocaleResolver` with English as the default locale and `LocaleChangeInterceptor` with the `lang` request parameter.

Examples:

- English: `/?lang=en`
- Spanish: `/?lang=es`
- Russian: `/?lang=ru`

Translations are used in Thymeleaf templates through `#{key}` and can also be accessed in Java through `MessageSource`.

The documented message bundles cover navigation, home page, cars, cart, authentication, account pages, administration, checkout, pagination, search, and validation feedback.

## 🖥️ Templates and User Interface

### Thymeleaf Layout

- `fragments/general.html` contains shared header, footer, language selector, and common scripts.
- The public layout provides navigation, cart, and user-related links.
- The admin layout provides administrative navigation and management pages.

### Styling

`estilos.css` contains the main design system:

- CSS custom properties for colors, spacing, typography, shadows, borders, and transitions.
- Utility classes for flexbox, grids, spacing, typography, and visibility.
- Reusable buttons, cards, forms, tables, badges, and pagination components.
- Responsive breakpoints.
- Dark-mode overrides as documented in the source file.

`admin.css` contains admin-specific components such as statistics cards, tables, status badges, and search/filter controls.

`checkout.css` contains checkout-specific styling.

### JavaScript

The project documentation identifies:

- `admin-orders.js` for client-side order filtering and search.
- JavaScript in shared fragments for language selection.
- jQuery and Mustache templates for parts of the public interface.

## 🗄️ Database Schema

The application uses a file-based H2 database for local development. The schema is generated/updated by Hibernate according to the JPA entity mappings.

| Table | Purpose | Main columns |
|---|---|---|
| `tabla_cars` | Car inventory | `id`, `title`, `price`, `brand`, `description`, image fields, `category_id` |
| `tabla_categories` | Car categories | `id`, `name`, `description` |
| `TABLA_USERS` | Customers and administrators | `id`, `email`, `pass`, names, `phone`, `role` |
| `tabla_cart` | Shopping carts | `id`, `user_id` |
| `tabla_cart_items` | Cart line items | `id`, `cart_id`, `car_id`, `quantity` |
| `tabla_orders` | Customer orders | `id`, `user_id`, `status`, `total_amount`, shipping, payment, delivery, and date fields |
| `tabla_order_items` | Order line items | `id`, `order_id`, `car_id`, `quantity`, `price_at_purchase` |
| `tabla_car_images` | Additional car images | `id`, `car_id`, `image_data`, `content_type` |
| `tabla_config` | Application settings | `id`, `key`, `value` |

### Main Relationships

- `Car` → `Category`: many-to-one.
- `Car` → `CarImage`: one-to-many.
- `User` → `Cart`: one-to-one.
- `Cart` → `CartItem`: one-to-many.
- `User` → `Order`: one-to-many.
- `Order` → `OrderItem`: one-to-many.
- `OrderItem` → `Car`: many-to-one.

### Constraints

- `TABLA_USERS.email` is unique.
- Category name uniqueness is checked in the service layer according to the supplied documentation.
- Foreign-key constraints represent entity relationships.

## 🔌 API Overview

The supplied technical documentation describes these JSON API areas:

### Car API

| Documented endpoint | Method | Purpose |
|---|---|---|
| `/api/cars` | GET | Retrieve cars |
| `/api/cars/page/{page}/size/{size}` | GET | Retrieve paginated cars |
| `/api/cars/{id}` | GET | Retrieve one car |

### Admin HTML Routes

Administrative HTML routes are grouped under `/admin/**`; see [Admin Panel](#admin-panel) for the main route groups.

> API paths can evolve independently of the UI. Verify the current `@RequestMapping` and `@GetMapping` / `@PostMapping` annotations before relying on this table as an API contract.

## ⚙️ Configuration

The project uses `src/main/resources/application.properties` for application configuration.

### Main Settings

The supplied documentation records these settings for local development:

```properties
spring.application.name=tienda.online

# H2 file-based database
spring.datasource.url=jdbc:h2:file:./data/base_de_datos
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.jpa.defer-datasource-initialization=true

# H2 Console
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# File upload
spring.servlet.multipart.max-file-size=20MB
spring.servlet.multipart.max-request-size=100MB

# Configure this outside source control
admin.password=${ADMIN_PASSWORD}

# Internationalization
spring.messages.basename=messages
spring.messages.encoding=UTF-8
spring.messages.fallback-to-system-locale=false
```

Use an environment variable such as `ADMIN_PASSWORD` for the administrator password. Do not commit credentials, production database files, or personal data to the repository.

### Test Configuration

The supplied documentation records an in-memory H2 database for tests:

```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.jpa.hibernate.ddl-auto=create-drop
```

## 🚀 Build and Run

### Requirements

- Java 17
- Maven 3.x (or use the included Maven Wrapper)

### Clone the Repository

```bash
git clone <YOUR_GITHUB_REPOSITORY_URL>
cd ShopOfCars
```

### Build the Project

On macOS / Linux:

```bash
./mvnw clean package
```

On Windows:

```bat
mvnw.cmd clean package
```

Or, if Maven is installed globally:

```bash
mvn clean package
```

### Configure the Administrator Password

Set the environment variable before starting the application.

macOS / Linux:

```bash
export ADMIN_PASSWORD='choose-a-secure-password'
```

Windows PowerShell:

```powershell
$env:ADMIN_PASSWORD = 'choose-a-secure-password'
```

### Run the Application

Using the Maven Wrapper:

```bash
./mvnw spring-boot:run
```

Or run the packaged JAR (adjust the JAR name to the artifact produced in `target/`):

```bash
java -jar target/<application-name>.jar
```

### Local URLs

| Area | URL |
|---|---|
| Public store | `http://localhost:8080/` |
| Admin login | `http://localhost:8080/loginAdmin` |
| H2 Console | `http://localhost:8080/h2-console` |
| Spanish interface | `http://localhost:8080/?lang=es` |
| Russian interface | `http://localhost:8080/?lang=ru` |

For the H2 console, the documented JDBC URL is `jdbc:h2:file:./data/base_de_datos`.

## 🧪 Testing

The supplied documentation describes this test structure:

```text
src/test/java/com/mikhail/shop/cars/
└── ApplicationTests.java
```

The documented test coverage currently includes a basic Spring application-context load test. It does not describe a comprehensive set of unit, repository, controller, or end-to-end tests.

Run the tests with:

```bash
./mvnw test
```

### Recommended Test Coverage

| Layer | Suggested approach |
|---|---|
| Repository | `@DataJpaTest` |
| Service | JUnit 5 and Mockito |
| Controller | `@WebMvcTest` and MockMvc |
| Integration | `@SpringBootTest` for checkout and other key flows |

## ⚠️ Known Limitations and Technical Debt

The following items are recorded in the supplied technical documentation. Their current status should be checked against the latest source code before treating them as unresolved.

### Security

- Administrator password was previously stored as a plain-text property; use an external environment variable or secret store.
- Spring Security authorization was documented as permissive, with admin checks delegated to the MVC interceptor.
- CSRF exclusions were documented for several state-changing routes.
- Rate limiting and explicit password-strength requirements were not documented.

### Architecture

- `ServiceCars` combines car and category operations.
- `ControllerAdmin` handles several administrative domains.
- Some validation and exception handling may be centralized further.
- Open Session in View can contribute to unexpected lazy-loading queries.

### Data and Persistence

- Category uniqueness is enforced in the service layer rather than by a database-level unique constraint.
- No Flyway or Liquibase migration workflow was documented.
- H2 file storage is intended for local use rather than production-scale concurrent access.
- Images are stored as database BLOBs; external storage could be considered as the project grows.

### Testing and Quality

- Comprehensive service, repository, controller, and integration tests were not documented.
- Exception handling was described as inconsistent across controllers.
- Some native SQL strings may be better centralized.
- Some forms bind directly to entities rather than dedicated DTOs.

### User Interface and Performance

- Some admin actions use full-page reloads.
- Loading states and user notifications could be improved.
- Caching and database connection-pool tuning were not documented.
- Synchronous image processing may increase request time for large uploads.

## 🛠️ Recommended Improvements

The original technical documentation groups suggested improvements into the following priorities.

### Priority 1 — Security and Production Readiness

- Move administrator credentials to environment variables or a secret manager.
- Implement role-based authorization consistently.
- Enable CSRF protection for state-changing operations where applicable.
- Add login and registration rate limiting.
- Define password-strength requirements.
- Configure security headers.

### Priority 2 — Architecture

- Consider separating car and category services.
- Consider separating the admin controller by responsibility.
- Introduce a database migration tool such as Flyway or Liquibase.
- Use DTOs for form binding where appropriate.
- Apply validation consistently at controller boundaries.

### Priority 3 — Data and Persistence

- Enforce category-name uniqueness at database level.
- Evaluate PostgreSQL for production.
- Consider filesystem or object storage for images.
- Add audit fields such as `createdAt` and `updatedAt` where useful.

### Priority 4 — Testing and Code Quality

- Add unit tests for service methods.
- Add repository tests.
- Add controller tests.
- Add integration tests for checkout and critical user flows.
- Consider a CI pipeline with GitHub Actions.

### Priority 5 — Performance and UX

- Consider caching for frequently requested catalog data.
- Consider asynchronous image processing.
- Improve partial-page updates in the admin interface.
- Improve catalog pagination and loading indicators.
- Add toast notifications for user feedback.

## 👨‍💻 Development Workflow

### Run Locally

```bash
# 1. Clone and enter the project directory
cd ShopOfCars

# 2. Build the project
./mvnw clean install

# 3. Start the application
./mvnw spring-boot:run
```

Then open `http://localhost:8080/` in your browser.

### IDE Setup

The project can be imported as a Maven project in IntelliJ IDEA or another Java IDE.

1. Open the project using its `pom.xml`.
2. Ensure the project SDK is Java 17.
3. Allow Maven to resolve dependencies.
4. Run the `Application` class to start the application.
5. Use the IDE debugger to inspect controllers and services when needed.

## 📌 Key Files

| File | Purpose |
|---|---|
| `pom.xml` | Maven dependencies and build configuration |
| `Application.java` | Spring Boot entry point |
| `Config.java` | MVC configuration, internationalization, and interceptors |
| `SecurityConfig.java` | Spring Security configuration |
| `InterceptorAdmin.java` | Admin route protection |
| `SetupRunner.java` | Startup setup and seed data |
| `ControllerAdmin.java` | Administrative pages and operations |
| `ControllerPublic.java` | Public portal and car catalog |
| `ControllerCart.java` | Shopping cart operations |
| `ControllerCheckout.java` | Four-step checkout |
| `ControllerUser.java` / `ControllerAuth.java` | User registration, profile, login, and logout |
| `ServiceCars.java` / `ServiceCarsImplJPA.java` | Car and category business logic |
| `ServiceUsers.java` / `ServiceUsersImplJPA.java` | User business logic |
| `ServiceOrder.java` / `ServiceOrderImplJPA.java` | Order business logic |
| `CartService.java` / `CartServiceImplJPA.java` | Shopping cart business logic |
| `Car.java`, `Category.java`, `User.java`, `Order.java`, `Cart.java`, `CarImage.java` | Main domain entities |
| `CarRepository.java`, `CategoryRepository.java`, `UserRepository.java`, `OrderRepository.java` | Data access layer |
| `messages*.properties` | English, Spanish, and Russian translations |
| `estilos.css`, `admin.css`, `checkout.css` | Application stylesheets |
| `templates/admin/*.html` | Administrative UI templates |
| `templates/checkout/*.html` | Checkout templates |
| `application.properties` | Application configuration |

## 🧾 SQL Reference

The technical documentation includes the following example native SQL query used to retrieve car information with its category:

```sql
SELECT
    c.id,
    c.title,
    c.price,
    c.brand,
    c.description,
    c.cover_image,
    cat.name AS category
FROM tabla_cars c
LEFT JOIN tabla_categories cat ON c.category_id = cat.id;
```

To retrieve a specific car, the query adds:

```sql
WHERE c.id = :id
```

---

## 📄 Documentation

This README provides the project overview and technical reference for the GitHub repository. The more detailed project report (`MEMORIA.md` or a formatted PDF/Word version) can be kept alongside it when required for academic submission.

---

*Car Store / ShopOfCars — technical project documentation.*
