package guinovart.joaquim.lafosca_beach.Utilities;

import java.util.Comparator;

import guinovart.joaquim.lafosca_beach.Models.Kid;

/**
 * Created by perecullera on 23/9/15.
 */
public class KidComparator implements Comparator<Kid> {

    @Override
    public int compare(Kid kid1, Kid kid2) {
        return (kid1.age > kid2.age ? 1 : (kid1.age == kid2.age ? 0 : -1));
    }
}
