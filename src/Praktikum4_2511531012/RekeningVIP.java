package Praktikum4_2511531012;

public class RekeningVIP extends Rekening {
	private double bonus;
	
	public RekeningVIP(String nomor, String nama, double saldoAwal, String pinAwal, double bonus) {
		
		super(nomor, nama, saldoAwal, pinAwal);
		this.bonus = bonus;
	}
	
	public void tambahBunga() {
		
		double bonus = 100000;
		saldo += bonus;
		
		System.out.println("Bonus " + bonus + " berhasil ditambahkan: Rp");
		System.out.println("Isi Saldo: Rp" + saldo);
	}

}
