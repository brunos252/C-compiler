package lab1;
/*
 * Na standardni ulaz dobiva sve ili vecinu potrebnih informacija.
 * Korisit se konacnim automatima (e-nka se preporuca).
 * Generator bi mogao razvijeni kod samo iskopirati u ovu klasu i dodati tekstualnu datoteku ako je potrebno.
 * Za funkcionalnost e-nka potrebne su funkcije prijelaza, koje cemo prije toga dobiti iz regularnih izraza.
 * Nakon svakog prijelaza izvrsavamo akciju definiranu u "Pravilima leksickog analizatora".
 * "Niz uniformnih znakova" ja rezultat rada ovog LA, odnosno to je ono sto se ispisuje na stdin.
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import simEnka.Action_E_NKA;

public class LA {

    private static BufferedReader br;
    private static LinkedList<Action_E_NKA> action_e_nkas=new LinkedList<>();
    private static LinkedList<String> allPosibleStates=new LinkedList<>();
    private static LinkedList<String> allPosibleActions=new LinkedList<>();
    private static String LAState;
    private static int row=1;
    private static int last=0; //index posljednjeg charactera u ulaznom textu
    private static int next=0; //index charactera kojeg sljedeceg citamo
    private static String text; //kompletan ulazni text (program)
    private static int first=0; //index prvog neanaliziranog charactera 
    private static int prefixLast=-1; //index zadnjeg charactera iz prepoznatog prefixa
    private static int first_h=0; //pomocna varijabla
    private static LinkedList<String> actions=new LinkedList<>();
    private static Action_E_NKA actEnka; //samo inicijalno, predstavlja zadnji enka koji je prihvatio niz

    public static void main(String[] args) throws IOException {
        //Stvaranje ref na datoteku i bf readera
        File tablica = new File("pomocni.txt"); //ovdje bi trebalo pisati nesto u stilu \\analizator\\pomocni.txt , ovisno kak ju nazove sacaric
        br = new BufferedReader(new FileReader(tablica));

        intializeAllPosibleStates();
        initalizeAllPosibleActions();
        initializeActEnkas();

        br.close();

        actEnka = action_e_nkas.get(0);
        // Zadnji dio
        br=new BufferedReader(new InputStreamReader(System.in));
        String current=new String();
        boolean result=false;
        LinkedList<String> entry=new LinkedList<>();
        String line=new String();
        
        
        StringBuilder sb=new StringBuilder();
        while(br.ready() && !(line=br.readLine()).isEmpty()) {  //citanje cijele datoteke
        	sb.append(line);
        }
        
        text=sb.toString();  //stvori veliki string od cijelog ulaza
        last=text.length();
        
        //zavrsna petlja
        while(first<last) { //dok se sa indexom prvog charactera nismo pozicionirali na kraj ulaznog texta
        	entry.clear();
        	result=false;
        	
        	++next;  //"procitaj" sljedeceg
        	if(next<=last) { //ako je nismo dosli do kraja ulaza
	        	current=text.substring(first, next);
	        	entry.add(current);
	        	
	        	for(Action_E_NKA enka : action_e_nkas) {
	        		if(enka.getName().equals(LAState)) {
	        			enka.setEntry(entry);
	        			result=enka.result();
	        			actEnka=enka;
	        			
	        			if(result) {  //ispunjeno da se postuje prvo pravilo koje prihvaca niz
	        				prefixLast=next;
	        				break;
	        			}
	        		}
	        	}
	        	
	        	if(!result && prefixLast!=(-1)) {
	        		
	        		doActions();
	        		
	        	}
	        	
	        }else if(prefixLast==(-1)){  //ako smo dosli do kraja ulaza
	    		System.err.println("Leksicka pogreska u redu: " + row);
	    		++first; //preskoci najlijeviji znak
	    		next=first; //vrati se nazad
	        }else {
	        	
	        	doActions();
	        	
	        }
        }
        
        br.close();
    }

    @SuppressWarnings("null")
	private static void initializeActEnkas() throws IOException {

        //Polja potrebna za inicijalizaciju svakog ActionENKA
        LinkedList<String> action, entry = new LinkedList<>();
        HashSet<String> allStates, alphabet, acceptableStates = new HashSet<>();
        HashMap<String, String> function = new HashMap<>();
        String firstState, name = null;
        allStates = alphabet = null;
        firstState = "0";
        acceptableStates.add("1");

        String line;
        while ((line = br.readLine()) != null) {
            if (line.startsWith("<S_")) {
                name = line.substring(line.indexOf("<") + 1, line.indexOf(">"));
                br.readLine();
                br.readLine();
            } else if (line.startsWith("{")) {
                action = extractActions();
                action_e_nkas.add(new Action_E_NKA(entry, allStates, alphabet, acceptableStates, firstState, function,
                        name, action));
                action.clear();
                entry.clear();
                allStates.clear();
                alphabet.clear();
                acceptableStates.clear();
                function.clear();
            } else {
                String transitions[] = new String[2];
                // (5,X->7) Na indeksu nula se nalazi kobinacija stanje,znak, a na indeksu jedan novo stanje
                transitions = line.split("->");
                function.put(transitions[0], transitions[1]);
                allStates.add(transitions[1]);
                //Na indeksu nula se nalazi trenutno stanje, a na indeksu jedan se nalazi znak
                transitions = transitions[0].split(",");
                allStates.add(transitions[0]);
                alphabet.add(transitions[1]);
            }
        }
    }

    private static LinkedList<String> extractActions() throws IOException {
        String line;
        LinkedList<String> action = new LinkedList<>();
        while (!((line = br.readLine()).equals("}"))) {
            action.add(line);
        }
        return action;
    }

    private static void intializeAllPosibleStates() throws IOException {
        String line;
        while ((line = br.readLine()) != null){
            if (line.startsWith("%X")){
                String[] states = line.split(" ");
                LAState = states[1];
                for(int i = 1; i < states.length; i++){
                    allPosibleStates.add(states[i]);
                }
            }
        }
    }

    private static void initalizeAllPosibleActions() throws IOException {
        String line;
        while ((line = br.readLine()) != null){
            if (line.startsWith("%L")){
                String[] actions = line.split(" ");
                for(int i = 1; i < actions.length; i++){
                    allPosibleActions.add(actions[i]);
                }
            }
        }
    }
    
    private static void doActions() {
    	first_h=first;
		next=prefixLast;  //vracanje na zadnji prepoznati prefix
		String current=text.substring(first,prefixLast); //prepoznati prefix
		
		actions=actEnka.getAction();
		for(String s: actions) {
			String[] splitted=s.split(" ");
			
			switch (splitted[0]){
			case "-": first=prefixLast;
			prefixLast=-1;
				break;
			case "NOVI_REDAK": ++row;
				break;
			case "UDJI_U_STANJE": LAState=splitted[1];
				break;
			case "VRATI_SE": prefixLast=first_h + Integer.parseInt(splitted[1]); //prefix last je zadnji kojeg cemo prepoznat, a u slucaju VRATI_SE
				next=prefixLast;  //vracanje na zadnji prepoznati prefix			// to mora biti samo vrati_se znakova
				first=first_h;
				break;																	
			
			default: System.out.println(splitted[0] + " " + row + " " + current);	//inace ispis sve sto treba na stdin
				first=prefixLast;
				prefixLast=-1;
				break;
			}
		}
    }

}
