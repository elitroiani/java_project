package model;

public class ShipConfig {

	private final String name;
	private final int size;
	private final int num; //quante navi dello stesso tipo devo posizionare
	
	public ShipConfig(String name, int size, int num) {
		if (size <= 0 || num <= 0) {
            throw new IllegalArgumentException("Size and number must be positive");
        }
		this.name = name;
		this.size = size;
		this.num = num;
	}

	public String getName() {
		return this.name;
	}

	public Integer getSize() {
		return this.size;
	}

	public Integer getNum() {
		return this.num;
	}
	
	@Override
    public String toString() {
        return name + " (size=" + size + ", num=" + num + ")";
    }
}
