public class Pair<T,K> {
	
	private T left;
	private K right;
	
	public Pair(T left,K right){
		this.setLeft(left);
		this.setRight(right);
	}

	public T getLeft() {
		return left;
	}

	public void setLeft(T left) {
		this.left = left;
	}

	public K getRight() {
		return right;
	}

	public void setRight(K right) {
		this.right = right;
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Pair))
			return false;
		Pair p = (Pair) o;
		return this.left.equals(p.getLeft()) && this.right.equals(p.getRight());
	}
	
	@Override
	public int hashCode() {
		return left.hashCode() ^ right.hashCode();
	}
}
