import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class SA {

    public static void main(String[] args) throws IOException {
        hehe();
    }

    public static void hehe() throws IOException
    {
        final String FILENAME = "pomocni.txt";
        FileReader fr = new FileReader(FILENAME);
        BufferedReader br = new BufferedReader(fr);
        BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
        String line;
        String inp;
        Map<String, List<String>> redukcije = new HashMap<String, List<String>>();
        Map<String, List<String>> akcije = new HashMap<String, List<String>>();
        List<String> stog=new ArrayList<String>();
        List<String> zavrsni=new ArrayList<String>();
        List<String> nezavrsni=new ArrayList<String>();
        stog.add("#");
        String pocetno = null;
        line=br.readLine();
        String lajn=line.substring(1, line.length()-1);
        String[] s=lajn.split(", ");
        for(String sx:s)
        {
            zavrsni.add(sx);
        }
        line=br.readLine();
        lajn=line.substring(1, line.length()-1);
        s=lajn.split(", ");
        for(int i=1;i<s.length;i++)
        {
            nezavrsni.add(s[i]);
        }

        while((line = br.readLine()) != null && !(line.isEmpty()))
        {
            if(line.startsWith("Akcije"))
            {
                while((line=br.readLine())!=null && !(line.isEmpty()))
                {
                    boolean a=false;
                    String[] kom=line.split(",");
                    String stanje=kom[0];
                    if(kom[0].startsWith("Nova stanja"))
                    {
                        break;
                    }
                    pocetno=stanje;
                    String prijelaz=kom[1];
                    List<String> prijelazizastanje=new ArrayList<String>();
                    prijelazizastanje.add(prijelaz);
                    br.mark(1000);
                    while((line=br.readLine())!=null && !(line.isEmpty()))
                    {
                        String[] x = line.split(",");
                        if(stanje.equals(x[0]))
                        {
                            prijelazizastanje.add(x[1]);
                            a=true;
                            br.mark(1000);
                            continue;
                        }
                        else
                        {
                            br.reset();
                            akcije.put(stanje, prijelazizastanje);
                            break;
                        }
                    }
                    akcije.put(stanje, prijelazizastanje);
                }
            }
            stog.add(pocetno);
            if(line.startsWith("Nova stanja"))
            {
                while((line=br.readLine())!=null && !(line.isEmpty()))
                {
                    boolean a=false;
                    String[] kom=line.split(",");
                    String stanje=kom[0];
                    String prijelaz=kom[1];
                    List<String> prijelazizastanje=new ArrayList<String>();
                    prijelazizastanje.add(prijelaz);
                    br.mark(1000);
                    while((line=br.readLine())!=null && !(line.isEmpty()))
                    {
                        String[] x = line.split(",");
                        if(stanje.equals(x[0]))
                        {
                            prijelazizastanje.add(x[1]);
                            a=true;
                            br.mark(1000);
                            continue;
                        }
                        else
                        {
                            br.reset();
                            redukcije.put(stanje, prijelazizastanje);
                            break;
                        }
                    }
                    redukcije.put(stanje, prijelazizastanje);
                }
            }
        }
        br.close();
        fr.close();
        List<String> ulazni=new ArrayList<String>();
        List<String> cijeli=new ArrayList<String>();
        List<String> prob=new ArrayList<String>();
        List<Cvor> ispisna=new ArrayList<Cvor>();
        int brred=0;
        List<String> pomocna= new ArrayList<String>();

        pomocna.addAll(zavrsni);
        pomocna.addAll(nezavrsni);
        while((inp=read.readLine())!=null)
        {
            cijeli.add(inp);
            String[] znak=inp.split(" ");
            String ch = znak[0];
            ulazni.add(ch);
        }
        for(int i=0;i<ulazni.size();i++)
        {
            String tren=ulazni.get(i);
            for(String akcija:akcije.get(stog.get(stog.size()-1)))
            {
                String[] pom = akcija.split("->");
                String p=pom[0];
                if(p.equals(tren))
                {
                    String x=Character.toString(pom[1].charAt(0));
                    String sta=pom[1].replace("(", "");
                    sta=sta.replace(")", "");
                    sta=sta.replace("p", "");
                    //sta=sta.stripLeading();
                    //sta=sta.stripTrailing();
                    if(x.equals("p"))
                    {
                        ispisna.add(new Cvor(cijeli.get(i), new LinkedList<Cvor>()));
                        stog.add(tren);
                        stog.add(sta);
                        continue;
                    }
                    else
                    {
                        i--;
                        brred++;
                        String redsena=pom[1];
                        String red=redsena.substring(2);
                        prob.add(red);
                        String redac=pom[2];
                        String reducira=redac.substring(0, redac.length()-1);
                        int skinibroj=reducira.length();
                        String ducko=reducira;
                        int abakus=0;
                        LinkedList<Cvor> makni= new LinkedList<Cvor>();
                        if(reducira.equals("$"))
                        {
                            makni.add(new Cvor("$",new LinkedList<Cvor>()));
                        }
                        for(String tt:pomocna)
                        {
                            if(ducko.contains(tt))
                            {
                                Cvor maknuta=ispisna.get(ispisna.size()-1);
                                ispisna.remove(ispisna.size()-1);
                                makni.add(maknuta);
                            }
                        }
                        for(String exx:zavrsni)
                        {
                            if (ducko.contains(exx))
                            {
                                abakus++;

                            }
                        }
                        for(String ixx:nezavrsni)
                        {
                            if(ducko.contains(ixx))
                            {
                                abakus++;
                            }
                        }
                        ispisna.add(new Cvor(red, makni));
                        for(int qq=0;qq<abakus*2;qq++)
                        {
                            stog.remove(stog.size()-1);
                        }
                        String zadnjestanje;
                        if(stog.size()==2)
                        {
                            zadnjestanje=stog.get(stog.size()-1);
                        }
                        else
                        {
                            if(stog.size()!=0)
                            {
                                zadnjestanje=stog.get(stog.size()-1);
                            }
                            else
                            {
                                System.out.println("Err.");
                                break;
                            }
                        }
                        stog.add(red);
                        for(String novo:redukcije.get(zadnjestanje))
                        {
                            String[] kekec=novo.split("->");
                            String prov=kekec[0];
                            if(prov.equals(red))
                            {
                                String nst=Character.toString(kekec[1].charAt(2));
                                stog.add(nst);
                            }
                        }
                    }
                }
            }
        }
        boolean prihvat=false;
        while(stog.size()>2 && !prihvat)
        {
            String tren="#";

            for(String akcija:akcije.get(stog.get(stog.size()-1)))
            {
                String[] pom = akcija.split("->");
                String p=pom[0];
                if(pom[1].equals("PRIHVATI"))
                {
                    //System.out.println("Niz je prihvacen.");
                    prihvat=true;
                    break;
                }
                if(p.equals(tren))
                {
                    String x=Character.toString(pom[1].charAt(0));
                    String sta=Character.toString(pom[1].charAt(2));
                    if(x.equals("p"))
                    {
                        ispisna.add(new Cvor(tren, new LinkedList<Cvor>()));
                        stog.add(tren);
                        stog.add(sta);
                        continue;
                    }
                    else
                    {
                        brred++;
                        String redsena=pom[1];
                        String red=redsena.substring(2);

                        prob.add(red);

                        String redac=pom[2];
                        String reducira=redac.substring(0, redac.length()-1);
                        int skinibroj=reducira.length();
                        String ducko=reducira;
                        int off=0;
                        int abakus=0;
                        LinkedList<Cvor> makni= new LinkedList<Cvor>();
                        if(reducira.equals("$"))
                        {
                            makni.add(new Cvor("$",new LinkedList<Cvor>()));
                        }
                        for(String tt:pomocna)
                        {
                            if(ducko.contains(tt))
                            {
                                Cvor maknuta=ispisna.get(ispisna.size()-1);
                                ispisna.remove(ispisna.size()-1);
                                makni.add(maknuta);
                            }
                        }
                        for(String exx:zavrsni)
                        {
                            if (ducko.contains(exx))
                            {
                                abakus++;
                            }
                        }
                        for(String ixx:nezavrsni)
                        {
                            if(ducko.contains(ixx))
                            {
                                abakus++;
                            }
                        }
                        ispisna.add(new Cvor(red, makni));
                        for(int qq=0;qq<abakus*2;qq++)
                        {

                            if(stog.size()!=0)
                            {
                                stog.remove(stog.size()-1);
                            }
                        }
                        String zadnjestanje;
                        if(stog.size()==2)
                        {
                            zadnjestanje=stog.get(stog.size()-1);
                        }
                        else
                        {
                            if(stog.size()!=0)
                            {
                                zadnjestanje=stog.get(stog.size()-1);
                            }
                            else
                            {
                                System.out.println("Err.");
                                break;
                            }
                        }
                        stog.add(red);
                        for(String novo:redukcije.get(zadnjestanje))
                        {
                            String[] kekec=novo.split("->");
                            String prov=kekec[0];
                            if(prov.equals(red))
                            {
                                String nst=Character.toString(kekec[1].charAt(2));
                                stog.add(nst);
                            }
                        }
                    }
                }
            }
        }
        for(Cvor c: ispisna)
        {
            //System.out.println(c);
            c.Ispisi(0);
        }
    }


}
