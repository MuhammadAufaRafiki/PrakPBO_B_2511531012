
package review;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import shop.exception.CatalogLoadException;
import shop.exception.OutOfStockException;
import shop.exception.PaymentFailedException;
import shop.payment.EWalletPayment;
import shop.payment.PaymentMethod;
import shop.service.Inventory;

/**
 * L5: Perbaikan exception handling pada FragileShop.
 */
public class FragileShop {

    /** Pays an order. */
    public static void payOrder(
            PaymentMethod payment,
            long amount
    ) throws PaymentFailedException {

        // Exception pembayaran diteruskan ke pemanggil.
        payment.pay(amount);
    }

    /** Is at least one item in stock? */
    public static boolean isInStock(
            Inventory inventory,
            String sku
    ) {

        // Mengecek stok tanpa mengubah jumlah stok.
        return inventory.available(sku) > 0;
    }

    /** Reads the first line (the header) of a catalog file. */
    public static String readCatalogHeader(Path file)
            throws CatalogLoadException {

        try (BufferedReader reader = Files.newBufferedReader(file)) {

            return reader.readLine();

        } catch (IOException e) {

            // Menyimpan exception asli sebagai cause.
            throw new CatalogLoadException(
                    "Katalog gagal dibaca: " + file,
                    e
            );
        }
    }

    /** Adds items to the cart from what the customer typed. */
    public static String addToCart(
            Inventory inventory,
            String sku,
            String quantityText
    ) {

        int quantity;

        // Memeriksa apakah input jumlah berupa angka.
        try {

            quantity = Integer.parseInt(quantityText.trim());

        } catch (NumberFormatException e) {

            return "Gagal: jumlah harus berupa angka.";
        }

        // Memeriksa apakah jumlah lebih dari 0.
        if (quantity <= 0) {

            return "Gagal: jumlah harus lebih dari 0.";
        }

        // Memeriksa dan melakukan reservasi stok.
        try {

            inventory.reserve(sku, quantity);

            return "Ditambahkan " + quantity + " x " + sku;

        } catch (OutOfStockException e) {

            return "Gagal: " + e.getMessage();
        }
    }

    public static void main(String[] args) {

        Inventory inventory = new Inventory();

        inventory.addStock("KOPI-001", 1);

        EWalletPayment wallet =
                new EWalletPayment("DompetKu", 50_000);

        // 1. Menguji pembayaran yang gagal.
        try {

            payOrder(wallet, 85_000);

            System.out.println(
                    "1) Bayar Rp85000 selesai."
            );

        } catch (PaymentFailedException e) {

            System.out.println(
                    "1) Pembayaran gagal: " + e.getMessage()
            );
        }

        System.out.println(
                "   Sisa saldo: Rp" + wallet.getBalance()
        );

        // 2. Menguji pengecekan stok.
        System.out.println(
                "2) Kopi tersedia? "
                + isInStock(inventory, "KOPI-001")
        );

        // 3. Menguji input jumlah berupa huruf.
        System.out.println(
                "3) "
                + addToCart(inventory, "KOPI-001", "dua")
        );

        // Menguji jumlah yang melebihi stok.
        System.out.println(
                "   "
                + addToCart(inventory, "KOPI-001", "5")
        );

        // 4. Menguji file katalog yang tidak ditemukan.
        try {

            System.out.println(
                    "4) Header: "
                    + readCatalogHeader(Path.of("katalog-lama.csv"))
            );

        } catch (CatalogLoadException e) {

            System.out.println(
                    "4) " + e.getMessage()
                    + " | penyebab: " + e.getCause()
            );
        }
    }
}
