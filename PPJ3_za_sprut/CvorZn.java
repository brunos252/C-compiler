import java.util.LinkedList;

public class CvorZn 
{

        CvorZn roditelj;
        LinkedList<identifikator> identifikatori=new LinkedList<>();
        LinkedList<CvorZn> djeca=new LinkedList<>();
        LinkedList<String> uDjelokrugu=new LinkedList<>();

        public void dodajDjete(CvorZn cvor){
            this.djeca.add(cvor);
        }
        
        public void setuDjelokrugu(LinkedList<String> uDj)
        {
        	this.uDjelokrugu=uDj;
        }
        
        public LinkedList<String> getuDjelokrugu()
        {
        	return this.uDjelokrugu;
        }
        
        public void setroditelj(CvorZn parent)
        {
        	this.roditelj=parent;
        }
        
        public CvorZn getroditelj()
        {
        	return this.roditelj;
        }
        
        public LinkedList<CvorZn> getdjeca()
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

        public void clone(CvorZn original)
        {
            if(original.getroditelj() == null)
                this.roditelj = null;
            else {
                this.roditelj = new CvorZn();
                this.roditelj.clone(original.getroditelj());
            }
            this.identifikatori = (LinkedList<identifikator>) original.getidentifikatori().clone();
            this.djeca = (LinkedList<CvorZn>) original.getdjeca().clone();
            this.uDjelokrugu = (LinkedList<String>) original.uDjelokrugu.clone();
        }

    public static class identifikator
    {
        LinkedList<String> tip;
        String ime;
        int DD; //-1: none, 0: deklarirano, 1: definirano
        boolean l_izraz;
        String vrijednost;
        
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
        
        public int getDD()
        {
        	return this.DD;
        }
        
        public void setDD(int DD)
        {
        	this.DD=DD;
        }


        public void clone(identifikator original)
        {
            this.tip = (LinkedList<String>) original.gettip().clone();
            this.ime = original.getime();
            this.DD = original.getDD();
            this.l_izraz = original.getl_izraz();
            this.vrijednost = original.vrijednost;
        }

        public identifikator(LinkedList<String> tip, String ime, int DD, boolean l_izraz)
        {
        	this.tip=tip;
        	this.ime=ime;
        	this.DD=DD;
        	this.l_izraz=l_izraz;
        	this.vrijednost=null;
        }
        
        public identifikator(){
            this.DD = -1;
            this.tip = new LinkedList<>();
            this.l_izraz = false;
        }
    }
}

