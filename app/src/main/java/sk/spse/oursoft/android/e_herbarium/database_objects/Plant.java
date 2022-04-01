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

    public Plant(String id, String name, String description, String author) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.author = author;
    }

}
