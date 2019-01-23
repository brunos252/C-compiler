import java.util.LinkedList;

public class Cvor
{

    LinkedList<String> ime;
    int brElem;
    boolean l_izraz;
    leksJed jedinka;
    private String vrijednost;
    LinkedList<Cvor> djeca = new LinkedList<>();
    LinkedList<String> tip = new LinkedList<>();
    LinkedList<String> ntip = new LinkedList<>();
    
    public void setVrijednost(String vrijednost) {
    	this.vrijednost = vrijednost;
    }
    
    public String getVrijednost() {
    	return vrijednost;
    }
    
    public void setJedinka(Cvor.leksJed jedinka)
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
    
    public leksJed getjedinka()
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

    public static class leksJed
    {
        String IDN, ime;
        int raz;

        public leksJed(String IDN, String ime, int raz)
        {
            this.IDN = IDN;
            this.ime = ime;
            this.raz = raz;
        }
    }
}

