package sk.spse.oursoft.android.e_herbarium.database_objects;

import android.util.Log;

import java.util.ArrayList;

public class Group {
    public ArrayList<Plant> plants;
    public String groupName;

    public Group(String groupName){
        this.groupName = groupName;
        this.plants = new ArrayList<>();
    }
    public void addPlant(Plant plant){
        Log.i("Plants", String.valueOf(plants));
        this.plants.add(plant);
    }

    public ArrayList<Plant> getPlants() {
        return plants;
    }
}
