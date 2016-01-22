package traffic_sim;

import java.awt.*;

/**
 * Created by hagbard on 1/18/16.
 */
public interface Drawable {
    void redraw(Graphics2D g2d);
    Integer priority();
}
