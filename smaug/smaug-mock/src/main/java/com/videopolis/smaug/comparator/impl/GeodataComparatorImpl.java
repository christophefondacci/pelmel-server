package com.videopolis.smaug.comparator.impl;

import java.util.Comparator;

import com.tripvisit.model.bean.geodata.Geodata;

/**
 * A comparator which compares distance between geodata objects
 * 
 * @author julien
 * 
 */
public class GeodataComparatorImpl implements Comparator<Geodata> {

    @Override
    public int compare(Geodata o1, Geodata o2) {
	return o1.getDistance().compareTo(o2.getDistance());
    }

}
