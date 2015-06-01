package com.spartanlaboratories.silver.main;

import java.util.ArrayList;
import java.util.HashMap;

public class Inventory {
	private HashMap<Item, Integer> items = new HashMap<Item, Integer>();
	public Inventory(){
		for(Item i: Item.values())
			items.put(i, 0);
	}
	public Inventory(Inventory inventory){
		this();
		for(Item i: Item.values())
			if (inventory.items.containsKey(i))
				items.put(i, inventory.items.get(i));
	}
	public void addItems(Item item, int amount){
		if(amount < 0)throw new IllegalArgumentException();
		items.put(item, items.get(item) + amount);
	}
	public int removeItems(Item item, int amount){
		if(amount < 0)throw new IllegalArgumentException();
		if(amount < items.get(item)){
			items.put(item, items.get(item) - amount);
			return amount;
		}
		amount = items.get(item);
		items.put(item, 0);
		return amount;
	}
	public boolean hasItem(Item item){
		return items.get(item) > 0;
	}
	public ArrayList<Item> getFoodList(){
		ArrayList<Item> food = new ArrayList<Item>();
		for(Item i:Item.values())
			if(i.hungerSatisfaction > 0 && hasItem(i))
				food.add(i);
		return food;
	}
	public int getNumberOfItems(Item item){
		return items.get(item);
	}
}
