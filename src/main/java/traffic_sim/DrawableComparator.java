package traffic_sim;

import java.util.Comparator;

/**
 * Created by hagbard on 1/19/16.
 */
public class DrawableComparator implements Comparator<Drawable> {
    @Override
    public int compare(Drawable d1, Drawable d2) {
        return d1.priority().compareTo(d2.priority());
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }
}
