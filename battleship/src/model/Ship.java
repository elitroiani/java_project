
public class Ship {

	//private String name;
	private int size;
	private int hits;
	private List<Point> positions = new ArrayList<>();
	
	public Ship(int size, List<Point> positions) {
		this.size = size;
		this.positions = positions;
	}
	
	
}
