import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

public class GeneratorKoda 
{
	private static int loop =0;
	private static Cvor korijen=new Cvor();
	private static LinkedHashMap<Integer, Cvor> razinaCvora=new LinkedHashMap<Integer, Cvor>();
	private static CvorTabZn korijenTabZn=new CvorTabZn();
	//main je ako se nalazimo u main funkciji a inFunction je za bilokoju drugu funkciju
	private static boolean main = false;
	private static Writer writer = null;
	//globalne varijable koje se dodaju u strojni program na kraju
	private static Map<String, String> globalVars = new TreeMap<String, String>();
	private static String imeVar;
	private static int G_Count = 0;
	private static boolean negative = false;

    public static void main(String[] args) throws IOException 
    {
    	String line;
    	try(BufferedReader br = new BufferedReader(new InputStreamReader(System.in)))
		{
    		line=br.readLine();
    		String pr=line.trim();
    		korijen.setJedinka(new Cvor.leksJed(pr, null, 0));
    		razinaCvora.put(0, korijen);
    		line=br.readLine();
    		Cvor cv;
    		while(line!=null && !(line.isEmpty()))
    		{
    			cv=new Cvor();
    			pr=line.trim();
    			int razina=line.length()-line.trim().length();
    			Cvor.leksJed pom;
    			if(pr.startsWith("<"))
    			{
    				pom=new Cvor.leksJed(pr, null, 0);
    			}
    			else
    			{
    				String[] x=pr.split(" ");
    				String dva=x[2];
    				pom=new Cvor.leksJed(x[0], dva, Integer.parseInt(x[1]));
    			}
    			cv.setJedinka(pom);
    			razinaCvora.put(razina, cv);
    			Cvor pomoc=razinaCvora.get(razina-1);
    			pomoc.dodajDjete(cv);
        		line=br.readLine();
    		}

    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	
    	//stvaranje output file-a
    	File output = new File("./a.frisc");
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output, false)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		writer.write("\tMOVE 40000, R7\n\tCALL F_MAIN\n\tHALT\n\n");

		//pocetak semanticke analize
		provjeriPrijevodnaJedinica(korijen, korijenTabZn);
		    	
		//provjera main funkcije, nije potrebna za 4.lab
    	for(CvorTabZn.identifikator IDN:korijenTabZn.getidentifikatori())
    	{
    		if(IDN.getime().equals("main"))
    		{
    			if(!IDN.gettip().get(0).equals("fun") ||
					!IDN.gettip().get(1).equals("int") ||
					!IDN.gettip().get(2).equals("void"))
    				main = false;
    			else
    				main = true;
    		}
    	}
    	if(!main)
    	{
    		ispisi("main");
    	}
		
		provjeriFun(korijenTabZn);
    
		if(!globalVars.isEmpty())
			writer.write("\n");
		for(String s : globalVars.keySet()) {
			writer.write("G_" + s.toUpperCase() + "\tDW %D " + globalVars.get(s) + "\n");
		}
		writer.close();
		System.out.println("closed");
    }

    private static boolean provjeriFun(CvorTabZn korijen_znakova) {
    	int i = 0;
    	CvorTabZn.identifikator IDN;
    	for(i = 0; i < korijen_znakova.getidentifikatori().size(); i++) {
    		IDN = korijen_znakova.getidentifikatori().get(i);
    		if(IDN.gettip().get(0).equals("fun"))
    			if(IDN.getdefin() != 1) {
    				ispisi("funkcija");
    			}
    	}
    	for(i = 0; i < korijen_znakova.getdjeca().size(); i++) {
    		provjeriFun(korijen_znakova.getdjeca().get(i));
    	}
		return true;
	}

	private static void provjeriPrimarniIzraz(Cvor trenutni, CvorTabZn trenutniZn) throws IOException
	{
		if(trenutni.getdjeca().get(0).getjedinkaIDN().equals("IDN"))
		{
			CvorTabZn.identifikator IDN = null;
			Cvor cv = trenutni.getdjeca().get(0);
			CvorTabZn cvTabZn = new CvorTabZn();
			cvTabZn.clone(trenutniZn);
			while(true) {
				for(CvorTabZn.identifikator i : cvTabZn.getidentifikatori())
				{
					if(cv.getjedinkaime().equals(i.getime()))
					{
						IDN = new CvorTabZn.identifikator();
						IDN.clone(i);
						break;
					}
				}
				if (IDN != null)
					break;
				if (cvTabZn.getroditelj() != null)
				{
					cvTabZn.clone(cvTabZn.getroditelj());
				}
				else
				{
					IDN = null;
					break;
				}
			}

			if (IDN==null)
			{
				greska(trenutni);
			}
			trenutni.setTip(IDN.gettip());
			trenutni.setl_izraz(IDN.getl_izraz());
			trenutni.setVrijednost(IDN.vrijednost);
			
			if(!IDN.gettip().get(0).equals("fun")) {
				writer.write("\tLOAD R0, ");
				//znaci da je globalna varijabla, inace je lokalna
				if(globalVars.containsKey(IDN.getime().toUpperCase())) {
					writer.write("(G_" + IDN.getime().toUpperCase() + ")\n");
				} /*else {
					writer.write("(R7 + " + odmak + ")\n");
				}*/
				writer.write("\tPUSH R0\n");
			}
		}
		else if(trenutni.getdjeca().get(0).getjedinkaIDN().equals("BROJ"))
		{
			BigInteger big=new BigInteger((trenutni.getdjeca().get(0).getjedinkaime()));
			String mali=String.valueOf(2147483647);
			String veli=String.valueOf(2147483647);
			BigInteger min=new BigInteger(mali);
			BigInteger max=new BigInteger(veli);
			if (big.compareTo(max)==1 || big.compareTo(min.negate())==-1)
			{
				greska(trenutni);
			}
			LinkedList<String> tip=new LinkedList<>();
			tip.add("int");
			trenutni.setTip(tip);
			trenutni.setl_izraz(false);
			//SAC krivo vjv
			//int h = Integer.parseInt(trenutni.getntip().get(0));
			int l = Integer.parseInt(trenutni.getdjeca().get(0).getjedinkaime());
			trenutni.setVrijednost(String.valueOf(1 * l));
			if(!trenutniZn.uBloku.isEmpty()) {
				//2^19
				BigInteger granica = new BigInteger(String.valueOf(524288));
				if(big.compareTo(granica) == 1 || big.compareTo(granica.negate()) == -1) {
					globalVars.put(String.valueOf(G_Count), trenutni.getdjeca().get(0).getjedinkaime());
					writer.write("\tLOAD R0, (G_" + String.valueOf(G_Count++) + ")\n\tPUSH R0\n");
				} else {
					if(negative) {
					//if(Integer.parseInt(trenutni.getVrijednost()) < 0) {
						writer.write("\tMOVE %D -" + trenutni.getdjeca().get(0).getjedinkaime() + ", R0\n\tPUSH R0\n");
						negative = false;
					} else
						writer.write("\tMOVE %D " + trenutni.getdjeca().get(0).getjedinkaime() + ", R0\n\tPUSH R0\n");
				}
			} else {
				globalVars.put(imeVar.toUpperCase(), trenutni.getdjeca().get(0).getjedinkaime());
			}
		}
		else if(trenutni.getdjeca().get(0).getjedinkaIDN().equals("ZNAK"))
		{
			if(trenutni.getdjeca().get(0).getjedinkaime().startsWith("\\"))
			{
				if(!(trenutni.getdjeca().get(0).getjedinkaime().startsWith("\\n") || trenutni.getdjeca().get(0).getjedinkaime().startsWith("\\t") || trenutni.getdjeca().get(0).getjedinkaime().startsWith("\\0") || trenutni.getdjeca().get(0).getjedinkaime().startsWith("\\\'") || trenutni.getdjeca().get(0).getjedinkaime().startsWith("\\\"") || trenutni.getdjeca().get(0).getjedinkaime().startsWith("\\\\")))
				{
					greska(trenutni);
				}
			}
			LinkedList<String> tip=new LinkedList<>();
			tip.add("char");
			trenutni.setTip(tip);
			trenutni.setl_izraz(false);
		}
		else if(trenutni.getdjeca().get(0).getjedinkaIDN().equals("NIZ_ZNAKOVA"))
		{
			String niz=trenutni.getdjeca().get(0).getjedinkaime();
			int index=niz.indexOf("\\");
			while(index>=0)
			{
				if(!(niz.charAt(index+1)=='t' || niz.charAt(index+1)=='n' || niz.charAt(index+1)=='0' || niz.charAt(index+1)=='\'' || niz.charAt(index+1)=='\"' || niz.charAt(index+1)=='\\'))
				{
					greska(trenutni);
				}
				niz=niz.substring(index+1, niz.length()-1);
				index=niz.indexOf("\\");
			}
			LinkedList<String> tip=new LinkedList<>();
			tip.add("niz");
			tip.add("const_char");
			trenutni.setTip(tip);
			trenutni.setl_izraz(false);
		}
		else if(trenutni.getdjeca().get(0).getjedinkaIDN().equals("L_ZAGRADA"))
		{
			provjeriIzraz(trenutni.getdjeca().get(1), trenutniZn);
			LinkedList<String> pr=trenutni.getdjeca().get(1).gettip();
			trenutni.setTip(pr);
			boolean priv=trenutni.getdjeca().get(1).getl_izraz();
			trenutni.setl_izraz(priv);
		}
	}

	private static void provjeriPostfiksIzraz(Cvor trenutni, CvorTabZn trenutniZn) throws IOException
	{
		if(trenutni.getdjeca().get(0).getjedinkaIDN().equals("<primarni_izraz>"))
		{
			//SACtrenutni.getdjeca().get(0).setntip(trenutni.getntip());
			provjeriPrimarniIzraz(trenutni.getdjeca().get(0), trenutniZn);
			LinkedList<String> pr=trenutni.getdjeca().get(0).gettip();
			trenutni.setTip(pr);
			boolean priv=trenutni.getdjeca().get(0).getl_izraz();
			trenutni.setl_izraz(priv);
			//SACtrenutni.setVrijednost(trenutni.getdjeca().get(0).getVrijednost());
		}
		else if(trenutni.getdjeca().get(1).getjedinkaIDN().equals("L_UGL_ZAGRADA"))
		{
			provjeriPostfiksIzraz(trenutni.getdjeca().get(0),trenutniZn);
			if(!trenutni.getdjeca().get(0).gettip().get(0).equals("niz"))
			{
				greska(trenutni);
			}
			if(!(trenutni.getdjeca().get(0).gettip().get(1).equals("int") || trenutni.getdjeca().get(0).gettip().get(1).equals("char") || trenutni.getdjeca().get(0).gettip().get(1).equals("const_int") || trenutni.getdjeca().get(0).gettip().get(1).equals("const_char")))
			{
				greska(trenutni);
			}
			provjeriIzraz(trenutni.getdjeca().get(2),trenutniZn);
			LinkedList<String> nov=new LinkedList<>();
			nov.add("int");
			boolean t=implProvjera(trenutni.getdjeca().get(2).gettip(),nov);
			if(!t)
			{
				greska(trenutni);
			}

			LinkedList<String> pom=new LinkedList<>();
			pom.add(trenutni.getdjeca().get(0).gettip().get(1));
			trenutni.setTip(pom);

			if(trenutni.getdjeca().get(0).gettip().get(1).equals("const_int") || trenutni.getdjeca().get(0).gettip().get(1).equals("const_char"))
			{
				boolean p=false;
				trenutni.setl_izraz(p);
			}
			else
			{
				boolean a=true;
				trenutni.setl_izraz(a);
			}
		}
		else if(trenutni.getdjeca().get(1).getjedinkaIDN().equals("L_ZAGRADA"))
		{
			if(trenutni.getdjeca().get(2).getjedinkaIDN().equals("D_ZAGRADA"))
			{
				provjeriPostfiksIzraz(trenutni.getdjeca().get(0),trenutniZn);
				if(!trenutni.getdjeca().get(0).gettip().get(0).equals("fun"))
				{
					greska(trenutni);
				}
				if(!trenutni.getdjeca().get(0).gettip().get(2).equals("void"))
				{
					greska(trenutni);
				}
				LinkedList<String> pom=new LinkedList<>();
				pom.add(trenutni.getdjeca().get(0).gettip().get(1));
				trenutni.setTip(pom);
				trenutni.setl_izraz(false);
				
				//SAC
				//trenutni.setIme(trenutni.getdjeca().get(0).getIme());
				//treba ici 	writer.write("\tCALL F_" + trenutni.getdjeca().get(0).getIme() + "\n");
				//writer.write("\tCALL F_F\n");
			}
			else if(trenutni.getdjeca().get(2).getjedinkaIDN().equals("<lista_argumenata>"))
			{
				provjeriPostfiksIzraz(trenutni.getdjeca().get(0), trenutniZn);
				provjeriListaArgumenata(trenutni.getdjeca().get(2), trenutniZn);
				if(!trenutni.getdjeca().get(0).gettip().get(0).equals("fun"))
				{
					greska(trenutni);
				}
				if(trenutni.getdjeca().get(0).gettip().size()-2!=trenutni.getdjeca().get(2).gettip().size())
				{
					greska(trenutni);
				}
				for(int i=0;i<trenutni.getdjeca().get(2).gettip().size();i++)
				{
					LinkedList<String> prvi=new LinkedList<>();
					LinkedList<String> drugi=new LinkedList<>();
					prvi.add(trenutni.getdjeca().get(2).gettip().get(i));
					drugi.add(trenutni.getdjeca().get(0).gettip().get(i+2));
					boolean t=implProvjera(prvi,drugi);
					if(!t)
					{
						greska(trenutni);
					}
				}
				LinkedList<String> p=new LinkedList<>();
				p.add(trenutni.getdjeca().get(0).gettip().get(1));
				trenutni.setTip(p);
				trenutni.setl_izraz(false);
				//SAC
				//trenutni.setIme(trenutni.getdjeca().get(0).getIme());
				/*
				//stavi predane parametre za funkciju na stog
				for(int i = 0; i < trenutni.getdjeca().get(2).getIme().size(); i++) {
					//param je konstanta
					if(trenutni.getdjeca().get(2).getIme().get(i).equals("")) {
						writer.write("\tMOVE %D " + trenutni.getdjeca().get(2).getVrijednost() + "\n");
					}
					
					//param je var
					else {
						//ako je globalna
						if(globalVars.keySet().contains(trenutni.getdjeca().get(2).getIme().get(i))) {
							writer.write("\tLOAD R0, (G_" + trenutni.getdjeca().get(2).getIme() + "\n");
						} else {
							writer.write("\tLOAD R0, (R7 + " + odmak + "\n");
						}
					}
					writer.write("\tPUSH R0\n");
				}
				writer.write("\tCALL F_" + trenutni.getdjeca().get(0).getIme() + "\n");
				//writer.write("\tADD R7, " + );*/
			}
		}
		else if(trenutni.getdjeca().get(1).getjedinkaIDN().equals("OP_INC") || trenutni.getdjeca().get(1).getjedinkaIDN().equals("OP_DEC"))
		{
			provjeriPostfiksIzraz(trenutni.getdjeca().get(0),trenutniZn);
			if(!trenutni.getdjeca().get(0).getl_izraz())
			{
				greska(trenutni);
			}
			LinkedList<String> p=new LinkedList<>();
			p.add("int");
			boolean t=implProvjera(trenutni.getdjeca().get(0).gettip(),p);
			if(!t)
			{
				greska(trenutni);
			}
			trenutni.setTip(p);
			trenutni.setl_izraz(false);
		}
	}

	private static void provjeriListaArgumenata(Cvor trenutni, CvorTabZn trenutniZn) throws IOException
	{
		if(trenutni.getdjeca().size() == 1)
		{
			provjeriIzrazPridruzivanja(trenutni.getdjeca().get(0),trenutniZn);
			trenutni.setTip(trenutni.getdjeca().get(0).gettip());
			//trenutni.setVrijednost(trenutni.getdjeca().get(0).getVrijednost());
			//trenutni.setIme(trenutni.getdjeca().get(0).getIme());

		}
		else
		{
			provjeriListaArgumenata(trenutni.getdjeca().get(0),trenutniZn);
			provjeriIzrazPridruzivanja(trenutni.getdjeca().get(2),trenutniZn);
			LinkedList<String> pr=new LinkedList<>();
			LinkedList<String> dr=new LinkedList<>();
			pr=trenutni.getdjeca().get(0).gettip();
			dr=trenutni.getdjeca().get(2).gettip();
			pr.addAll(dr);
			//trenutni.setTip(pr);
			//trenutni.setVrijednost(trenutni.getdjeca().get(0).getVrijednost() + trenutni.getdjeca().get(2).getVrijednost());
		}
	}

	private static void provjeriUnarniIzraz(Cvor trenutni, CvorTabZn trenutniZn) throws IOException
	{
		if(trenutni.getdjeca().size() == 1)
		{
			provjeriPostfiksIzraz(trenutni.getdjeca().get(0),trenutniZn);
			trenutni.setTip(trenutni.getdjeca().get(0).gettip());
			trenutni.setl_izraz(trenutni.getdjeca().get(0).getl_izraz());
			//trenutni.setIme(trenutni.getdjeca().get(0).getIme());
			//trenutni.setVrijednost(trenutni.getdjeca().get(0).getVrijednost());
		}
		else if(trenutni.getdjeca().get(0).getjedinkaIDN().equals("OP_INC") || trenutni.getdjeca().get(0).getjedinkaIDN().equals("OP_DEC"))
		{
			/*LinkedList<String> pom = new LinkedList<String>();
			pom.add("1");
			trenutni.setntip(pom);*/
			provjeriUnarniIzraz(trenutni.getdjeca().get(1), trenutniZn);
			if(!trenutni.getdjeca().get(1).getl_izraz())
			{
				greska(trenutni);
			}
			LinkedList<String> pr=new LinkedList<>();
			pr.add("int");
			boolean t=implProvjera(trenutni.getdjeca().get(1).gettip(),pr);
			if(!t)
			{
				greska(trenutni);
			}
			trenutni.setTip(pr);
			trenutni.setl_izraz(false);
		}
		else
		{
			//unarni operator
			//LinkedList<String> pom = new LinkedList<String>();
			//SAC i tu je krivo valjda
			if(trenutni.getdjeca().get(0).getdjeca().get(0).getjedinkaIDN().equals("MINUS")) {
				System.out.println("aaL " + trenutni.getdjeca().get(1).getVrijednost());
				negative = true;
				//pom.add("-1");
			} else {
				//pom.add("1");
			}
			//trenutni.getdjeca().get(1).setntip(pom);

			
			provjeriCastIzraz(trenutni.getdjeca().get(1), trenutniZn);
			LinkedList<String> pr=new LinkedList<>();

			//SAC nekaj krivo
			/*if(trenutni.getdjeca().get(0).getdjeca().get(0).getjedinkaIDN().equals("MINUS")) {
				trenutni.setVrijednost("-" + trenutni.getdjeca().get(1).getVrijednost());
			} else
				trenutni.setVrijednost(trenutni.getdjeca().get(1).getVrijednost());*/
			
			pr.add("int");
			boolean t=implProvjera(trenutni.getdjeca().get(1).gettip(),pr);
			if(!t)
			{
				greska(trenutni);
			}
			trenutni.setTip(pr);
			trenutni.setl_izraz(false);
		}
	}

	private static void provjeriCastIzraz(Cvor trenutni, CvorTabZn trenutniZn) throws IOException
	{
		if(trenutni.getdjeca().size() == 1)
		{
			trenutni.getdjeca().get(0).setntip(trenutni.getntip());
			provjeriUnarniIzraz(trenutni.getdjeca().get(0), trenutniZn);
			trenutni.setTip(trenutni.getdjeca().get(0).gettip());
			trenutni.setl_izraz(trenutni.getdjeca().get(0).getl_izraz());
			//trenutni.setVrijednost(trenutni.getdjeca().get(0).getVrijednost());
			//trenutni.setIme(trenutni.getdjeca().get(0).getIme());
		}
		else
		{
			provjeriImeTipa(trenutni.getdjeca().get(1),trenutniZn);
			provjeriCastIzraz(trenutni.getdjeca().get(3),trenutniZn);
			if(!(trenutni.getdjeca().get(1).gettip().get(0).equals("int") || trenutni.getdjeca().get(1).gettip().get(0).equals("const_int") || trenutni.getdjeca().get(1).gettip().get(0).equals("char") || trenutni.getdjeca().get(1).gettip().get(0).equals("const_char")))
			{
				greska(trenutni);
			}
			if(!(trenutni.getdjeca().get(3).gettip().get(0).equals("int") || trenutni.getdjeca().get(3).gettip().get(0).equals("const_int") || trenutni.getdjeca().get(3).gettip().get(0).equals("char") || trenutni.getdjeca().get(3).gettip().get(0).equals("const_char")))
			{
				greska(trenutni);
			}
			trenutni.setTip(trenutni.getdjeca().get(1).gettip());
			trenutni.setl_izraz(false);
			//trenutni.setVrijednost(trenutni.getdjeca().get(3).getVrijednost());
		}
	}

	private static void provjeriImeTipa(Cvor trenutni, CvorTabZn trenutniZn)
	{
		if(trenutni.getdjeca().size() == 1)
		{
			provjeriSpecifikatorTipa(trenutni.getdjeca().get(0), trenutniZn);
			trenutni.setTip(trenutni.getdjeca().get(0).gettip());
		}
		else
		{
			provjeriSpecifikatorTipa(trenutni.getdjeca().get(1), trenutniZn);
			if(trenutni.getdjeca().get(1).gettip().contains("void"))
			{
				greska(trenutni);
			}
			String ps=trenutni.getdjeca().get(1).gettip().get(0);
			String x="const_" + ps;
			LinkedList<String> t=new LinkedList<>();
			t.add(x);
			trenutni.setTip(t);
		}
	}

	private static void provjeriSpecifikatorTipa(Cvor trenutni, CvorTabZn trenutniZn)
	{
		LinkedList<String> pom=new LinkedList<>();
		if(trenutni.getdjeca().get(0).getjedinkaIDN().equals("KR_VOID"))
		{
			pom.add("void");
			trenutni.setTip(pom);
		}
		else if(trenutni.getdjeca().get(0).getjedinkaIDN().equals("KR_CHAR"))
		{
			pom.add("char");
			trenutni.setTip(pom);
		}
		else
		{
			pom.add("int");
			trenutni.setTip(pom);
		}
	}

	private static void provjeriMultiplikativniIzraz(Cvor trenutni, CvorTabZn trenutniZn) throws IOException
	{
		LinkedList<String> po = new LinkedList<>();
		po.add("OP_PUTA");
		po.add("OP_DIJELI");
		po.add("OP_MOD");
		if(trenutni.getdjeca().size() == 1)
		{
			provjeriCastIzraz(trenutni.getdjeca().get(0),trenutniZn);
			trenutni.setTip(trenutni.getdjeca().get(0).gettip());
			trenutni.setl_izraz(trenutni.getdjeca().get(0).getl_izraz());
			//trenutni.setVrijednost(trenutni.getdjeca().get(0).getVrijednost());
			//trenutni.setIme(trenutni.getdjeca().get(0).getIme());
		}
		else if(po.contains(trenutni.getdjeca().get(1).getjedinkaIDN()))
		{
			provjeriMultiplikativniIzraz(trenutni.getdjeca().get(0),trenutniZn);
			LinkedList<String> pr=new LinkedList<>();
			pr.add("int");
			boolean t=implProvjera(trenutni.getdjeca().get(0).gettip(),pr);
			if(!t)
			{
				greska(trenutni);
			}
			provjeriCastIzraz(trenutni.getdjeca().get(2),trenutniZn);
			boolean ab=implProvjera(trenutni.getdjeca().get(2).gettip(),pr);
			if(!ab)
			{
				greska(trenutni);
			}
			
			trenutni.setTip(pr);
			trenutni.setl_izraz(false);

		}
	}

	private static void provjeriAditivniIzraz(Cvor trenutni, CvorTabZn trenutniZn) throws IOException
	{
		LinkedList<String> po = new LinkedList<>();
		po.add("PLUS");
		po.add("MINUS");
		if(trenutni.getdjeca().size() == 1)
		{
			provjeriMultiplikativniIzraz(trenutni.getdjeca().get(0),trenutniZn);
			trenutni.setTip(trenutni.getdjeca().get(0).gettip());
			trenutni.setl_izraz(trenutni.getdjeca().get(0).getl_izraz());
			//trenutni.setVrijednost(trenutni.getdjeca().get(0).getVrijednost());
			//trenutni.setIme(trenutni.getdjeca().get(0).getIme());
		}
		else if(po.contains(trenutni.getdjeca().get(1).getjedinkaIDN()))
		{
			provjeriAditivniIzraz(trenutni.getdjeca().get(0),trenutniZn);
			LinkedList<String> pr=new LinkedList<>();
			pr.add("int");
			boolean t=implProvjera(trenutni.getdjeca().get(0).gettip(),pr);
			if(!t)
			{
				greska(trenutni);
			}
			provjeriMultiplikativniIzraz(trenutni.getdjeca().get(2),trenutniZn);
			boolean ab=implProvjera(trenutni.getdjeca().get(2).gettip(),pr);
			if(!ab)
			{
				greska(trenutni);
			}
			
			if(trenutni.getdjeca().get(1).getjedinkaIDN().equals("PLUS"))
				writer.write("\tPOP R1\n\tPOP R0\n\tADD R0, R1, R0\n\tPUSH R0\n\n");
			else if(trenutni.getdjeca().get(1).getjedinkaIDN().equals("MINUS"))
				writer.write("\tPOP R1\n\tPOP R0\n\tSUB R0, R1, R0\n\tPUSH R0\n\n");
			/*else if(trenutni.getdjeca().get(1).getjedinkaIDN().equals("OP_BIN_I"))
				writer.write("\tPOP R1\n\tPOP R0\n\tAND R0, R1, R0\n\tPUSH R0\n\n");
			else if(trenutni.getdjeca().get(1).getjedinkaIDN().equals("OP_BIN_XILI"))
				writer.write("\tPOP R1\n\tPOP R0\n\tXOR R0, R1, R0\n\tPUSH R0\n\n");
			else if(trenutni.getdjeca().get(1).getjedinkaIDN().equals("OP_BIN_ILI"))
				writer.write("\tPOP R1\n\tPOP R0\n\tOR R0, R1, R0\n\tPUSH R0\n\n");*/
			
			trenutni.setTip(pr);
			trenutni.setl_izraz(false);

		}
	}

	private static void provjeriOdnosniIzraz(Cvor trenutni, CvorTabZn trenutniZn) throws IOException
	{
		LinkedList<String> po = new LinkedList<>();
		po.add("OP_LT");
		po.add("OP_GT");
		po.add("OP_LTE");
		po.add("OP_GTE");

		if(trenutni.getdjeca().size() == 1)
		{
			provjeriAditivniIzraz(trenutni.getdjeca().get(0),trenutniZn);
			trenutni.setTip(trenutni.getdjeca().get(0).gettip());
			trenutni.setl_izraz(trenutni.getdjeca().get(0).getl_izraz());
			//trenutni.setVrijednost(trenutni.getdjeca().get(0).getVrijednost());
			//trenutni.setIme(trenutni.getdjeca().get(0).getIme());
		}
		else if(po.contains(trenutni.getdjeca().get(1).getjedinkaIDN()))
		{
			provjeriOdnosniIzraz(trenutni.getdjeca().get(0),trenutniZn);
			LinkedList<String> pr=new LinkedList<>();
			pr.add("int");
			boolean t=implProvjera(trenutni.getdjeca().get(0).gettip(),pr);
			if(!t)
			{
				greska(trenutni);
			}
			provjeriAditivniIzraz(trenutni.getdjeca().get(2),trenutniZn);
			boolean ab=implProvjera(trenutni.getdjeca().get(2).gettip(),pr);
			if(!ab)
			{
				greska(trenutni);
			}
			
			/*if(trenutni.getdjeca().get(1).getjedinkaIDN().equals("PLUS"))
				writer.write("\tPOP R1\n\tPOP R0\n\tADD R0, R1, R0\n\tPUSH R0\n\n");
			else if(trenutni.getdjeca().get(1).getjedinkaIDN().equals("MINUS"))
				writer.write("\tPOP R1\n\tPOP R0\n\tSUB R0, R1, R0\n\tPUSH R0\n\n");
			else if(trenutni.getdjeca().get(1).getjedinkaIDN().equals("OP_BIN_I"))
				writer.write("\tPOP R1\n\tPOP R0\n\tAND R0, R1, R0\n\tPUSH R0\n\n");
			else if(trenutni.getdjeca().get(1).getjedinkaIDN().equals("OP_BIN_XILI"))
				writer.write("\tPOP R1\n\tPOP R0\n\tXOR R0, R1, R0\n\tPUSH R0\n\n");
			else if(trenutni.getdjeca().get(1).getjedinkaIDN().equals("OP_BIN_ILI"))
				writer.write("\tPOP R1\n\tPOP R0\n\tOR R0, R1, R0\n\tPUSH R0\n\n");*/
			
			trenutni.setTip(pr);
			trenutni.setl_izraz(false);

		}
	}

	private static void provjeriJednakosniIzraz(Cvor trenutni, CvorTabZn trenutniZn) throws IOException
	{
		LinkedList<String> po = new LinkedList<>();
		po.add("OP_EQ");
		po.add("OP_NEQ");
		if(trenutni.getdjeca().size() == 1)
		{
			provjeriOdnosniIzraz(trenutni.getdjeca().get(0),trenutniZn);
			trenutni.setTip(trenutni.getdjeca().get(0).gettip());
			trenutni.setl_izraz(trenutni.getdjeca().get(0).getl_izraz());
			//trenutni.setVrijednost(trenutni.getdjeca().get(0).getVrijednost());
			//trenutni.setIme(trenutni.getdjeca().get(0).getIme());
		}
		else if(po.contains(trenutni.getdjeca().get(1).getjedinkaIDN()))
		{
			provjeriJednakosniIzraz(trenutni.getdjeca().get(0),trenutniZn);
			LinkedList<String> pr=new LinkedList<>();
			pr.add("int");
			boolean t=implProvjera(trenutni.getdjeca().get(0).gettip(),pr);
			if(!t)
			{
				greska(trenutni);
			}
			provjeriOdnosniIzraz(trenutni.getdjeca().get(2),trenutniZn);
			boolean ab=implProvjera(trenutni.getdjeca().get(2).gettip(),pr);
			if(!ab)
			{
				greska(trenutni);
			}
			
			/*if(trenutni.getdjeca().get(1).getjedinkaIDN().equals("PLUS"))
				writer.write("\tPOP R1\n\tPOP R0\n\tADD R0, R1, R0\n\tPUSH R0\n\n");
			else if(trenutni.getdjeca().get(1).getjedinkaIDN().equals("MINUS"))
				writer.write("\tPOP R1\n\tPOP R0\n\tSUB R0, R1, R0\n\tPUSH R0\n\n");
			else if(trenutni.getdjeca().get(1).getjedinkaIDN().equals("OP_BIN_I"))
				writer.write("\tPOP R1\n\tPOP R0\n\tAND R0, R1, R0\n\tPUSH R0\n\n");
			else if(trenutni.getdjeca().get(1).getjedinkaIDN().equals("OP_BIN_XILI"))
				writer.write("\tPOP R1\n\tPOP R0\n\tXOR R0, R1, R0\n\tPUSH R0\n\n");
			else if(trenutni.getdjeca().get(1).getjedinkaIDN().equals("OP_BIN_ILI"))
				writer.write("\tPOP R1\n\tPOP R0\n\tOR R0, R1, R0\n\tPUSH R0\n\n");*/
			
			trenutni.setTip(pr);
			trenutni.setl_izraz(false);

		}
	}

	private static void provjeriBinIIzraz(Cvor trenutni, CvorTabZn trenutniZn) throws IOException
	{
		LinkedList<String> po = new LinkedList<>();
		po.add("OP_BIN_I");
		if(trenutni.getdjeca().size() == 1)
		{
			provjeriJednakosniIzraz(trenutni.getdjeca().get(0),trenutniZn);
			trenutni.setTip(trenutni.getdjeca().get(0).gettip());
			trenutni.setl_izraz(trenutni.getdjeca().get(0).getl_izraz());
			//trenutni.setVrijednost(trenutni.getdjeca().get(0).getVrijednost());
			//trenutni.setIme(trenutni.getdjeca().get(0).getIme());
		}
		else if(po.contains(trenutni.getdjeca().get(1).getjedinkaIDN()))
		{
			provjeriBinIIzraz(trenutni.getdjeca().get(0),trenutniZn);
			LinkedList<String> pr=new LinkedList<>();
			pr.add("int");
			boolean t=implProvjera(trenutni.getdjeca().get(0).gettip(),pr);
			if(!t)
			{
				greska(trenutni);
			}
			provjeriJednakosniIzraz(trenutni.getdjeca().get(2),trenutniZn);
			boolean ab=implProvjera(trenutni.getdjeca().get(2).gettip(),pr);
			if(!ab)
			{
				greska(trenutni);
			}
			
			/*if(trenutni.getdjeca().get(1).getjedinkaIDN().equals("PLUS"))
				writer.write("\tPOP R1\n\tPOP R0\n\tADD R0, R1, R0\n\tPUSH R0\n\n");
			else if(trenutni.getdjeca().get(1).getjedinkaIDN().equals("MINUS"))
				writer.write("\tPOP R1\n\tPOP R0\n\tSUB R0, R1, R0\n\tPUSH R0\n\n");
			else*/ if(trenutni.getdjeca().get(1).getjedinkaIDN().equals("OP_BIN_I"))
				writer.write("\tPOP R1\n\tPOP R0\n\tAND R0, R1, R0\n\tPUSH R0\n\n");
			/*else if(trenutni.getdjeca().get(1).getjedinkaIDN().equals("OP_BIN_XILI"))
				writer.write("\tPOP R1\n\tPOP R0\n\tXOR R0, R1, R0\n\tPUSH R0\n\n");
			else if(trenutni.getdjeca().get(1).getjedinkaIDN().equals("OP_BIN_ILI"))
				writer.write("\tPOP R1\n\tPOP R0\n\tOR R0, R1, R0\n\tPUSH R0\n\n");*/
			
			trenutni.setTip(pr);
			trenutni.setl_izraz(false);

		}
	}

	private static void provjeriBinXiliIzraz(Cvor trenutni, CvorTabZn trenutniZn) throws IOException
	{
		LinkedList<String> po = new LinkedList<>();
		po.add("OP_BIN_XILI");
		if(trenutni.getdjeca().size() == 1)
		{
			provjeriBinIIzraz(trenutni.getdjeca().get(0),trenutniZn);
			trenutni.setTip(trenutni.getdjeca().get(0).gettip());
			trenutni.setl_izraz(trenutni.getdjeca().get(0).getl_izraz());
			//trenutni.setVrijednost(trenutni.getdjeca().get(0).getVrijednost());
			//trenutni.setIme(trenutni.getdjeca().get(0).getIme());
		}
		else if(po.contains(trenutni.getdjeca().get(1).getjedinkaIDN()))
		{
			provjeriBinXiliIzraz(trenutni.getdjeca().get(0),trenutniZn);
			LinkedList<String> pr=new LinkedList<>();
			pr.add("int");
			boolean t=implProvjera(trenutni.getdjeca().get(0).gettip(),pr);
			if(!t)
			{
				greska(trenutni);
			}
			provjeriBinIIzraz(trenutni.getdjeca().get(2),trenutniZn);
			boolean ab=implProvjera(trenutni.getdjeca().get(2).gettip(),pr);
			if(!ab)
			{
				greska(trenutni);
			}
			
			/*if(trenutni.getdjeca().get(1).getjedinkaIDN().equals("PLUS"))
				writer.write("\tPOP R1\n\tPOP R0\n\tADD R0, R1, R0\n\tPUSH R0\n\n");
			else if(trenutni.getdjeca().get(1).getjedinkaIDN().equals("MINUS"))
				writer.write("\tPOP R1\n\tPOP R0\n\tSUB R0, R1, R0\n\tPUSH R0\n\n");
			else if(trenutni.getdjeca().get(1).getjedinkaIDN().equals("OP_BIN_I"))
				writer.write("\tPOP R1\n\tPOP R0\n\tAND R0, R1, R0\n\tPUSH R0\n\n");
			else */if(trenutni.getdjeca().get(1).getjedinkaIDN().equals("OP_BIN_XILI"))
				writer.write("\tPOP R1\n\tPOP R0\n\tXOR R0, R1, R0\n\tPUSH R0\n\n");
			/*else if(trenutni.getdjeca().get(1).getjedinkaIDN().equals("OP_BIN_ILI"))
				writer.write("\tPOP R1\n\tPOP R0\n\tOR R0, R1, R0\n\tPUSH R0\n\n");*/
			
			trenutni.setTip(pr);
			trenutni.setl_izraz(false);

		}
	}

	private static void provjeriBinIliIzraz(Cvor trenutni, CvorTabZn trenutniZn) throws IOException
	{
		LinkedList<String> po = new LinkedList<>();
		po.add("OP_BIN_ILI");
		if(trenutni.getdjeca().size() == 1)
		{
			provjeriBinXiliIzraz(trenutni.getdjeca().get(0),trenutniZn);
			trenutni.setTip(trenutni.getdjeca().get(0).gettip());
			trenutni.setl_izraz(trenutni.getdjeca().get(0).getl_izraz());
			//trenutni.setVrijednost(trenutni.getdjeca().get(0).getVrijednost());
			//trenutni.setIme(trenutni.getdjeca().get(0).getIme());
		}
		else if(po.contains(trenutni.getdjeca().get(1).getjedinkaIDN()))
		{
			provjeriBinIliIzraz(trenutni.getdjeca().get(0),trenutniZn);
			LinkedList<String> pr=new LinkedList<>();
			pr.add("int");
			boolean t=implProvjera(trenutni.getdjeca().get(0).gettip(),pr);
			if(!t)
			{
				greska(trenutni);
			}
			provjeriBinXiliIzraz(trenutni.getdjeca().get(2),trenutniZn);
			boolean ab=implProvjera(trenutni.getdjeca().get(2).gettip(),pr);
			if(!ab)
			{
				greska(trenutni);
			}
			
			/*if(trenutni.getdjeca().get(1).getjedinkaIDN().equals("PLUS"))
				writer.write("\tPOP R1\n\tPOP R0\n\tADD R0, R1, R0\n\tPUSH R0\n\n");
			else if(trenutni.getdjeca().get(1).getjedinkaIDN().equals("MINUS"))
				writer.write("\tPOP R1\n\tPOP R0\n\tSUB R0, R1, R0\n\tPUSH R0\n\n");
			else if(trenutni.getdjeca().get(1).getjedinkaIDN().equals("OP_BIN_I"))
				writer.write("\tPOP R1\n\tPOP R0\n\tAND R0, R1, R0\n\tPUSH R0\n\n");
			else if(trenutni.getdjeca().get(1).getjedinkaIDN().equals("OP_BIN_XILI"))
				writer.write("\tPOP R1\n\tPOP R0\n\tXOR R0, R1, R0\n\tPUSH R0\n\n");
			else */if(trenutni.getdjeca().get(1).getjedinkaIDN().equals("OP_BIN_ILI"))
				writer.write("\tPOP R1\n\tPOP R0\n\tOR R0, R1, R0\n\tPUSH R0\n\n");
			
			trenutni.setTip(pr);
			trenutni.setl_izraz(false);

		}
	}

	private static void provjeriLogIIzraz(Cvor trenutni, CvorTabZn trenutniZn) throws IOException
	{
		LinkedList<String> po = new LinkedList<>();
		po.add("OP_I");
		if(trenutni.getdjeca().size() == 1)
		{
			provjeriBinIliIzraz(trenutni.getdjeca().get(0),trenutniZn);
			trenutni.setTip(trenutni.getdjeca().get(0).gettip());
			trenutni.setl_izraz(trenutni.getdjeca().get(0).getl_izraz());
			//trenutni.setVrijednost(trenutni.getdjeca().get(0).getVrijednost());
			//trenutni.setIme(trenutni.getdjeca().get(0).getIme());
		}
		else if(po.contains(trenutni.getdjeca().get(1).getjedinkaIDN()))
		{
			provjeriLogIIzraz(trenutni.getdjeca().get(0),trenutniZn);
			LinkedList<String> pr=new LinkedList<>();
			pr.add("int");
			boolean t=implProvjera(trenutni.getdjeca().get(0).gettip(),pr);
			if(!t)
			{
				greska(trenutni);
			}
			provjeriBinIliIzraz(trenutni.getdjeca().get(2),trenutniZn);
			boolean ab=implProvjera(trenutni.getdjeca().get(2).gettip(),pr);
			if(!ab)
			{
				greska(trenutni);
			}
			
			/*if(trenutni.getdjeca().get(1).getjedinkaIDN().equals("PLUS"))
				writer.write("\tPOP R1\n\tPOP R0\n\tADD R0, R1, R0\n\tPUSH R0\n\n");
			else if(trenutni.getdjeca().get(1).getjedinkaIDN().equals("MINUS"))
				writer.write("\tPOP R1\n\tPOP R0\n\tSUB R0, R1, R0\n\tPUSH R0\n\n");
			else if(trenutni.getdjeca().get(1).getjedinkaIDN().equals("OP_BIN_I"))
				writer.write("\tPOP R1\n\tPOP R0\n\tAND R0, R1, R0\n\tPUSH R0\n\n");
			else if(trenutni.getdjeca().get(1).getjedinkaIDN().equals("OP_BIN_XILI"))
				writer.write("\tPOP R1\n\tPOP R0\n\tXOR R0, R1, R0\n\tPUSH R0\n\n");
			else if(trenutni.getdjeca().get(1).getjedinkaIDN().equals("OP_BIN_ILI"))
				writer.write("\tPOP R1\n\tPOP R0\n\tOR R0, R1, R0\n\tPUSH R0\n\n");*/
			
			trenutni.setTip(pr);
			trenutni.setl_izraz(false);

		}
	}

	private static void provjeriLogIliIzraz(Cvor trenutni, CvorTabZn trenutniZn) throws IOException
	{
		LinkedList<String> po = new LinkedList<>();
		po.add("OP_ILI");
		if(trenutni.getdjeca().size() == 1)
		{
			provjeriLogIIzraz(trenutni.getdjeca().get(0),trenutniZn);
			trenutni.setTip(trenutni.getdjeca().get(0).gettip());
			trenutni.setl_izraz(trenutni.getdjeca().get(0).getl_izraz());
			//trenutni.setVrijednost(trenutni.getdjeca().get(0).getVrijednost());
			//trenutni.setIme(trenutni.getdjeca().get(0).getIme());
		}
		else if(po.contains(trenutni.getdjeca().get(1).getjedinkaIDN()))
		{
			provjeriLogIliIzraz(trenutni.getdjeca().get(0),trenutniZn);
			LinkedList<String> pr=new LinkedList<>();
			pr.add("int");
			boolean t=implProvjera(trenutni.getdjeca().get(0).gettip(),pr);
			if(!t)
			{
				greska(trenutni);
			}
			provjeriLogIIzraz(trenutni.getdjeca().get(2),trenutniZn);
			boolean ab=implProvjera(trenutni.getdjeca().get(2).gettip(),pr);
			if(!ab)
			{
				greska(trenutni);
			}
			
			/*if(trenutni.getdjeca().get(1).getjedinkaIDN().equals("PLUS"))
				writer.write("\tPOP R1\n\tPOP R0\n\tADD R0, R1, R0\n\tPUSH R0\n\n");
			else if(trenutni.getdjeca().get(1).getjedinkaIDN().equals("MINUS"))
				writer.write("\tPOP R1\n\tPOP R0\n\tSUB R0, R1, R0\n\tPUSH R0\n\n");
			else if(trenutni.getdjeca().get(1).getjedinkaIDN().equals("OP_BIN_I"))
				writer.write("\tPOP R1\n\tPOP R0\n\tAND R0, R1, R0\n\tPUSH R0\n\n");
			else if(trenutni.getdjeca().get(1).getjedinkaIDN().equals("OP_BIN_XILI"))
				writer.write("\tPOP R1\n\tPOP R0\n\tXOR R0, R1, R0\n\tPUSH R0\n\n");
			else if(trenutni.getdjeca().get(1).getjedinkaIDN().equals("OP_BIN_ILI"))
				writer.write("\tPOP R1\n\tPOP R0\n\tOR R0, R1, R0\n\tPUSH R0\n\n");*/
			
			trenutni.setTip(pr);
			trenutni.setl_izraz(false);

		}
	}

	private static void provjeriIzrazPridruzivanja(Cvor trenutni, CvorTabZn trenutniZn) throws IOException
	{
		if(trenutni.getdjeca().size() == 1)
		{
			provjeriLogIliIzraz(trenutni.getdjeca().get(0),trenutniZn);
			trenutni.setTip(trenutni.getdjeca().get(0).gettip());
			trenutni.setl_izraz(trenutni.getdjeca().get(0).getl_izraz());
			//trenutni.setVrijednost(trenutni.getdjeca().get(0).getVrijednost());
			//trenutni.setIme(trenutni.getdjeca().get(0).getIme());
		}
		else
		{
			provjeriPostfiksIzraz(trenutni.getdjeca().get(0), trenutniZn);
			if(!trenutni.getdjeca().get(0).getl_izraz())
			{
				greska(trenutni);
			}
			provjeriIzrazPridruzivanja(trenutni.getdjeca().get(2),trenutniZn);
			boolean t=implProvjera(trenutni.getdjeca().get(2).gettip(),trenutni.getdjeca().get(0).gettip());
			if(!t)
			{
				greska(trenutni);
			}
			trenutni.setTip(trenutni.getdjeca().get(0).gettip());
			trenutni.setl_izraz(false);
		}
	}

	private static void provjeriIzraz(Cvor trenutni, CvorTabZn trenutniZn) throws IOException
	{
		if(trenutni.getdjeca().size() == 1)
		{
			provjeriIzrazPridruzivanja(trenutni.getdjeca().get(0),trenutniZn);
			trenutni.setTip(trenutni.getdjeca().get(0).gettip());
			trenutni.setl_izraz(trenutni.getdjeca().get(0).getl_izraz());
			//trenutni.setVrijednost(trenutni.getdjeca().get(0).getVrijednost());
			//trenutni.setIme(trenutni.getdjeca().get(0).getIme());
		}
		else
		{
			provjeriIzraz(trenutni.getdjeca().get(0),trenutniZn);
			provjeriIzrazPridruzivanja(trenutni.getdjeca().get(2),trenutniZn);
			//trenutni.setTip(trenutni.getdjeca().get(2).gettip());
			//trenutni.setl_izraz(false);
		}
	}

	private static void provjeriSlozenaNaredba(Cvor trenutni, CvorTabZn trenutniZn) throws IOException
	{
		if(trenutni.getdjeca().size() == 3)
		{
			provjeriListaNaredbi(trenutni.getdjeca().get(1),trenutniZn);
		}
		else
		{
			provjeriListaDeklaracija(trenutni.getdjeca().get(1),trenutniZn);
			provjeriListaNaredbi(trenutni.getdjeca().get(2),trenutniZn);
		}
	}

	private static void provjeriListaNaredbi(Cvor trenutni, CvorTabZn trenutniZn) throws IOException
	{
		if(trenutni.getdjeca().size() == 1)
		{
			provjeriNaredba(trenutni.getdjeca().get(0),trenutniZn);
		}
		else
		{
			provjeriListaNaredbi(trenutni.getdjeca().get(0),trenutniZn);
			provjeriNaredba(trenutni.getdjeca().get(1),trenutniZn);
		}
	}

	private static void provjeriNaredba(Cvor trenutni, CvorTabZn trenutniZn) throws IOException
	{
		if(trenutni.getdjeca().get(0).getjedinkaIDN().equals("<slozena_naredba>"))
		{
			CvorTabZn novi_cvor_znak = new CvorTabZn();
			novi_cvor_znak.setroditelj(trenutniZn);
			trenutniZn.dodajDjete(novi_cvor_znak);
			novi_cvor_znak.setUBloku(trenutniZn.getUBloku());
			provjeriSlozenaNaredba(trenutni.getdjeca().get(0),novi_cvor_znak);
			trenutniZn=trenutniZn.getroditelj();
		}
		else if(trenutni.getdjeca().get(0).getjedinkaIDN().equals("<izraz_naredba>"))
		{
			provjeriIzrazNaredba(trenutni.getdjeca().get(0),trenutniZn);
		}
		else if(trenutni.getdjeca().get(0).getjedinkaIDN().equals("<naredba_grananja>"))
		{
			provjeriNaredbaGrananja(trenutni.getdjeca().get(0),trenutniZn);
		}
		else if(trenutni.getdjeca().get(0).getjedinkaIDN().equals("<naredba_petlje>"))
		{
			provjeriNaredbaPetlje(trenutni.getdjeca().get(0),trenutniZn);
		}
		else if(trenutni.getdjeca().get(0).getjedinkaIDN().equals("<naredba_skoka>"))
		{
			provjeriNaredbaSkoka(trenutni.getdjeca().get(0),trenutniZn);
		}
	}

	private static void provjeriIzrazNaredba(Cvor trenutni, CvorTabZn trenutniZn) throws IOException
	{
		if(trenutni.getdjeca().get(0).getjedinkaIDN().equals("TOCKAZAREZ"))
		{
			LinkedList<String> pom=new LinkedList<>();
			pom.add("int");
			trenutni.setTip(pom);
		}
		else
		{
			provjeriIzraz(trenutni.getdjeca().get(0),trenutniZn);
			trenutni.setTip(trenutni.getdjeca().get(0).gettip());
		}
	}

	private static void provjeriNaredbaGrananja(Cvor trenutni, CvorTabZn trenutniZn) throws IOException
	{
		if(trenutni.getdjeca().size()==5)
		{
			provjeriIzraz(trenutni.getdjeca().get(2),trenutniZn);
			LinkedList<String> pom = new LinkedList<>();
			pom.add("int");
			boolean t=implProvjera(trenutni.getdjeca().get(2).gettip(),pom);
			if(!t)
			{
				greska(trenutni);
			}
			provjeriNaredba(trenutni.getdjeca().get(4),trenutniZn);
		}
		else
		{
			provjeriIzraz(trenutni.getdjeca().get(2),trenutniZn);
			LinkedList<String> pom = new LinkedList<>();
			pom.add("int");
			boolean t=implProvjera(trenutni.getdjeca().get(2).gettip(),pom);
			if(!t)
			{
				greska(trenutni);
			}
			provjeriNaredba(trenutni.getdjeca().get(4),trenutniZn);
			provjeriNaredba(trenutni.getdjeca().get(6),trenutniZn);

		}
	}

	private static void provjeriNaredbaPetlje(Cvor trenutni, CvorTabZn trenutniZn) throws IOException
	{
		if(trenutni.getdjeca().size() == 5)
		{
			provjeriIzraz(trenutni.getdjeca().get(2),trenutniZn);
			LinkedList<String> pom = new LinkedList<>();
			pom.add("int");
			boolean t=implProvjera(trenutni.getdjeca().get(2).gettip(),pom);
			if(!t)
			{
				greska(trenutni);
			}
			loop +=1;
			provjeriNaredba(trenutni.getdjeca().get(4),trenutniZn);
			loop -=1;

		}
		else if(trenutni.getdjeca().size() == 6)
		{
			provjeriIzrazNaredba(trenutni.getdjeca().get(2),trenutniZn);
			provjeriIzrazNaredba(trenutni.getdjeca().get(3),trenutniZn);
			LinkedList<String> pom = new LinkedList<>();
			pom.add("int");
			boolean t=implProvjera(trenutni.getdjeca().get(2).gettip(),pom);
			if(!t)
			{
				greska(trenutni);
			}
			loop +=1;
			provjeriNaredba(trenutni.getdjeca().get(5),trenutniZn);
			loop -=1;
		}
		else
		{
			provjeriIzrazNaredba(trenutni.getdjeca().get(2),trenutniZn);
			provjeriIzrazNaredba(trenutni.getdjeca().get(3),trenutniZn);
			LinkedList<String> pom = new LinkedList<>();
			pom.add("int");
			boolean t=implProvjera(trenutni.getdjeca().get(3).gettip(),pom);
			if(!t)
			{
				greska(trenutni);
			}
			provjeriIzraz(trenutni.getdjeca().get(4),trenutniZn);
			loop +=1;
			provjeriNaredba(trenutni.getdjeca().get(6),trenutniZn);
			loop -=1;
		}
	}

	private static void provjeriNaredbaSkoka(Cvor trenutni, CvorTabZn trenutniZn) throws IOException
	{
		if(trenutni.getdjeca().get(0).getjedinkaIDN().equals("KR_CONTINUE") || trenutni.getdjeca().get(0).getjedinkaIDN().equals("KR_BREAK"))
		{
			if(loop <=0)
			{
				greska(trenutni);
			}
		}
		else if(trenutni.getdjeca().get(1).getjedinkaIDN().equals("TOCKAZAREZ"))
		{
			if(!trenutniZn.getUBloku().get(1).equals("void"))
			{
				greska(trenutni);
			}
		}
		else
		{
			provjeriIzraz(trenutni.getdjeca().get(1),trenutniZn);
			if(trenutniZn.getUBloku().get(1).equals("void"))
			{
				greska(trenutni);
			}
			LinkedList<String> pom=new LinkedList<>();
			pom.add(trenutniZn.getUBloku().get(1));
			boolean t=implProvjera(trenutni.getdjeca().get(1).gettip(),pom);
			if(!t)
			{
				greska(trenutni);
			}
			//SAC
			//if(!nadiFunkciju(trenutni.getdjeca().get(1).getIme()))
			writer.write("\tPOP R6\n");
		}
	}

	private static void provjeriPrijevodnaJedinica(Cvor trenutni, CvorTabZn trenutniZn) throws IOException
	{
		if(trenutni.getdjeca().size() == 1)
		{
			provjeriVanjskaDeklaracija(trenutni.getdjeca().get(0),trenutniZn);
		}
		else
		{
			provjeriPrijevodnaJedinica(trenutni.getdjeca().get(0),trenutniZn);
			provjeriVanjskaDeklaracija(trenutni.getdjeca().get(1),trenutniZn);
			
			//SAC
			//trenutni.brojVar = trenutni.getdjeca().get(0).brojVar + trenutni.getdjeca().get(1).brojVar;
		}
	}

	private static void provjeriVanjskaDeklaracija(Cvor trenutni, CvorTabZn trenutniZn) throws IOException
	{
		if(trenutni.getdjeca().get(0).getjedinkaIDN().equals("<definicija_funkcije>")) {
			provjeriDefinicijaFunkcije(trenutni.getdjeca().get(0),trenutniZn);
		}
		else
		{
			provjeriDeklaracija(trenutni.getdjeca().get(0),trenutniZn);
			//SAC
			//trenutni.brojVar = trenutni.getdjeca().get(0).brojVar;
		}
	}

	private static void provjeriDefinicijaFunkcije(Cvor trenutni, CvorTabZn trenutniZn) throws IOException
	{
		if(trenutni.getdjeca().get(3).getjedinkaIDN().equals("KR_VOID"))
		{
			provjeriImeTipa(trenutni.getdjeca().get(0),trenutniZn);
			if(trenutni.getdjeca().get(0).gettip().get(0).equals("const_int") || (trenutni.getdjeca().get(0).gettip().get(0).equals("const_char")))
			{
				greska(trenutni);
			}
			CvorTabZn.identifikator IDN=nadiJedUTren(trenutni.getdjeca().get(1),korijenTabZn);
			if(IDN!=null)
			{
				if(IDN.getdefin()==1)
				{
					greska(trenutni);
				}
				if(IDN.getdefin()==0)
				{
					if(!IDN.gettip().subList(2, IDN.gettip().size()).contains("void"))
					{
						greska(trenutni);
					}
					if(!IDN.gettip().get(1).equals(trenutni.getdjeca().get(0).gettip().get(0)))
					{
						greska(trenutni);
					}
					IDN.setdefin(1);
				}
			}
			else
			{
				LinkedList<String> pom = new LinkedList<>();
				pom.add("fun");
				pom.add(trenutni.getdjeca().get(0).gettip().get(0));
				pom.add("void");
				/*if(trenutni.getdjeca().get(1).getjedinkaime().equals("main")) {
					//writer.write("F_MAIN");
					main = true;
				}*/
				IDN=new CvorTabZn.identifikator(pom, trenutni.getdjeca().get(1).getjedinkaime(), 1, false);
				trenutniZn.dodajIdentifikator(IDN);
			}
			CvorTabZn novi_cvor_znak=new CvorTabZn();
			novi_cvor_znak.setroditelj(trenutniZn);
			trenutniZn.dodajDjete(novi_cvor_znak);
			LinkedList<String> pom = new LinkedList<>();
			pom.add("fun");
			pom.add(trenutni.getdjeca().get(0).gettip().get(0));
			pom.add("void");
			novi_cvor_znak.setUBloku(pom);
			writer.write("F_" + trenutni.getdjeca().get(1).getjedinkaime().toUpperCase());
			provjeriSlozenaNaredba(trenutni.getdjeca().get(5),novi_cvor_znak);
			trenutniZn=trenutniZn.getroditelj();
			writer.write("\tRET\n");
			//main = false;
		}
		else if(trenutni.getdjeca().get(3).getjedinkaIDN().equals("<lista_parametara>"))
		{
			provjeriImeTipa(trenutni.getdjeca().get(0),trenutniZn);
			if(trenutni.getdjeca().get(0).gettip().get(0).equals("const_int") || trenutni.getdjeca().get(0).gettip().get(0).equals("const_char"))
			{
				greska(trenutni);
			}
			CvorTabZn.identifikator IDN= nadiJedUTren(trenutni.getdjeca().get(1),korijenTabZn);
			if(IDN!=null)
			{
				if(IDN.getdefin()==1)
				{
					greska(trenutni);
				}
			}
			provjeriListaParametara(trenutni.getdjeca().get(3),trenutniZn);
			
			//SAC
			//trenutni_odmak = 8;
			//jos toga
			
			CvorTabZn novi_cvor_znak= new CvorTabZn();
			if(IDN!=null)
			{
				if(IDN.getdefin()==0)
				{
					if(!IDN.gettip().get(1).equals(trenutni.getdjeca().get(0).gettip().get(0)))
					{
						greska(trenutni);
					}
					if(!IDN.gettip().subList(2, IDN.gettip().size()).equals(trenutni.getdjeca().get(3).gettip()))
					{
						greska(trenutni);
					}
					IDN.setdefin(1);
					novi_cvor_znak.setUBloku(IDN.gettip());
				}
			}
			else
			{
				LinkedList<String> tip_funkcije=new LinkedList<>();
				tip_funkcije.add("fun");
				tip_funkcije.add(trenutni.getdjeca().get(0).gettip().get(0));
				tip_funkcije.addAll(trenutni.getdjeca().get(3).gettip());
				IDN = new CvorTabZn.identifikator(tip_funkcije, trenutni.getdjeca().get(1).getjedinkaime(),1,false);
				trenutniZn.dodajIdentifikator(IDN);
				novi_cvor_znak.setUBloku(tip_funkcije);
			}
			novi_cvor_znak.setroditelj(trenutniZn);
			trenutniZn.dodajDjete(novi_cvor_znak);
			LinkedList<String> pom=trenutni.getdjeca().get(3).gettip();
			LinkedList<LinkedList<String>> tipovi=new LinkedList<LinkedList<String>>();

			for(int i=0;i<pom.size();i++)
			{
				if(pom.get(i).equals("niz"))
				{
					LinkedList<String> x=new LinkedList<>();
					x.add(pom.get(i));
					x.add(pom.get(i+1));
					tipovi.add(x);
					i+=1;
				}
				else
				{
					LinkedList<String> x=new LinkedList<>();
					x.add(pom.get(i));
					tipovi.add(x);
				}
			}
			for(int i=0;i<tipovi.size();i++)
			{
				if(tipovi.get(i).size()>1)
				{
					IDN=new CvorTabZn.identifikator(tipovi.get(i),trenutni.getdjeca().get(3).getIme().get(i),1,true);
				}
				else
				{
					IDN=new CvorTabZn.identifikator(tipovi.get(i),trenutni.getdjeca().get(3).getIme().get(i),1,true);
				}
				novi_cvor_znak.dodajIdentifikator(IDN);
			}
			
			//writer.write("F_" + trenutni.getdjeca().get(1).getjedinkaime().toUpperCase() + "\n");

			provjeriSlozenaNaredba(trenutni.getdjeca().get(5),novi_cvor_znak);
			
			//writer.write("\tRET\n\n");
			
			trenutniZn=trenutniZn.getroditelj();
		}
	}

	private static void provjeriListaParametara(Cvor trenutni, CvorTabZn trenutniZn)
	{
		if(trenutni.getdjeca().size() == 1)
		{
			provjeriDeklaracijaParametra(trenutni.getdjeca().get(0),trenutniZn);
			//trenutni.setTip(trenutni.getdjeca().get(0).gettip());
			//trenutni.setIme(trenutni.getdjeca().get(0).getIme());
		}
		else
		{
			provjeriListaParametara(trenutni.getdjeca().get(0),trenutniZn);
			provjeriDeklaracijaParametra(trenutni.getdjeca().get(2),trenutniZn);
			if(trenutni.getdjeca().get(0).getIme().equals(trenutni.getdjeca().get(2).getIme()))
			{
				greska(trenutni);
			}
			LinkedList<String> pom=trenutni.getdjeca().get(0).gettip();
			pom.addAll(trenutni.getdjeca().get(2).gettip());
			trenutni.setTip(pom);
			LinkedList<String> t=trenutni.getdjeca().get(0).getIme();
			t.addAll(trenutni.getdjeca().get(2).getIme());
			trenutni.setIme(t);
		}
	}

	private static void provjeriDeklaracijaParametra(Cvor trenutni, CvorTabZn trenutniZn)
	{
		if(trenutni.getdjeca().size()==2)
		{
			provjeriImeTipa(trenutni.getdjeca().get(0),trenutniZn);
			if(trenutni.getdjeca().get(0).gettip().contains("void"))
			{
				greska(trenutni);
			}
			trenutni.setTip(trenutni.getdjeca().get(0).gettip());
			LinkedList<String> pom =new LinkedList<>();
			pom.add(trenutni.getdjeca().get(1).getjedinkaime());
			trenutni.setIme(pom);
		}
		else
		{
			provjeriImeTipa(trenutni.getdjeca().get(0),trenutniZn);
			if(trenutni.getdjeca().get(0).gettip().contains("void"))
			{
				greska(trenutni);
			}
			LinkedList<String> pom =new LinkedList<>();
			pom.add("niz");
			pom.add(trenutni.getdjeca().get(0).gettip().get(0));
			trenutni.setTip(pom);
			LinkedList<String> p=new LinkedList<>();
			p.add(trenutni.getdjeca().get(1).getjedinkaime());
			trenutni.setIme(p);
		}
	}

	private static void provjeriListaDeklaracija(Cvor trenutni, CvorTabZn trenutniZn) throws IOException
	{
		if(trenutni.getdjeca().size() == 1)
		{
			provjeriDeklaracija(trenutni.getdjeca().get(0),trenutniZn);
			//SAC
			//trenutni.brojVar = trenutni.getdjeca().get(0).brojVar;
		}
		else
		{
			provjeriListaDeklaracija(trenutni.getdjeca().get(0),trenutniZn);
			provjeriDeklaracija(trenutni.getdjeca().get(1),trenutniZn);
			
			//SAC
			//trenutni.brojVar = trenutni.getdjeca().get(0).brojVar * 2;
		}
	}

	private static void provjeriDeklaracija(Cvor trenutni, CvorTabZn trenutniZn) throws IOException
	{

		provjeriImeTipa(trenutni.getdjeca().get(0),trenutniZn);
		trenutni.getdjeca().get(1).setntip(trenutni.getdjeca().get(0).gettip());
		provjeriListaInitDeklaratora(trenutni.getdjeca().get(1),trenutniZn);
		
		//SAC
		//trenutni.brojVar = trenutni.getdjeca().get(1).brojVar;
	}

	private static void provjeriListaInitDeklaratora(Cvor trenutni, CvorTabZn trenutniZn) throws IOException
	{
		if(trenutni.getdjeca().size() == 1)
		{
			trenutni.getdjeca().get(0).setntip(trenutni.getntip());
			provjeriInitDeklarator(trenutni.getdjeca().get(0),trenutniZn);
			//SAC
			//trenutni.brojVar = 1;
		}
		else 
		{
			trenutni.getdjeca().get(0).setntip(trenutni.getntip());
			provjeriListaInitDeklaratora(trenutni.getdjeca().get(0),trenutniZn);
			trenutni.getdjeca().get(2).setntip(trenutni.getntip());
			provjeriInitDeklarator(trenutni.getdjeca().get(2),trenutniZn);
			
			//SAC
			//trenutni.brojVar = trenutni.getdjeca().get(0).brojVar + 1;
		}
	}

	private static void provjeriInitDeklarator(Cvor trenutni, CvorTabZn trenutniZn) throws IOException
	{
		if(trenutni.getdjeca().size()==1)
		{
			trenutni.getdjeca().get(0).setntip(trenutni.getntip());
			provjeriIzravniDeklarator(trenutni.getdjeca().get(0),trenutniZn);
			if(trenutni.getdjeca().get(0).gettip().get(0).equals("const_int") || trenutni.getdjeca().get(0).gettip().get(0).equals("const_char"))
			{
				greska(trenutni);
			}
			if(trenutni.getdjeca().get(0).gettip().get(0).equals("niz") && (trenutni.getdjeca().get(0).gettip().get(1).equals("const_int") || trenutni.getdjeca().get(0).gettip().get(1).equals("const_char")))
			{
				greska(trenutni);
			}
			
			//SAC
			//trenutni.brojVar = trenutni.getdjeca().get(0).brojVar;
			
			//ako se var nalazi u globalnom djelokrugu
			/*if(trenutniZn.getUBloku().isEmpty()) {
				globalVars.put(trenutni.getdjeca().get(0).getdjeca().get(0).getjedinkaime(), "0");
			}*/
		}
		else 
		{
			trenutni.getdjeca().get(0).setntip(trenutni.getntip());
			provjeriIzravniDeklarator(trenutni.getdjeca().get(0),trenutniZn);
			provjeriInicijalizator(trenutni.getdjeca().get(2),trenutniZn);
						
			if(trenutni.getdjeca().get(0).gettip().get(0).equals("int") || trenutni.getdjeca().get(0).gettip().get(0).equals("const_int") || trenutni.getdjeca().get(0).gettip().get(0).equals("char") || trenutni.getdjeca().get(0).gettip().get(0).equals("const_char"))
			{
				boolean t=implProvjera(trenutni.getdjeca().get(2).gettip(),trenutni.getdjeca().get(0).gettip());
				if(!t)
				{
					greska(trenutni);
				}
			}
			else if(trenutni.getdjeca().get(0).gettip().get(0).equals("niz"))
			{
				if(trenutni.getdjeca().get(0).gettip().get(1).equals("int") || trenutni.getdjeca().get(0).gettip().get(1).equals("const_int") || trenutni.getdjeca().get(0).gettip().get(1).equals("char") || trenutni.getdjeca().get(0).gettip().get(1).equals("const_char"))
				{
					if(trenutni.getdjeca().get(2).getbrElem()>trenutni.getdjeca().get(0).getbrElem())
					{
						greska(trenutni);
					}
					for(int i=0;i<trenutni.getdjeca().get(2).gettip().size()-1;i++)
					{
						LinkedList<String> pom = new LinkedList<>();
						pom.add(trenutni.getdjeca().get(2).gettip().get(i+1));
						LinkedList<String> pom2 = new LinkedList<>();
						pom2.add(trenutni.getdjeca().get(0).gettip().get(1));
						boolean t=implProvjera(pom,pom2);
						if(!t)
						{
							greska(trenutni);
						}
					}
				}
				else
				{
					greska(trenutni);
				}
			}
			else
			{
				greska(trenutni);
			}
			
			//SAC
			//trenutni.brojVar = trenutni.getdjeca().get(0).brojVar;
			
			//SAC ovo je sjebavalo 2. primjer
			//ako se var nalazi u globalnom djelokrugu:
			if(trenutniZn.getUBloku().isEmpty()) {
				//globalVars.put(trenutni.getdjeca().get(0).getdjeca().get(0).getjedinkaime(), trenutni.getdjeca().get(2).getVrijednost());
			}
		}
	}

	private static void provjeriIzravniDeklarator(Cvor trenutni, CvorTabZn trenutniZn)
	{
		if(trenutni.getdjeca().size()==1)
		{
			if(trenutni.getdjeca().get(0).getntip().contains("void"))
			{
				greska(trenutni);
			}
			CvorTabZn.identifikator IDN=nadiJedUTren(trenutni.getdjeca().get(0),trenutniZn);
			if(IDN!=null)
			{
				greska(trenutni);
			}
			IDN=new CvorTabZn.identifikator(trenutni.getntip(),trenutni.getdjeca().get(0).getjedinkaime(),1,true);
			trenutniZn.dodajIdentifikator(IDN);
			trenutni.setTip(trenutni.getntip());
			imeVar = trenutni.getdjeca().get(0).getjedinkaime();
		}
		else if(trenutni.getdjeca().get(2).getjedinkaIDN().equals("BROJ"))
		{
			if(trenutni.getntip().contains("void"))
			{
				greska(trenutni);
			}
			CvorTabZn.identifikator IDN=nadiJedUTren(trenutni.getdjeca().get(0),trenutniZn);
			if(IDN!=null)
			{
				greska(trenutni);
			}
			if(Integer.parseInt(trenutni.getdjeca().get(2).getjedinkaime())<0 || Integer.parseInt(trenutni.getdjeca().get(2).getjedinkaime())>1024)
			{
				greska(trenutni);
			}
			LinkedList<String>p = new LinkedList<>();
			p.add("niz");
			p.add(trenutni.getntip().get(0));
			IDN= new CvorTabZn.identifikator(p,trenutni.getdjeca().get(0).getjedinkaime(),1,true);
			trenutniZn.dodajIdentifikator(IDN);
			trenutni.setTip(p);
			trenutni.setbrElem(Integer.parseInt(trenutni.getdjeca().get(2).getjedinkaime()));
		}
		else if(trenutni.getdjeca().get(2).getjedinkaIDN().equals("KR_VOID"))
		{
			CvorTabZn.identifikator IDN=nadiJedUTren(trenutni.getdjeca().get(0),trenutniZn);
			if(IDN!=null)
			{
				boolean p=IDN.gettip().get(1).equals(trenutni.getntip().get(0));
				boolean t=(p && trenutni.getntip().size()==1);

				if(!t)
				{
					greska(trenutni);
				}
				if(!IDN.gettip().subList(2, IDN.gettip().size()).contains("void"))
				{
					greska(trenutni);
				}
			}
			else
			{
				LinkedList<String> pom=new LinkedList<>();
				pom.add("fun");
				pom.add(trenutni.getntip().get(0));
				pom.add("void");
				IDN=new CvorTabZn.identifikator(pom,trenutni.getdjeca().get(0).getjedinkaime(),0,false);
				trenutniZn.dodajIdentifikator(IDN);
			}
			LinkedList<String> pom=new LinkedList<>();
			pom.add("fun");
			pom.add(trenutni.getntip().get(0));
			pom.add("void");
			trenutni.setTip(pom);
		}
		else if(trenutni.getdjeca().get(2).getjedinkaIDN().equals("<lista_parametara>"))
		{
			provjeriListaParametara(trenutni.getdjeca().get(2),trenutniZn);
			CvorTabZn.identifikator IDN=nadiJedUTren(trenutni.getdjeca().get(0),trenutniZn);
			if(IDN!=null)
			{
				if(IDN.gettip().get(1)!=trenutni.getntip().get(0))
				{
					greska(trenutni);
				}
				if(!IDN.gettip().subList(2, IDN.gettip().size()).equals(trenutni.getdjeca().get(2).gettip()))
				{
					greska(trenutni);
				}
			}
			else
			{
				LinkedList<String> pom =new LinkedList<>();
				pom.add("fun");
				pom.add(trenutni.getntip().get(0));
				pom.addAll(trenutni.getdjeca().get(2).gettip());
				IDN=new CvorTabZn.identifikator(pom, trenutni.getdjeca().get(0).getjedinkaime(),0,false);
				trenutniZn.dodajIdentifikator(IDN);
			}
			LinkedList<String> pom =new LinkedList<>();
			pom.add("fun");
			pom.add(trenutni.getntip().get(0));
			pom.addAll(trenutni.getdjeca().get(2).gettip());
			trenutni.setTip(pom);
		}
	}

	private static void provjeriInicijalizator(Cvor trenutni, CvorTabZn trenutniZn) throws IOException
	{
		if(trenutni.getdjeca().size() == 1)
		{
			provjeriIzrazPridruzivanja(trenutni.getdjeca().get(0),trenutniZn);
			Cvor cvor_NIZ_ZNAKOVA=null;
			LinkedList<Cvor> treba_provjeriti = new LinkedList<Cvor>();
			treba_provjeriti.addAll(trenutni.getdjeca());

			while(treba_provjeriti.size()>0)
			{
				if(treba_provjeriti.get(0).getjedinkaIDN().equals("NIZ_ZNAKOVA"))
				{
					cvor_NIZ_ZNAKOVA=treba_provjeriti.get(0);
					break;
				}
				else
				{
					if(treba_provjeriti.get(0).getdjeca().size()>0)
					{
						treba_provjeriti.addAll(treba_provjeriti.get(0).getdjeca());
					}
				}
				treba_provjeriti.remove(0);
			}
			if(cvor_NIZ_ZNAKOVA!=null)
			{
				trenutni.setbrElem(cvor_NIZ_ZNAKOVA.getjedinkaime().length()-2+1);
				LinkedList<String> pom = new LinkedList<>();
				for(int i=0;i<trenutni.getbrElem();i++)
				{
					pom.add("char");
				}
				trenutni.setTip(pom);
			}
			else
			{
				trenutni.setTip(trenutni.getdjeca().get(0).gettip());
			}
			
			//trenutni.setVrijednost(trenutni.getdjeca().get(0).getVrijednost());
		}
		else if(trenutni.getdjeca().size() == 3)
		{
			provjeriListaIzrazaPridruzivanja(trenutni.getdjeca().get(1),trenutniZn);
			trenutni.setbrElem(trenutni.getdjeca().get(1).getbrElem());
			trenutni.setTip(trenutni.getdjeca().get(1).gettip());
		}
	}

	private static void provjeriListaIzrazaPridruzivanja(Cvor trenutni, CvorTabZn trenutniZn) throws IOException
	{
		if(trenutni.getdjeca().size() == 1)
		{
			provjeriIzrazPridruzivanja(trenutni.getdjeca().get(0),trenutniZn);
			trenutni.setTip(trenutni.getdjeca().get(0).gettip());
			trenutni.setbrElem(1);
		}
		else if(trenutni.getdjeca().size() == 3)
		{
			provjeriListaIzrazaPridruzivanja(trenutni.getdjeca().get(0),trenutniZn);
			provjeriIzrazPridruzivanja(trenutni.getdjeca().get(2),trenutniZn);
			LinkedList<String> pom =new LinkedList<>();
			pom.addAll(trenutni.getdjeca().get(0).gettip());
			pom.addAll(trenutni.getdjeca().get(2).gettip());
			trenutni.setTip(pom);
			trenutni.setbrElem(trenutni.getdjeca().get(0).getbrElem()+1);
		}
	}

	private static void greska(Cvor cvor)
	{
		boolean first = true;
		String ispis = cvor.getjedinkaIDN()+" ::= ";
		for (Cvor dijete : cvor.getdjeca())
		{
			if(first)
				first = false;
			else
				ispis += " ";

			if(dijete.getjedinkaIDN().startsWith("<"))
			{
				ispis += dijete.getjedinkaIDN();
			}
			else
			{
				ispis += dijete.getjedinkaIDN()+"("+Integer.toString(dijete.getjedinkaraz())+","+dijete.getjedinkaime()+")";
			}
		}
		ispisi(ispis);
	}

	private static void ispisi(String ispis)
	{
		System.out.println(ispis);
		System.exit(0);
	}

	private static CvorTabZn.identifikator nadiJedUTren(Cvor cv, CvorTabZn trenutniZnak)
	{
		CvorTabZn.identifikator IDN;
		CvorTabZn.identifikator ret = null;
		for(int i = 0; i < trenutniZnak.getidentifikatori().size(); i++) {
			IDN = trenutniZnak.getidentifikatori().get(i);
			if(cv.getjedinkaime().equals(IDN.getime())) {
				return IDN;
			}
		}
		return ret;
	}

	private static boolean implProvjera(LinkedList<String> tip1, LinkedList<String> tip2)
	{
		String prvi = tip1.get(0);
		String drugi = tip2.get(0);
		if(tip1.get(0).equals(tip2.get(0)) ||
				prvi.equals("int") && drugi.equals("const_int") ||
				prvi.equals("const_int") && drugi.equals("int") ||
				prvi.equals("char") && drugi.equals("const_char") ||
				prvi.equals("const_char") && drugi.equals("char") ||
				prvi.equals("char") && drugi.equals("int") ||
				prvi.equals("niz") && tip1.get(1).equals("int") && drugi.equals("niz") && tip2.get(1).equals("const_int") ||
				prvi.equals("niz") && tip1.get(1).equals("char") && drugi.equals("niz") && tip2.get(1).equals("const_char"))
		{
			return true;
		} else
			return false;
	}
}
