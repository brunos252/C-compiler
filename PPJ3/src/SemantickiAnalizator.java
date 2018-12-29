import java.util.LinkedList;

import hehe.Cvor;
import hehe.CvorZn;
import hehe.CvorZn.identifikator;

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
    	
    	
    	
    //kraj metode provjeri	
    }
//kraj seman
}