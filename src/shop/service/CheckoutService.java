
package shop.service;

import java.util.ArrayList;
import java.util.List;
import shop.exception.OutOfStockException;
import shop.exception.PaymentFailedException;
import shop.model.Product;
import shop.payment.PaymentMethod;
import shop.payment.RefundablePayment;
import shop.shipping.Shippable;
import shop.shipping.ShippingService;

public class CheckoutService {

    private final Inventory inventory;

    public CheckoutService(Inventory inventory) {
        if (inventory == null) {
            throw new IllegalArgumentException("Inventory is required");
        }

        this.inventory = inventory;
    }

    public long subtotal(List<Product> items) {
        long total = 0;

        for (Product product : items) {
            total += product.getFinalPrice();
        }

        return total;
    }

    public int totalWeight(List<Product> items) {
        int grams = 0;

        for (Product product : items) {
            if (product instanceof Shippable shippable) {
                grams += shippable.getWeightGrams();
            }
        }

        return grams;
    }

    public long total(List<Product> items, ShippingService shipping) {
        return subtotal(items) + shipping.calculateCost(totalWeight(items));
    }

    // Urutan parameter harus: List, ShippingService, PaymentMethod.
    public long checkout(
            List<Product> items,
            ShippingService shipping,
            PaymentMethod payment
    ) throws OutOfStockException, PaymentFailedException {

        if (items == null) {
            throw new IllegalArgumentException("Items must not be null");
        }

        if (shipping == null) {
            throw new IllegalArgumentException("Shipping service is required");
        }

        if (payment == null) {
            throw new IllegalArgumentException("Payment method is required");
        }

        if (items.isEmpty()) {
            throw new IllegalStateException("Keranjang belanja kosong.");
        }

        List<Product> reserved = new ArrayList<>();

        try {
            for (Product product : items) {
                inventory.reserve(product.getSku(), 1);
                reserved.add(product);
            }

            long totalAmount = total(items, shipping);

            payment.pay(totalAmount);

            return totalAmount;

        } catch (OutOfStockException | PaymentFailedException e) {

            // Mengembalikan stok jika checkout gagal.
            for (Product product : reserved) {
                inventory.release(product.getSku(), 1);
            }

            // Melempar kembali exception asli.
            throw e;
        }
    }

    public boolean refund(RefundablePayment payment, long amount) {
        return payment.refund(amount);
    }
}
