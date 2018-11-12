import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/*
 * Generira sintaksni analizator pomocu podataka dobivenih na standardnom ulazu (zapravo samo nadopunjuje
 * kod vec postojeceg sintaksnog analizatora)
 * generator analizatoru potrebne podatke predaje putem datoteke
 */

public class GSA {	
	public static void main(String[] args) {
		Map<String, List<List<String>>> productions = new LinkedHashMap<String, List<List<String>>>();
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
		
		/*for(String key : productions.keySet()) {
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
}
