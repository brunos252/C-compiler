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
    	if(tip1.get(0).equals("const_int") & tip2.get(0).equals("int"))
    	{
    		return true;
    	}
    	if(tip1.get(0).equals("int")&tip2.get(0).equals("const_int"))
    	{
    		return true;
    	}
    	if(tip1.get(0).equals("const_char")&tip2.get(0).equals("char"))
    	{
    		return true;
    	}
    	if(tip1.get(0).equals("char")&tip2.get(0).equals("const_char"))
    	{
    		return true;
    	}
    	if(tip1.get(0).equals("char")&tip2.get(0).equals("int"))
    	{
    		return true;
    	}
    	if(tip1.get(0).equals("niz")&tip1.get(1).equals("int")&tip2.get(0).equals("niz")&tip2.get(1).equals("const_int"))
    	{
    		return true;
    	}
    	if(tip1.get(0).equals("niz")&tip1.get(1).equals("char")&tip2.get(0).equals("niz")&tip2.get(1).equals("const_char"))
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

    private String pretraziTabZnakova(){
        //TODO
        return "";
    }

    private String provjeriTrenCvZnakova(){
        //TODO
        return "";
    }

    private boolean imaMain(){
        //TODO
        return true;
    }

    private boolean provjeriFun(){
        //TODO
        return true;
    }

    private void provjeri(){
        //TODO
    }
}
