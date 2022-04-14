package sk.spse.oursoft.android.e_herbarium.database_objects;

public class Plant {
    /*"id": "123456789",
            "name": "meno",
            "description": "popis",
            "author": "autor",
            "image": "obrázok"
         */
    public String id;
    public String name;
    public String description;
    public String author;

    public Plant(){

    }
    public Plant(String name, String description, String author) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.author = author;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getAuthor() {
        return author;
    }
    public void setId(String id){
        this.id = id;

    }
}
