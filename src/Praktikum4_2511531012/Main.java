package Praktikum4_2511531012;
import java.util.Scanner;
import java.util.ArrayList;
public class Main {
	public static void main(String[]args) {
	Scanner input = new Scanner(System.in);
	ArrayList<Rekening> daftarRekening = new ArrayList<>();
	Rekening akunAktif = null;
	boolean isRunning = true;
	
	System.out.println("\"=== SISTEM PERBANKAN MINI ===\"");
	
	while (isRunning) {
		if (akunAktif != null) {
			System.out.println("\n[ Akun Aktif: " + akunAktif.getNamaPemilik() + " ( No Rekening: " + akunAktif.getNomorRekening() + ")  ]");
		} else {
			System.out.println("\n[ Akun Aktif: Belum ada akun terpilih ]");
		}
		System.out.println("\nMenu Utama:");
		System.out.println("1. Buka Rekening Baru");
		System.out.println("2. Setor Tunai");
		System.out.println("3. Tarik Tunai");
		System.out.println("4. Cek Informasi Rekening");
		System.out.println("5. Ganti Akun");
		System.out.println("6. Cetak Mutasi (Riwayat)");
		System.out.println("7. Simulasi Akhir Bulan (Khusus Tabungan)");
		System.out.println("0. Keluar");
		System.out.print("Pilih menu:");
		
		int pilihan = input.nextInt();
		input.nextLine();
		
		switch (pilihan) {
			case 1:
			System.out.print("Masukkan No Rekening: ");
			String no = input.nextLine();
			boolean sudahAda = false;
			for (Rekening r : daftarRekening) {
			    if (r.getNomorRekening().equalsIgnoreCase(no)) {
			        sudahAda = true;
			        break;
			    }
			}
		    if (sudahAda) {
		        System.out.println("Error: Nomor rekening tersebut sudah terdaftar!");
		    } else {
		    	System.out.print("Masukkan Nama Pemilik: ");
				String nama = input.nextLine();
				System.out.print("Masukkan Saldo Awal: ");
				Double saldo = input.nextDouble();
				input.nextLine();
				//Input PIN
				System.out.print("Masukkan PIN (6 digit): ");
				String pin = input.nextLine();
				
				System.out.println("Pilih Produk: 1. Tabungan Umum | 2. Giro Bisnis | 3. Rekening VIP");
				System.out.println("Produk yang dipilih: ");
				int pilihannya = input.nextInt();
				input.nextLine();
				switch (pilihannya) {
					case 1: 
						System.out.print("Masukkan Suku Bunga (%): ");
						Double sukuBunga = input.nextDouble();
						
						RekeningTabungan rekeningTabungan = new RekeningTabungan(no, nama, saldo, pin, sukuBunga);
						daftarRekening.add(rekeningTabungan);
						akunAktif = rekeningTabungan;
						break;
						
					case 2: 
						System.out.print("Masukkan Limit Pinjaman : ");
						Double  batasOverdraft = input.nextDouble();
						
						RekeningGiro rekeningGiro = new RekeningGiro(no, nama, saldo, pin, batasOverdraft);
						daftarRekening.add(rekeningGiro);
						akunAktif = rekeningGiro;
						break;
				}
				
		    }
			break;
			
			case 2:
				if (akunAktif == null) {
					System.out.println("Error: mohon maaf, anda belum memiliki nomor rekening!");
				} else {
					System.out.println("Masukkan nominal setor: ");
					double setor = input.nextDouble();
					akunAktif.setorTunai(setor);
				}
				break;
			
			case 3:
				if (akunAktif == null) {
					System.out.println("Error: mohon maaf, anda belum memiliki nomor rekening!");
				} else {
					// Minta PIN sebelum Tarik Tunai
					System.out.print("Masukkan PIN (6 digit): ");
					String inputPin = input.nextLine();
					
					// Otentikasi PIN
					if (akunAktif.otentikasi(inputPin)) {
						System.out.print("Masukkan nominal tarik tunai: ");
						double tarik = input.nextDouble();
						input.nextLine();
						akunAktif.tarikTunai(tarik);
					} else {
						System.out.println("Akses Ditolak: PIN yang Anda masukkan salah!");
					}
				}
				break;
				
			case 4:
				if (akunAktif == null) {
					System.out.println("Error: Anda belum membuka rekening!");
				} else {
					akunAktif.cekInformasi();
				}
				break;
				
			case 5:
				if (daftarRekening.isEmpty()) {
					System.out.println("Error: Belum ada rekening yang terdaftar di dalam sistem!");
				} else {
					System.out.print("Masukkan No Rekening yang ingin diakses: ");
					String cariNo = input.nextLine();

					Rekening akunDitemukan = null;
					for (Rekening r : daftarRekening) {
						if (r.getNomorRekening().equalsIgnoreCase(cariNo)) {
							akunDitemukan = r;
							break;
						}
					}

					if (akunDitemukan != null) {
						akunAktif = akunDitemukan;
						System.out.println("Berhasil beralih ke rekening atas nama " + akunAktif.getNamaPemilik());
					} else {
						System.out.println("Error: Nomor rekening " + cariNo + " tidak ditemukan!");
					}
				}
				break;
				
			case 6:
				if (akunAktif == null) {
					System.out.println("Error: Anda belum membuka rekening!");
				} else {
					// Minta PIN sebelum Cetak Mutasi
					System.out.print("Masukkan PIN (6 digit): ");
					String inputPin = input.nextLine();
					
					// Otentikasi PIN
					if (akunAktif.otentikasi(inputPin)) {
						akunAktif.cetakMutasi();
					} else {
						System.out.println("Akses Ditolak: PIN yang Anda masukkan salah!");
					}
				}
				break;
				
			case 7:
				if (akunAktif == null) {
					System.out.println("Error: Anda belum memilih rekening aktif!");
				}
				else if (akunAktif instanceof RekeningTabungan tabungan) {
					tabungan.tambahBungaAkhirBulan();
				} else {
					System.out.println("Gagal: Fitur bunga akhir bulan hanya berlaku untuk Rekening Tabungan.");
				}
				 break;
				
			case 0:
				isRunning = false;
				System.out.println("Sistem ditutup. terima kasih!");
				break;
				
			default:
				System.out.println("Pilihan tidak valid!");
				
		}
	}
	input.close();
	}
}
