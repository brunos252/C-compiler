import java.util.LinkedList;

import java.util.LinkedList;

import Cvor;
import CvorZn;
import CvorZn.identifikator;

public class SemantickiAnalizator {

    public static void main(String[] args) {
	// write your code here
    }

    private boolean provjeriTip(LinkedList<String> tip1, LinkedList<String> tip2)
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

    private void greska(Cvor cvor)
    {
    	System.out.println(cvor.getjedinkaIDN()+"::=");
    	for (Cvor baby : cvor.getdjeca())
    	{
    		if(baby.getjedinkaIDN().equals("<"))
    		{
    			System.out.println(baby.getjedinkaIDN());
    		}
    		else
    		{
    			System.out.println(baby.getjedinkaIDN()+"("+Integer.toString(baby.getjedinkaraz())+","+baby.getjedinkaime()+")");
    		}
    	}  	
        System.exit(0);
    }

    private identifikator pretraziTabZnakova(Cvor jedinka, CvorZn trenutni_cvor_znak)
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

    private identifikator provjeriTrenCvZnakova(Cvor jedinka, CvorZn trenutni_cvor_znak)
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

    private boolean imaMain(CvorZn korijen_znakova)
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

    private boolean provjeriFun(CvorZn korijen_znakova)
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

    private void provjeri(Cvor trenutni_cvor, CvorZn trenutni_cvor_u_tablici_znakova)
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
        		if (Integer.parseInt(trenutni_cvor.getdjeca().get(0).getjedinkaime()) < Math.pow(-2, 31) || Integer.parseInt(trenutni_cvor.getdjeca().get(0).getjedinkaime()) > Math.pow(2, 31)-1)
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
        		if(!(trenutni_cvor.getdjeca().get(0).getjedinkaime().startsWith("\\") || trenutni_cvor.getdjeca().get(0).getjedinkaime().startsWith("\\n") || trenutni_cvor.getdjeca().get(0).getjedinkaime().startsWith("\\t") || trenutni_cvor.getdjeca().get(0).getjedinkaime().startsWith("\\0") || trenutni_cvor.getdjeca().get(0).getjedinkaime().startsWith("\\\'") || trenutni_cvor.getdjeca().get(0).getjedinkaime().startsWith("\\\"") || trenutni_cvor.getdjeca().get(0).getjedinkaime().startsWith("\\\\")))
        		{
        			greska(trenutni_cvor);
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
    	else if(trenutni_cvor.getjedinkaIDN().equals("<izraz"))
    	{
    		
    	}
    //kraj metode provjeri	
    }
//kraj seman
}