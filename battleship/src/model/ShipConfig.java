package model;

public class ShipConfig {

	private String name;
	private Integer size;
	private Integer num; //quante navi dello stesso tipo devo posizionare
	
	public ShipConfig(String name, Integer size, Integer num) {
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
	
	
}
