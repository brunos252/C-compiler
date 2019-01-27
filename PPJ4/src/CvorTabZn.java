import java.util.LinkedList;

public class CvorTabZn 
{

        CvorTabZn roditelj;
        LinkedList<identifikator> identifikatori=new LinkedList<>();
        LinkedList<CvorTabZn> djeca=new LinkedList<>();
        LinkedList<String> uBloku=new LinkedList<>();

        public void dodajDjete(CvorTabZn cvor){
            this.djeca.add(cvor);
        }
        
        public void setUBloku(LinkedList<String> uBloku)
        {
        	this.uBloku=uBloku;
        }
        
        public LinkedList<String> getUBloku()
        {
        	return this.uBloku;
        }
        
        public void setroditelj(CvorTabZn parent)
        {
        	this.roditelj=parent;
        }
        
        public CvorTabZn getroditelj()
        {
        	return this.roditelj;
        }
        
        public LinkedList<CvorTabZn> getdjeca()
        {
        	return this.djeca;
        }
        
        public LinkedList<identifikator> getidentifikatori()
        {
        	return this.identifikatori;
        }
        
        void dodajIdentifikator(identifikator idn){
            this.identifikatori.add(idn);
        }

        @SuppressWarnings("unchecked")
		public void clone(CvorTabZn original)
        {
            if(original.getroditelj() == null)
                this.roditelj = null;
            else {
                this.roditelj = new CvorTabZn();
                this.roditelj.clone(original.getroditelj());
            }
            this.identifikatori = (LinkedList<identifikator>) original.getidentifikatori().clone();
            this.djeca = (LinkedList<CvorTabZn>) original.getdjeca().clone();
            this.uBloku = (LinkedList<String>) original.uBloku.clone();
        }

    public static class identifikator
    {
        LinkedList<String> tip;
        String ime;
        int defin; //-1: none, 0: deklarirano, 1: definirano
        boolean l_izraz;
        String vrijednost;
        
        public void setVrijednost(String vrijednost)
        {
        	this.vrijednost=vrijednost;
        }
        
        public String getVrijednost()
        {
        	return this.vrijednost;
        }
        
        public boolean getl_izraz()
        {
        	return this.l_izraz;
        }
        
        public String getime()
        {
        	return this.ime;
        }
        
        public LinkedList<String> gettip()
        {
        	return this.tip;
        }
        
        public int getdefin()
        {
        	return this.defin;
        }
        
        public void setdefin(int defin)
        {
        	this.defin=defin;
        }


        @SuppressWarnings("unchecked")
		public void clone(identifikator original)
        {
            this.tip = (LinkedList<String>) original.gettip().clone();
            this.ime = original.getime();
            this.defin = original.getdefin();
            this.l_izraz = original.getl_izraz();
            this.vrijednost = original.vrijednost;
        }

        public identifikator(LinkedList<String> tip, String ime, int defin, boolean l_izraz)
        {
        	this.tip=tip;
        	this.ime=ime;
        	this.defin=defin;
        	this.l_izraz=l_izraz;
        	this.vrijednost=null;
        }
        
        public identifikator(){
            this.defin = -1;
            this.tip = new LinkedList<>();
            this.l_izraz = false;
        }
    }
}