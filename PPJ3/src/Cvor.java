package hehe;

import java.util.LinkedList;

public class Cvor
{

    LinkedList<String> ime;
    int brElem;
    boolean l_izraz;
    lexJed jedinka;
    LinkedList<Cvor> djeca = new LinkedList<>();
    LinkedList<String> tip = new LinkedList<>();
    LinkedList<String> ntip=new LinkedList<>();
    
    public Cvor()
    {
    	
    }
    
    public void setjedinka(Cvor.lexJed jedinka)
    {
    	this.jedinka=jedinka;
    }
    
    public void setbrElem(int brElem)
    {
    	this.brElem=brElem;
    }
    
    public int getbrElem()
    {
    	return this.brElem;
    }
    
    public LinkedList<String> getntip()
    {
    	return this.ntip;
    }
    
    public void setntip(LinkedList<String> pom)
    {
    	this.ntip=pom;
    }
    
    public LinkedList<String> getIme()
    {
    	return this.ime;
    }
    
    public void setIme(LinkedList<String> ime)
    {
    	this.ime=ime;
    }
    
    public lexJed getjedinka()
    {
    	return this.jedinka;
    }
    
	public void setTip(LinkedList<String> tip) {
		this.tip = tip;
	}
	
	public LinkedList<String> gettip()
	{
		return this.tip;
	}
	
	public void setl_izraz(boolean izraz)
	{
		this.l_izraz=izraz;
	}
	
	public boolean getl_izraz()
	{
		return this.l_izraz;
	}
	
	public LinkedList<Cvor> getdjeca()
    {
    	return this.djeca;
    }
    
    public String getjedinkaIDN()
    {
    	return this.jedinka.IDN;
    }
    
    public void dodajDjete(Cvor djete)
    {
        djeca.add(djete);
    }
    
    public int getjedinkaraz()
    {
    	return this.jedinka.raz;
    }
    
    public String getjedinkaime()
    {
    	return this.jedinka.ime;
    }

    @Override
    public String toString() {
        if((this.jedinka.IDN.startsWith("<")) || (this.jedinka.IDN.equals("$"))){
            return this.jedinka.IDN;
        }else{
            return this.jedinka.IDN + " " + this.jedinka.ime + " " + this.jedinka.raz;
        }
    }

    void printStablo(){
        //TODO
    }

    public static class lexJed
    {
        String IDN, ime;
        int raz;

        public lexJed(String IDN, String ime, int raz)
        {
            this.IDN = IDN;
            this.ime = ime;
            this.raz = raz;
        }
        

        @Override
        public String toString() {
            return this.IDN + " " + this.raz + " " + this.ime;
        }
    }
}

