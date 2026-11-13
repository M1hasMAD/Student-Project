# ShopOfCars - Technical Documentation (Memoria Técnica)

> **Project:** ShopOfCars (tienda.online)  
> **Version:** 0.0.1-SNAPSHOT  
> **Last Updated:** 2025-11-12  
> **Author:** M1khail  
> **Framework:** Spring Boot 4.1.1 | Java 17 | Spring Data JPA | Thymeleaf | H2 Database

---

## 1. Project Overview

**ShopOfCars** is a full-stack web application for an online car store built with **Spring Boot 4.1.1**. The application provides both a public-facing customer portal and an administrative backend for managing inventory, categories, users, and orders.

### Core Features

| Area | Features |
|------|----------|
| **Public Portal** | Browse cars with search/pagination, view car details, user registration/login, shopping cart, multi-step checkout (shipping → payment → delivery → confirmation) |
| **Admin Panel** | Secure login, car CRUD, category CRUD, user management, order management with status tracking, statistics dashboard |
| **Internationalization** | English (default), Spanish, Russian with locale switching via `?lang=` parameter |
| **Image Handling** | Main cover image + multiple additional images, automatic thumbnail generation (400×300), JPEG/PNG support, 20MB max per image |

---

## 2. Architecture

### Architectural Pattern: Layered Architecture (Controller → Service → Repository)

```
┌─────────────────────────────────────────────────────────────────┐
│                      Presentation Layer                          │
│  Thymeleaf Templates (Server-side rendering) + Static Assets    │
├─────────────────────────────────────────────────────────────────┤
│                      Controller Layer                            │
│  ControllerAdmin, ControllerPublic, ControllerCart,             │
│  ControllerCheckout, ControllerUser, ControllerAuth             │
├─────────────────────────────────────────────────────────────────┤
│                      Service Layer                               │
│  ServiceCarsImplJPA, ServiceUsersImplJPA, ServiceOrderImplJPA,  │
│  CartServiceImplJPA, ServiceSetUpImplJPA                        │
├─────────────────────────────────────────────────────────────────┤
│                      Repository Layer                            │
│  CarRepository, CategoryRepository, UserRepository,             │
│  OrderRepository, CartRepository, CartItemRepository,           │
│  CarImageRepository, OrderItemRepository                        │
├─────────────────────────────────────────────────────────────────┤
│                      Domain Model (JPA Entities)                 │
│  Car, Category, User, Cart, CartItem, Order, OrderItem,         │
│  CarImage, Configuration                                        │
├─────────────────────────────────────────────────────────────────┤
│                      Database (H2 File-based)                    │
│  ./data/base_de_datos.mv.db                                      │
└─────────────────────────────────────────────────────────────────┘
```

### Design Principles

- **Interface-based Services**: `ServiceCars` interface with `ServiceCarsImplJPA` implementation (allows easy swapping/testing)
- **Transactional Services**: `@Transactional` on service implementations; read-only for queries
- **Bean Validation**: JSR-380 annotations on entities (`@NotEmpty`, `@Size`, `@Min`, `@Max`, `@Pattern`)
- **Custom Exceptions**: Hierarchy with `BusinessException` base, specific exceptions for domain errors
- **Security by Interceptor**: Custom `InterceptorAdmin` for admin routes (session-based password check)
- **Open Session in View**: Default Spring Boot behavior (Hibernate `spring.jpa.open-in-view=true`)

---

## 3. Technology Stack

| Category | Technology | Version |
|----------|------------|---------|
| **Core Framework** | Spring Boot | 4.1.1 |
| **Language** | Java | 17 |
| **Build Tool** | Maven | 3.x (Wrapper) |
| **Database** | H2 (file-based) | 2.x |
| **ORM** | Spring Data JPA / Hibernate | 6.x |
| **Template Engine** | Thymeleaf | 3.x |
| **Security** | Spring Security | 6.x |
| **Validation** | Hibernate Validator | 8.x |
| **Image Processing** | Thumbnailator | 0.4.20 |
| **JSON** | Gson | 2.13.2 |
| **Testing** | Spring Boot Test, JUnit 5 | - |

### Key Dependencies (from `pom.xml`)

```xml
<!-- Core -->
spring-boot-starter-webmvc
spring-boot-starter-data-jpa
spring-boot-starter-thymeleaf
spring-boot-starter-security
spring-boot-starter-validation

<!-- Database -->
h2 (runtime)
spring-boot-h2console

<!-- Utilities -->
gson:2.13.2
thumbnailator:0.4.20

<!-- Dev/Test -->
spring-boot-devtools
spring-boot-starter-data-jpa-test
spring-boot-starter-thymeleaf-test
spring-boot-starter-webmvc-test
```

---

## 4. Project Structure

```
src/main/java/com/mikhail/shop/cars/
├── Application.java                    # @SpringBootApplication entry point
├── Config.java                         # WebMvcConfigurer: i18n + admin interceptor
├── SetupRunner.java                    # ApplicationRunner: seed admin user, categories
├── interceptores/
│   └── InterceptorAdmin.java           # Admin route protection (session password check)
├── constantesSQL/
│   └── ConstantesSQL.java              # SQL constants for native queries
├── controllers/
│   ├── ControllerAdmin.java            # Admin panel: cars, categories, users, orders
│   ├── ControllerPublic.java           # Public: home, car listing, search, car details
│   ├── ControllerCart.java             # Shopping cart operations
│   ├── ControllerCheckout.java         # Multi-step checkout flow
│   ├── ControllerUser.java             # User registration, profile
│   └── ControllerAuth.java             # Login/logout
├── dto/
│   ├── DeliveryMethod.java             # Enum: HOME_DELIVERY, STORE_PICKUP
│   ├── PaymentDetails.java             # Card type, holder, billing address
│   └── ShippingDetails.java            # Name, address, province, city, phone, postal code
├── enums/
│   └── Orders.java                     # Order status: PENDING, IN_PROGRESS, COMPLETED, CANCELLED
├── exception/
│   ├── BusinessException.java          # Base exception for business logic errors
│   ├── CarNotFoundException.java       # Car not found by ID
│   ├── CategoryNotFoundException.java  # Category not found by ID
│   ├── DuplicateCategoryException.java # Duplicate category name
│   └── EntityNotFoundException.java    # Base for entity-not-found exceptions
├── interfaces/
│   ├── ServiceCars.java                # Car & Category service contract
│   ├── ServiceUsers.java               # User service contract
│   ├── ServiceOrder.java               # Order service contract
│   ├── CartService.java                # Cart service contract
│   └── ServiceSetUp.java               # Setup/seed service contract
├── model/
│   ├── Car.java                        # Car entity (JPA)
│   ├── Category.java                   # Category entity (JPA)
│   ├── User.java                       # User entity (JPA) with Role enum
│   ├── Cart.java                       # Cart entity (JPA)
│   ├── CartItem.java                   # Cart item entity (JPA)
│   ├── Order.java                      # Order entity (JPA)
│   ├── OrderItem.java                  # Order item entity (JPA)
│   ├── CarImage.java                   # Car image entity (JPA)
│   └── Configuration.java              # App configuration entity (JPA)
├── repository/
│   ├── CarRepository.java              # Car JPA repository + search queries
│   ├── CategoryRepository.java         # Category JPA repository
│   ├── UserRepository.java             # User JPA repository
│   ├── OrderRepository.java            # Order JPA repository
│   ├── CartRepository.java             # Cart JPA repository
│   ├── CartItemRepository.java         # CartItem JPA repository
│   ├── CarImageRepository.java         # CarImage JPA repository
│   └── OrderItemRepository.java        # OrderItem JPA repository
├── serviciosImpl/
│   ├── ServiceCarsImplJPA.java         # Car/Category service implementation
│   ├── ServiceUsersImplJPA.java        # User service implementation
│   ├── ServiceOrderImplJPA.java        # Order service implementation
│   ├── CartServiceImplJPA.java         # Cart service implementation
│   └── ServiceSetUpImplJPA.java        # Setup/seed implementation
└── config/
    └── SecurityConfig.java             # Spring Security configuration
```

### Resources

```
src/main/resources/
├── application.properties              # Main configuration
├── messages.properties                 # English (default) i18n messages
├── messages_es.properties              # Spanish i18n messages
├── messages_ru.properties              # Russian i18n messages
├── templates/
│   ├── index.html                      # Home page
│   ├── register.html                   # User registration
│   ├── login.html                      # User login
│   ├── car_list.html                   # Car listing with search/pagination
│   ├── car_detail.html                 # Car detail page
│   ├── cart.html                       # Shopping cart
│   ├── checkout/
│   │   ├── check_out_1.html           # Step 1: Shipping details
│   │   ├── check_out_2.html           # Step 2: Payment details
│   │   ├── check_out_3.html           # Step 3: Delivery method
│   │   └── check_out_4.html           # Step 4: Order confirmation
│   ├── fragments/
│   │   └── general.html               # Header, footer, language selector
│   └── admin/
│       ├── start_admin.html           # Admin dashboard
│       ├── login_admin.html           # Admin login
│       ├── manage_cars.html           # Car management (CRUD + search/pagination)
│       ├── manage_categories.html     # Category management (CRUD)
│       ├── manage_users.html          # User management (CRUD)
│       ├── manage_orders.html         # Order management (list, filter, stats)
│       ├── order_details.html         # Order detail + status change
│       ├── user_details.html          # User detail + role change
│       ├── edit_car.html              # Edit car form
│       ├── edit_category.html         # Edit category form
│       ├── edit_user.html             # Edit user form
│       ├── manage_category.html       # Register category form
│       ├── manage_product.html        # Legacy product management
│       └── register_product_ok.html   # Success page
├── static/
│   ├── css/
│   │   ├── estilos.css                # Main stylesheet (CSS custom properties)
│   │   ├── admin.css                  # Admin-specific styles
│   │   └── checkout.css               # Checkout-specific styles
│   ├── js/
│   │   └── admin-orders.js            # Admin orders filtering/pagination
│   └── plantillas_html/               # Legacy HTML templates
└── test/
    └── resources/application.properties # Test configuration (H2 mem)
```

---

## 5. Domain Models (JPA Entities)

### 5.1 Car (`tabla_cars`)

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| `id` | Long | `@Id`, `@GeneratedValue(IDENTITY)` | Primary key |
| `title` | String | `@NotEmpty`, `@Size(3-40)`, `@Pattern` | Car title (letters, numbers, hyphens, spaces) |
| `price` | double | `@Min(1)`, `@Max(99999)` | Price in euros |
| `brand` | String | `@NotNull` | Car brand |
| `description` | String | - | Optional description |
| `coverImage` | byte[] | `@Lob` | Main image (JPEG/PNG, max 20MB) |
| `thumbnailImage` | byte[] | `@Lob` | Auto-generated 400×300 thumbnail |
| `category` | Category | `@ManyToOne` | Category association |
| `images` | List<CarImage> | `@OneToMany(cascade=ALL, orphanRemoval=true)` | Additional images |
| `uploadedPhoto` | MultipartFile | `@Transient` | Temp upload field |
| `uploadedPhotos` | List<MultipartFile> | `@Transient` | Temp multiple upload field |

**Relationships:**
- `ManyToOne` → Category (optional, can be null)
- `OneToMany` → CarImage (cascade ALL, orphan removal)

### 5.2 Category (`tabla_categories`)

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| `id` | Long | `@Id`, `@GeneratedValue(IDENTITY)` | Primary key |
| `name` | String | - | Category name (unique, case-insensitive) |
| `description` | String | - | Category description |
| `cars` | List<Car> | `@OneToMany(mappedBy="category")`, `@JsonIgnore` | Back-reference to cars |

**Business Rules:**
- Name must be unique (case-insensitive) - enforced in `ServiceCarsImplJPA.createCategory()`
- Cannot delete if cars exist - cars' category set to null first

### 5.3 User (`TABLA_USERS`)

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| `id` | Long | `@Id`, `@GeneratedValue(IDENTITY)` | Primary key |
| `email` | String | `@Column(unique=true, nullable=false)` | Login email (unique) |
| `pass` | String | `@Column(nullable=false)` | BCrypt-hashed password |
| `firstName` | String | `@Column(nullable=false)` | First name |
| `lastName` | String | `@Column(nullable=false)` | Last name |
| `phone` | String | `@Column(nullable=false)` | Phone number |
| `role` | Role | `@Enumerated(STRING)`, default USER | USER or ADMIN |
| `cart` | Cart | `@OneToOne(mappedBy="user", cascade=ALL)`, `@JsonIgnore` | User's shopping cart |

### 5.4 Cart & CartItem

**Cart** (`tabla_cart`):
- `id` (PK)
- `user` (OneToOne, mappedBy="cart")
- `items` (OneToMany → CartItem, cascade ALL, orphanRemoval)

**CartItem** (`tabla_cart_items`):
- `id` (PK)
- `cart` (ManyToOne)
- `car` (ManyToOne)
- `quantity` (int)

### 5.5 Order & OrderItem

**Order** (`tabla_orders`):
- `id` (PK)
- `user` (ManyToOne)
- `status` (OrderStatus enum: PENDING, IN_PROGRESS, COMPLETED, CANCELLED)
- `totalAmount` (double)
- `shippingDetails` (embeddable: fullName, address, province, city, phone, postalCode)
- `paymentDetails` (embeddable: cardType, cardholder, billingAddress)
- `deliveryMethod` (DeliveryMethod enum: HOME_DELIVERY, STORE_PICKUP)
- `createdAt` (LocalDateTime)
- `items` (OneToMany → OrderItem, cascade ALL)

**OrderItem** (`tabla_order_items`):
- `id` (PK)
- `order` (ManyToOne)
- `car` (ManyToOne)
- `quantity` (int)
- `priceAtPurchase` (double) - snapshot of price at order time

### 5.6 CarImage (`tabla_car_images`)

| Field | Type | Description |
|-------|------|-------------|
| `id` | Long | PK |
| `imageData` | byte[] | Image bytes (@Lob) |
| `contentType` | String | image/jpeg or image/png |
| `car` | Car | ManyToOne back-reference |

### 5.7 Configuration (`tabla_config`)

- `id` (PK)
- `key` (String, unique)
- `value` (String)
- Used for application settings storage

---

## 6. Repositories

All repositories extend `JpaRepository<T, Long>` with custom queries where needed.

### CarRepository
```java
List<Car> findAllByOrderByIdDesc();
Optional<Car> findByTitleIgnoreCase(String title);
Page<Car> findBySearchTerm(String search, Pageable pageable);  // Search across title, brand, description, category
List<Car> findBySearchTermList(String search, Pageable pageable); // Same without pagination
```

### CategoryRepository
- Standard CRUD + `existsByNameIgnoreCase(String name)`
- `findByNameIgnoreCase(String name)` (returns Optional)

### UserRepository
- `findByEmail(String email)` (unique lookup)

### OrderRepository
- `findByUserOrderByCreatedAtDesc(User user)` - user's order history
- `findAllByOrderByCreatedAtDesc()` - admin: all orders

### CartRepository
- `findByUser(User user)` - get user's cart

### CarImageRepository
- `findByCarIdOrderById(Long carId)` - ordered images for a car

---

## 7. Services

### 7.1 ServiceCars (Interface) → ServiceCarsImplJPA

**Car Operations:**
- `registerCar(Car car)` - Save car + process main image + additional images
- `getCars()` - All cars (List)
- `getCars(page, size, sortBy, sortDir)` - Paginated + sorted
- `searchCars(search, page, size, sortBy, sortDir)` - Paginated search
- `searchCarsList(search, page, size, sortBy, sortDir)` - List search
- `getCarById(id)` - Single car by ID
- `deleteCarById(id)` - Delete with CarNotFoundException
- `saveChangesOfCar(carToEdit, categoryId)` - Update car + optional image replacement

**JSON API Methods:**
- `getJSONCars()` - Native SQL query: all cars as List<Map>
- `getJSONCarsPaginated(page, size)` - Paginated JSON
- `getJSONCarById(id)` - Single car as Map

**Category Operations:**
- `getCategories()` - All categories
- `getCategoryById(id)` - Category by ID
- `createCategory(category)` - Throws DuplicateCategoryException
- `updateCategory(category)` - Throws DuplicateCategoryException/CategoryNotFoundException
- `deleteCategory(id)` - Sets category=null on associated cars first

**Image Operations:**
- `getImagesByCarId(carId)` - All additional images
- `getImageById(imageId)` - Single image

**Image Processing (Private):**
- `processMainImage(car, file)` - Validates, stores cover + generates thumbnail
- `saveAdditionalImages(car, files)` - Stores additional CarImage entities
- `validateImage(file)` - Validates format (JPEG/PNG), size (<20MB), reads via ImageIO
- `createThumbnail(bytes)` - Thumbnailator 400×300 JPEG

### 7.2 ServiceUsers (Interface) → ServiceUsersImplJPA
- User registration with BCrypt password encoding
- User lookup by email
- User CRUD for admin

### 7.3 ServiceOrder (Interface) → ServiceOrderImplJPA
- Create order from cart + checkout details
- Get orders by user
- Get all orders (admin)
- Get order by ID
- Update order status

### 7.4 CartService (Interface) → CartServiceImplJPA
- Get/create cart for user
- Add/remove/update cart items
- Clear cart
- Calculate totals

### 7.5 ServiceSetUp (Interface) → ServiceSetUpImplJPA
- `setup()` - Creates admin user (from `admin.password` property), seeds default categories

---

## 8. Controllers

### 8.1 ControllerAdmin (Main Admin Controller)

| Endpoint | Method | Template | Description |
|----------|--------|----------|-------------|
| `/loginAdmin` | GET | `admin/login_admin` | Admin login form |
| `/loginAdmin` | POST | redirect | Password validated by InterceptorAdmin |
| `/admin/startAdmin` | GET | `admin/start_admin` | Admin dashboard |
| `/logoutAdmin` | GET | redirect | Invalidate session |
| `/admin/manageCategories` | GET | `admin/manage_categories` | List categories |
| `/admin/registerCategory` | GET | `admin/manage_category` | New category form |
| `/admin/saveCategory` | POST | redirect | Create category |
| `/admin/editCategory?id=` | GET | `admin/edit_category` | Edit category form |
| `/admin/saveChangesOfCategory` | POST | redirect | Update category |
| `/admin/deleteCategory?id=` | GET | redirect | Delete category |
| *(Car management similar)* | | | |

**Note:** `ControllerCategories.java`, `ServiceCategories.java`, `ServiceCategoriesImplJPA.java` were **deleted** - category management consolidated into `ControllerAdmin` + `ServiceCars`.

### 8.2 ControllerPublic (Public Portal)

| Endpoint | Method | Template | Description |
|----------|--------|----------|-------------|
| `/` | GET | `index` | Home page |
| `/vehicles` | GET | `car_list` | Car listing with search/pagination |
| `/car/{id}` | GET | `car_detail` | Car detail page |
| `/search` | GET | `car_list` | Search with query params |

### 8.3 ControllerCart

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/cart` | GET | View cart |
| `/cart/add/{carId}` | POST | Add to cart |
| `/cart/update/{itemId}` | POST | Update quantity |
| `/cart/remove/{itemId}` | POST | Remove item |

### 8.4 ControllerCheckout (4-Step Flow)

| Step | Endpoint | Template | Data Collected |
|------|----------|----------|----------------|
| 1 | `/checkout/step1` | `checkout/check_out_1` | ShippingDetails |
| 2 | `/checkout/step2` | `checkout/check_out_2` | PaymentDetails |
| 3 | `/checkout/step3` | `checkout/check_out_3` | DeliveryMethod |
| 4 | `/checkout/step4` | `checkout/check_out_4` | Order Confirmation |

### 8.5 ControllerUser / ControllerAuth
- `/register` - User registration
- `/login` - User login
- `/my-account` - User profile
- `/my-orders` - User order history

---

## 9. Security

### 9.1 SecurityConfig (`SecurityConfig.java`)

```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/h2-console/**").permitAll()
    .requestMatchers("/api/users/**").permitAll()
    .requestMatchers("/register", "/login").permitAll()
    .requestMatchers("/css/**", "/js/**", "/images/**", "/librerias_js/**", "/plantillas_html/**").permitAll()
    .anyRequest().permitAll()  // NOTE: All requests permitted!
)
```

**⚠️ Current State:** Security is **minimally configured** - all requests permitted. Admin protection is handled by `InterceptorAdmin` at the MVC interceptor level, not Spring Security.

### 9.2 InterceptorAdmin (`InterceptorAdmin.java`)

- Intercepts `/admin/**` routes
- Checks session for `adminLoggedIn` attribute
- Redirects to `/loginAdmin` if not authenticated
- Password validated in `ControllerAdmin.loginAdminPost()` against `admin.password` property (default: "123")

### 9.3 Password Encoding
- `BCryptPasswordEncoder` bean defined
- User passwords hashed on registration
- Admin password stored as plain text in `application.properties` (should be env var in production)

### 9.4 CSRF Configuration
```java
.csrf(csrf -> csrf
    .ignoringRequestMatchers("/h2-console/**", "/api/users/**", "/api/cart/**", 
                             "/checkout/**", "/loginAdmin", "/admin/**")
)
```
**⚠️ CSRF disabled** for admin, checkout, and API endpoints.

### 9.5 H2 Console
- Enabled at `/h2-console`
- Frame options set to SAMEORIGIN for H2 console iframe

---

## 10. Internationalization (i18n)

### Configuration (`Config.java`)
```java
@Bean
public LocaleResolver localeResolver() {
    SessionLocaleResolver resolver = new SessionLocaleResolver();
    resolver.setDefaultLocale(Locale.ENGLISH);
    return resolver;
}

@Bean
public LocaleChangeInterceptor localeChangeInterceptor() {
    LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
    interceptor.setParamName("lang");
    return interceptor;
}
```

### Supported Locales
| Language | Code | File |
|----------|------|------|
| English | `en` | `messages.properties` |
| Spanish | `es` | `messages_es.properties` |
| Russian | `ru` | `messages_ru.properties` |

### Usage
- Switch language: `?lang=es` or `?lang=ru`
- Stored in session via `SessionLocaleResolver`
- Thymeleaf: `#{key}` syntax in templates
- Java: `messageSource.getMessage("key", args, locale)`

### Message Categories (300+ keys)
- Navigation, hero, home, car listing, cart, footer
- Login, registration, account
- Admin: login, dashboard, cars, categories, users, orders
- Checkout: 4 steps with detailed labels
- Pagination, search, validation messages
- Error/success flash messages

---

## 11. Templates & UI

### Template Engine: Thymeleaf 3.x

### Layout Structure
- **Fragment**: `fragments/general.html` - Header, footer, language selector, common scripts
- **Admin Layout**: Admin header with navigation, main content area, footer
- **Public Layout**: Public header with nav/cart/user, main content, footer

### CSS Architecture (`estilos.css`)
- **CSS Custom Properties** for theming (colors, spacing, typography, shadows, radius, transitions)
- **Utility Classes**: flex, grid, spacing, typography, visibility
- **Components**: buttons (primary/secondary/ghost/accent/error), cards, forms, tables, badges, pagination
- **Responsive**: Mobile-first, breakpoints at 640px, 1024px
- **Dark Mode**: `@media (prefers-color-scheme: dark)` overrides

### Admin-Specific CSS (`admin.css`)
- Stat cards with icons
- Category/order tables with action columns
- Status badges (success/primary/accent/error)
- Search/filter controls

### JavaScript
- `admin-orders.js` - Client-side search/filter/pagination for orders table
- `fragments/general.html` - Language selector handler

---

## 12. Database Schema (H2)

### Tables (Auto-generated via `spring.jpa.hibernate.ddl-auto=update`)

| Table | Description | Key Columns |
|-------|-------------|-------------|
| `tabla_cars` | Car inventory | id, title, price, brand, description, cover_image, thumbnail_image, category_id |
| `tabla_categories` | Car categories | id, name, description |
| `TABLA_USERS` | Users (customers + admins) | id, email, pass, first_name, last_name, phone, role |
| `tabla_cart` | Shopping carts | id, user_id |
| `tabla_cart_items` | Cart line items | id, cart_id, car_id, quantity |
| `tabla_orders` | Customer orders | id, user_id, status, total_amount, shipping_*, payment_*, delivery_method, created_at |
| `tabla_order_items` | Order line items | id, order_id, car_id, quantity, price_at_purchase |
| `tabla_car_images` | Additional car images | id, car_id, image_data, content_type |
| `tabla_config` | App configuration | id, key, value |

### Key Relationships
- `Car` → `Category` (ManyToOne, FK: category_id)
- `Car` → `CarImage` (OneToMany, FK: car_id, cascade ALL)
- `User` → `Cart` (OneToOne, FK: user_id in tabla_cart)
- `Cart` → `CartItem` (OneToMany, FK: cart_id)
- `User` → `Order` (OneToMany, FK: user_id)
- `Order` → `OrderItem` (OneToMany, FK: order_id)
- `OrderItem` → `Car` (ManyToOne, FK: car_id)

### Indexes & Constraints
- `TABLA_USERS.email` - UNIQUE
- `tabla_categories.name` - Unique (enforced in service, not DB)
- FK constraints on all relationships

---

## 13. API Endpoints Summary

### Public API (JSON)
| Endpoint | Method | Response | Description |
|----------|--------|----------|-------------|
| `/api/cars` | GET | `List<Map>` | All cars (native query) |
| `/api/cars/page/{page}/size/{size}` | GET | `List<Map>` | Paginated cars |
| `/api/cars/{id}` | GET | `Map` | Single car |

### Admin Routes (HTML)
All under `/admin/**` - require admin session

| Area | Routes |
|------|--------|
| Dashboard | `/admin/startAdmin` |
| Cars | `/admin/manageCars`, `/admin/registerCar`, `/admin/saveCar`, `/admin/editCar`, `/admin/saveChangesOfCar`, `/admin/deleteCar` |
| Categories | `/admin/manageCategories`, `/admin/registerCategory`, `/admin/saveCategory`, `/admin/editCategory`, `/admin/saveChangesOfCategory`, `/admin/deleteCategory` |
| Users | `/admin/manageUsers`, `/admin/registerUser`, `/admin/saveUser`, `/admin/editUser`, `/admin/saveChangesOfUser`, `/admin/deleteUser`, `/admin/userDetails` |
| Orders | `/admin/manageOrders`, `/admin/orderDetails` |

### Checkout Flow (Session-based)
1. `POST /checkout/step1` → saves ShippingDetails in session
2. `POST /checkout/step2` → saves PaymentDetails in session
3. `POST /checkout/step3` → saves DeliveryMethod in session
4. `POST /checkout/step4` → creates Order, clears cart, shows confirmation

---

## 14. Configuration

### `application.properties`
```properties
# App
spring.application.name=tienda.online

# Database (H2 file-based)
spring.datasource.url=jdbc:h2:file:./data/base_de_datos
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.jpa.defer-datasource-initialization=true

# H2 Console
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# File Upload
spring.servlet.multipart.max-file-size=20MB
spring.servlet.multipart.max-request-size=100MB

# Admin (override in production via env var)
admin.password=123

# i18n
spring.messages.basename=messages
spring.messages.encoding=UTF-8
spring.messages.fallback-to-system-locale=false
```

### Test Configuration (`src/test/resources/application.properties`)
```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.jpa.hibernate.ddl-auto=create-drop
```

---

## 15. Build & Deployment

### Build
```bash
# Using Maven Wrapper
./mvnw clean package

# Or with Maven directly
mvn clean package
```

### Run
```bash
# Development
./mvnw spring-boot:run

# Production (JAR)
java -jar target/otro-0.0.1-SNAPSHOT.jar

# With custom admin password
java -Dadmin.password=secure123 -jar target/otro-0.0.1-SNAPSHOT.jar
```

### Database
- **Development**: File-based H2 at `./data/base_de_datos.mv.db`
- **Test**: In-memory H2
- **Production**: Should migrate to PostgreSQL/MySQL (change datasource URL + driver)

### Maven Profile Considerations
- No profiles defined; use environment variables for configuration

---

## 16. Recent Changes (Git History)

| Commit | Date | Description |
|--------|------|-------------|
| `ba15948` | Recent | Add security config and user details page |
| `104b911` | Recent | Fix "My Orders" page & JSON serialization |
| `90de9de` | Recent | Add order status management and admin/user logout |
| `639d699` | Recent | Add cookie-based user identification and logout |
| `4479169` | Recent | Add search and pagination for cars in public and admin area |

### Notable Refactoring (Current Working Tree)
- **Deleted**: `ControllerCategories.java`, `ServiceCategories.java`, `ServiceCategoriesImplJPA.java`
- **Consolidated**: Category management into `ControllerAdmin` + `ServiceCars`
- **Added**: `CategoryNotFoundException.java`, `DuplicateCategoryException.java`
- **Modified**: `ControllerAdmin.java`, `ServiceCars.java`, `ServiceCarsImplJPA.java`, `CategoryRepository.java`
- **Updated**: Admin templates (`manage_categories.html`, `manage_orders.html`), i18n messages, CSS

---

## 17. Testing

### Test Structure
```
src/test/java/com/mikhail/shop/cars/
└── ApplicationTests.java  # @SpringBootTest context load test
```

### Current State
- Only basic context load test exists
- **No unit tests** for services, repositories, or controllers
- **No integration tests** for web layer or security

### Recommended Test Coverage
| Layer | Target | Tools |
|-------|--------|-------|
| Repository | 80%+ | `@DataJpaTest`, Testcontainers (for PostgreSQL) |
| Service | 80%+ | `@ExtendWith(MockitoExtension.class)`, `@MockBean` |
| Controller | 70%+ | `@WebMvcTest`, MockMvc |
| Integration | Key flows | `@SpringBootTest`, Testcontainers |

---

## 18. Known Issues & Technical Debt

### Security
1. **Admin password in plaintext** in `application.properties` - should use environment variable
2. **Spring Security permits all requests** - relies on MVC interceptor for admin protection
3. **CSRF disabled** for admin, checkout, API endpoints
4. **No rate limiting** on login/register endpoints
5. **No password complexity requirements** for user registration
6. **Session fixation protection** not explicitly configured

### Architecture
1. **ServiceCars interface does too much** - mixes Car and Category operations (violates ISP)
2. **ControllerAdmin is a God Controller** - handles cars, categories, users, orders
3. **Business logic in service implementation** - some validation could move to domain entities
4. **Open Session in View** enabled by default - potential N+1 queries in templates

### Data & Persistence
1. **Category name uniqueness** enforced only in service, not DB constraint
2. **No database migration tool** (Flyway/Liquibase) - relies on `ddl-auto=update`
3. **H2 file-based** not suitable for production concurrent access
4. **Large binary data** (images) stored in database - consider filesystem/S3

### Code Quality
1. **No comprehensive test suite** - only context load test
2. **Inconsistent exception handling** - some controllers catch exceptions, others don't
3. **Magic strings** in native SQL queries (`ConstantesSQL.java` helps but not used everywhere)
4. **DTOs only for checkout** - other forms bind directly to entities
5. **Spanish/English mixed** in code comments and some variable names

### UI/UX
1. **No client-side validation** - relies on server-side only
2. **No AJAX/fetch** - full page reloads for all admin actions
3. **Pagination only in admin** - public car listing has search but limited pagination UI
4. **No loading states** for form submissions

### Performance
1. **No caching** - all queries hit database
2. **No connection pool tuning** - uses HikariCP defaults
3. **Image processing synchronous** - blocks request thread
4. **Native SQL queries** bypass JPA second-level cache

---

## 19. Recommended Improvements

### Priority 1 (Security & Production Readiness)
- [ ] Move `admin.password` to environment variable only
- [ ] Implement proper Spring Security with role-based access (`@PreAuthorize("hasRole('ADMIN')")`)
- [ ] Enable CSRF for state-changing operations
- [ ] Add rate limiting (Spring Boot Actuator + Bucket4j)
- [ ] Configure password strength requirements
- [ ] Add security headers (CSP, HSTS, etc.)

### Priority 2 (Architecture)
- [ ] Split `ServiceCars` into `CarService` and `CategoryService`
- [ ] Split `ControllerAdmin` into `CarAdminController`, `CategoryAdminController`, `UserAdminController`, `OrderAdminController`
- [ ] Add Flyway/Liquibase for database migrations
- [ ] Introduce DTOs for all form bindings
- [ ] Add `@Valid` + `@Validated` on controller parameters

### Priority 3 (Data & Persistence)
- [ ] Add unique constraint on `tabla_categories.name` (case-insensitive index)
- [ ] Plan migration to PostgreSQL for production
- [ ] Move image storage to filesystem/S3 with DB references only
- [ ] Add audit fields (createdAt, updatedAt) to entities

### Priority 4 (Testing & Quality)
- [ ] Add unit tests for all service methods
- [ ] Add repository tests with `@DataJpaTest`
- [ ] Add controller tests with `@WebMvcTest`
- [ ] Add integration tests for checkout flow
- [ ] Set up CI/CD pipeline (GitHub Actions)

### Priority 5 (Performance & UX)
- [ ] Add Redis caching for car listings, categories
- [ ] Implement async image processing (CompletableFuture or @Async)
- [ ] Add HTMX/Alpine.js for partial updates in admin
- [ ] Implement infinite scroll or proper pagination in public car listing
- [ ] Add loading indicators and toast notifications

---

## 20. Development Workflow

### Running Locally
```bash
# 1. Clone & build
cd ShopOfCars
./mvnw clean install

# 2. Run (dev mode with live reload)
./mvnw spring-boot:run

# 3. Access
# Public: http://localhost:8080
# Admin: http://localhost:8080/loginAdmin (password: 123)
# H2 Console: http://localhost:8080/h2-console (JDBC: jdbc:h2:file:./data/base_de_datos)
# Language: http://localhost:8080/?lang=es
```

### IDE Setup (IntelliJ IDEA / VS Code)
- Import as Maven project
- Enable annotation processing (Lombok not used but JPA needs it)
- Run `Application.main()` for debugging
- Thymeleaf plugin recommended for template editing

### Code Style
- Standard Java conventions
- 4-space indentation
- No Checkstyle/SpotBugs configured (recommended to add)
- Spanish/English mix in comments - standardize to English

---

## 21. File Index (Key Files)

| File | Purpose |
|------|---------|
| `pom.xml` | Maven build configuration |
| `Application.java` | Spring Boot entry point |
| `Config.java` | MVC config: i18n, interceptors |
| `SecurityConfig.java` | Spring Security config |
| `InterceptorAdmin.java` | Admin route protection |
| `SetupRunner.java` | Startup data seeding |
| `ControllerAdmin.java` | Main admin controller |
| `ControllerPublic.java` | Public portal controller |
| `ControllerCart.java` | Cart operations |
| `ControllerCheckout.java` | 4-step checkout |
| `ServiceCars.java` / `ServiceCarsImplJPA.java` | Car/Category service |
| `ServiceUsers.java` / `ServiceUsersImplJPA.java` | User service |
| `ServiceOrder.java` / `ServiceOrderImplJPA.java` | Order service |
| `CartService.java` / `CartServiceImplJPA.java` | Cart service |
| `Car.java`, `Category.java`, `User.java`, `Order.java`, `Cart.java`, `CarImage.java` | Domain entities |
| `CarRepository.java`, `CategoryRepository.java`, `UserRepository.java`, `OrderRepository.java` | Data access |
| `messages.properties`, `messages_es.properties`, `messages_ru.properties` | i18n bundles |
| `estilos.css`, `admin.css`, `checkout.css` | Stylesheets |
| `templates/admin/*.html` | Admin UI templates |
| `templates/checkout/*.html` | Checkout flow templates |
| `application.properties` | Main configuration |

---

## 22. Appendix: SQL Reference (ConstantesSQL.java)

```java
// Example native queries used in ServiceCarsImplJPA
SELECT c.id, c.title, c.price, c.brand, c.description, 
       c.cover_image, cat.name as category
FROM tabla_cars c 
LEFT JOIN tabla_categories cat ON c.category_id = cat.id

// With WHERE clause for getJSONCarById
WHERE c.id = :id
```

---

*Generated as technical documentation (Memoria Técnica) for the ShopOfCars project.*
*This document should be updated as the project evolves.*