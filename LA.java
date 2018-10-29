/*
 * Na standardni ulaz dobiva sve ili vecinu potrebnih informacija.
 * Korisit se konacnim automatima (e-nka se preporuca).
 * Generator bi mogao razvijeni kod samo iskopirati u ovu klasu i dodati tekstualnu datoteku ako je potrebno.
 * Za funkcionalnost e-nka potrebne su funkcije prijelaza, koje cemo prije toga dobiti iz regularnih izraza.
 * Nakon svakog prijelaza izvrsavamo akciju definiranu u "Pravilima leksickog analizatora".
 * "Niz uniformnih znakova" ja rezultat rada ovog LA, odnosno to je ono sto se ispisuje na stdin.
 */

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class LA {

    private static BufferedReader br;
    private static LinkedList<Action_E_NKA> action_e_nkas;
    private static LinkedList<String> allPosibleStates;
    private static LinkedList<String> allPosibleActions;
    private static String LAState = "";

    public static void main(String[] args) throws IOException {
        //Stvaranje ref na datoteku i bf readera
        File tablica = new File(""); //ovdje bi trebalo pisati nesto u stilu \\analizator\\pomocni.txt , ovisno kak ju nazove sacaric
        br = new BufferedReader(new FileReader(tablica));
        //tu ispod bi islo prvo citanje onih 5 ili kolko redaka i spremanje u odgovarajuče liste, polja itd (to če koristit evacic)
        //a nakon toga citanje cijele datoteke do kraja i to poslat u ovu funkciju kaj si napisal(ako ce funckija sve e-nka inicijalizirat) ili čitanje blokova
        //ako ce funkcija inicijalizirat jedan po jedan

        intializeAllPosibleStates();
        initalizeAllPosibleActions();
        initializeActEnkas();

        br.close();

        //tu nakon inicijalizacije ide evacicev dio, i citas sa stdina (koji je naravno upaljen od pocekta rada ovog programa pa je vazno da u inicijalizaciji
        //se nista ne cita sa stdina)

    }

    private static void initializeActEnkas() throws IOException {
        //mislim da ovdje trebas primat u funkciju barem jedan veliki String koji ce u sebi sadrzavat sve za inicijalizaciju jednog enka
        //mozda ne bi bilo lose da ti je ovo funkicja za inicjalizaciju jednog akcijskog enka, koju pozivas za svakog
        //jer ako nije onda mislim da je problem  u "else if" dijelu zato kaj za svaki novi akcijski enka moras re-inicijalizirat
        //sve ove mape,list,i hashove (ili popravi to ili radi funkciju da je za inicijalizaciju jednog akcijskog enka)


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
                name = line;
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

}
