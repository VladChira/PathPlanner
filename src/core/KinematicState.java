package core;

/**
 * An object that completely (not really, but assuming constant jerk is good enough) describes
 * a kinematic state in time
 **/


public class KinematicState {

    double x = 0, v = 0, a = 0, j = 0;

    public KinematicState() {

    }

    public KinematicState(double x, double v, double a, double j) {
        this.x = x;
        this.v = v;
        this.a = a;
        this.j = j;
    }


    /**
     * Returns the future core.KinematicState based on the
     * current values of x, v, a and j according to the laws of motion
     **/
    public KinematicState getFutureState(double t) {
        double newPosition = v * t + 0.5 * a * t * t + 1.0 / 6.0 * j * t * t * t;
        double newVelocity = v + a * t + 0.5 * j * t * t;
        double newAcceleration = a + j * t;
        return new KinematicState(newPosition, newVelocity, newAcceleration, j);
    }

}