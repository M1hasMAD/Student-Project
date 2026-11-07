package com.mikhail.shop.cars.serviciosImpl;

import com.mikhail.shop.cars.interfaces.ServiceSetUp;
import com.mikhail.shop.cars.model.*;
import com.mikhail.shop.cars.repository.CarRepository;
import com.mikhail.shop.cars.repository.CategoryRepository;
import com.mikhail.shop.cars.repository.OrderRepository;
import com.mikhail.shop.cars.repository.OrderItemRepository;
import com.mikhail.shop.cars.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ServiceSetUpImplJPA implements ServiceSetUp {

    private static final Logger log = LoggerFactory.getLogger(ServiceSetUpImplJPA.class);

    private final CategoryRepository categoryRepository;
    private final CarRepository carRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PasswordEncoder passwordEncoder;

    public ServiceSetUpImplJPA(CategoryRepository categoryRepository,
                                CarRepository carRepository,
                                UserRepository userRepository,
                                OrderRepository orderRepository,
                                OrderItemRepository orderItemRepository,
                                PasswordEncoder passwordEncoder) {
        this.categoryRepository = categoryRepository;
        this.carRepository = carRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void prepareInitialRecords() {

        // 1. Create categories if they don't exist.
        Category suv = categoryRepository.findByNameIgnoreCase("SUV")
                .orElseGet(() -> categoryRepository.save(new Category("SUV", "Sport utility vehicles")));
        Category sedan = categoryRepository.findByNameIgnoreCase("Sedan")
                .orElseGet(() -> categoryRepository.save(new Category("Sedan", "Four-door passenger cars")));
        Category hatchback = categoryRepository.findByNameIgnoreCase("Hatchback")
                .orElseGet(() -> categoryRepository.save(new Category("Hatchback", "Compact cars with rear hatch door")));
        Category coupe = categoryRepository.findByNameIgnoreCase("Coupe")
                .orElseGet(() -> categoryRepository.save(new Category("Coupe", "Two-door sporty cars")));
        Category convertible = categoryRepository.findByNameIgnoreCase("Convertible")
                .orElseGet(() -> categoryRepository.save(new Category("Convertible", "Cars with retractable roof")));
        Category pickup = categoryRepository.findByNameIgnoreCase("Pickup")
                .orElseGet(() -> categoryRepository.save(new Category("Pickup", "Trucks with open cargo bed")));
        Category wagon = categoryRepository.findByNameIgnoreCase("Wagon")
                .orElseGet(() -> categoryRepository.save(new Category("Wagon", "Extended roof cargo space")));
        Category electric = categoryRepository.findByNameIgnoreCase("Electric")
                .orElseGet(() -> categoryRepository.save(new Category("Electric", "Battery electric vehicles")));

        // 2. Create 25 test cars if they don't exist.
        List<Car> cars = new ArrayList<>();

        // SUVs
        Car car1 = new Car("Toyota RAV4", 35000, "Toyota", "Compact SUV with excellent reliability and fuel economy."); car1.setCategory(suv); cars.add(car1);
        Car car2 = new Car("Honda CR-V", 34000, "Honda", "Spacious interior with advanced safety features."); car2.setCategory(suv); cars.add(car2);
        Car car3 = new Car("Ford Explorer", 42000, "Ford", "Three-row SUV with powerful engine options."); car3.setCategory(suv); cars.add(car3);
        Car car4 = new Car("Mazda CX-5", 33000, "Mazda", "Premium feel with engaging driving dynamics."); car4.setCategory(suv); cars.add(car4);
        Car car5 = new Car("Hyundai Tucson", 32000, "Hyundai", "Bold design with hybrid and plug-in hybrid options."); car5.setCategory(suv); cars.add(car5);

        // Sedans
        Car car6 = new Car("Toyota Camry", 28000, "Toyota", "Best-selling midsize sedan with hybrid option."); car6.setCategory(sedan); cars.add(car6);
        Car car7 = new Car("Honda Accord", 29000, "Honda", "Refined interior with strong turbo engine."); car7.setCategory(sedan); cars.add(car7);
        Car car8 = new Car("BMW 3 Series", 45000, "BMW", "Sports sedan with precise handling and luxury."); car8.setCategory(sedan); cars.add(car8);
        Car car9 = new Car("Mercedes C-Class", 47000, "Mercedes-Benz", "Elegant design with cutting-edge technology."); car9.setCategory(sedan); cars.add(car9);
        Car car10 = new Car("Audi A4", 43000, "Audi", "Quattro AWD with virtual cockpit display."); car10.setCategory(sedan); cars.add(car10);

        // Hatchbacks
        Car car11 = new Car("Volkswagen Golf", 26000, "Volkswagen", "Iconic compact hatchback with practical interior."); car11.setCategory(hatchback); cars.add(car11);
        Car car12 = new Car("Mazda 3 Hatchback", 25000, "Mazda", "Stunning design with premium interior materials."); car12.setCategory(hatchback); cars.add(car12);
        Car car13 = new Car("Toyota Corolla Hatchback", 24000, "Toyota", "Efficient hybrid powertrain with sporty styling."); car13.setCategory(hatchback); cars.add(car13);
        Car car14 = new Car("Honda Civic Hatchback", 25500, "Honda", "Spacious cargo area with turbo engine option."); car14.setCategory(hatchback); cars.add(car14);

        // Coupes
        Car car15 = new Car("Ford Mustang", 32000, "Ford", "Iconic American muscle car with V8 power."); car15.setCategory(coupe); cars.add(car15);
        Car car16 = new Car("Chevrolet Camaro", 31000, "Chevrolet", "Track-ready performance with aggressive styling."); car16.setCategory(coupe); cars.add(car16);
        Car car17 = new Car("Toyota GR86", 30000, "Toyota", "Lightweight rear-wheel-drive sports coupe."); car17.setCategory(coupe); cars.add(car17);
        Car car18 = new Car("BMW 2 Series", 38000, "BMW", "Compact luxury coupe with M-performance variant."); car18.setCategory(coupe); cars.add(car18);

        // Convertibles
        Car car19 = new Car("Mazda MX-5 Miata", 29000, "Mazda", "Lightweight roadster with perfect balance."); car19.setCategory(convertible); cars.add(car19);
        Car car20 = new Car("BMW Z4", 52000, "BMW", "Premium roadster with powerful turbo engines."); car20.setCategory(convertible); cars.add(car20);
        Car car21 = new Car("Porsche 718 Boxster", 68000, "Porsche", "Mid-engine perfection with razor-sharp handling."); car21.setCategory(convertible); cars.add(car21);

        // Pickups
        Car car22 = new Car("Ford F-150", 38000, "Ford", "Best-selling truck with multiple engine choices."); car22.setCategory(pickup); cars.add(car22);
        Car car23 = new Car("Toyota Tacoma", 35000, "Toyota", "Mid-size pickup with legendary off-road capability."); car23.setCategory(pickup); cars.add(car23);
        Car car24 = new Car("RAM 1500", 40000, "RAM", "Luxurious interior with coil-spring rear suspension."); car24.setCategory(pickup); cars.add(car24);

        // Wagons
        Car car25 = new Car("Subaru Outback", 32000, "Subaru", "Rugged wagon with standard AWD and ground clearance."); car25.setCategory(wagon); cars.add(car25);
        Car car26 = new Car("Volvo V60 Cross Country", 48000, "Volvo", "Swedish luxury wagon with off-road pretensions."); car26.setCategory(wagon); cars.add(car26);

        // Electric
        Car car27 = new Car("Tesla Model 3", 42000, "Tesla", "Best-selling EV with supercharger network access."); car27.setCategory(electric); cars.add(car27);
        Car car28 = new Car("Hyundai Ioniq 5", 41000, "Hyundai", "Retro-futuristic design with 800V fast charging."); car28.setCategory(electric); cars.add(car28);
        Car car29 = new Car("Ford Mustang Mach-E", 43000, "Ford", "Electric SUV with Mustang-inspired performance."); car29.setCategory(electric); cars.add(car29);

        for (Car newCar : cars) {
            if (carRepository.findByTitleIgnoreCase(newCar.getTitle()).isEmpty()) {
                carRepository.save(newCar);
            }
        }

        // 3. Create a test user if it does not exist.
        User user = userRepository.findByEmailIgnoreCase("setup@example.com")
                .orElseGet(() -> {
                    User newUser = new User("setup@example.com", passwordEncoder.encode("setup123"), "Test", "User", "600000000");
                    return userRepository.save(newUser);
                });

        // 4. Create admin user if it does not exist.
        User adminUser = userRepository.findByEmailIgnoreCase("m.lchkn.dev@gmail.com")
                .orElseGet(() -> {
                    User newAdmin = new User("m.lchkn.dev@gmail.com", passwordEncoder.encode("admin123"), "Admin", "User", "600000001");
                    newAdmin.setRole(User.Role.ADMIN);
                    return userRepository.save(newAdmin);
                });

        // 5. Create an initial order if no orders exist.
        long orderCount = orderRepository.count();

        if (orderCount == 0) {
            Order order = new Order();
            order.setUser(user);
            order.setStatus("COMPLETED");
            order.setFullName(user.getFirstName() + " " + user.getLastName());
            order.setAddress("Test address");
            order.setProvince("Madrid");

            orderRepository.save(order);
            orderRepository.flush();

            Car firstCar = carRepository.findAllByOrderByIdDesc().stream().findFirst().orElse(null);

            if (firstCar != null) {
                OrderItem item = new OrderItem();
                item.setOrder(order);
                item.setCar(firstCar);
                item.setQuantity(1);

                orderItemRepository.save(item);
                order.getOrderItems().add(item);
            }

            log.info("Initial order created successfully. Order ID: {}", order.getId());
        } else {
            log.info("Orders already exist. No initial order created.");
        }
    }
}