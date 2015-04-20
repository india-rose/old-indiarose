package org.indiarose.indiarosetimebar.canvas.horloge;

public enum TypeHorloge {
	CERCLE ("Cercle"),
	BARRE_VERTICALE ("Barre Verticale"),
	BARRE_HORIZONTALE ("Barre Horizontale");
	
	public static TypeHorloge[] types = new TypeHorloge[]{
		CERCLE,
		BARRE_VERTICALE,
		BARRE_HORIZONTALE
	};
	
	public static String[] typesNames = new String[]{
		CERCLE.getName(),
		BARRE_VERTICALE.getName(),
		BARRE_HORIZONTALE.getName()
	};

	private String name = "";

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	

	TypeHorloge(String name){
		this.name = name;
	}
}
