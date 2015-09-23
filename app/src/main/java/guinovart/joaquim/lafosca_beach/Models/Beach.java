package guinovart.joaquim.lafosca_beach.Models;

import java.util.ArrayList;

/**
 * Created by perecullera on 22/9/15.
 */
public class Beach {
    public String state
            ;
    public int flag;
    public int happiness;
    public int dirtiness;
    public ArrayList<Kid> kids;

    public Beach(){

    }
    //Contructor only used when Beach is closed
    public Beach(String close) {
        this.state = "closed";
    }
}
