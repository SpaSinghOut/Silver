package com.spartanlaboratories.silver.main;

import com.spartanlaboratories.engine.game.Actor;
import com.spartanlaboratories.engine.structure.Engine;

public class FinancialUnit extends Actor{
	protected double silver;
	protected Inventory inventory = new Inventory();
	public FinancialUnit(Engine engine){
		super(engine);
	}
	public void transferMoney(FinancialUnit to, double amount){
		silver -= amount;
		to.silver += amount;
	}
	public void transferItem(FinancialUnit to, Item item){
		inventory.removeItems(item, 1);
		to.inventory.addItems(item, 1);
	}
}
