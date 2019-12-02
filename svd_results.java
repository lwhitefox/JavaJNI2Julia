
/**
 * Simple class to store and organize results from SVD.
 *
 */

public class svd_results{
  double[][] _u;
  double[] _s;
  double[][] _vt;

  /**
   *  Default constructors sets all arrays to null
   */
  public svd_results(){
    _u = null;
    _s = null;
    _vt = null;
  }

  /**
   * Constructor when results are all provided in full 2D matrix form for U and Vt.
   */
  public svd_results(double[][] _u, double[] _s, double[][] _vt) {
    this._u = _u;
    this._s = _s;
    this._vt = _vt;
  }

  /**
   * Constructor when results are all provided as 1D arrays.  Sets up 
   * complete 2D matrix forms given the provided dimensions.
   */
  public svd_results(double[] u1d, int u_nrows, int u_ncols,
                     double[] s,
                     double[] vt1d, int vt_nrows, int vt_ncols) {
    double[][] u = new double[u_nrows][u_ncols];
    double[][] vt = new double[vt_nrows][vt_ncols];
    
    for(int i=0;i<u_nrows;i++){
      for(int j=0;j<u_ncols;j++){
        u[i][j] = u1d[i + j * u_nrows];
      }
    }
    for(int i=0;i<vt_nrows;i++){
      for(int j=0;j<vt_ncols;j++){
        vt[i][j] = vt1d[i + j * vt_nrows];
      }
    }

    this._u = u;
    this._s = s;
    this._vt = vt;
  }


  public double[][] get_u() {
    return _u;
  }

  public void set_u(double[][] _u) {
    this._u = _u;
  }

  public double[] get_s() {
    return _s;
  }

  public void set_s(double[] _s) {
    this._s = _s;
  }

  public double[][] get_vt() {
    return _vt;
  }

  public void set_vt(double[][] _vt) {
    this._vt = _vt;
  }

}

