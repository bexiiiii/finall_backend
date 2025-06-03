-- Check if fee column exists, if not add it
DO $$ 
BEGIN
    IF NOT EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'orders' 
        AND column_name = 'fee'
    ) THEN
        ALTER TABLE orders ADD COLUMN fee DECIMAL(19,2) NOT NULL DEFAULT 0.00;
    END IF;
END $$;

-- Update existing orders to calculate fee if it's 0
UPDATE orders 
SET fee = subtotal * 0.15,
    total = subtotal + (subtotal * 0.15)
WHERE fee = 0; 