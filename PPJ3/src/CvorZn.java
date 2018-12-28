import java.util.LinkedList;

public class CvorZn {

        CvorZn roditelj;
        LinkedList<identifikator> identifikatori;
        LinkedList<CvorZn> djeca;
        CvorZn uDjelokrugu;

        void dodajDjete(CvorZn cvor){
            this.djeca.add(cvor);
        }

        void dodajIdentifikator(identifikator idn){
            this.identifikatori.add(idn);
        }

    private class identifikator{
        LinkedList<String> tip;
        String ime;
        int DD; //-1: none, 0: deklarirano, 1: definirano
        boolean l_izraz;
        String vrijednost;

        public identifikator(){
            this.DD = -1;
            this.tip = new LinkedList<>();
            this.ime = ime;
            this.vrijednost = vrijednost;
            this.l_izraz = false;
        }
    }
}
