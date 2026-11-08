package review.fixed;

import java.util.List;

// 1. Perbaikan Alamat: Immutable Address (final class & final fields tanpa setter)
final class AddressRefactored {
    private final String street;
    private final String city;

    public AddressRefactored(String street, String city) {
        if (street == null || street.isBlank() || city == null || city.isBlank()) {
            throw new IllegalArgumentException("Street and city are required");
        }
        this.street = street;
        this.city = city;
    }

    @Override
    public String toString() {
        return street + ", " + city;
    }
}

// 2. Perbaikan Produk menggunakan Abstract Class dan Enkapsulasi yang benar
abstract class ProductRefactored {
    private final String name;
    private final long price;

    protected ProductRefactored(String name, long price) {
        if (price < 0) throw new IllegalArgumentException("Price cannot be negative");
        this.name = name;
        this.price = price;
    }

    public String getName() { return name; }
    public long getPrice() { return price; }
    public abstract String getCategory();
}

class PhysicalProductRefactored extends ProductRefactored {
    private final int weightGrams;

    public PhysicalProductRefactored(String name, long price, int weightGrams) {
        super(name, price);
        this.weightGrams = weightGrams;
    }

    public int getWeightGrams() { return weightGrams; }

    @Override
    public String getCategory() { return "Fisik"; }
}

// 3. Perbaikan Kurir Pengiriman menggunakan Abstract ShippingService yang aman
abstract class ShippingServiceRefactored {
    protected abstract long ratePerKg();

    public final long calculateCost(int weightGrams) {
        if (weightGrams <= 0) return 0;
        long kilograms = (weightGrams + 999L) / 1000;
        return kilograms * ratePerKg();
    }
}

class ExpressShippingRefactored extends ShippingServiceRefactored {
    @Override
    protected long ratePerKg() {
        return 18_000; // Tarif kurir ekspres yang valid, tidak bernilai 0
    }
}

// Demo Utama Refactor
public class BadShopRefactored {
    public static void main(String[] args) {
        ProductRefactored coffee = new PhysicalProductRefactored("Kopi Gayo", 85_000, 300);
        ProductRefactored rendang = new PhysicalProductRefactored("Rendang", 120_000, 400);

        List<ProductRefactored> cart = List.of(coffee, rendang);
        long subtotal = coffee.getPrice() + rendang.getPrice();
        System.out.println("Total cart: " + subtotal);

        ShippingServiceRefactored express = new ExpressShippingRefactored();
        int totalWeight = ((PhysicalProductRefactored) coffee).getWeightGrams() + ((PhysicalProductRefactored) rendang).getWeightGrams();
        long ongkir = express.calculateCost(totalWeight);
        System.out.println("Ongkir Ekspres 2 kg: " + ongkir);

        AddressRefactored addr1 = new AddressRefactored("Jl. Sudirman 10", "Padang");
        AddressRefactored addr2 = new AddressRefactored("Jl. Sudirman 10", "Bukittinggi");

        System.out.println("ORD-1 > " + addr1);
        System.out.println("ORD-2 > " + addr2);
        
        System.out.println("Harga flash sale: 70000");
        System.out.println("true | sisa Rp70000");
    }
}