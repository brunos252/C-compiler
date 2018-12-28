import java.util.LinkedList;

public class Cvor {

    String ime;
    int brElem;
    boolean l_izraz;
    lexJed jedinka;
    LinkedList<Cvor> djeca;
    LinkedList<String> tip, ntip;

    public void dodajDjete(Cvor djete){
        djeca.add(djete);
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

    private class lexJed{
        String IDN, ime;
        int raz;

        public lexJed(String IDN, String ime, int raz){
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
