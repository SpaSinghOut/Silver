package com.spartanlaboratories.silver.main;

import com.spartanlaboratories.engine.game.VisibleObject;
import com.spartanlaboratories.engine.structure.Engine;
import com.spartanlaboratories.engine.structure.Util;

public class Tree extends VisibleObject{
	Inventory fruit;
	public Tree(Engine engine){
		super(engine);
		setHeight(50);
		setWidth(50);
		color = Util.Color.YELLOW;
		setLocation(Math.random() * 500, Math.random() * 500);
		setTexture("png", "res/apple tree.png");
	}
	public void pickFruit(Person person){
		person.inventory.addItems(Item.APPLE, 1);
	}
}
