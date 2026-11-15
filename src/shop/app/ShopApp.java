
package shop.app;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import shop.StudentInfo;
import shop.exception.CatalogLoadException;
import shop.exception.InsufficientBalanceException;
import shop.exception.OutOfStockException;
import shop.exception.PaymentFailedException;
import shop.io.CatalogReader;
import shop.model.Product;
import shop.payment.EWalletPayment;
import shop.payment.PaymentMethod;
import shop.service.CheckoutService;
import shop.service.Inventory;
import shop.shipping.RegularShipping;
import shop.shipping.ShippingService;

public class ShopApp {

    public static void main(String[] args) {

        // Membaca katalog produk dari file CSV.
        Path catalogFile = Path.of(
                args.length > 0 ? args[0] : "katalog.csv"
        );

        CatalogReader reader = new CatalogReader();
        List<Product> catalog;

        try {
            catalog = reader.load(catalogFile);

        } catch (CatalogLoadException e) {
            System.out.println("Toko tidak bisa dibuka: " + e.getMessage());
            System.out.println("Penyebab asli: " + e.getCause());
            return;
        }

        // Membuat inventory dan mengisi stok awal.
        Inventory inventory = new Inventory();

        for (Product product : catalog) {
            inventory.addStock(
                    product.getSku(),
                    StudentInfo.stockFor(product.getSku())
            );
        }

        System.out.println("== Toko Online UMKM ==");

        System.out.println(
                "(" + catalog.size() + " produk, "
                + reader.getSkippedCount()
                + " baris katalog dilewati)"
        );

        // Menampilkan daftar produk.
        for (int i = 0; i < catalog.size(); i++) {
            System.out.println(
                    (i + 1) + ". " + catalog.get(i).getInfo()
            );
        }

        List<Product> cart = new ArrayList<>();

        // Membaca pilihan produk dari pengguna.
        try (Scanner in = new Scanner(System.in)) {

            while (true) {
                System.out.print("Nomor produk (0 = selesai): ");

                int choice;

                try {
                    choice = Integer.parseInt(in.nextLine().trim());

                } catch (NumberFormatException e) {
                    System.out.println("Input harus berupa angka.");
                    continue;
                }

                // Angka 0 berarti selesai memilih produk.
                if (choice == 0) {
                    break;
                }

                // Memeriksa nomor produk.
                if (choice < 1 || choice > catalog.size()) {
                    System.out.println("Nomor produk tidak ada.");
                    continue;
                }

                Product product = catalog.get(choice - 1);

                System.out.print("Jumlah: ");

                int quantity;

                try {
                    quantity = Integer.parseInt(in.nextLine().trim());

                } catch (NumberFormatException e) {
                    System.out.println("Input harus berupa angka.");
                    continue;
                }

                // Jumlah harus lebih dari 0.
                if (quantity <= 0) {
                    System.out.println("Jumlah harus lebih dari 0.");
                    continue;
                }

                // Memasukkan produk ke keranjang.
                for (int i = 0; i < quantity; i++) {
                    cart.add(product);
                }

                System.out.println(
                        "  + " + quantity + " x " + product.getName()
                );
            }
        }

        // Jika keranjang kosong, proses checkout tidak dilanjutkan.
        if (cart.isEmpty()) {
            System.out.println("Keranjang belanja kosong.");
            return;
        }

        // Membuat layanan checkout dan pengiriman.
        CheckoutService checkout = new CheckoutService(inventory);
        ShippingService regular = new RegularShipping();

        // Membuat metode pembayaran e-wallet.
        EWalletPayment wallet = new EWalletPayment(
                "DompetKu",
                StudentInfo.walletBalance()
        );

        System.out.println(
                "\nKeranjang: " + cart.size()
                + " barang, total Reguler Rp"
                + checkout.total(cart, regular)
        );

        // Memproses pembelian.
        System.out.println(
                buy(checkout, cart, regular, wallet)
        );

        System.out.println("Sisa saldo: Rp" + wallet.getBalance());
    }

    // Memproses checkout dan menangani exception.
    public static String buy(
            CheckoutService checkout,
            List<Product> cart,
            ShippingService shipping,
            PaymentMethod payment
    ) {

        try {
            long amountPaid = checkout.checkout(
                    cart,
                    shipping,
                    payment
            );

            return "[OK] " + payment.getName()
                    + " membayar Rp" + amountPaid;

        } catch (InsufficientBalanceException e) {

            // Menangani saldo atau limit yang tidak cukup.
            return "[SALDO] " + e.getMessage()
                    + " (kurang Rp" + e.getShortfall() + ")";

        } catch (PaymentFailedException e) {

            // Menangani pembayaran yang gagal.
            return "[BAYAR] " + e.getMessage();

        } catch (OutOfStockException e) {

            // Menangani stok produk yang tidak cukup.
            return "[STOK] " + e.getMessage();
        }
    }
}
