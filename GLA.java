import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/*
 * Generira leksicki analizator pomocu podataka dobivenih na standardnom ulazu (zapravo samo nadopunjuje
 * kod vec postojeceg leksickog analizatora)
 * Regularne izraze  se moze preoblikovati pogodno za LA (slika 2.13), sve nezavrsne znakove na desnoj strani
 * pretvara u zavrsne.
 * Svako pravilo LA je napisano za jedno stanje LA
 * Regularne definicije ce sluziti za prepoznavanje niza, a pravila leksickog analizatora ce sluziti za stvaranje
 * e-nka i odredivanje akcija prilikom svakog prijelaza e-nka
 */

public class GLA {
	private static int stageName=0;
	
	public static void main(String[] args) {
//		String string=e_nka_rules("<S_unarni>(a)((b)|a)*","abc");
//		System.out.println(string);
		
		Map<String, String> regularDefinitions = new HashMap<String, String>();
		List<String> rules = new LinkedList<String>();
		String states = null, names = null, line, refRegDef;
		String[] split;
		boolean definitions = true;
		int startIndex, endIndex;
		
		try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))){
			line = br.readLine();
			while(line != null && !(line.isEmpty())) {
				if(line.startsWith("%X")){
						states = line + "\n";
						definitions = false;
				} else if(line.startsWith("%L")) {
						names = line + "\n";
				} else if(definitions) {
					split = line.split(" ");
					regularDefinitions.put(split[0], split[1]);
				} else {
					rules.add(line);
				}
			line = br.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/*
		 * algoritam 2.4.2
		 * sve nezavrsne znakove sa desne strane regularnih definicija u ulaznoj
		 * datoteci zamijenjuje zavrsnima
		 */
		String regEx;
		for(String key : regularDefinitions.keySet()) {
			regEx = regularDefinitions.get(key);
			for(int i = 0; i < regEx.length(); i++) {
				if(regEx.charAt(i) == '\\') {				//zaobilazi slucajeve kad je { medu zavrsnim znakovima, zapisan kao /{
					i++;
					continue;
				}
				else if(regEx.charAt(i) == '{') {
					endIndex = regEx.indexOf('}', i);
					refRegDef = regEx.substring(i, endIndex + 1);
					regEx = regEx.replace(refRegDef, "(" + regularDefinitions.get(refRegDef) + ")");
					regularDefinitions.put(key, regEx);
				}
			}
		}
		
		/*
		 * odvaja pravila na stringove prvi redak i ostatak, kakvi su potrebni za Segin algoritam ispod
		 * takoder zamijenjuje nezavrsne znakove zavrsnima, npr {znamenka} postaje 0|1|2|3..
		 * primjer first : <S_pocetno>\(
		 * primjer rest  :	{
		 *				  	-
		 *					NOVI_REDAK
		 *					}
		 */
		List<String> firsts = new LinkedList<String>();
		List<String> rests = new LinkedList<String>();
		String currentRest = "";
		boolean first = true;
		String substring;
		for(String s : rules) {
			if(s.startsWith("<")) {
				while((startIndex = s.indexOf("{")) != -1 && s.charAt(startIndex - 1) != '\\') {					
					endIndex = s.indexOf("}");
					substring = s.substring(startIndex, endIndex + 1);
					s = s.replace(substring,"("+ regularDefinitions.get(substring)+")");
				}
				
				if(!first) {
					rests.add(currentRest + "\n");
					currentRest = "";
				} else
					first = false;
				firsts.add(s);
			} else {
				currentRest += currentRest.isEmpty() ? s : ("\n" + s);
			}
		}
		rests.add(currentRest + "\n");
		
		/*
		 * upis u izlaznu datoteku, prvo stanja i imena leksickih jedinki pa izlaze iz Seginog algoritma
		 */
		try {
			File output = new File("./analizator/pomocni.txt");
			//output.delete();
			output.createNewFile();
			FileOutputStream out = new FileOutputStream(output);
			
			out.write(states.getBytes());
			out.write(names.getBytes());
			
			int size = firsts.size();
			for(int i = 0; i < size; i++) {
				
//				System.out.println(firsts.get(i));
				
				stageName=0;
				out.write(e_nka_rules(firsts.get(i), rests.get(i)).getBytes());
				//System.out.print(e_nka_rules(firsts.get(i), rests.get(i)) + "\n\n");
			}
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
/* 
 * Prihvaca npr: <S_pocetno>\n
					 {
					 -
					 NOVI_REDAK
					 }
					 
		Vraca: <S_pocetno>
				//pocetno stanje
				//skup prihvatljivih stanja
			   //pravila za e-nka u obliku : Stanje, ulaz-> novoStanje
			   {
			   AKCIJA prepisana
			   }
			   
		NAPOMENA - epsilon se nadomjesta simbolom '$'
		'first' je prvi red, npr '<S_pocetno>\n', a 'rest' su svi ostali redovi jedne cijeline
	 */
	public static String e_nka_rules(String first, String rest) {
		StringBuilder result = new StringBuilder();
		
		String[] bits=first.split(">", 2);
		result.append(bits[0]);
		result.append(">");
		result.append("\n");
		result.append("0");
		result.append("\n");
		result.append("1");
		result.append("\n");
		
		
		// Analiza regularnog izraza i stvaranje pravila za e-nka.
		turn(bits[1],result);
		
		result.append(rest);
		
		String back=result.toString();
		return back;
	}
	
	//vraca dva broja koja oznacavaju ime prvog i zavrsnog stanja tog izraza
	public static int[] turn(String regular, StringBuilder result) {
		int first=stageName++;
		int last=stageName++;
		int previousStage=first;
		
		// rjesavanje 'vanjskih' ili ('|') operatora
//		System.out.println(regular);
		LinkedList<String> splited=operator_split(regular);
		if(splited.size()>1) {//ako je nesto splitano
			for(String s:splited) {
				int[] sub=turn(s,result);
				
				result.append(first + ",$->" + sub[0]); //spoji prvog i prvog iz rezultata podizraza
				result.append("\n");
				result.append(sub[1] + ",$->" + last); //spoji zadnjeg iz rezultata podizraza i zadnjeg
				result.append("\n");
			}
		}else {		// kad nema vise ili operatora, onda je preostalo citati znak po znak
			boolean prefix=false;
			char[] reg=regular.toCharArray();
			String sign;
			int[] temp= {-1,-1};
			int a=-1;
			
			for(int i=0; i<reg.length; ++i) {
				if(prefix) {
					prefix=false;
					
					switch(reg[i]) {
					case 't' : sign="\t";
					break;
					
					case 'n': sign="\n";
					break;
					
					case '_': sign=" ";
					break;
					
					default: sign=Character.toString(reg[i]);
					break;
					}
					
					result.append(previousStage + "," + sign + "->" + (stageName)); //prijelaz iz proslog u sadasnje stanje
					result.append("\n");
					previousStage=stageName;
					++stageName;
				}else {
					if(reg[i]=='\\') {
						prefix=true;
						continue;
					}
					if(reg[i]!='(') {
						
						if((i+1) < reg.length && reg[i+1]=='*') { //ako se ponavlja
							result.append(stageName+","+reg[i]+"->"+stageName); //dodaj prijelaz u sebe
							result.append("\n");
							result.append(previousStage+",$->"+stageName); //dodaj prijelaz iz proslog u sadasnji kao epsilon jer ga moze i preskociti
							result.append("\n");
							
							++i;
						}else { //ako se ne ponavlja
							result.append(previousStage + "," + reg[i] + "->" + (stageName)); //dodaj novi prijelaz
							result.append("\n");
						}
					
						previousStage=stageName;
						++stageName;
						
					//zatvorena zagrada je rijesena preko else dijela
//					}else if(reg[i]==')'){
//						result.append("ZATVORENA ZAGRADA");
//						result.append("\n");
						
					//zvjezdica rijesena preko ++i dijela
//					}else if(reg[i]=='*') {
//						result.append("ZVJEZDICA");
//						result.append("\n");
						
					}else {
						int j=closed_bracket(reg, i);
						temp=turn(substring(reg,i+1, j),result);
						i=j;
						a=previousStage;
						result.append(previousStage + ",$->" + temp[0]);
						result.append("\n");
						previousStage=temp[1];
						
						if((i+1) < reg.length && reg[i+1]=='*') {
							result.append(temp[1]+",$->"+temp[0]);
							result.append("\n");
							result.append(a+",$->"+temp[1]);
							result.append("\n");
						
							++i; //da se preskoci *
					}
				}
				
					//ako se ponavlja
				
				}
			}
			
			
		}
		if(previousStage!=first) { //u nekim slucajevima nebi radilo bez ovoga if
			result.append(previousStage + ",$->" + last);
			result.append("\n");
		}
		
		int [] p= {first,last};
		return p;
	}
	
	//prima regularan izraz i vraca polje u kojem su izrazi dobiveni micanje operatora '|'
	public static LinkedList<String> operator_split(String regular) {
		LinkedList<String> result = new LinkedList<>();
		char[] array=regular.toCharArray();
		int brackets=0;
		StringBuilder previous=new StringBuilder();
		int k=0;  //position of 'c'
		
		for(char c: array) {
			if(c=='(' && is_operator(array,k))
				++brackets;
			if(c==')' && is_operator(array,k))
				--brackets;
			if(c=='|') {
				if(brackets==0) {
					if(is_operator(array,k)){
						result.add(previous.toString());
						previous=new StringBuilder();
					}else {
						previous.append(c);
					}
				}else {
					previous.append(c);
				}
			}else {
				previous.append(c);
			}
			++k;
		}
		result.add(previous.toString());
//		for(String s: result)
//			System.out.println(s);
		
//		System.out.println("ovdje");
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		return result;
	}
	
	
	public static boolean is_operator(char[] field,int k) {
		int n=0;
		while(k-1>=0 && field[k-1]=='\\') {
			++n;
			--k;
		}
		return n%2==0;
	}
	
	//vraca indeks zatvorene zagrade na temelju indeksa otvorene zagrade
	public static int closed_bracket(char[] array,int i) {
		int brackets=0;
		
		for(int j=i+1; j<array.length; ++j) {
			if(array[j]==')' && is_operator(array,j)) {
				--brackets;
				if(brackets==(-1)) {
					return j;
				}
			}
			
			if(array[j]=='(' && is_operator(array, j)) 
				++brackets;
		}
		return -1;
	}
	
	//i is included, j is not in the substring
	public static String substring(char[] array,int i, int j) {
		StringBuilder sb=new StringBuilder();
		for(int k=i; k<j; ++k) {
			sb.append(array[k]);
		}
		String back=sb.toString();
		return back;
	}

}
