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
    private LinkedList<Action_E_NKA> action_e_nkas;

    public static void main(String[] args) throws IOException {
        //Stvaranje ref na datoteku i bf readera
        File tablica = new File("");
        br = new BufferedReader(new FileReader(tablica));



        br.close();
    }

    private void initializeActEnkas() throws IOException {
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


    private LinkedList<String> extractActions() throws IOException {
        String line;
        LinkedList<String> action = new LinkedList<>();
        while (!((line = br.readLine()).equals("}"))) {
            action.add(line);
        }
        return action;
    }

}
