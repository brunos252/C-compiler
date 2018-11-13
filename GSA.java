import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/*
 * Generira sintaksni analizator pomocu podataka dobivenih na standardnom ulazu (zapravo samo nadopunjuje
 * kod vec postojeceg sintaksnog analizatora)
 * generator analizatoru potrebne tablice predaje putem datoteke
 */

public class GSA {	
	public static void main(String[] args) {
		Map<String, List<List<String>>> productions = new HashMap<String, List<List<String>>>();
		List<String> nezavrsni = new ArrayList<String>();
		List<String> zavrsni = new ArrayList<String>();
		List<String> syncron = new ArrayList<String>();
		String line, leftSide = null;
		boolean prod = false;
		
		/*
		 * cita ulazne podatke:
		 * %V - nezavrsni znakovi
		 * %T - zavrsni znakovi
		 * %Syn - sinkronizacijski zavrsni znakovi
		 * ostali redovi: produkcije gramatike
		 * 
		 * nezavrsni se spremaju zajedno za trokutastim zagradama, npr. <znak> sto ih razlikuje od zavrsnih znakova
		 * prvi nezavrsni znak je i pocetni, ostaje prvi u kolekciji
		 * 
		 * u kolekciji Map<String, List<List<String>>> productions svaki nezavrsni znak ima "dvodimenzijsko polje"
		 * desnih strana produkcija, jedna produkcija po redu
		 */
		
		try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))){
			line = br.readLine();
			while(line != null && !(line.isEmpty())) {
				if(!prod) {
					if(line.startsWith("%V")){
						nezavrsni.addAll(Arrays.asList(line.split(" ")));
						nezavrsni.remove(0);
						for(String s : nezavrsni) {
							productions.put(s, new ArrayList<List<String>>());
						}
					} else if(line.startsWith("%T")) {
						zavrsni.addAll(Arrays.asList(line.split(" ")));
						zavrsni.remove(0);
					} else if(line.startsWith("%Syn")) {
						syncron.addAll(Arrays.asList(line.split(" ")));
						syncron.remove(0);
						prod = true;
					}
				} else {
					if(!line.startsWith(" "))
						leftSide = line;
					else {
						line = line.trim();
						productions.get(leftSide).add(Arrays.asList(line.split(" ")));
					}
				}
			line = br.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/* po uputama:
		 * dodavanje novog pocetnog stanja i odgovarajucu produkciju koja pokazuje na originalno pocetno
		 * stanje, str 140 u udzb fusnota
		 */
		productions.put("<%>", new ArrayList<List<String>>());
		productions.get("<%>").add(Arrays.asList(nezavrsni.get(0)));
		nezavrsni.add(0, "<%>");

		//inicijalizacija klase koja racuna ZAPOCINJE skupove
		Zapocinje z = new Zapocinje(zavrsni, nezavrsni, productions);
		
		System.out.println();
		System.out.println("ZAPOCINJE skupovi nezavrsnih znakova: ");
		for(String s : nezavrsni)
			System.out.println(s + " : " + z.izracunajZapocinje(s));
		
		buildENKA(productions, nezavrsni, z);
		
		/*System.out.println();
		System.out.println("sve produkcije (zanemari okomite crte na kraju redova) :");
		for(String key : productions.keySet()) {
			System.out.print(key + " -> ");
			List<List<String>> temp = productions.get(key);
			for(List<String> list : temp) {
				for(String s : list) {
					System.out.print(s + " ");
				}
				System.out.print("|");
			}
			System.out.println();
		}*/
	}
	
	public static void buildENKA(Map<String, List<List<String>>> productions, List<String> nezavrsni, Zapocinje z) {
		ValuePairs<List<String>, List<String>> prijelazi = new ValuePairs<>();
		List<List<String>> stanja = new LinkedList<List<String>>();			//LR(1) stavke odnosno LRstavke + skup zavrsnih znakova
		//List<String> ulazniZnakovi = new LinkedList<String>();
		//ulazniZnakovi.addAll(zavrsni);
		//ulazniZnakovi.addAll(nezavrsni);
		
		/*
		 * prijelazi su oblika: left: <stanje>	,	ulazniZnak
		 * 					   right: <stanje>
		 * gdje je <stanje> oblika <A> 	->	*	a	b	,	{	c	d	}		(kao u knjizi)
		 */
		
		//dodavanje pocetnog stanja
		List<String> left = new ArrayList<String>();
		List<String> right = new ArrayList<String>();
		left.addAll(Arrays.asList(nezavrsni.get(0), "->", "*", nezavrsni.get(1), "{", "#", "}"));
		stanja.add(left);
		
		//148. str. 4.b) + c) dio, definiranje funkcija prijelaza
		boolean promjena = true;
		while(promjena) {
			promjena = false;
			for(int i = 0; i < stanja.size(); i++) {
				List<String> stanje = stanja.get(i);
				left = new ArrayList<String>();
				right = new ArrayList<String>();
				int indtocke = stanje.indexOf("*");
				int indzagrade = stanje.indexOf("{");
				if(indzagrade > indtocke + 1) {
					//4.b)
					left.addAll(stanje);
					left.addAll(Arrays.asList(",", stanje.get(indtocke + 1)));
					right.addAll(stanje);
					right.remove(indtocke);
					right.add(indtocke + 1, "*");
					Pair<List<String>, List<String>> par = new Pair<List<String>, List<String>>(left, right);
					if(!(prijelazi.containsPair(par)))
						prijelazi.addPair(par);
					if(!stanja.contains(right)) {
						stanja.add(right);
						promjena = true;
					}
					//4.c) nisam siguran da je gotov ovaj dio
					left = new ArrayList<String>();
					String sljedeci = stanje.get(indtocke + 1);
					if(nezavrsni.contains(sljedeci)) {
						left.addAll(stanje);
						left.addAll(Arrays.asList(",", "$"));
						List<List<String>> produkcijeSljedeceg = productions.get(sljedeci);
						for(List<String> desno : produkcijeSljedeceg) {
							List<String> T = new ArrayList<>();
							if(indzagrade > indtocke + 2) {
								T.addAll(z.izracunajZapocinje(stanje.subList(indtocke + 2, indzagrade)));
								T.add("#");
							} else
								T.addAll(stanje.subList(indzagrade + 1, stanje.indexOf("}")));
							right = new ArrayList<String>();
							if(desno.get(0).equals("$")) {
								right.addAll(Arrays.asList(sljedeci, "->", "*", "{"));
								right.addAll(T);
								right.add("}");
							} else {
								right.addAll(Arrays.asList(sljedeci, "->", "*"));
								right.addAll(desno);
								right.add("{");
								right.addAll(T);
								right.add("}");
							}
							Pair<List<String>, List<String>> par2 = new Pair<List<String>, List<String>>(left, right);
							if(!(prijelazi.containsPair(par2)))
								prijelazi.addPair(par2);
							if(!stanja.contains(right)) {
								stanja.add(right);
								promjena = true;
							}
						}
					}
				}
			}
		}
		
		//ispis svih stanja e-NKA
		System.out.println();
		System.out.println("stanja: " + stanja.size());
		for(List<String> s : stanja) {
			List<String> state = new ArrayList<String>();
			state.addAll(s);
			System.out.println(state);
		}
		System.out.println();
		
		//ispis prijelaza
		System.out.println();
		System.out.println("prijelazi: " + prijelazi.size());
		for(Pair<List<String>, List<String>> pair : prijelazi) {
			System.out.print("[");
			for(String s : pair.getLeft()) {
				if(s.equals(","))
					System.out.print("]");
				System.out.print(s + " ");
			}
			System.out.print("-> [");
			for(String s : pair.getRight()) {
				System.out.print(s + " ");
			}
			System.out.print("]");
			System.out.println();
		}
	}
}
