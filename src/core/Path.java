package core;

import java.util.ArrayList;

/**
 * A path contains a list of segments
 * and displacement along the path
 **/

public class Path {

    public static double TANGENT_COEFFICIENT = 0.5;

    private ArrayList<Segment> segments;
    private double totalDisplacement = 0;

    public Path() {
        segments = new ArrayList<>();
    }

    public Path(ArrayList<Vector2D> points) {
        segments = new ArrayList<>();
        if (points.size() == 0) return;

        for (int i = 0; i < points.size() - 1; i++) {
            Vector2D currentPoint = points.get(i);
            Vector2D nextPoint = points.get(i + 1);
            Vector2D nextNextPoint = null;
            if (i != points.size() - 2) {
                nextNextPoint = points.get(i + 2);
            }

            // Compute the end tangent based on these three points.
            Vector2D computedTangent;
            if (nextNextPoint != null) {
                computedTangent = firstDerivativeHeuristic(currentPoint, nextPoint, nextNextPoint);
            } else computedTangent = new Vector2D(0.0, 0.0);

            Segment prevSegment = new Segment();
            if (i == 0) {
                prevSegment.firstTangentX = 0.0;
                prevSegment.secondTangentX = 0.0;
                prevSegment.firstTangentY = 0.0;
                prevSegment.secondTangentY = 0.0;
            } else {
                prevSegment = segments.get(segments.size() - 1);
            }


            Segment a = new Segment();
            a.calculateXCoeffs(currentPoint.x, prevSegment.firstTangentX, prevSegment.secondTangentX, nextPoint.x, computedTangent.x, 0.0);
            a.calculateYCoeffs(currentPoint.y, prevSegment.firstTangentY, prevSegment.secondTangentY, nextPoint.y, computedTangent.y, 0.0);
            double len = a.getLength();
            a.startLen = totalDisplacement;
            a.endLen = totalDisplacement + len;
            addSegment(a);
            totalDisplacement += len;
        }
    }

    private Vector2D firstDerivativeHeuristic(Vector2D A, Vector2D B, Vector2D C) {
        double slopeOfPerpendicularToBisector = -1 / Utilities.getSlopeOfAngleBisector(A, B, C);
        double magnitude = TANGENT_COEFFICIENT * Math.min(Vector2D.dist(B, A), Vector2D.dist(B, C));

        // Orient the tangent vector towards the third point. So dumb it actually works
        double b = -slopeOfPerpendicularToBisector * B.x + B.y;

        Vector2D u = (new Vector2D(0, b).sub(B)).normalize();

        Vector2D truePoint = B.copy().add(u.copy().mult(magnitude));
        Vector2D truePointReflected = B.copy().sub(u.copy().mult(magnitude));

        Vector2D directionVector = new Vector2D(1, slopeOfPerpendicularToBisector).normalize();

        Vector2D point = directionVector.copy().mult(magnitude);
        Vector2D pointReflected = directionVector.copy().mult(-magnitude);

        if (Vector2D.dist(C, truePoint) < Vector2D.dist(C, truePointReflected)) point = pointReflected.copy();

        double alpha = Math.atan2(point.y, point.x);
        return new Vector2D(magnitude * Math.cos(alpha), magnitude * Math.sin(alpha));
    }

    private Vector2D secondDerivativeHeuristic(Vector2D A, Vector2D B, Vector2D C) {
        return null;
    }

    public double getCurvaturePointAtDisplacement(double displacement) {
        Vector2D deriv = getFirstDerivativePointAtDisplacement(displacement);
        Vector2D doubleDeriv = getSecondDerivativePointAtDisplacement(displacement);
        return Vector2D.absCross(deriv, doubleDeriv) / Math.pow(deriv.mag(), 3);
    }

    public Vector2D getFirstDerivativePointAtDisplacement(double displacement) {
        for (Segment s : segments) {
            if (displacement >= s.startLen && displacement <= s.endLen) {
                // we are in this segment
                // find the corresponding t
                double sLen = s.getLength();
                double relativeDisplacement = displacement - s.startLen;
                double t = relativeDisplacement / sLen;
                return s.getFirstDerivativePointAt(t);
            }
        }
        return null;
    }

    public Vector2D getSecondDerivativePointAtDisplacement(double displacement) {
        for (Segment s : segments) {
            if (displacement >= s.startLen && displacement <= s.endLen) {
                // we are in this segment
                // find the corresponding t
                double sLen = s.getLength();
                double relativeDisplacement = displacement - s.startLen;
                double t = relativeDisplacement / sLen;
                return s.getSecondDerivativePointAt(t);
            }
        }
        return null;
    }

    public Vector2D getPointAtDisplacement(double displacement) {
        // we need to know in which segment we are with the displacement
        for (Segment s : segments) {
            if (displacement >= s.startLen && displacement <= s.endLen) {
                // we are in this segment
                // find the corresponding t
                double sLen = s.getLength();
                double relativeDisplacement = displacement - s.startLen;
                double t = relativeDisplacement / sLen;
                return s.getPointAt(t);
            }
        }

        return null;
    }

    public double getTotalDisplacement() {
        return totalDisplacement;
    }

    // It is up to the user to make sure the continuities are okay
    public void addSegment(Segment s) {
        segments.add(s);
    }

    public void clear() {
        segments.clear();
    }

    public ArrayList<Vector2D> getPoints() {
        if (segments.size() == 0 || totalDisplacement == 0) return null;

        ArrayList<Vector2D> pathPoints = new ArrayList<>();

        for (double s = 0.0; s <= totalDisplacement; s += 1) {
            Vector2D point = getPointAtDisplacement(s);
            pathPoints.add(point);
        }
        return pathPoints;
    }
}