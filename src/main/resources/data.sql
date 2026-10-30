-- Initial data for production database
-- This runs on application startup when using file-based H2

-- Insert categories
INSERT INTO tabla_categories (name, description) VALUES
('SUV', 'Sport utility vehicles'),
('Sedan', 'Traditional sedan cars'),
('Sports', 'High-performance sports cars'),
('Truck', 'Pickup trucks and commercial vehicles'),
('Electric', 'Electric vehicles')
ON DUPLICATE KEY UPDATE name=name;

-- Insert sample cars (id will be auto-generated)
INSERT INTO tabla_cars (title, price, brand, description, category_id, cover_image, thumbnail_image) VALUES
('Toyota RAV4', 35000, 'Toyota', 'Reliable compact SUV with excellent fuel economy',
 (SELECT id FROM tabla_categories WHERE name='SUV'), NULL, NULL),
('Honda Civic', 25000, 'Honda', 'Popular sedan with great reliability and efficiency',
 (SELECT id FROM tabla_categories WHERE name='Sedan'), NULL, NULL),
('Ford Mustang', 55000, 'Ford', 'Iconic American muscle car with powerful V8 engine',
 (SELECT id FROM tabla_categories WHERE name='Sports'), NULL, NULL),
('Tesla Model 3', 45000, 'Tesla', 'Electric sedan with long range and autopilot features',
 (SELECT id FROM tabla_categories WHERE name='Electric'), NULL, NULL),
('Chevrolet Silverado', 40000, 'Chevrolet', 'Full-size pickup truck with strong towing capacity',
 (SELECT id FROM tabla_categories WHERE name='Truck'), NULL, NULL),
('BMW X5', 65000, 'BMW', 'Luxury SUV with premium interior and advanced technology',
 (SELECT id FROM tabla_categories WHERE name='SUV'), NULL, NULL);