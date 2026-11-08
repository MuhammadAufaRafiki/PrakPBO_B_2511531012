package shop.app;

import java.util.List;
import shop.StudentInfo;
import shop.model.DigitalProduct;
import shop.model.PhysicalProduct;
import shop.model.Product;
import shop.notification.*;
import shop.payment.BankTransferPayment;
import shop.payment.CreditCardPayment;
import shop.payment.EWalletPayment;
import shop.payment.PaymentMethod;
import shop.payment.RefundablePayment;
import shop.service.CheckoutService;
import shop.shipping.ExpressShipping;
import shop.shipping.ShippingService;

public class TugasApp {
    public static void main(String[] args) {
        System.out.println("Tugas oleh " + StudentInfo.NAME + " (" + StudentInfo.NIM + ")");

        // 1. Keranjang contoh dari modul latihan
        Product coffee = new PhysicalProduct("KOPI-001", "Kopi Arabika Gayo 250g", 85_000, 300);
        Product rendang = new PhysicalProduct("RDG-002", "Rendang Kemasan 250g", 120_000, 400);
        Product ebook = new DigitalProduct("EBK-003", "E-book Resep Kue Nusantara", 50_000, "https://toko-umkm.example/unduh/ebk-003");
        List<Product> cart = List.of(coffee, rendang, ebook);

        CheckoutService checkout = new CheckoutService();
        ShippingService express = new ExpressShipping();

        PaymentMethod wallet = new EWalletPayment("Dompetku", 300_000);
        PaymentMethod transfer = new BankTransferPayment("Bank Nagari");
        CreditCardPayment card = new CreditCardPayment("Siti Rahma", 1_000_000);

        // DEMO T1: Total belanja dengan biaya admin kurir Ekspres
        System.out.println("\n== T1: Total dengan biaya admin (Ekspres) ==");
        long gTotalWallet = checkout.grandTotal(cart, express, wallet);
        System.out.println(wallet.getName() + " Rp" + gTotalWallet + " (admin Rp" + wallet.adminFee(checkout.total(cart, express)) + ")");

        long gTotalTransfer = checkout.grandTotal(cart, express, transfer);
        System.out.println(transfer.getName() + " Rp" + gTotalTransfer + " (admin Rp" + transfer.adminFee(checkout.total(cart, express)) + ")");

        long gTotalCard = checkout.grandTotal(cart, express, card);
        System.out.println(card.getName() + ": Rp" + gTotalCard + " (admin Rp" + card.adminFee(checkout.total(cart, express)) + ")");

        System.out.println("\nKebijakan kartu kredit : " + card.policy());
        System.out.println("Kebijakan e-wallet : " + ((RefundablePayment) wallet).policy());

        // DEMO T2: Sistem Notifikasi Pesanan
        System.out.println("\n== T2: Notifikasi status pesanan ==");
        NotificationService service = new NotificationService();
        service.register(new EmailChannel());
        service.register(new SmsChannel(StudentInfo.SMS_MAX_LENGTH)); // Menggunakan batas panjang SMS dari NIM
        service.register(new WhatsAppChannel());

        // Mendaftarkan satu kanal log konsol menggunakan anonymous class
        service.register(new NotificationChannel() {
            @Override
            public String channelName() {
                return "LOG";
            }

            @Override
            public boolean send(CustomerContact contact, String message) {
                System.out.println("[LOG] " + contact.getName() + ": " + message);
                return true;
            }
        });

        // Kontak pelanggan uji coba
        CustomerContact siti = new CustomerContact("Siti Rahma", "siti@example.com", "081234567890");
        CustomerContact budi = new CustomerContact("Budi", null, "+6281298765432");

        String longMsg = "Pesanan ORD-0001 sudah dikirim dengan kurir Ekspres. Nomor resi akan muncul di aplikasi dalam 1x24 jam. Terima kasih telah berbelanja produk UMKM Sumatera Barat di toko kami!";

        // Broadcast ke Siti (memiliki email dan nomor berawalan 08...)
        int countSiti = service.broadcast(siti, longMsg);
        System.out.println("Terkirim ke Siti lewat " + countSiti + " kanal");

        // Broadcast ke Budi (tidak punya email, nomor berawalan +62...)
        int countBudi = service.broadcast(budi, longMsg);
        System.out.println("Terkirim ke Budi lewat " + countBudi + " kanal");
    }
}