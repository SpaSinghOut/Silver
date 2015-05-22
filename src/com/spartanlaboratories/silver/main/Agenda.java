package com.spartanlaboratories.silver.main;

import java.util.ArrayList;

public class Agenda extends ArrayList<Action>{
	public void tick(){
		if(get(0).tick())remove(get(0));
	}
}
