package core;

import GUI.CanvasRenderer;

import java.util.ArrayList;

/**
 * A path contains a list of segments
 * and displacement along the path
 **/

public class Path {

    public static double TANGENT_COEFFICIENT = 0.8;

    ArrayList<Segment> segments;
    double totalDisplacement = 0;

    public Path() {
        segments = new ArrayList<>();
    }

    public Path(ArrayList<Vector2D> points) {
        segments = new ArrayList<>();
        if (points.size() == 0) return;

//        Segment a = new Segment();
//        a.calculateXCoeffs(points.get(0).x, 0.0, 0.0, points.get(1).x, 0.0, 0.0); // TODO First & Second order derivatives?
//        a.calculateYCoeffs(points.get(0).y, 0.0, 0.0, points.get(1).y, 0.0, 0.0);
//        double len = a.getLength();
//        a.startLen = totalDisplacement;
//        a.endLen = totalDisplacement + len;
//        addSegment(a);
//        totalDisplacement += len;

        for (int i = 0; i < points.size() - 1; i++) {
            Vector2D currentPoint = points.get(i);
            Vector2D nextPoint = points.get(i + 1);
            Vector2D nextNextPoint = null;
            if (i != points.size() - 2) {
                nextNextPoint = points.get(i + 2);
            }

            // Compute the end tangent based on these three points.
            Vector2D computedTangent = null;
            if (nextNextPoint != null) {
                computedTangent = firstDerivativeHeuristic(currentPoint, nextPoint, nextNextPoint);
//                System.out.println(currentPoint.x + " " + currentPoint.y);
//                System.out.println(nextPoint.x + " " + nextPoint.y);
//                System.out.println(nextNextPoint.x + " " + nextNextPoint.y);
                //System.out.println(computedTangent.x + " " + computedTangent.y);
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

            //if (i == 2) {
            //  println(t + " " + a.getSlopeAt(0.999) + " " + a.x.getFirstDerivative().eval(0.999) + " " + a.y.getFirstDerivative().eval(0.999));
            //}
        }
    }

    private Vector2D firstDerivativeHeuristic(Vector2D A, Vector2D B, Vector2D C) {
        double slopeOfPerpendicularToBisector = -1 / Utilities.getSlopeOfAngleBisector(A, B, C);
        System.out.println(slopeOfPerpendicularToBisector);
        double magnitude = TANGENT_COEFFICIENT * Math.min(Vector2D.dist(B, A), Vector2D.dist(B, C));
        Vector2D directionVector = new Vector2D(1, slopeOfPerpendicularToBisector).normalize();
        Vector2D point = directionVector.mult(magnitude);
        double alpha = Math.atan2(point.y, point.x);
        return new Vector2D(magnitude * Math.cos(alpha), magnitude * Math.sin(alpha));
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