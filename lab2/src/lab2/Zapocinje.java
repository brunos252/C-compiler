package lab2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Zapocinje {
	private List<String> zavrsni;
	private List<String> nezavrsni;
	private List<String> prazni;
	private int[][] table;
	private Map<String, List<List<String>>> productions;
	
	/* KAJ OVO ZNACI?
	 * "Za potrebe generiranja LR(1) parsera potrebno je prosiriti domenu ZAPOCINJE skupova sa
	 * desnih strana produkcije na bilo koji sufiks desne strane produkcije (ili jednostavno na
	 * proizvoljne nizove zavrsnih i nezavrsnih znakova)"
	 */

	
	public Zapocinje(List<String> zavrsni, List<String> nezavrsni, Map<String, List<List<String>>> productions) {
		this.zavrsni = zavrsni;
		this.nezavrsni = nezavrsni;
		this.productions = productions;
		table = new int[nezavrsni.size()][nezavrsni.size() + zavrsni.size()];
		nadiPrazneZnakove();
		zapocinjeIzravnoZnakom();
		zapocinjeZnakom();
	}
	
	/*
	 * racuna skup ZAPOCINJE(<X>), dvije su funkcije istog naziva a razlikuju se u argumentima;
	 * ova koja prima List<String> poziva ovu drugu koja prima samo jedan String odnosno znak
	 */
	public Set<String> izracunajZapocinje(List<String> list) {
		Set<String> set = new HashSet<>();
		for(String znak : list) {
			if(zavrsni.contains(znak)) {
				set.add(znak);
				return set;
			} else {
				set.addAll(izracunajZapocinje(znak));
			}
			if(!prazni.contains(znak)) 
				break;
		}
		
		return set;
	}
	
	
	public List<String> izracunajZapocinje(String entry) {
		List<String> ZAPOCINJE = new ArrayList<String>();
		int index = nezavrsni.indexOf(entry);
		int offset = nezavrsni.size();
		for(String z : zavrsni) {
			if(table[index][offset + zavrsni.indexOf(z)] > 0)
				ZAPOCINJE.add(z);
		}
		
		return ZAPOCINJE;
	}
	
	private void zapocinjeZnakom() {
		/*
		 * true samo ako je iz znaka A moguce generirati niz koji zapocinje znakom B
		 */
		boolean promjena = true;
		int retci = table.length;
		int stupci = table[0].length;
		while(promjena) {
			promjena = false;
			for(int i = 0; i < retci; i++) {
				for(int j = 0; j < stupci; j++) {
					if(i == j && table[i][j] == 0) {
						table[i][j] = 1;
						promjena = true;
					} else {
						for(int k = 0; k < retci; k++) {
							if(table[i][k] > 0 && table[k][j] > 0 && table[i][j] == 0) {
								table[i][j] = 1;
								promjena = true;
							}
						}
					}
				}
			}
		}
//		int retci2 = table.length;
//		int stupci2 = table[0].length;
//		System.out.println("zapocinjeZnakom: ");
//		System.out.print("               ");
//		for(String s : nezavrsni)
//			System.out.printf("%15s", s);
//		for(String s : zavrsni)
//			System.out.printf("%15s", s);
//		System.out.println();
//		for(int i = 0; i < retci2; i++) {
//			System.out.printf("%15s ", nezavrsni.get(i));
//			for(int j = 0; j < stupci2; j++) {
//				System.out.printf("%15d", table[i][j]);
//			}
//			System.out.println();
//		}
//		System.out.println();
	}
	
	private void zapocinjeIzravnoZnakom() {
		/*
		 * vrijednost u tablici je 1 samo ako je barem jedna od produkcija oblika A -> alfaBbeta,
		 *  gdje je alfa niz praznih znakova a -*> $ a beta proizvoljni niz znakova
		 */
		for(String lijevo : productions.keySet()) {
			int indexL = nezavrsni.indexOf(lijevo);
			for(List<String> desno : productions.get(lijevo)) {
				for(String s : desno) {
					if(nezavrsni.contains(s)) {
						table[indexL][nezavrsni.indexOf(s)] = 1;
						if(!prazni.contains(s))
							break;
					} else {
						table[indexL][nezavrsni.size() + zavrsni.indexOf(s)] = 1;
						break;
					}
				}
			}
		}
	}
	
	private void nadiPrazneZnakove() {
		boolean prazan = true;
		boolean biloPromjene = true;
		prazni = new ArrayList<String>();
		
		while(biloPromjene) {
			biloPromjene = false;
			for(String lijevo : productions.keySet()) {
				if(!prazni.contains(lijevo)) {
					for(List<String> desno : productions.get(lijevo)) {
						if(desno.get(0).equals("$")) {
							prazni.add(lijevo);
							biloPromjene = true;
							break;
						} else if(!prazni.isEmpty()) {
							int size = desno.size();
							prazan = true;
							for(int i = 0; i < size; i++) {
								if(!prazni.contains(desno.get(i))) {
									prazan = false;
									break;
								}
							}
							if(prazan) {
								prazni.add(lijevo);
								biloPromjene = true;
								break;
							}
						}
					}
				}
			}
		}
//		System.out.println("prazni znakovi: " + prazni);

	}
}