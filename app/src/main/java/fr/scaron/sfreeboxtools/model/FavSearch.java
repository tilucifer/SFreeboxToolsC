package fr.scaron.sfreeboxtools.model;

import java.io.Serializable;

public class FavSearch implements Serializable{


    private static final long serialVersionUID = -7454477060085227995L;
    private String name;
    private int index;



	public FavSearch(int index, String name){
        setIndex(index);
        setName(name);
	}


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
