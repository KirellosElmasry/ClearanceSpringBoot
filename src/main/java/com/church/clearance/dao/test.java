package com.church.clearance.dao;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class test {

	public static void main(String[] args) {

		List<Integer> list = Arrays.asList(7, 3, 2, 7, 1, 0, 1);
		CopyOnWriteArrayList<Integer> parcels = new CopyOnWriteArrayList(list);
		System.out.println(list);
		
		int numOfDays = 0;		

		while (true) {
			//remove all zeros
			parcels.removeIf(e -> e == 0);
			//substract smaller num from all elements
			parcels.replaceAll(x -> {return x - Collections.min(parcels);});
			System.out.println(parcels);
			
			numOfDays++;
			//stop loop if all array become zeros
			if (Collections.max(parcels) == 0)
				break;
		}
		System.out.println(numOfDays);
	}
}
