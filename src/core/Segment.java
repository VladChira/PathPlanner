package core;

import Jama.Matrix;

public class Segment {

  /**
   * A segment is made up of two parametrized quintic polynomials
   * and a heading interpolator
   */

  double startLen = 0, endLen = 0;
  double len = -1;
  double firstTangentX = 0, secondTangentX = 0;
  double firstTangentY = 0, secondTangentY = 0;

  QuinticPolynomial x, y;
  double[][] coeffsArray = {{0, 0, 0, 0, 0, 1}, {0, 0, 0, 0, 1, 0}, {0, 0, 0, 2, 0, 0}, {1, 1, 1, 1, 1, 1}, {5, 4, 3, 2, 1, 0}, {20, 12, 6, 2, 0, 0}};
  Matrix coeffsMatrix = new Matrix(coeffsArray);

  public Segment() {
    x = new QuinticPolynomial();
    y = new QuinticPolynomial();
  }

  public double getLength() {
    if (len == -1) len = getDisplacementAt(1);
    return len;
  }

  public double getDisplacementAt(double t) {
    // calculate integral from 0 to t of sqrt( (dx/dtau)^2 + (dy/dtau)^2 ) dtau
    QuinticPolynomial xDeriv = x.getFirstDerivative();
    QuinticPolynomial yDeriv = y.getFirstDerivative();
    return integrate(xDeriv, yDeriv, 0, t);
  }
  
  public double getSlopeAt(double t) {
    return getFirstDerivativePointAt(t).y / getFirstDerivativePointAt(t).x;
  }
  
  public Vector2D getFirstDerivativePointAt(double t) {
    return new Vector2D (x.getFirstDerivative().eval(t), y.getFirstDerivative().eval(t));
  }
  
  public Vector2D getSecondDerivativePointAt(double t) {
    return new Vector2D (x.getSecondDerivative().eval(t), y.getSecondDerivative().eval(t));
  }

  public Vector2D getPointAt(double t) {
    return new Vector2D(x.eval(t), y.eval(t));
  }

  private double f(QuinticPolynomial xDeriv, QuinticPolynomial yDeriv, double tau) {
    // sqrt( (dx/dtau)^2 + (dy/dtau)^2 )
    return Math.sqrt(xDeriv.eval(tau) * xDeriv.eval(tau) + yDeriv.eval(tau) * yDeriv.eval(tau));
  }

  private double integrate(QuinticPolynomial xDeriv, QuinticPolynomial yDeriv, double a, double b) {
    int N = 10000;                    // precision parameter
    double h = (b - a) / (N - 1);     // step size

    // 1/3 terms
    double sum = 1.0 / 3.0 * (f(xDeriv, yDeriv, a) + f(xDeriv, yDeriv, b));

    // 4/3 terms
    for (int i = 1; i < N - 1; i += 2) {
      double x = a + h * i;
      sum += 4.0 / 3.0 * f(xDeriv, yDeriv, x);
    }

    // 2/3 terms
    for (int i = 2; i < N - 1; i += 2) {
      double x = a + h * i;
      sum += 2.0 / 3.0 * f(xDeriv, yDeriv, x);
    }

    return sum * h;
  }

  public void calculateXCoeffs(double x, double x1, double x2, double xn, double xn1, double xn2) {
    double[] freeTerms = {x, x1, x2, xn, xn1, xn2};
    Matrix freeTermsVector = new Matrix(freeTerms, 6);
    Matrix ans = coeffsMatrix.solve(freeTermsVector);
    double [] coeffs = ans.getRowPackedCopy();
    this.x.setCoeffs(coeffs);
    
    this.firstTangentX = xn1;
    this.secondTangentX = xn2;
  }
  
  public void calculateYCoeffs(double y, double y1, double y2, double yn, double yn1, double yn2) {
    double[] freeTerms = {y, y1, y2, yn, yn1, yn2};
    Matrix freeTermsVector = new Matrix(freeTerms, 6);
    Matrix ans = coeffsMatrix.solve(freeTermsVector);
    double [] coeffs = ans.getRowPackedCopy();
    this.y.setCoeffs(coeffs);
    
    this.firstTangentY = yn1;
    this.secondTangentY = yn2;
  }
}