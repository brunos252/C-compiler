import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import valuePair.ValuePairs;

/*
 * Nije potrebno inicijalizirati polje koje sadrzi sva stanja i nije potrebno inicijalizirati
 *  stanja koja su prihvatljiva.
 */

public class E_NKA {
	@SuppressWarnings("unused")
	private HashSet<String> allStates;
	private HashSet<String> alphabet;
	private HashSet<String> acceptableStates;
	private String firstState;
	private HashMap<String,String> function;
	private ValuePairs<String,String> pairs;
	private LinkedList<String> entry;
	private TreeSet<String> currentStates;
	private TreeSet<String> nextStates;
	private boolean enabledSetters=true;  //omogucavanje mjenjanja definicije automata
											//(npr ulaza) , dok automat radi to je zabranjeno
	
//	public E_NKA(LinkedList<String> entry, HashSet<String> allStates,
//					HashSet<String> alphabet, HashSet<String> acceptableStates,
//					String firstState, HashMap<String,String> function) {
//		
//		this.allStates=allStates;
//		this.alphabet=alphabet;
//		this.acceptableStates=acceptableStates;
//		this.firstState=firstState;
//		this.function=function;
//		this.entry=entry;
//	}
	
	public E_NKA(LinkedList<String> entry, HashSet<String> allStates,
			HashSet<String> alphabet, HashSet<String> acceptableStates,
			String firstState, ValuePairs<String,String> pairs) {

		this.allStates=allStates;
		this.alphabet=alphabet;
		this.acceptableStates=acceptableStates;
		this.firstState=firstState;
		this.pairs=pairs;
		this.entry=entry;
}
	
	public LinkedList<String> getEntry() {
		if(enabledSetters==false) {
			System.err.println("Ulaz se ne smije mijenjati dok automat radi!");
			return null;
		}
		
		return entry;
	}

	public void setEntry(LinkedList<String> entry) {
		if(enabledSetters==false) {
			System.err.println("Ulaz se ne smije mijenjati dok automat radi!");
			return;
		}
		this.entry = entry;
	}
	
	
	/*
	 * mora biti koristen prvi konstruktor
	 * Ako se nad istim e-nka pozove vise funkcija run(), sa istim ili razlicitim ulazom, e-nka ce raditi ispravno
	 * jer se na pocetku svakog pocetka rada e-nka on resetira i ne pamti prijasnje rezultate.
	 */
	public void run() {  //pretpostavka je da treeSet sam azurira poredak clanova unutar stabla (sortira)
		enabledSetters=false;
		
		boolean rf=true;		//isto kao firstInArow samo za pocetke redova
		boolean firstInARow=true; //prvi u redu nakon - System.out.print(firstState + "|");
		currentStates=new TreeSet<>();
		nextStates=new TreeSet<>();
		String[] fStates=firstState.split(",");		//rezanje prvih stanja
		for(String fs : fStates)					//i dodavanje u trenutna stanja/stanje
			currentStates.add(fs);
		
		currentStates.addAll(findNewStates(new String("$"), firstState, currentStates));
		for(String s:currentStates) {
			if(rf)
				System.out.print(s);
			else
				System.out.print("," + s);
			rf=false;
		}
		System.out.print("|"); //prvi pocetak
		
		for(String e : entry) {
			if(!alphabet.contains(e)) {			// AKO NEPOSTOJI BACI IZNIMKU
				throw new IllegalArgumentException("Ulazni znak ne postoji za ovaj Epsilon NKA!");
			}
			boolean relativeFirst=true; //prvi u odlomku
			
			if(e.equals("|")) {                    //za sljedeci ulazni skup ide novi red, i novi set sa pocetnim stanjem
				System.out.println();
				currentStates.removeAll(currentStates);
				
				for(String fs : fStates)					//i dodavanje u trenutna stanja/stanje
					currentStates.add(fs);
				
				nextStates.removeAll(nextStates);
				firstInARow=true;
				relativeFirst=true;
				currentStates.addAll(findNewStates(new String("$"), firstState, currentStates));
				rf=true;
				for(String s:currentStates) {
					if(rf)
						System.out.print(s);
					else
						System.out.print("," + s);
					rf=false;
				}
				System.out.print("|"); //pocetak reda
				
			}else {
				nextStates.removeAll(nextStates);
				
				for(String o: currentStates) {           //za svaki ulazni znak idemo po svim trenutnim stanjima i 
					String key = new String(o + "," + e);		//stvaramo string "TrenutnoStanje,ulaz" i provjeravamo
					if(pairs.containsLeft(key)) {			//postoji li kljuc za tu kombinaciju
					
						List<String> epsilon=findNewStates(e, o,currentStates);	//nadji sva stanja u koja prelazi s tim ulazom       
						if(!epsilon.isEmpty())  						//i stavi ih u listu sljedecih stanja
							nextStates.addAll(epsilon);
								
					/*}else {
						//if(!nextStates.contains("#"))
							nextStates.add("#");*/					//inace stavi u stanje '#' ako vec nije (osigurava set)
					}
					
				}
				if(nextStates.isEmpty())
					nextStates.add("#");
				
				if(firstInARow) {								//ako je prvi u redu, zbog '|'
					for(String output: nextStates) {
						if(relativeFirst) {						//ako je prvi u odlomku, zbog zareza
							System.out.print(output);
							relativeFirst=false;
						}else {
							System.out.print("," + output);
						}
					}
				}else {
					System.out.print("|");
					for(String output: nextStates) {
						if(relativeFirst) {						//ako je prvi u odlomku, zbog zareza
							System.out.print(output);
							relativeFirst=false;
						}else {
							System.out.print("," + output);
						}
					}
				}
				
				firstInARow=false;			//vise nije prvi u redu
				currentStates.removeAll(currentStates);	//sljedeca stanja cemo u sljedecem krugu smatrat sadasnjim stanjima
				currentStates.addAll(nextStates);
			}
		}
		enabledSetters=true;
	}
	
	
	//mora biti koristen drugi konsturktor
	//funkcija vraca krajnji rezultat rada automata, tj. true ako se niz prihvaca ili
	//false ako se niz ne prihvaca.
	//naravno rezultat se odnosi na zadnji niz ulaznih znakova!
	public Boolean result() {
		enabledSetters=false;
		
		currentStates=new TreeSet<>();
		nextStates=new TreeSet<>();
		String[] fStates=firstState.split(",");		//rezanje prvih stanja
		for(String fs : fStates)					//i dodavanje u trenutna stanja/stanje
			currentStates.add(fs);
		
		currentStates.addAll(findNewStates(new String("$"), firstState, currentStates));
			
		for(String e : entry) {
			if(!alphabet.contains(e)) {			// AKO NEPOSTOJI BACI IZNIMKU
//				throw new IllegalArgumentException("Ulazni znak ne postoji za ovaj Epsilon NKA!");
				enabledSetters=true;
				return false;
			}
			
			if(e.equals("|")) {                    //za sljedeci ulazni skup ide novi red, i novi set sa pocetnim stanjem
				currentStates.removeAll(currentStates);
				
				for(String fs : fStates)					//i dodavanje u trenutna stanja/stanje
					currentStates.add(fs);
				
				nextStates.removeAll(nextStates);
				currentStates.addAll(findNewStates(new String("$"), firstState, currentStates));
				
			}else {
				nextStates.removeAll(nextStates);
				
				for(String o: currentStates) {           //za svaki ulazni znak idemo po svim trenutnim stanjima i 
					String key = new String(o + "," + e);		//stvaramo string "TrenutnoStanje,ulaz" i provjeravamo
					if(pairs.containsLeft( key )) {			//postoji li kljuc za tu kombinaciju
					
						List<String> epsilon=findNewStates(e, o,currentStates);	//nadji sva stanja u koja prelazi s tim ulazom       
						if(!epsilon.isEmpty())  						//i stavi ih u listu sljedecih stanja
							nextStates.addAll(epsilon);
								
					/*}else {
						//if(!nextStates.contains("#"))
							nextStates.add("#");*/					//inace stavi u stanje '#' ako vec nije (osigurava set)
					}
					
				}
				if(nextStates.isEmpty())
					nextStates.add("#");
				
				currentStates.removeAll(currentStates);	//sljedeca stanja cemo u sljedecem krugu smatrat sadasnjim stanjima
				currentStates.addAll(nextStates);
			}
		}
		
		boolean res=false;
		for(String s: currentStates) {
			if(acceptableStates.contains(s)) {
				res=true;
				break;
			}
		}
		enabledSetters=true;
		return res;
	}
	
	/**
	 * Za entry koji predstavlja ulazni znak u E-NKA automatu i za stanje u kojem se automat trenutno nalazi,
	 * funkcija ce pronaci sva sljedeca stanja u kojem ce se nalazit automat (ukljucujuci epsilon prelaze) i 
	 * vratiti ih u obliku liste.
	 * 
	 * @param entry
	 * @param currentState
	 * @return
	 */
	private List<String> findNewStates(String entry, String currentState,TreeSet<String> forbidden_epsilon){
		ArrayList<String> epsilon=new ArrayList<>();		//lista koju vracamo
		ArrayList<String> metaStates=new ArrayList<>();		//pomocna lista
		boolean hasNewStates=true;
		
		//#1 provjeravamo prvo epsilon prijelaze iz pocetnog stanja, a onda i  svih koji su iz pocetnog (epsilon okolina)
		String key=new String(currentState + ",$");
		if(pairs.containsLeft(key)) {		//stvaranje meta liste i ako postoji onda trazimo ostale epsilon prijelaze
			String[] strings=pairs.getRight(key).split(",");	//ako je vise stanja 
			for(String s: strings) {		//sve ih stavi u meta
				//if(!forbidden_epsilon.contains(s)) {
					metaStates.add(s);
					epsilon.add(s);
				//}
			}
		
			while(hasNewStates) {			//dok postoje nova stanja koja smo dodali
				hasNewStates=false;
				
				for(String m: metaStates) {
					key=new String(m + ",$");
					if(pairs.containsLeft(key)) {
						
						strings=pairs.getRight(key).split(",");
						/*if(!metaStates.contains(function.get(key)))
							metaStates.add(function.get(key)); */
						for(String s: strings) {			//u izlaznu listu
							if(!epsilon.contains(s) //&& !forbidden_epsilon.contains(m)
									 && !s.equals(new String("#"))) {
								epsilon.add(s);
								hasNewStates=true;
							}
						}
						
					}
				}
				ArrayList<String> temp=new ArrayList<>();		//iz 'epsilona' smo oduzeli sve sto je 
				temp.addAll(epsilon);							//u metaStates i time dobili samo nova stanja
				temp.removeAll(metaStates);					//koja cemo u sljedecoj iteraciji ispitivati
				metaStates.removeAll(metaStates);			//bez da smo izgubili epsilon listu
				metaStates.addAll(temp);
			}
		}
		//#2 provjeravamo za ulazni znak entry sva stanja koja smo dobili u prethodnom koraku
		metaStates.removeAll(metaStates);
		metaStates.addAll(epsilon);
		epsilon.removeAll(epsilon);
		//if(metaStates.isEmpty())
			metaStates.add(currentState);
		String[] strings; //definiram ga ovdje umjesto u petlji
		HashSet<String> remove_forbidden=new HashSet<>();
		
		for(String curr: metaStates) {
			key=new String(curr + "," + entry);
			if(pairs.containsLeft(key)) {
				
				strings=pairs.getRight(key).split(",");
				for(String s: strings) {			//u izlaznu listu
					if(!epsilon.contains(s) && !s.equals(new String("#"))) { //ako je prijelaz u nista ne dodavaj ga
						epsilon.add(s);
						remove_forbidden.add(s);
						hasNewStates=true;	//ako smo dodali ijedan novi onda moramo odraditi zadnji korak
					}
				}
			}
		}
		
		
		//#3 posljedni korak gdje ponovo provjeravamo epsilon okolinu od svih koji smo dodali u prezhodnom koraku
		ArrayList<String> temp=new ArrayList<>();	//prvo trazimo koje smo nova stanja dobili u prethodnom koraku
		temp.addAll(epsilon);
		//temp.removeAll(metaStates);			//i stavljamo ih u metaStates
		metaStates.removeAll(metaStates);
		metaStates.addAll(temp);
		
		//POTPUNO ISTI KOD KAO U #1 jer opet trazimo i dodajemo epsilon okolinu za stanja koja vec nisu dodana
		//samo sto ne inicijaliziramo metaStates jer ga vec imamo
		
		while(hasNewStates) {			//dok postoje nova stanja koja smo dodali
			hasNewStates=false;
			
			for(String m: metaStates) {
				key=new String(m + ",$");
				if(pairs.containsLeft(key)) {
					
					strings=pairs.getRight(key).split(",");
					/*if(!metaStates.contains(function.get(key)))
						metaStates.add(function.get(key)); */
					for(String s: strings) {			//u izlaznu listu
						if(!epsilon.contains(s) //&& remove_forbidden.contains(m)
								 && !s.equals(new String("#"))) {
							epsilon.add(s);
							hasNewStates=true;
						}
					}
					
				}
			}
			ArrayList<String> temp2=new ArrayList<>();		//iz 'epsilona' smo oduzeli sve sto je 
			temp2.addAll(epsilon);							//u metaStates i time dobili samo nova stanja
			temp2.removeAll(metaStates);					//koja cemo u sljedecoj iteraciji ispitivati
			metaStates.removeAll(metaStates);			//bez da smo izgubili epsilon listu
			metaStates.addAll(temp2);
		}
		
		//KRAJ , vracamo listu epsilon kao pronalazak svih stanja iz ulaza entry i pocentog stanja currentState
		//(epsilon nisu samo stanja koja smo dobili epsilon prelazima nego i prelazima s ulaznim znakom)
		
		return epsilon;
	}	//KOD SVIH "function.get(key)" TAJ STRING SPLITAT PO ZAREZIMA I DODAVATI KAO SKUP STANJA!!!

}
