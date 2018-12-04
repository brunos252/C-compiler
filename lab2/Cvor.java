import java.util.LinkedList;

public class Cvor{
    private String uniformniZnak;
    private int razina;
    private LinkedList<Cvor> djeca;

    public Cvor(String uniformniZnak, LinkedList<Cvor> djeca){
        this.uniformniZnak = uniformniZnak;
        this.djeca = djeca;
    }

    public LinkedList<Cvor> Dohvat()
    {
        return djeca;
    }

    public void Ispisi(int razina) {
        for(int i = 0; i < razina; i++)
            System.out.print(" ");
        System.out.println(uniformniZnak);
        for(int p=djeca.size();p>0;p--)
        {
            djeca.get(p-1).Ispisi(razina+1);
        }
    }
}