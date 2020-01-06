package Chapter10;

import net.jcip.annotations.GuardedBy;

import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

public class Taxi {
    @GuardedBy("this")
    private Point location;
    @GuardedBy("this")
    private Point destination;
    private final Dispatcher dispatcher;

    public Taxi(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public synchronized Point getLocation() {
        return location;
    }

    public void setLocation(Point location) {
        boolean reachedDestination;

        synchronized (this) {
            this.location = location;
            reachedDestination = location.equals(destination);
        }

        if (reachedDestination) {
            dispatcher.notifyAvailable(this);
        }
    }
}

class Dispatcher {
    @GuardedBy("this")
    private final Set<Taxi> taxis;
    @GuardedBy("this")
    private final Set<Taxi> availableTaxis;

    public Dispatcher() {
        this.taxis = new HashSet<>();
        this.availableTaxis = new HashSet<>();
    }

    public synchronized void notifyAvailable(Taxi taxi) {
        availableTaxis.add(taxi);
    }

    public Image getImage() {
        Set<Taxi> copy;
        synchronized (this) {
            copy = new HashSet<>(taxis);
        }

        Image image = new Image();
        copy.forEach(t -> image.drawMarker(t.getLocation()));
        return image;
    }

    static class Image {
        void drawMarker(Point location) {
            /*
            ...
            */
        }
    }
}
