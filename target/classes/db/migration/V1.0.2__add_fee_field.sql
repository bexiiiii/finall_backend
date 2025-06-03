-- Add fee column to orders table
ALTER TABLE orders ADD COLUMN fee DECIMAL(19,2) NOT NULL DEFAULT 0.00;

-- Update existing orders to calculate fee
UPDATE orders 
SET fee = subtotal * 0.15,
    total = subtotal + (subtotal * 0.15)
WHERE fee = 0; 