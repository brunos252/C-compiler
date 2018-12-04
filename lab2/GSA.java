import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
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

        //long start;
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

		//System.out.println();
		//System.out.println("ZAPOCINJE skupovi nezavrsnih znakova: ");
		//for(String s : nezavrsni)
			//System.out.println(s + " : " + z.izracunajZapocinje(s));

        LinkedList<String> prijelazi = buildENKA(productions, nezavrsni, z);

        //drugi dio
        ValuePairs<String,String> pairs=new ValuePairs<>();
        String[] bits1;
        String[] bits2;
        for(String s : prijelazi) {
            bits1=s.split("-->");
            if(pairs.containsLeft(bits1[0])) {
                pairs.updateRight(bits1[0], pairs.getRight(bits1[0])+","+bits1[1]);
            }else {
                pairs.add(bits1[0], bits1[1]);
            }
        }

//		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
//		for(String s: prijelazi){
//			System.out.println(s);
//		}
//		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
//		for(Pair<String,String> s: pairs){
//			System.out.println(s.getLeft()+"->"+s.getRight());
//		}

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

        //#1 pretvaranje eNKA u NKA
//		ArrayList<String> prijelaziNKA = new ArrayList<>();
        ArrayList<String> temp = new ArrayList<>();
        ValuePairs<String,String> prijelaziNKA = new ValuePairs<>();

        for(Pair<String,String> prijelaz : pairs) {
            bits2=prijelaz.getLeft().split(",");

            if(!bits2[1].equals("$")) {
                temp=findNewStatesENKA(pairs, bits2[0], bits2[1]);
                StringBuilder sb = new StringBuilder();
                for(String s : temp) {
                    sb.append(s+",");
                }
                sb.deleteCharAt(sb.length()-1);

                prijelaziNKA.add(bits2[0]+","+bits2[1],sb.toString());
            }
        }
//		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
//		for(Pair<String,String> p : prijelaziNKA) {
//			System.out.println(p.getLeft()+"->"+p.getRight());
//		}

        //#2 pretvaranje NKA u DKA
        //pretpostavka da ako A,ulaz->{A,B,C} , da se u drugim produkcijama
        //nece pojaviti B,ulaz->{C,B,A} nego B,ulaz->{A,B,C}

//		ValuePairs<String,String> newPairs = new ValuePairs<>();
//		for(Pair<String,String> p: prijelaziNKA) {
//			bits=p.getLeft().split(",");
//			for(String za: zavrsni) {
//				if(!prijelaziNKA.containsLeft(bits[0]+","+za)) {
//					newPairs.add(bits[0]+","+za,"$$");
//				}
//			}
//			for(String ne: nezavrsni) {
//				if(!ne.equals("<%>") && !prijelaziNKA.containsLeft(bits[0]+","+ne)) {
//					newPairs.add(bits[0]+","+ne,"$$");
//				}
//			}
//		}
//		for(String za: zavrsni) {
//			prijelaziNKA.add("$$,"+za, "$$" );
//		}
//		for(String ne: nezavrsni) {
//			if(!ne.equals("<%>"))
//				prijelaziNKA.add("$$,"+ne, "$$" );
//		}
//
//		for(Pair<String,String> p: newPairs)
//			prijelaziNKA.addPair(p);

        //#3 generiranje tablice potrebne za sintaksni analizator
        String bits[];
        StringBuilder sb = new StringBuilder();
        List<String> nezavrsniBezPocetnog = new ArrayList<String>();
        List<String> zavrsniSaZadnjim = new ArrayList<String>();

        for(String n : nezavrsni) {
            if(!n.equals("<%>"))
                nezavrsniBezPocetnog.add(n);
        }
        zavrsniSaZadnjim.addAll(zavrsni);
        zavrsniSaZadnjim.add("#");

        String first=null;
        for(Pair<String,String> s: pairs) {
            first=s.getLeft();
            break;
        }
        bits=first.split(",");
        first=bits[0];

        ArrayList<String> firstStates = findNewStatesENKA(pairs, first, new String("$"));
        for(String s: firstStates) {
            sb.append(s+",");
        }
        sb.deleteCharAt(sb.length()-1);
        prijelaziNKA.add("[<%>]", sb.toString()+","+first);


//		String[] akcija = new String[(zavrsniSaZadnjim.size())*prijelaziNKA.size()];
//		String[] novoStanje = new String[(nezavrsniBezPocetnog.size())*prijelaziNKA.size()];
        ValuePairs<String,String> akcija = new ValuePairs<>();
        ValuePairs<String,String> novoStanje = new ValuePairs<>();
        char[] viticaste=null;

        int i=0;
        for(Pair<String,String> prijelaz: prijelaziNKA) {
            bits=prijelaz.getRight().split(",");

            for(String s: bits) { //reduciraj produkcije i prihvati produkcija
                viticaste=s.substring(s.indexOf("{")+1,s.length()-2).toCharArray();
                if(s.contains("*{")) {
                    if(s.contains("->*{")) {
                        for(char c: viticaste) {
                            if(!akcija.containsLeft(String.valueOf(i)+","+c))
                                akcija.add(String.valueOf(i)+","+c, "r("+ s.substring(1,6) +"$)");
                            else
                                akcija.updateRight(String.valueOf(i)+","+c,  "r("+ s.substring(1,6) +"$)");
                        }
                        break;
                    }else {
                        for(char c: viticaste) {
                            if(c=='#' && viticaste.length==1 && s.substring(1,5).equals(first.substring(1, 5))) {
                                if(!akcija.containsLeft(String.valueOf(i)+","+c))
                                    akcija.add(String.valueOf(i)+","+c, "PRIHVATI");
                                else
                                    akcija.updateRight(String.valueOf(i)+","+c, "PRIHVATI");
                            }else {
                                if(!akcija.containsLeft(String.valueOf(i)+","+c))
                                    akcija.add(String.valueOf(i)+","+c, "r("+ s.substring(1,s.indexOf("*")) +")");
                                else
                                    akcija.updateRight(String.valueOf(i)+","+c, "r("+ s.substring(1,s.indexOf("*")) +")");
                            }
                        }
                        break;
                    }
                }
            }
            for(String s: bits) { //pomakni produkcije
                for(String n: zavrsni) {
                    if(prijelaziNKA.containsLeft(s+","+n)) {

                        int j=0;
                        for(Pair<String,String> pair: prijelaziNKA) {
                            if((pair.getLeft()).equals(s+","+n))
                                break;
                            ++j;
                        }

                        if(!akcija.containsLeft(String.valueOf(i)+","+n))
                            akcija.add(String.valueOf(i)+","+n, "p("+String.valueOf(j)+")");
                        else
                            akcija.updateRight(String.valueOf(i)+","+n, "p("+String.valueOf(j)+")");

                        break;
                    }
                }
            }

            for(String s: bits) {
                for(String ne : nezavrsniBezPocetnog) {
                    if(prijelaziNKA.containsLeft(s+","+ne)) {

                        int j=0;
                        for(Pair<String,String> pair: prijelaziNKA) {
                            if((pair.getLeft()).equals(s+","+ne))
                                break;
                            ++j;
                        }

                        if(!novoStanje.containsLeft(String.valueOf(i)+","+ne))
                            novoStanje.add(String.valueOf(i)+","+ne, "s("+String.valueOf(j)+")");
                        else
                            novoStanje.updateRight(String.valueOf(i)+","+ne, "s("+String.valueOf(j)+")");

                        break;
                    }
                }
            }
            ++i;
        }

//        System.out.println("Drugi dio traje: "+(System.currentTimeMillis()-start));

//		System.out.println("########################################");
//		for(Pair<String,String> p : prijelaziNKA) {
//			System.out.println(p.getLeft()+"->"+p.getRight());
//		}
//
        try {
			File output = new File("./analizator/pomocni.txt");
			output.createNewFile();
			Writer writer= new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output)));
			
			writer.write(zavrsni+"\r\n");
			writer.write(nezavrsni+"\r\n");
			writer.write("Akcije:\r\n");
			for(Pair<String,String> p : akcija) {
				writer.write(p.getLeft()+"->"+p.getRight()+"\r\n");
			}
			
			writer.write("Nova stanja:\r\n");
			for(Pair<String,String> p : novoStanje) {
				writer.write(p.getLeft()+"->"+p.getRight()+"\r\n");
			}
			
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

    }

    public static ArrayList<String> findNewStatesENKA(ValuePairs<String,String> prijelazi, String stanje, String ulaz){
        ArrayList<String> epsilon=new ArrayList<>();		//lista koju vracamo
        ArrayList<String> metaStates=new ArrayList<>();		//pomocna lista
        boolean hasNewStates=false;

        //#1
        String key=new String(stanje + ",$");
        if(prijelazi.containsLeft(key)) {
            String[] strings=prijelazi.getRight(key).split(",");
            for(String s: strings) {
                metaStates.add(s);
                epsilon.add(s);
                hasNewStates=true;
            }
        }

        while(hasNewStates) {
            hasNewStates=false;

            for(String m: metaStates) {
                key=new String(m + ",$");
                if(prijelazi.containsLeft(key)) {

                    String[] strings=prijelazi.getRight(key).split(",");
                    for(String s: strings) {
                        if(!epsilon.contains(s)) {
                            epsilon.add(s);
                            hasNewStates=true;
                        }
                    }

                }
            }
            ArrayList<String> temp=new ArrayList<>();
            temp.addAll(epsilon);
            temp.removeAll(metaStates);
            metaStates.removeAll(metaStates);
            metaStates.addAll(temp);
        }
        //#2
        metaStates.removeAll(metaStates);
        metaStates.addAll(epsilon);
        epsilon.removeAll(epsilon);
        metaStates.add(stanje);

        for(String curr: metaStates) {
            key=new String(curr + "," + ulaz);
            if(prijelazi.containsLeft(key)) {
                String[] strings=prijelazi.getRight(key).split(",");
                for(String s: strings) {
                    if(!epsilon.contains(s)) {
                        epsilon.add(s);
                        hasNewStates=true;
                    }
                }
            }
        }

        //#3
        metaStates.removeAll(metaStates);
        metaStates.addAll(epsilon);

        while(hasNewStates) {
            hasNewStates=false;

            for(String m: metaStates) {
                key=new String(m + ",$");
                if(prijelazi.containsLeft(key)) {

                    String[] strings=prijelazi.getRight(key).split(",");
                    for(String s: strings) {
                        if(!epsilon.contains(s)) {
                            epsilon.add(s);
                            hasNewStates=true;
                        }
                    }
                }
            }
            ArrayList<String> temp=new ArrayList<>();
            temp.addAll(epsilon);
            temp.removeAll(metaStates);
            metaStates.removeAll(metaStates);
            metaStates.addAll(temp);
        }
        return epsilon;
    }


    public static LinkedList<String> buildENKA(Map<String, List<List<String>>> productions, List<String> nezavrsni, Zapocinje z) {
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
//		System.out.println();
//		System.out.println("stanja: " + stanja.size());
//		for(List<String> s : stanja) {
//			List<String> state = new ArrayList<String>();
//			state.addAll(s);
//			System.out.println(state);
//		}
//		System.out.println();

        //ispis prijelaza
//		System.out.println();
//		System.out.println("prijelazi: " + prijelazi.size());
//		for(Pair<List<String>, List<String>> pair : prijelazi) {
//			System.out.print("[");
//			for(String s : pair.getLeft()) {
//				if(s.equals(","))
//					System.out.print("]");
//				System.out.print(s + " ");
//			}
//			System.out.print("-> [");
//			for(String s : pair.getRight()) {
//				System.out.print(s + " ");
//			}
//			System.out.print("]");
//			System.out.println();
//		}

        //prijelazi u string
        LinkedList<String> result = new LinkedList<>();
        for(Pair<List<String>, List<String>> pair : prijelazi) {
            StringBuilder sb = new StringBuilder();
            boolean viticaste = false;
            boolean first = false;
            
            sb.append("[");
            for(String s : pair.getLeft()) {
                if(s.equals(","))
                    sb.append("]");
                if(s.equals("{")) {
                	viticaste = true;
                	first = true;
                }
                else if(s.equals("}")) {
                	viticaste = false;
                	sb.deleteCharAt(sb.length() - 1);
                }
                sb.append(s);
                if(viticaste) {
                	if(first)
                		first = false;
                	else
                		sb.append(" ");
                }
            }
            System.out.println("sb: " + sb);
            sb.append("-->[");
            for(String s : pair.getRight()) {
                sb.append(s);
            }
            sb.append("]");

            result.add(sb.toString());
        }
        return result;
    }
}
