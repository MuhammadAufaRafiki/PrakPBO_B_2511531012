package Pekan1_2511531012;
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
			System.out.println("\n[ Akun Aktif: " + akunAktif.namaPemilik + " ( No Rekening: " + akunAktif.nomorRekening + ") ]");
		} else {
			System.out.println("\n[ Akun Aktif: Belum ada akun terpilih ]");
		}
		System.out.println("\nMenu Utama:");
		System.out.println("1. Buka Rekening Baru");
		System.out.println("2. Setor Tunai");
		System.out.println("3. Tarik Tunai");
		System.out.println("4. Cek Informasi Rekening");
		System.out.println("5. Ganti Akun");
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
			    if (r.nomorRekening.equalsIgnoreCase(no)) {
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
				Rekening rekeningBaru = new Rekening(no, nama, saldo);
				daftarRekening.add(rekeningBaru);
				akunAktif = rekeningBaru;
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
					System.out.println("Masukkan nominal tarik tunai: ");
					double tarik = input.nextDouble();
					akunAktif.tarikTunai(tarik);
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
						if (r.nomorRekening.equalsIgnoreCase(cariNo)) {
							akunDitemukan = r;
							break;
						}
					}

					if (akunDitemukan != null) {
						akunAktif = akunDitemukan;
						System.out.println("Berhasil beralih ke rekening atas nama " + akunAktif.namaPemilik);
					} else {
						System.out.println("Error: Nomor rekening " + cariNo + " tidak ditemukan!");
					}
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
