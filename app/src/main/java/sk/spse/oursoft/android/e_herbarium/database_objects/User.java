package sk.spse.oursoft.android.e_herbarium.database_objects;

public class User {
    public String email;
    public String herbarium;
    //this is temp so I don't forget hte passwords delete later

    public String password;

    public User(String email, String herbarium,String password) {
        this.email = email;
        this.herbarium = herbarium;
        this.password = password;
    }

}


