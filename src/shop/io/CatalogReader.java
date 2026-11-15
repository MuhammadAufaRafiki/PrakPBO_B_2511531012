
package shop.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import shop.exception.CatalogLoadException;
import shop.model.PhysicalProduct;
import shop.model.Product;

public class CatalogReader {

    private static final Logger LOG =
            Logger.getLogger(CatalogReader.class.getName());

    private int skippedCount;

    public List<Product> load(Path file) throws CatalogLoadException {

        List<Product> products = new ArrayList<>();
        skippedCount = 0;

        int lineNumber = 0;

        // Membuka file menggunakan try-with-resources.
        try (BufferedReader reader = Files.newBufferedReader(file)) {

            String line;

            // Membaca file baris demi baris.
            while ((line = reader.readLine()) != null) {

                lineNumber++;

                // Baris kosong dilewati dan tidak dihitung sebagai baris rusak.
                if (line.isBlank()) {
                    continue;
                }

                try {
                    // Mengubah baris CSV menjadi objek Product.
                    Product product = parse(line);

                    // Menambahkan produk yang valid.
                    products.add(product);

                } catch (IllegalArgumentException e) {

                    // Menghitung baris yang benar-benar rusak.
                    skippedCount++;

                    // Mencatat baris yang dilewati.
                    LOG.warning(
                            "Baris " + lineNumber
                            + " dilewati: " + e.getMessage()
                    );

                    // Melanjutkan ke baris berikutnya.
                    continue;
                }
            }

        } catch (IOException e) {

            // Membungkus IOException dengan CatalogLoadException.
            throw new CatalogLoadException(
                    "Katalog gagal dibaca: " + file,
                    e
            );
        }

        return products;
    }

    // Mengembalikan jumlah baris rusak.
    public int getSkippedCount() {
        return skippedCount;
    }

    // Mengubah satu baris CSV menjadi PhysicalProduct.
    private Product parse(String line) {

        String[] fields = line.split(";");

        // Setiap baris harus memiliki 4 kolom.
        if (fields.length != 4) {
            throw new IllegalArgumentException(
                    "expected 4 fields, found " + fields.length
            );
        }

        return new PhysicalProduct(
                fields[0].trim(),
                fields[1].trim(),
                Long.parseLong(fields[2].trim()),
                Integer.parseInt(fields[3].trim())
        );
    }
}
