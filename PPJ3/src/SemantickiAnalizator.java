package paket;
import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import paket.Cvor;
import paket.CvorZn;
import paket.CvorZn.identifikator;
import paket.Cvor.lexJed;

public class SemantickiAnalizator 
{
	public static int u_petlji=0;
	public static int razina=0;
	public static Cvor korijen=new Cvor();
	public static LinkedHashMap<Integer, Cvor> dict_razina=new LinkedHashMap<Integer, Cvor>();
	public static CvorZn korijen_znakova=new CvorZn();
	public static CvorZn novi_cvor_znak=new CvorZn();

    public static void main(String[] args) 
    {
    	String line=null;
    	try(BufferedReader br = new BufferedReader(new FileReader("D:\\FAKS\\UTR\\PPJLabos3\\semanal\\testovi\\a (30)\\test.in")))
    	{
    		line=br.readLine();
    		String pr=line.trim();
    		korijen.setjedinka(new Cvor.lexJed(pr, null, 0));
    		dict_razina.put(0, korijen);
    		line=br.readLine();
    		while(line!=null && !(line.isEmpty()))
    		{
    			int broj_razmaka=line.length()-line.trim().length();
    			pr=line.trim();
    			lexJed pom;
    			if(pr.startsWith("<"))
    			{
    				pom=new Cvor.lexJed(pr, null, 0);
    			}
    			else
    			{
    				String[] x=pr.split(" ");
    				String dva=x[2];
    				pom=new Cvor.lexJed(x[0], dva, Integer.parseInt(x[1]));
    			}
    			Cvor cv=new Cvor();
    			cv.setjedinka(pom);
    			dict_razina.put(broj_razmaka, cv);
    			Cvor pomoc=dict_razina.get(broj_razmaka-1);
    			pomoc.dodajDjete(cv);
        		line=br.readLine();
    		}

    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	
    	provjeri(korijen, korijen_znakova);
    	imaMain(korijen_znakova);
    	provjeriFun(korijen_znakova);
    }

    private static boolean provjeriTip(LinkedList<String> tip1, LinkedList<String> tip2)
    {
    	if(tip1.get(0).equals(tip2.get(0)))
    	{
    		return true;
    	}
    	if(tip1.get(0).equals("const_int")&& tip2.get(0).equals("int"))
    	{
    		return true;
    	}
    	if(tip1.get(0).equals("int")&&tip2.get(0).equals("const_int"))
    	{
    		return true;
    	}
    	if(tip1.get(0).equals("const_char")&&tip2.get(0).equals("char"))
    	{
    		return true;
    	}
    	if(tip1.get(0).equals("char")&&tip2.get(0).equals("const_char"))
    	{
    		return true;
    	}
    	if(tip1.get(0).equals("char")&&tip2.get(0).equals("int"))
    	{
    		return true;
    	}
    	if(tip1.get(0).equals("niz")&&tip1.get(1).equals("int")&&tip2.get(0).equals("niz")&&tip2.get(1).equals("const_int"))
    	{
    		return true;
    	}
    	if(tip1.get(0).equals("niz")&&tip1.get(1).equals("char")&&tip2.get(0).equals("niz")&&tip2.get(1).equals("const_char"))
    	{
    		return true;
    	}
        return false;
    }

    private static void greska(Cvor cvor)
    {
        boolean first = true;
        String ispis = cvor.getjedinkaIDN()+" ::= ";
    	for (Cvor baby : cvor.getdjeca())
    	{
    	    //ispred svih djece koji nisu prvi treba ic razmak
    	    if(first)
    			first = false;
    		else
    			ispis += " ";
    		
    		if(baby.getjedinkaIDN().startsWith("<"))
    		{
    		    ispis += baby.getjedinkaIDN();
    		}
    		else
    		{
                ispis += baby.getjedinkaIDN()+"("+Integer.toString(baby.getjedinkaraz())+","+baby.getjedinkaime()+")";
    		}
    	}  	
    	System.out.println(ispis);
        System.exit(0);
    }

    private static  identifikator pretraziTabZnakova(Cvor jedinka, CvorZn trenutni_cvor_znak)
    {
        for(identifikator IDN: trenutni_cvor_znak.getidentifikatori())
        {
        	if(jedinka.getjedinkaime().equals(IDN.getime()))
        	{
        		return IDN;
        	}
        }
    	
        if (trenutni_cvor_znak.getroditelj() == null)
        {
        	return null;
        }

       return pretraziTabZnakova(jedinka, trenutni_cvor_znak.getroditelj());
    }

    private static identifikator provjeriTrenCvZnakova(Cvor jedinka, CvorZn trenutni_cvor_znak)
    {
    	for(identifikator IDN: trenutni_cvor_znak.getidentifikatori())
        {
        	if(jedinka.getjedinkaime().equals(IDN.getime()))
        	{
        		return IDN;
        	}
        }
    	return null;
    }

    private static boolean imaMain(CvorZn korijen_znakova)
    {
    	boolean ima=false;
    	
    	for(identifikator IDN:korijen_znakova.getidentifikatori())
    	{
    		if(IDN.getime().equals("main"))
    		{
    			LinkedList<String> test=new LinkedList<>();
    			test.add("fun");
    			test.add("int");
    			test.add("void");
    			if(IDN.gettip().equals(test))
    			{
    				ima=true;
    			}
    		}
    	}
    	if(!ima)
    	{
    		System.out.println("main");
    		System.exit(0);
    	}
        return true;
    }

    private static boolean provjeriFun(CvorZn korijen_znakova)
    {
    	for(identifikator IDN:korijen_znakova.getidentifikatori())
    	{
    		if(IDN.gettip().get(0).equals("fun"))
    		{
    			if(IDN.getDD()!=1)
    			{
    				System.out.println("funkcija");
    				System.exit(0);
    			}
    		}
    	}
    	for(CvorZn baby:korijen_znakova.getdjeca())
    	{
    		provjeriFun(baby);
    	}
    	return true;
    }

    private static void provjeri(Cvor trenutni_cvor, CvorZn trenutni_cvor_u_tablici_znakova)
    {
    	if(trenutni_cvor.getjedinkaIDN().equals("<primarni_izraz>"))
    	{
    		if(trenutni_cvor.getdjeca().get(0).getjedinkaIDN().equals("IDN"))
    		{
    			identifikator IDN=pretraziTabZnakova(trenutni_cvor.getdjeca().get(0),trenutni_cvor_u_tablici_znakova);
    			if (IDN==null)
    			{
    				greska(trenutni_cvor);
    			}
    			trenutni_cvor.setTip(IDN.gettip());
    			trenutni_cvor.setl_izraz(IDN.getl_izraz());	
    		}
    		else if(trenutni_cvor.getdjeca().get(0).getjedinkaIDN().equals("BROJ"))
        	{
    			BigInteger big=new BigInteger((trenutni_cvor.getdjeca().get(0).getjedinkaime()));
    			//String mali=String.valueOf(Math.pow(-2, 31));
    			//String veli=String.valueOf(Math.pow(2, 31)-1);
    			String mali=String.valueOf(2147483647);
    			String veli=String.valueOf(2147483647);
    			BigInteger min=new BigInteger(mali);
    			BigInteger max=new BigInteger(veli);
        		if (big.compareTo(max)==1 || big.compareTo(min.negate())==-1)
        		{
        			greska(trenutni_cvor);
        		}
        		LinkedList<String> tip=new LinkedList<>();
        		tip.add("int");
        		trenutni_cvor.setTip(tip);
        		trenutni_cvor.setl_izraz(false);
        	}
    		//PROVJERI OVO 
        	else if(trenutni_cvor.getdjeca().get(0).getjedinkaIDN().equals("ZNAK"))
        	{
        		/*
        		if(!(trenutni_cvor.getdjeca().get(0).getjedinkaime().startsWith("\\") || trenutni_cvor.getdjeca().get(0).getjedinkaime().startsWith("\\n") || trenutni_cvor.getdjeca().get(0).getjedinkaime().startsWith("\\t") || trenutni_cvor.getdjeca().get(0).getjedinkaime().startsWith("\\0") || trenutni_cvor.getdjeca().get(0).getjedinkaime().startsWith("\\\'") || trenutni_cvor.getdjeca().get(0).getjedinkaime().startsWith("\\\"") || trenutni_cvor.getdjeca().get(0).getjedinkaime().startsWith("\\\\")))
        		{
        			greska(trenutni_cvor);
        		}
        		*/
        		if(trenutni_cvor.getdjeca().get(0).getjedinkaime().startsWith("\\"))
        		{
        			if(!(trenutni_cvor.getdjeca().get(0).getjedinkaime().startsWith("\\n") || trenutni_cvor.getdjeca().get(0).getjedinkaime().startsWith("\\t") || trenutni_cvor.getdjeca().get(0).getjedinkaime().startsWith("\\0") || trenutni_cvor.getdjeca().get(0).getjedinkaime().startsWith("\\\'") || trenutni_cvor.getdjeca().get(0).getjedinkaime().startsWith("\\\"") || trenutni_cvor.getdjeca().get(0).getjedinkaime().startsWith("\\\\")))
        			{
        				greska(trenutni_cvor);
        			}
        		}
        		LinkedList<String> tip=new LinkedList<>();
        		tip.add("char");
        		trenutni_cvor.setTip(tip);
        		trenutni_cvor.setl_izraz(false);
        	}
        	else if(trenutni_cvor.getdjeca().get(0).getjedinkaIDN().equals("NIZ ZNAKOVA"))
        	{
        		String niz=trenutni_cvor.getdjeca().get(0).getjedinkaime();
        		int index=niz.indexOf("\\");
        		while(index>=0)
        		{
        			if(!(niz.charAt(index+1)=='n' || niz.charAt(index+1)=='t' || niz.charAt(index+1)=='0' || niz.charAt(index+1)=='\'' || niz.charAt(index+1)=='\"' || niz.charAt(index+1)=='\\'))
        			{
        				greska(trenutni_cvor);
        			}
        			niz=niz.substring(index+1, niz.length()-1);
        			index=niz.indexOf("\\");
        		}
        		LinkedList<String> tip=new LinkedList<>();
        		tip.add("niz");
        		tip.add("const_char");
        		trenutni_cvor.setTip(tip);
        		trenutni_cvor.setl_izraz(false);
        	}
        	else if(trenutni_cvor.getdjeca().get(0).getjedinkaIDN().equals("L_ZAGRADA"))
        	{
        		provjeri(trenutni_cvor.getdjeca().get(1), trenutni_cvor_u_tablici_znakova);
        		LinkedList<String> pr=trenutni_cvor.getdjeca().get(1).gettip();
        		trenutni_cvor.setTip(pr);
        		boolean priv=trenutni_cvor.getdjeca().get(1).getl_izraz();
        		trenutni_cvor.setl_izraz(priv);
        	}
            else
            {
                	greska(trenutni_cvor);
            }
    	}
    	
    	else if(trenutni_cvor.getjedinkaIDN().equals("<postfiks_izraz>"))
    	{
    		if(trenutni_cvor.getdjeca().get(0).getjedinkaIDN().equals("<primarni_izraz>"))
    		{
    			provjeri(trenutni_cvor.getdjeca().get(0), trenutni_cvor_u_tablici_znakova);
    			//LinkedList<String> pr = new LinkedList<String>();
    			//pr.addAll(trenutni_cvor.getdjeca().get(0).gettip());
    			LinkedList<String> pr=trenutni_cvor.getdjeca().get(0).gettip();
    			trenutni_cvor.setTip(pr);
    			boolean priv=trenutni_cvor.getdjeca().get(0).getl_izraz();
    			trenutni_cvor.setl_izraz(priv);
    		}
    		else if(trenutni_cvor.getdjeca().get(1).getjedinkaIDN().equals("L_UGL_ZAGRADA"))
    		{
    			provjeri(trenutni_cvor.getdjeca().get(0),trenutni_cvor_u_tablici_znakova);
    			if(!trenutni_cvor.getdjeca().get(0).gettip().get(0).equals("niz"))
    			{
    				greska(trenutni_cvor);
    			}
    			if(!(trenutni_cvor.getdjeca().get(0).gettip().get(1).equals("int") || trenutni_cvor.getdjeca().get(0).gettip().get(1).equals("char") || trenutni_cvor.getdjeca().get(0).gettip().get(1).equals("const_int") || trenutni_cvor.getdjeca().get(0).gettip().get(1).equals("const_char")))
    			{
    				greska(trenutni_cvor);		
    			}
    			provjeri(trenutni_cvor.getdjeca().get(2),trenutni_cvor_u_tablici_znakova);
    			LinkedList<String> nov=new LinkedList<>();
    			nov.add("int");
    			boolean t=provjeriTip(trenutni_cvor.getdjeca().get(2).gettip(),nov);
    			if(!t)
    			{
    				greska(trenutni_cvor);
    			}
    			
				LinkedList<String> pom=new LinkedList<>();
				pom.add(trenutni_cvor.getdjeca().get(0).gettip().get(1));
				trenutni_cvor.setTip(pom);
    			
    			if(trenutni_cvor.getdjeca().get(0).gettip().get(1).equals("const_int") || trenutni_cvor.getdjeca().get(0).gettip().get(1).equals("const_char"))
    			{
    				boolean p=false;
    				trenutni_cvor.setl_izraz(p);
    			}
    			else
    			{
    				boolean a=true;
    				trenutni_cvor.setl_izraz(a);
    			}
    		}
    		else if(trenutni_cvor.getdjeca().get(1).getjedinkaIDN().equals("L_ZAGRADA"))
    		{
    			if(trenutni_cvor.getdjeca().get(2).getjedinkaIDN().equals("D_ZAGRADA"))
    			{
    				provjeri(trenutni_cvor.getdjeca().get(0),trenutni_cvor_u_tablici_znakova);
    				if(!trenutni_cvor.getdjeca().get(0).gettip().get(0).equals("fun"))
    				{
    					greska(trenutni_cvor);
    				}
    				if(!trenutni_cvor.getdjeca().get(0).gettip().get(2).equals("void"))
    				{
    					greska(trenutni_cvor);
    				}
    				LinkedList<String> pom=new LinkedList<>();
    				pom.add(trenutni_cvor.getdjeca().get(0).gettip().get(1));
    				trenutni_cvor.setTip(pom);
    				trenutni_cvor.setl_izraz(false);	
    			}
    			else if(trenutni_cvor.getdjeca().get(2).getjedinkaIDN().equals("<lista_argumenata>"))
    			{
    				provjeri(trenutni_cvor.getdjeca().get(0), trenutni_cvor_u_tablici_znakova);
    				provjeri(trenutni_cvor.getdjeca().get(2), trenutni_cvor_u_tablici_znakova);
    				if(!trenutni_cvor.getdjeca().get(0).gettip().get(0).equals("fun"))
    				{
    					greska(trenutni_cvor);
    				}
    				if(trenutni_cvor.getdjeca().get(0).gettip().size()-2!=trenutni_cvor.getdjeca().get(2).gettip().size())
    				{
    					greska(trenutni_cvor);
    				}
    				for(int i=0;i<trenutni_cvor.getdjeca().get(2).gettip().size();i++)
    				{
    					LinkedList<String> prvi=new LinkedList<>();
    					LinkedList<String> drugi=new LinkedList<>();
    					prvi.add(trenutni_cvor.getdjeca().get(2).gettip().get(i));
    					drugi.add(trenutni_cvor.getdjeca().get(0).gettip().get(i+2));
    					boolean t=provjeriTip(prvi,drugi);
    					if(!t)
    					{
    						greska(trenutni_cvor);
    					}
    				}
    				LinkedList<String> p=new LinkedList<>();
    				p.add(trenutni_cvor.getdjeca().get(0).gettip().get(1));
    				trenutni_cvor.setTip(p);
    				trenutni_cvor.setl_izraz(false);
    			}
    			else
    			{
    				greska(trenutni_cvor);
    			}
    		}
    		else if(trenutni_cvor.getdjeca().get(1).getjedinkaIDN().equals("OP_INC") || trenutni_cvor.getdjeca().get(1).getjedinkaIDN().equals("OP_DEC"))
    		{
    			provjeri(trenutni_cvor.getdjeca().get(0),trenutni_cvor_u_tablici_znakova);
    			if(!trenutni_cvor.getdjeca().get(0).getl_izraz())
    			{
    				greska(trenutni_cvor);
    			}
    			LinkedList<String> p=new LinkedList<>();
    			p.add("int");
    			boolean t=provjeriTip(trenutni_cvor.getdjeca().get(0).gettip(),p);
    			if(!t)
    			{
    				greska(trenutni_cvor);
    			}
    			trenutni_cvor.setTip(p);
    			trenutni_cvor.setl_izraz(false);
    		}
    		else
    		{
    			greska(trenutni_cvor);
    		}
    	}
    	else if(trenutni_cvor.getjedinkaIDN().equals("<lista_argumenata>"))
    	{
    		if(trenutni_cvor.getdjeca().get(0).getjedinkaIDN().equals("<izraz_pridruzivanja>"))
    		{
    			provjeri(trenutni_cvor.getdjeca().get(0),trenutni_cvor_u_tablici_znakova);
    			trenutni_cvor.setTip(trenutni_cvor.getdjeca().get(0).gettip());
    		}
    		else if(trenutni_cvor.getdjeca().get(0).getjedinkaIDN().equals("<lista_argumenata>"))
    		{
    			provjeri(trenutni_cvor.getdjeca().get(0),trenutni_cvor_u_tablici_znakova);
    			provjeri(trenutni_cvor.getdjeca().get(2),trenutni_cvor_u_tablici_znakova);
    			LinkedList<String> pr=new LinkedList<>();
    			LinkedList<String> dr=new LinkedList<>();
    			pr=trenutni_cvor.getdjeca().get(0).gettip();
    			dr=trenutni_cvor.getdjeca().get(2).gettip();
    			pr.addAll(dr);
    			trenutni_cvor.setTip(pr);
    		}
    		else
    		{
    			greska(trenutni_cvor);
    		}
    	}
    	else if(trenutni_cvor.getjedinkaIDN().equals("<unarni_izraz>"))
    	{
    		if(trenutni_cvor.getdjeca().get(0).getjedinkaIDN().equals("<postfiks_izraz>"))
    		{
    			provjeri(trenutni_cvor.getdjeca().get(0),trenutni_cvor_u_tablici_znakova);
    			trenutni_cvor.setTip(trenutni_cvor.getdjeca().get(0).gettip());
    			trenutni_cvor.setl_izraz(trenutni_cvor.getdjeca().get(0).getl_izraz());
    		}
    		else if(trenutni_cvor.getdjeca().get(0).getjedinkaIDN().equals("OP_INC") || trenutni_cvor.getdjeca().get(0).getjedinkaIDN().equals("OP_DEC"))
    		{
    			provjeri(trenutni_cvor.getdjeca().get(1), trenutni_cvor_u_tablici_znakova);
    			if(!trenutni_cvor.getdjeca().get(1).getl_izraz())
    			{
    				greska(trenutni_cvor);
    			}
    			LinkedList<String> pr=new LinkedList<>();
    			pr.add("int");
    			boolean t=provjeriTip(trenutni_cvor.getdjeca().get(1).gettip(),pr);
    			if(!t)
    			{
    				greska(trenutni_cvor);
    			}
    			trenutni_cvor.setTip(pr);
    			trenutni_cvor.setl_izraz(false);
    		}
    		else if(trenutni_cvor.getdjeca().get(0).getjedinkaIDN().equals("<unarni_operator>"))
    		{
    			provjeri(trenutni_cvor.getdjeca().get(1), trenutni_cvor_u_tablici_znakova);
    			LinkedList<String> pr=new LinkedList<>();
    			pr.add("int");
    			boolean t=provjeriTip(trenutni_cvor.getdjeca().get(1).gettip(),pr);
    			if(!t)
    			{
    				greska(trenutni_cvor);
    			}
    			trenutni_cvor.setTip(pr);
    			trenutni_cvor.setl_izraz(false);
    			
    		}
    		else
    		{
    			greska(trenutni_cvor);
    		}
    	}
    	else if(trenutni_cvor.getjedinkaIDN().equals("<cast_izraz>"))
    	{
    		if(trenutni_cvor.getdjeca().get(0).getjedinkaIDN().equals("<unarni_izraz>"))
    		{
    			provjeri(trenutni_cvor.getdjeca().get(0), trenutni_cvor_u_tablici_znakova);
    			trenutni_cvor.setTip(trenutni_cvor.getdjeca().get(0).gettip());
    			trenutni_cvor.setl_izraz(trenutni_cvor.getdjeca().get(0).getl_izraz());
    		}
    		else if(trenutni_cvor.getdjeca().get(0).getjedinkaIDN().equals("L_ZAGRADA"))
    		{
    			provjeri(trenutni_cvor.getdjeca().get(1),trenutni_cvor_u_tablici_znakova);
    			provjeri(trenutni_cvor.getdjeca().get(3),trenutni_cvor_u_tablici_znakova);
    			if(!(trenutni_cvor.getdjeca().get(1).gettip().get(0).equals("int") || trenutni_cvor.getdjeca().get(1).gettip().get(0).equals("const_int") || trenutni_cvor.getdjeca().get(1).gettip().get(0).equals("char") || trenutni_cvor.getdjeca().get(1).gettip().get(0).equals("const_char")))
    			{
    				greska(trenutni_cvor);
    			}
    			if(!(trenutni_cvor.getdjeca().get(3).gettip().get(0).equals("int") || trenutni_cvor.getdjeca().get(3).gettip().get(0).equals("const_int") || trenutni_cvor.getdjeca().get(3).gettip().get(0).equals("char") || trenutni_cvor.getdjeca().get(3).gettip().get(0).equals("const_char")))
    			{
    				greska(trenutni_cvor);
    			}
    			trenutni_cvor.setTip(trenutni_cvor.getdjeca().get(1).gettip());
    			trenutni_cvor.setl_izraz(false);
    		}
    		else
    		{
    			greska(trenutni_cvor);
    		}
    	}
    	else if(trenutni_cvor.getjedinkaIDN().equals("<ime_tipa>"))
    	{
    		if(trenutni_cvor.getdjeca().get(0).getjedinkaIDN().equals("<specifikator_tipa>"))
    		{
    			provjeri(trenutni_cvor.getdjeca().get(0), trenutni_cvor_u_tablici_znakova);
    			trenutni_cvor.setTip(trenutni_cvor.getdjeca().get(0).gettip());
    		}
    		else if(trenutni_cvor.getdjeca().get(0).getjedinkaIDN().equals("KR_CONST"))
    		{
    			provjeri(trenutni_cvor.getdjeca().get(1), trenutni_cvor_u_tablici_znakova);
    			if(trenutni_cvor.getdjeca().get(1).gettip().contains("void"))
    			{
    				greska(trenutni_cvor);
    			}
    			String ps=trenutni_cvor.getdjeca().get(1).gettip().get(0);
    			String x="const";
    			x.concat(ps);
    			LinkedList<String> t=new LinkedList<>();
    			t.add(x);
    			trenutni_cvor.setTip(t);
    		}
    		else
    		{
    			greska(trenutni_cvor);
    		}           
    	}
    	else if(trenutni_cvor.getjedinkaIDN().equals("<specifikator_tipa>"))
    	{
    		LinkedList<String> pom=new LinkedList<>();
    		if(trenutni_cvor.getdjeca().get(0).getjedinkaIDN().equals("KR_VOID"))
    		{
    			pom.add("void");
    			trenutni_cvor.setTip(pom);
    		}
    		else if(trenutni_cvor.getdjeca().get(0).getjedinkaIDN().equals("KR_CHAR"))
    		{
    			pom.add("char");
    			trenutni_cvor.setTip(pom);
    		}
    		else if(trenutni_cvor.getdjeca().get(0).getjedinkaIDN().equals("KR_INT"))
    		{
    			pom.add("int");
    			trenutni_cvor.setTip(pom);
    		}
    		else
    		{
    			greska(trenutni_cvor);
    		}
    	}
    	else if(trenutni_cvor.getjedinkaIDN().equals("<multiplikativni_izraz>") || trenutni_cvor.getjedinkaIDN().equals("<aditivni_izraz>") || trenutni_cvor.getjedinkaIDN().equals("<odnosni_izraz>") || trenutni_cvor.getjedinkaIDN().equals("<jednakosni_izraz>") || trenutni_cvor.getjedinkaIDN().equals("<bin_i_izraz>") || trenutni_cvor.getjedinkaIDN().equals("<bin_xili_izraz>") || trenutni_cvor.getjedinkaIDN().equals("<bin_ili_izraz>") || trenutni_cvor.getjedinkaIDN().equals("<log_i_izraz>") || trenutni_cvor.getjedinkaIDN().equals("<log_ili_izraz>"))
    	{
    		LinkedList<String> po=new LinkedList<>();
    		po.add("OP_PUTA");
    		po.add("OP_DIJELI");
    		po.add("OP_MOD");
    		po.add("PLUS");
    		po.add("MINUS");
    		po.add("OP_LT");
    		po.add("OP_GT");
    		po.add("OP_LTE");
    		po.add("PO_GTE");
    		po.add("OP_EQ");
    		po.add("OP_NEQ");
    		po.add("OP_BIN_I");
    		po.add("OP_BIN_XILI");
    		po.add("OP_BIN_ILI");
    		po.add("OP_I");
    		po.add("OP_ILI");
    		if(trenutni_cvor.getdjeca().size()==1)
    		{
    			provjeri(trenutni_cvor.getdjeca().get(0),trenutni_cvor_u_tablici_znakova);
    			trenutni_cvor.setTip(trenutni_cvor.getdjeca().get(0).gettip());
    			trenutni_cvor.setl_izraz(trenutni_cvor.getdjeca().get(0).getl_izraz());
    		}
    		
    		
    		else if(po.contains(trenutni_cvor.getdjeca().get(1).getjedinkaIDN()))
    		{
    			provjeri(trenutni_cvor.getdjeca().get(0),trenutni_cvor_u_tablici_znakova);
    			LinkedList<String> pr=new LinkedList<>();
    			pr.add("int");
    			boolean t=provjeriTip(trenutni_cvor.getdjeca().get(0).gettip(),pr);
    			if(!t)
    			{
    				greska(trenutni_cvor);
    			}
    			provjeri(trenutni_cvor.getdjeca().get(2),trenutni_cvor_u_tablici_znakova);
    			boolean ab=provjeriTip(trenutni_cvor.getdjeca().get(2).gettip(),pr);
    			if(!ab)
    			{
    				greska(trenutni_cvor);
    			}
    			trenutni_cvor.setTip(pr);
    			trenutni_cvor.setl_izraz(false);
    			
    		}
    		else
    		{
    			greska(trenutni_cvor);
    		}
    	}
    	else if(trenutni_cvor.getjedinkaIDN().equals("<izraz_pridruzivanja>"))
    	{
    		if(trenutni_cvor.getdjeca().get(0).getjedinkaIDN().equals("<log_ili_izraz>"))
    		{
    			provjeri(trenutni_cvor.getdjeca().get(0),trenutni_cvor_u_tablici_znakova);
    			trenutni_cvor.setTip(trenutni_cvor.getdjeca().get(0).gettip());
    			trenutni_cvor.setl_izraz(trenutni_cvor.getdjeca().get(0).getl_izraz());
    		}
    		else if(trenutni_cvor.getdjeca().get(0).getjedinkaIDN().equals("<postfiks_izraz>"))
    		{
    			provjeri(trenutni_cvor.getdjeca().get(0), trenutni_cvor_u_tablici_znakova);
    			if(!trenutni_cvor.getdjeca().get(0).getl_izraz())
    			{
    				greska(trenutni_cvor);
    			}
    			provjeri(trenutni_cvor.getdjeca().get(2),trenutni_cvor_u_tablici_znakova);
    			boolean t=provjeriTip(trenutni_cvor.getdjeca().get(2).gettip(),trenutni_cvor.getdjeca().get(0).gettip());
    			if(!t)
    			{
    				greska(trenutni_cvor);
    			}
    			trenutni_cvor.setTip(trenutni_cvor.getdjeca().get(0).gettip());
    			trenutni_cvor.setl_izraz(false);
    		}
    		else
    		{
    			greska(trenutni_cvor);
    		}
    	}
    	else if(trenutni_cvor.getjedinkaIDN().equals("<izraz>"))
    	{
    		if(trenutni_cvor.getdjeca().get(0).getjedinkaIDN().equals("<izraz_pridruzivanja>"))
    		{
    			provjeri(trenutni_cvor.getdjeca().get(0),trenutni_cvor_u_tablici_znakova);
    			trenutni_cvor.setTip(trenutni_cvor.getdjeca().get(0).gettip());
    			trenutni_cvor.setl_izraz(trenutni_cvor.getdjeca().get(0).getl_izraz());
    		}
    		else if(trenutni_cvor.getdjeca().get(0).getjedinkaIDN().equals("<izraz>"))
    		{
    			provjeri(trenutni_cvor.getdjeca().get(0),trenutni_cvor_u_tablici_znakova);
    			provjeri(trenutni_cvor.getdjeca().get(2),trenutni_cvor_u_tablici_znakova);
    			trenutni_cvor.setTip(trenutni_cvor.getdjeca().get(2).gettip());
    			trenutni_cvor.setl_izraz(false);
    		}
    		else
    		{
    			greska(trenutni_cvor);
    		}
    	}
    	else if(trenutni_cvor.getjedinkaIDN().equals("<slozena_naredba>"))
    	{
    		if(trenutni_cvor.getdjeca().get(1).getjedinkaIDN().equals("<lista_naredbi>"))
    		{
    			provjeri(trenutni_cvor.getdjeca().get(1),trenutni_cvor_u_tablici_znakova);
    		}
    		else if(trenutni_cvor.getdjeca().get(1).getjedinkaIDN().equals("<lista_deklaracija>"))
    		{
    			provjeri(trenutni_cvor.getdjeca().get(1),trenutni_cvor_u_tablici_znakova);
    			provjeri(trenutni_cvor.getdjeca().get(2),trenutni_cvor_u_tablici_znakova);
    		}
    		else
    		{
    			greska(trenutni_cvor);
    		}
    	}
    	else if(trenutni_cvor.getjedinkaIDN().equals("<lista_naredbi>"))
    	{
    		if(trenutni_cvor.getdjeca().get(0).getjedinkaIDN().equals("<naredba>"))
    		{
    			provjeri(trenutni_cvor.getdjeca().get(0),trenutni_cvor_u_tablici_znakova);
    		}
    		else if(trenutni_cvor.getdjeca().get(0).getjedinkaIDN().equals("<lista_naredbi>"))
    		{
    			provjeri(trenutni_cvor.getdjeca().get(0),trenutni_cvor_u_tablici_znakova);
    			provjeri(trenutni_cvor.getdjeca().get(1),trenutni_cvor_u_tablici_znakova);
    		}
    		else
    		{
    			greska(trenutni_cvor);
    		}
    	}
    	else if(trenutni_cvor.getjedinkaIDN().equals("<naredba>"))
    	{
    		if(trenutni_cvor.getdjeca().get(0).getjedinkaIDN().equals("<slozena_naredba>"))
    		{
    			novi_cvor_znak.setroditelj(trenutni_cvor_u_tablici_znakova);
    			trenutni_cvor_u_tablici_znakova.dodajDjete(novi_cvor_znak);
    			novi_cvor_znak.setuDjelokrugu(trenutni_cvor_u_tablici_znakova.getuDjelokrugu());
    			provjeri(trenutni_cvor.getdjeca().get(0),novi_cvor_znak);
    			trenutni_cvor_u_tablici_znakova=trenutni_cvor_u_tablici_znakova.getroditelj();
    		}
    		else if(trenutni_cvor.getdjeca().get(0).getjedinkaIDN().equals("<izraz_naredba>") || trenutni_cvor.getdjeca().get(0).getjedinkaIDN().equals("naredba_grananja>") || trenutni_cvor.getdjeca().get(0).getjedinkaIDN().equals("<naredba_petlje>") || trenutni_cvor.getdjeca().get(0).getjedinkaIDN().equals("<naredba_skoka>"))
    		{
    			provjeri(trenutni_cvor.getdjeca().get(0),trenutni_cvor_u_tablici_znakova);
    		}
    		else
    		{
    			greska(trenutni_cvor);
    		}
    	}
    	else if(trenutni_cvor.getjedinkaIDN().equals("<izraz_naredba>"))
    	{
    		if(trenutni_cvor.getdjeca().get(0).getjedinkaIDN().equals("TOCKAZAREZ"))
    		{
    			LinkedList<String> pom=new LinkedList<>();
    			pom.add("int");
    			trenutni_cvor.setTip(pom);
    		}
    		else if(trenutni_cvor.getdjeca().get(0).getjedinkaIDN().equals("<izraz>"))
    		{
    			provjeri(trenutni_cvor.getdjeca().get(0),trenutni_cvor_u_tablici_znakova);
    			trenutni_cvor.setTip(trenutni_cvor.getdjeca().get(0).gettip());
    		}
    		else
    		{
    			greska(trenutni_cvor);
    		}
    	}
    	else if(trenutni_cvor.getjedinkaIDN().equals("<naredba_grananja>"))
    	{
    		if(trenutni_cvor.getdjeca().size()==5)
    		{
    			provjeri(trenutni_cvor.getdjeca().get(2),trenutni_cvor_u_tablici_znakova);
    			LinkedList<String> pom = new LinkedList<>();
    			pom.add("int");
    			boolean t=provjeriTip(trenutni_cvor.getdjeca().get(2).gettip(),pom);
    			if(!t)
    			{
    				greska(trenutni_cvor);
    			}
    			provjeri(trenutni_cvor.getdjeca().get(4),trenutni_cvor_u_tablici_znakova);
    		}
    		else if(trenutni_cvor.getdjeca().size()==7)
    		{
    			provjeri(trenutni_cvor.getdjeca().get(2),trenutni_cvor_u_tablici_znakova);
    			LinkedList<String> pom = new LinkedList<>();
    			pom.add("int");
    			boolean t=provjeriTip(trenutni_cvor.getdjeca().get(2).gettip(),pom);
    			if(!t)
    			{
    				greska(trenutni_cvor);
    			}
    			provjeri(trenutni_cvor.getdjeca().get(4),trenutni_cvor_u_tablici_znakova);
    			provjeri(trenutni_cvor.getdjeca().get(6),trenutni_cvor_u_tablici_znakova);
    			
    		}
    		else
    		{
    			greska(trenutni_cvor);
    		}
    	}
    	else if(trenutni_cvor.getjedinkaIDN().equals("<naredba_petlje>"))
    	{
    		if(trenutni_cvor.getdjeca().get(0).getjedinkaIDN().equals("KR_WHILE"))
    		{
    			provjeri(trenutni_cvor.getdjeca().get(2),trenutni_cvor_u_tablici_znakova);
    			LinkedList<String> pom = new LinkedList<>();
    			pom.add("int");
    			boolean t=provjeriTip(trenutni_cvor.getdjeca().get(2).gettip(),pom);
    			if(!t)
    			{
    				greska(trenutni_cvor);
    			}
    			u_petlji+=1;
    			provjeri(trenutni_cvor.getdjeca().get(4),trenutni_cvor_u_tablici_znakova);
    			u_petlji-=1;
    			
    		}
    		else if(trenutni_cvor.getdjeca().get(4).getjedinkaIDN().equals("D_ZAGRADA"))
    		{
    			provjeri(trenutni_cvor.getdjeca().get(2),trenutni_cvor_u_tablici_znakova);
    			provjeri(trenutni_cvor.getdjeca().get(3),trenutni_cvor_u_tablici_znakova);
    			LinkedList<String> pom = new LinkedList<>();
    			pom.add("int");
    			boolean t=provjeriTip(trenutni_cvor.getdjeca().get(2).gettip(),pom);
    			if(!t)
    			{
    				greska(trenutni_cvor);
    			}
    			u_petlji+=1;
    			provjeri(trenutni_cvor.getdjeca().get(5),trenutni_cvor_u_tablici_znakova);
    			u_petlji-=1;
    		}
    		else if(trenutni_cvor.getdjeca().get(4).getjedinkaIDN().equals("<izraz>"))
    		{
    			provjeri(trenutni_cvor.getdjeca().get(2),trenutni_cvor_u_tablici_znakova);
    			provjeri(trenutni_cvor.getdjeca().get(3),trenutni_cvor_u_tablici_znakova);
    			LinkedList<String> pom = new LinkedList<>();
    			pom.add("int");
    			boolean t=provjeriTip(trenutni_cvor.getdjeca().get(3).gettip(),pom);
    			if(!t)
    			{
    				greska(trenutni_cvor);
    			}
    			provjeri(trenutni_cvor.getdjeca().get(4),trenutni_cvor_u_tablici_znakova);
    			u_petlji+=1;
    			provjeri(trenutni_cvor.getdjeca().get(6),trenutni_cvor_u_tablici_znakova);
    			u_petlji-=1;
    		}
    		else
    		{
    			greska(trenutni_cvor);
    		}
    	}
    	else if(trenutni_cvor.getjedinkaIDN().equals("<naredba_skoka>"))
    	{
    		if(trenutni_cvor.getdjeca().get(0).getjedinkaIDN().equals("KR_CONTINUE") || trenutni_cvor.getdjeca().get(0).getjedinkaIDN().equals("KR_BREAK"))
    		{
    			if(u_petlji<=0)
    			{
    				greska(trenutni_cvor);
    			}
    		}
    		else if(trenutni_cvor.getdjeca().get(1).getjedinkaIDN().equals("TOCKAZAREZ"))
    		{
    			if(!trenutni_cvor_u_tablici_znakova.getuDjelokrugu().get(1).equals("void"))
    			{
    				greska(trenutni_cvor);
    			}
    		}
    		else if(trenutni_cvor.getdjeca().get(1).getjedinkaIDN().equals("<izraz>"))
    		{
    			provjeri(trenutni_cvor.getdjeca().get(1),trenutni_cvor_u_tablici_znakova);
    			if(trenutni_cvor_u_tablici_znakova.getuDjelokrugu().get(1).equals("void"))
    			{
    				greska(trenutni_cvor);
    			}
    			LinkedList<String> pom=new LinkedList<>();
    			pom.add(trenutni_cvor_u_tablici_znakova.getuDjelokrugu().get(1));
    			boolean t=provjeriTip(trenutni_cvor.getdjeca().get(1).gettip(),pom);
    			if(!t)
    			{
    				greska(trenutni_cvor);
    			}
    			
    		}
    		else
    		{
    			greska(trenutni_cvor);
    		}
    	}
    	else if(trenutni_cvor.getjedinkaIDN().equals("<prijevodna_jedinica>"))
    	{
    		if(trenutni_cvor.getdjeca().get(0).getjedinkaIDN().equals("<vanjska_deklaracija>"))
    		{
    			provjeri(trenutni_cvor.getdjeca().get(0),trenutni_cvor_u_tablici_znakova);
    		}
    		else if(trenutni_cvor.getdjeca().get(0).getjedinkaIDN().equals("<prijevodna_jedinica>"))
    		{
    			provjeri(trenutni_cvor.getdjeca().get(0),trenutni_cvor_u_tablici_znakova);
    			provjeri(trenutni_cvor.getdjeca().get(1),trenutni_cvor_u_tablici_znakova);
    		}
    		else
    		{
    			greska(trenutni_cvor);
    		}
    	}
    	else if(trenutni_cvor.getjedinkaIDN().equals("<vanjska_deklaracija>"))
    	{
    		if(trenutni_cvor.getdjeca().get(0).getjedinkaIDN().equals("<definicija_funkcije>") || trenutni_cvor.getdjeca().get(0).getjedinkaIDN().equals("<deklaracija>"))
    		{
    			provjeri(trenutni_cvor.getdjeca().get(0),trenutni_cvor_u_tablici_znakova);
    		}
    		else
    		{
    			greska(trenutni_cvor);
    		}
    	}
    	else if(trenutni_cvor.getjedinkaIDN().equals("<definicija_funkcije>"))
    	{
    		if(trenutni_cvor.getdjeca().get(3).getjedinkaIDN().equals("KR_VOID"))
    		{
    			provjeri(trenutni_cvor.getdjeca().get(0),trenutni_cvor_u_tablici_znakova);
    			if(trenutni_cvor.getdjeca().get(0).gettip().get(0).equals("const_int") || (trenutni_cvor.getdjeca().get(0).gettip().get(0).equals("const_char")))
    			{
    				greska(trenutni_cvor);
    			}
    			//tu sam se mozda zajebo
    			identifikator IDN=provjeriTrenCvZnakova(trenutni_cvor.getdjeca().get(1),korijen_znakova);
    			if(IDN!=null)
    			{
    				if(IDN.getDD()==1)
    				{
    					greska(trenutni_cvor);
    				}
    				if(IDN.getDD()==0)
    				{
    					if(!IDN.gettip().subList(2, IDN.gettip().size()).contains("void"))
    					{
    						greska(trenutni_cvor);
    					}
    					if(!IDN.gettip().get(1).equals(trenutni_cvor.getdjeca().get(0).gettip().get(0)))
    					{
    						greska(trenutni_cvor);
    					}
    					IDN.setDD(1);
    				}
    			}
    			else
    			{
    				LinkedList<String> pom = new LinkedList<>();
    				pom.add("fun");
    				pom.add(trenutni_cvor.getdjeca().get(0).gettip().get(0));
    				pom.add("void");
    				IDN=new CvorZn.identifikator(pom, trenutni_cvor.getdjeca().get(1).getjedinkaime(), 1, false);
    				trenutni_cvor_u_tablici_znakova.dodajIdentifikator(IDN);
    			}
    			CvorZn novi_cvor_znak=new CvorZn();
    			novi_cvor_znak.setroditelj(trenutni_cvor_u_tablici_znakova);
    			trenutni_cvor_u_tablici_znakova.dodajDjete(novi_cvor_znak);
    			LinkedList<String> pom = new LinkedList<>();
				pom.add("fun");
				pom.add(trenutni_cvor.getdjeca().get(0).gettip().get(0));
				pom.add("void");
				novi_cvor_znak.setuDjelokrugu(pom);
				provjeri(trenutni_cvor.getdjeca().get(5),novi_cvor_znak);
				trenutni_cvor_u_tablici_znakova=trenutni_cvor_u_tablici_znakova.getroditelj();
    		}
    		else if(trenutni_cvor.getdjeca().get(3).getjedinkaIDN().equals("<lista_parametara>"))
    		{
    			provjeri(trenutni_cvor.getdjeca().get(0),trenutni_cvor_u_tablici_znakova);
    			if(trenutni_cvor.getdjeca().get(0).gettip().get(0).equals("const_int") || trenutni_cvor.getdjeca().get(0).gettip().get(0).equals("const_char"))
    			{
    				greska(trenutni_cvor);
    			}
    			identifikator IDN= provjeriTrenCvZnakova(trenutni_cvor.getdjeca().get(1),korijen_znakova);
    			if(IDN!=null)
    			{
    				if(IDN.getDD()==1)
    				{
    					greska(trenutni_cvor);
    				}
    			}
    			provjeri(trenutni_cvor.getdjeca().get(3),trenutni_cvor_u_tablici_znakova);
    			//novi_cvor_znak se treba prije deklarirat jer ako se deklarira u bloku naredbi
    			//onda mu je vidljivost samo u tom bloku, pa se na liniji <ova+29> nalazil prazan novi_cvor_znak (najbitnije, nije imal uDjelokrugu)
				CvorZn novi_cvor_znak= new CvorZn();
    			if(IDN!=null)
    			{
    				if(IDN.getDD()==0)
    				{
    					if(!IDN.gettip().get(1).equals(trenutni_cvor.getdjeca().get(0).gettip().get(0)))
    					{
    						greska(trenutni_cvor);
    					}
    					if(!IDN.gettip().subList(2, IDN.gettip().size()).equals(trenutni_cvor.getdjeca().get(3).gettip()))
    					{
    						greska(trenutni_cvor);
    					}
    					IDN.setDD(1);
    					novi_cvor_znak.setuDjelokrugu(IDN.gettip());
    				}
    			}
    			else
    			{
    				LinkedList<String> tip_funkcije=new LinkedList<>();
    				tip_funkcije.add("fun");
    				tip_funkcije.add(trenutni_cvor.getdjeca().get(0).gettip().get(0));
    				tip_funkcije.addAll(trenutni_cvor.getdjeca().get(3).gettip());
    				IDN = new identifikator(tip_funkcije, trenutni_cvor.getdjeca().get(1).getjedinkaime(),1,false);
    				trenutni_cvor_u_tablici_znakova.dodajIdentifikator(IDN);
    				//CvorZn novi_cvor_znak=new CvorZn();
    				novi_cvor_znak.setuDjelokrugu(tip_funkcije);
    			}
    			novi_cvor_znak.setroditelj(trenutni_cvor_u_tablici_znakova);
    			trenutni_cvor_u_tablici_znakova.dodajDjete(novi_cvor_znak);
    			LinkedList<String> pom=trenutni_cvor.getdjeca().get(3).gettip();
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
    					//ovaj dio s .getjedinkaime i nesto ime[i] provjeri ak bu trebalo
    					IDN=new identifikator(tipovi.get(i),trenutni_cvor.getdjeca().get(3).getIme().get(i),1,true);
    				}
    				else
    				{
    					IDN=new identifikator(tipovi.get(i),trenutni_cvor.getdjeca().get(3).getIme().get(i),1,true);
    				}
					novi_cvor_znak.dodajIdentifikator(IDN);
    			}
    			provjeri(trenutni_cvor.getdjeca().get(5),novi_cvor_znak);
    			trenutni_cvor_u_tablici_znakova=trenutni_cvor_u_tablici_znakova.getroditelj();
    		}
    		else
    		{
    			greska(trenutni_cvor);
    		}
    	}
    	else if(trenutni_cvor.getjedinkaIDN().equals("<lista_parametara>"))
    	{
    		if(trenutni_cvor.getdjeca().get(0).getjedinkaIDN().equals("<deklaracija_parametra>"))
    		{
    			provjeri(trenutni_cvor.getdjeca().get(0),trenutni_cvor_u_tablici_znakova);
    			trenutni_cvor.setTip(trenutni_cvor.getdjeca().get(0).gettip());
    			trenutni_cvor.setIme(trenutni_cvor.getdjeca().get(0).getIme());
    		}
    		else if(trenutni_cvor.getdjeca().get(0).getjedinkaIDN().equals("<lista_parametara>"))
    		{
    			provjeri(trenutni_cvor.getdjeca().get(0),trenutni_cvor_u_tablici_znakova);
    			provjeri(trenutni_cvor.getdjeca().get(2),trenutni_cvor_u_tablici_znakova);
    			if(trenutni_cvor.getdjeca().get(0).getIme().equals(trenutni_cvor.getdjeca().get(2).getIme()))
    			{
    				greska(trenutni_cvor);
    			}
    			LinkedList<String> pom=trenutni_cvor.getdjeca().get(0).gettip();
    			pom.addAll(trenutni_cvor.getdjeca().get(2).gettip());
    			trenutni_cvor.setTip(pom);
    			LinkedList<String> t=trenutni_cvor.getdjeca().get(0).getIme();
    			t.addAll(trenutni_cvor.getdjeca().get(2).getIme());
    			trenutni_cvor.setIme(t);
    		}
    		else
    		{
    			greska(trenutni_cvor);
    		}
    	}
    	else if(trenutni_cvor.getjedinkaIDN().equals("<deklaracija_parametra>"))
    	{
    		if(trenutni_cvor.getdjeca().size()==2)
    		{
    			provjeri(trenutni_cvor.getdjeca().get(0),trenutni_cvor_u_tablici_znakova);
    			if(trenutni_cvor.getdjeca().get(0).gettip().contains("void"))
    			{
    				greska(trenutni_cvor);
    			}
    			trenutni_cvor.setTip(trenutni_cvor.getdjeca().get(0).gettip());
    			//ehmmm
    			LinkedList<String> pom =new LinkedList<>();
    			pom.add(trenutni_cvor.getdjeca().get(1).getjedinkaime());
    			trenutni_cvor.setIme(pom);
    		}
    		else if(trenutni_cvor.getdjeca().size()==4)
    		{
    			provjeri(trenutni_cvor.getdjeca().get(0),trenutni_cvor_u_tablici_znakova);
    			if(trenutni_cvor.getdjeca().get(0).gettip().contains("void"))
    			{
    				greska(trenutni_cvor);
    			}
    			LinkedList<String> pom =new LinkedList<>();
    			pom.add("niz");
    			pom.add(trenutni_cvor.getdjeca().get(0).gettip().get(0));
    			trenutni_cvor.setTip(pom);
    			LinkedList<String> p=new LinkedList<>();
    			p.add(trenutni_cvor.getdjeca().get(1).getjedinkaime());
    			trenutni_cvor.setIme(p);
    		}
    		else
    		{
    			greska(trenutni_cvor);
    		}
    	}
    	else if(trenutni_cvor.getjedinkaIDN().equals("<lista_deklaracija>"))
    	{
    		if(trenutni_cvor.getdjeca().get(0).getjedinkaIDN().equals("<deklaracija>"))
    		{
    			provjeri(trenutni_cvor.getdjeca().get(0),trenutni_cvor_u_tablici_znakova);
    		}
    		else if(trenutni_cvor.getdjeca().get(0).getjedinkaIDN().equals("<lista_deklaracija>"))
    		{
    			provjeri(trenutni_cvor.getdjeca().get(0),trenutni_cvor_u_tablici_znakova);
    			provjeri(trenutni_cvor.getdjeca().get(1),trenutni_cvor_u_tablici_znakova);
    		}
    		else
    		{
    			greska(trenutni_cvor);
    		}
    	}
    	else if(trenutni_cvor.getjedinkaIDN().equals("<deklaracija>"))
    	{
    		if(trenutni_cvor.getdjeca().get(0).getjedinkaIDN().equals("<ime_tipa>"))
    		{
    			provjeri(trenutni_cvor.getdjeca().get(0),trenutni_cvor_u_tablici_znakova);
    			trenutni_cvor.getdjeca().get(1).setntip(trenutni_cvor.getdjeca().get(0).gettip());
    			provjeri(trenutni_cvor.getdjeca().get(1),trenutni_cvor_u_tablici_znakova);
    		}
    		else
    		{
    			greska(trenutni_cvor);
    		}
    	}
    	else if(trenutni_cvor.getjedinkaIDN().equals("<lista_init_deklaratora>"))
    	{
    		if(trenutni_cvor.getdjeca().get(0).getjedinkaIDN().equals("<init_deklarator>"))
    		{
    			trenutni_cvor.getdjeca().get(0).setntip(trenutni_cvor.getntip());
    			provjeri(trenutni_cvor.getdjeca().get(0),trenutni_cvor_u_tablici_znakova);
    		}
    		else if(trenutni_cvor.getdjeca().get(0).getjedinkaIDN().equals("<lista_init_deklaratora>"))
    		{
    			trenutni_cvor.getdjeca().get(0).setntip(trenutni_cvor.getntip());
    			provjeri(trenutni_cvor.getdjeca().get(0),trenutni_cvor_u_tablici_znakova);
    			trenutni_cvor.getdjeca().get(2).setntip(trenutni_cvor.getntip());
    			provjeri(trenutni_cvor.getdjeca().get(2),trenutni_cvor_u_tablici_znakova);
    		}
    		else
    		{
    			greska(trenutni_cvor);
    		}
    	}
    	else if(trenutni_cvor.getjedinkaIDN().equals("<init_deklarator>"))
    	{
    		if(trenutni_cvor.getdjeca().size()==1)
    		{
    			trenutni_cvor.getdjeca().get(0).setntip(trenutni_cvor.getntip());
    			provjeri(trenutni_cvor.getdjeca().get(0),trenutni_cvor_u_tablici_znakova);
    			if(trenutni_cvor.getdjeca().get(0).gettip().get(0).equals("const_int") || trenutni_cvor.getdjeca().get(0).gettip().get(0).equals("const_char"))
    			{
    				greska(trenutni_cvor);
    			}
    			if(trenutni_cvor.getdjeca().get(0).gettip().get(0).equals("niz") && (trenutni_cvor.getdjeca().get(0).gettip().get(1).equals("const_int") || trenutni_cvor.getdjeca().get(0).gettip().get(1).equals("const_char")))
    			{
    				greska(trenutni_cvor);
    			}
    		}
    		else if(trenutni_cvor.getdjeca().size()==3)
    		{
    			trenutni_cvor.getdjeca().get(0).setntip(trenutni_cvor.getntip());
    			provjeri(trenutni_cvor.getdjeca().get(0),trenutni_cvor_u_tablici_znakova);
    			provjeri(trenutni_cvor.getdjeca().get(2),trenutni_cvor_u_tablici_znakova);
    			if(trenutni_cvor.getdjeca().get(0).gettip().get(0).equals("int") || trenutni_cvor.getdjeca().get(0).gettip().get(0).equals("const_int") || trenutni_cvor.getdjeca().get(0).gettip().get(0).equals("char") || trenutni_cvor.getdjeca().get(0).gettip().get(0).equals("const_char"))
    			{
    				boolean t=provjeriTip(trenutni_cvor.getdjeca().get(2).gettip(),trenutni_cvor.getdjeca().get(0).gettip());
    				if(!t)
    				{
    					greska(trenutni_cvor);
    				}
    			}
    			else if(trenutni_cvor.getdjeca().get(0).gettip().get(0).equals("niz"))
    			{
    				if(trenutni_cvor.getdjeca().get(0).gettip().get(1).equals("int") || trenutni_cvor.getdjeca().get(0).gettip().get(1).equals("const_int") || trenutni_cvor.getdjeca().get(0).gettip().get(1).equals("char") || trenutni_cvor.getdjeca().get(0).gettip().get(1).equals("const_char"))
    				{
    					if(trenutni_cvor.getdjeca().get(2).getbrElem()>trenutni_cvor.getdjeca().get(0).getbrElem())
    					{
    						greska(trenutni_cvor);
    					}
    					for(int i=0;i<trenutni_cvor.getdjeca().get(2).gettip().size()-1;i++)
    					{
    						LinkedList<String> pom = new LinkedList<>();
    						pom.add(trenutni_cvor.getdjeca().get(2).gettip().get(i+1));
    						LinkedList<String> pom2 = new LinkedList<>();
    						pom2.add(trenutni_cvor.getdjeca().get(0).gettip().get(1));
    						boolean t=provjeriTip(pom,pom2);
    						if(!t)
    						{
    							greska(trenutni_cvor);
    						}
    					}
    				}
    				else
    				{
    					greska(trenutni_cvor);
    				}
    			}
    			else
    			{
    				greska(trenutni_cvor);
    			}
    		}
    		else
    		{
    			greska(trenutni_cvor);
    		}
    	}
    	else if(trenutni_cvor.getjedinkaIDN().equals("<izravni_deklarator>"))
    	{
    		if(trenutni_cvor.getdjeca().size()==1)
    		{
    			if(trenutni_cvor.getdjeca().get(0).getntip().contains("void"))
    			{
    				greska(trenutni_cvor);
    			}
    			identifikator IDN=provjeriTrenCvZnakova(trenutni_cvor.getdjeca().get(0),trenutni_cvor_u_tablici_znakova);
    			if(IDN!=null)
    			{
    				greska(trenutni_cvor);
    			}
    			IDN=new identifikator(trenutni_cvor.getntip(),trenutni_cvor.getdjeca().get(0).getjedinkaime(),1,true);
    			trenutni_cvor_u_tablici_znakova.dodajIdentifikator(IDN);
    			trenutni_cvor.setTip(trenutni_cvor.getntip());
    		}
    		else if(trenutni_cvor.getdjeca().get(2).getjedinkaIDN().equals("BROJ"))
    		{
    			if(trenutni_cvor.getntip().contains("void"))
    			{
    				greska(trenutni_cvor);
    			}
    			identifikator IDN=provjeriTrenCvZnakova(trenutni_cvor.getdjeca().get(0),trenutni_cvor_u_tablici_znakova);
    			if(IDN!=null)
    			{
    				greska(trenutni_cvor);
    			}
    			if(Integer.parseInt(trenutni_cvor.getdjeca().get(2).getjedinkaime())<0 || Integer.parseInt(trenutni_cvor.getdjeca().get(2).getjedinkaime())>1024)
    			{
    				greska(trenutni_cvor);
    			}
    			LinkedList<String>p = new LinkedList<>();
    			p.add("niz");
    			p.add(trenutni_cvor.getntip().get(0));
    			IDN= new identifikator(p,trenutni_cvor.getdjeca().get(0).getjedinkaime(),1,true);
    			trenutni_cvor_u_tablici_znakova.dodajIdentifikator(IDN);
    			trenutni_cvor.setTip(p);
    			trenutni_cvor.setbrElem(Integer.parseInt(trenutni_cvor.getdjeca().get(2).getjedinkaime()));
    		}
    		else if(trenutni_cvor.getdjeca().get(2).getjedinkaIDN().equals("KR_VOID"))
    		{
    			identifikator IDN=provjeriTrenCvZnakova(trenutni_cvor.getdjeca().get(0),trenutni_cvor_u_tablici_znakova);
    			if(IDN!=null)
    			{
    				//ctrl F SAC do ovog dijela za nijedan ispis ni ne dode mozda bude poslije problem
    				boolean p=IDN.gettip().get(1).equals(trenutni_cvor.getntip().get(0));
                    boolean t=(p && trenutni_cvor.getntip().size()==1);
                    /*if(!t == !(IDN.gettip().get(1).equals(trenutni_cvor.getntip().get(0)) && trenutni_cvor.getntip().size() == 1))
                    	System.out.println("tocno");
                    else
                    	System.out.println("netocno");*/
                    if(!t)
                    {
                        greska(trenutni_cvor);
                    }
    				if(!IDN.gettip().subList(2, IDN.gettip().size()).contains("void"))
    				{
    					greska(trenutni_cvor);
    				}
    			}
    			else
    			{
    				LinkedList<String> pom=new LinkedList<>();
    				pom.add("fun");
    				pom.add(trenutni_cvor.getntip().get(0));
    				pom.add("void");
    				IDN=new identifikator(pom,trenutni_cvor.getdjeca().get(0).getjedinkaime(),0,false);
    				trenutni_cvor_u_tablici_znakova.dodajIdentifikator(IDN);
    			}
    			LinkedList<String> pom=new LinkedList<>();
				pom.add("fun");
				pom.add(trenutni_cvor.getntip().get(0));
				pom.add("void");
				trenutni_cvor.setTip(pom);
    		}
    		else if(trenutni_cvor.getdjeca().get(2).getjedinkaIDN().equals("<lista_parametara>"))
    		{
    			provjeri(trenutni_cvor.getdjeca().get(2),trenutni_cvor_u_tablici_znakova);
    			identifikator IDN=provjeriTrenCvZnakova(trenutni_cvor.getdjeca().get(0),trenutni_cvor_u_tablici_znakova);
    			if(IDN!=null)
    			{
    				if(IDN.gettip().get(1)!=trenutni_cvor.getntip().get(0))
    				{
    					greska(trenutni_cvor);
    				}
    				if(!IDN.gettip().subList(2, IDN.gettip().size()).equals(trenutni_cvor.getdjeca().get(2).gettip()))
    				{
    					greska(trenutni_cvor);
    				}
    			}
    			else
    			{
    				LinkedList<String> pom =new LinkedList<>();
    				pom.add("fun");
    				pom.add(trenutni_cvor.getntip().get(0));
    				pom.addAll(trenutni_cvor.getdjeca().get(2).gettip());
    				IDN=new identifikator(pom, trenutni_cvor.getdjeca().get(0).getjedinkaime(),0,false);
    				trenutni_cvor_u_tablici_znakova.dodajIdentifikator(IDN);
    			}
    			LinkedList<String> pom =new LinkedList<>();
				pom.add("fun");
				pom.add(trenutni_cvor.getntip().get(0));
				pom.addAll(trenutni_cvor.getdjeca().get(2).gettip());
				trenutni_cvor.setTip(pom);
    		}
    		else
    		{
    			greska(trenutni_cvor);
    		}
    	}
    	else if(trenutni_cvor.getjedinkaIDN().equals("<inicijalizator>"))
    	{
    		if(trenutni_cvor.getdjeca().get(0).getjedinkaIDN().equals("<izraz_pridruzivanja>"))
    		{
    			provjeri(trenutni_cvor.getdjeca().get(0),trenutni_cvor_u_tablici_znakova);
    			Cvor cvor_NIZ_ZNAKOVA=null;
    			LinkedList<Cvor> treba_provjeriti = new LinkedList<Cvor>();
                treba_provjeriti.addAll(trenutni_cvor.getdjeca());
                //ovo neje valjalo
    			//LinkedList<Cvor> treba_provjeriti=trenutni_cvor.getdjeca();
    			
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
    				trenutni_cvor.setbrElem(cvor_NIZ_ZNAKOVA.getjedinkaime().length()-2+1);
    				LinkedList<String> pom = new LinkedList<>();
    				for(int i=0;i<trenutni_cvor.getbrElem();i++)
    				{
    					pom.add("char");
    				}
    				trenutni_cvor.setTip(pom);
    			}
    			else
    			{
    				trenutni_cvor.setTip(trenutni_cvor.getdjeca().get(0).gettip());
    			}
    		}
    		else if(trenutni_cvor.getdjeca().get(0).getjedinkaIDN().equals("L_VIT_ZAGRADA"))
    		{
    			provjeri(trenutni_cvor.getdjeca().get(1),trenutni_cvor_u_tablici_znakova);
    			trenutni_cvor.setbrElem(trenutni_cvor.getdjeca().get(1).getbrElem());
    			trenutni_cvor.setTip(trenutni_cvor.getdjeca().get(1).gettip());
    		}
    		else
    		{
    			greska(trenutni_cvor);
    		}
    	}
    	else if(trenutni_cvor.getjedinkaIDN().equals("<lista_izraza_pridruzivanja>"))
    	{
    		if(trenutni_cvor.getdjeca().get(0).getjedinkaIDN().equals("<izraz_pridruzivanja>"))
    		{
    			provjeri(trenutni_cvor.getdjeca().get(0),trenutni_cvor_u_tablici_znakova);
    			trenutni_cvor.setTip(trenutni_cvor.getdjeca().get(0).gettip());
    			trenutni_cvor.setbrElem(1);
    		}
    		else if(trenutni_cvor.getdjeca().get(0).getjedinkaIDN().equals("<lista_izraza_pridruzivanja>"))
    		{
    			provjeri(trenutni_cvor.getdjeca().get(0),trenutni_cvor_u_tablici_znakova);
    			provjeri(trenutni_cvor.getdjeca().get(2),trenutni_cvor_u_tablici_znakova);
    			LinkedList<String> pom =new LinkedList<>();
    			pom.addAll(trenutni_cvor.getdjeca().get(0).gettip());
    			pom.addAll(trenutni_cvor.getdjeca().get(2).gettip());
    			trenutni_cvor.setTip(pom);
    			trenutni_cvor.setbrElem(trenutni_cvor.getdjeca().get(0).getbrElem()+1);
    		}
    		else
    		{
    			greska(trenutni_cvor);
    		}
    	}
    	else
    	{
    		greska(trenutni_cvor);
    	}
    //kraj metode provjeri	
    }
//kraj seman
}