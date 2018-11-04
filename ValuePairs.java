import java.util.LinkedList;

public class ValuePairs<T,K> {
	private LinkedList<Pair<T,K>> list;
	
	public ValuePairs() {
		list = new LinkedList<>();
	}
	
	public void addPair(Pair<T, K> p) {
		this.list.add(p);
	}
	
	public void remove(Pair<T, K> p) {
		if(this.list.contains(p))
			this.list.remove(p);
	}
	
	public boolean containsPair(Pair<T, K> p) {
		return this.list.contains(p);
	}
	
	public boolean containsLeft(T left) {
		for(Pair<T,K> p: list) {
			if(p.getLeft().equals(left))
				return true;
		}
		return false;
	}
	
	public boolean containsRight(K right) {
		for(Pair<T,K> p: list) {
			if(p.getRight().equals(right))
				return true;
		}
		return false;
	}
	
	public T getLeft(K right) {
		for(Pair<T,K> p: list) {
			if(p.getRight().equals(right)) {
				return p.getLeft();
			}
		}
		return null;
	}
	
	public K getRight(T left) {
		for(Pair<T,K> p: list) {
			if(p.getLeft().equals(left)) {
				return p.getRight();
			}
		}
		return null;
	}
	
	public void add(T left, K right) {
		Pair<T,K> p = new Pair<>(left, right);
		this.list.add(p);
	}
	
	public void updateRight(T left, K right) {
		for(Pair<T,K> p: list) {
			if(p.getLeft().equals(left))
				p.setRight(right);
		}
	}
}
