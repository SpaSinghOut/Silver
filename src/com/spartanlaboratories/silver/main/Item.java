package com.spartanlaboratories.silver.main;

public enum Item {
	APPLE(5),;
	int hungerSatisfaction;
	private Item(int hungerSatisfaction){
		this.hungerSatisfaction = hungerSatisfaction;
	}
}
