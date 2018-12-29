import java.util.LinkedList;

public class CvorZn {

        CvorZn roditelj;
        LinkedList<identifikator> identifikatori;
        LinkedList<CvorZn> djeca;
        LinkedList<String> uDjelokrugu;

        void dodajDjete(CvorZn cvor){
            this.djeca.add(cvor);
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

    public class identifikator{
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
        
        public identifikator(){
            this.DD = -1;
            this.tip = new LinkedList<>();
            this.ime = ime;
            this.vrijednost = vrijednost;
            this.l_izraz = false;
        }
    }
}
