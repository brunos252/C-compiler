import java.util.HashSet;
import java.util.LinkedList;

import valuePair.ValuePairs;


/*
 * Samo ima dodanu listu (ostalo je sve e_nka) u koju spremamo stringove koji predstavljaju akcije
 */
public class Action_E_NKA extends E_NKA{
	private String name;
	private LinkedList<String> action;

	public Action_E_NKA(LinkedList<String> entry, HashSet<String> allStates, HashSet<String> alphabet,
			HashSet<String> acceptableStates, String firstState, ValuePairs<String, String> function,
			String name, LinkedList<String> action) {
		super(entry, allStates, alphabet, acceptableStates, firstState, function);
		
		this.name=name;
		this.action=action;
	}
	
	
	public String getName() {
		return this.name;
	}
	
	public LinkedList<String> getAction() {
		return this.action;
	}
}
