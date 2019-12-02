
/*
 * Tips for setting up JNI, for beginners like me.....
 *
 *  steps: 
 * (1) write line for native call            
 * (2) compile to create class (javac java_julia_svd)
 * (3) generate include file stubs: javah -jni java_julia_svd
 *
 * --------> better: combine (2) and (3) as javac -h . java_julia_svd.java
 *
 * tip: use javap -s -p  to get class method signatures and private members
 *     note: for inner member classes, use $ e.g., outerclass$innerclass
 *            but on command line need to escape $, "javap -s -p outerclass\$innerclass"
 * 
 * see https://web.mit.edu/javadev/doc/tutorial/native1.1/implementing/index.html
 * 
 * LOTS OF GOOD EXAMPLES:
 * more thorough, up to date: https://www3.ntu.edu.sg/home/ehchua/programming/java/JavaNativeInterface.html
 */        



class java_julia_svd {

  private double[] _inMatrix;
  private int _ncols, _nrows;

  static {
    System.loadLibrary("java_julia_svd");
  }
  

  /**
   * default constructor 
   * 
   * @param somedata input data array set up as a 1D array, in column order
   *
   */
  java_julia_svd(){
    _inMatrix = null ;
  }


  /**
   * constructor for a data array
   * 
   * @param somedata input data array set up as a 1D array, in column order
   *
   */
  java_julia_svd(double[] somedata, int nrows, int ncols){
    _inMatrix = somedata ;
    _nrows = nrows;
    _ncols = ncols;
  }


  private native svd_results get_svd(double[] inMatrix, int nrows, int ncols);
  

  private native double[] get_singularvals(double[] inMatrix, int nrows, int ncols);


  public double[] getDataMatrix(){
    return _inMatrix ;
  }

  public int get_nrows(){
    return _nrows;
  }

  public int get_ncols(){
    return _ncols;
  }


  public double[] get_julia_singularvals(){
    double[] s_array = null;

    if(_inMatrix != null)
      s_array = get_singularvals(_inMatrix,_nrows,_ncols);
    else
      throw new RuntimeException(this.getClass().getCanonicalName()
                                 + ".run_svd - ERROR: calling get_singularvalues with null data array");

    return s_array;
  }


  public svd_results get_julia_svd(){
    svd_results the_results = null;
    if(_inMatrix != null)
      the_results = get_svd(_inMatrix,_nrows,_ncols);
    else
      throw new RuntimeException(this.getClass().getCanonicalName()
                                 + ".run_svd - ERROR: calling get_svd with null data array");

    return the_results;
  }

  
  /** 
   * Calls svd function returning only singular values, and the 
   * function returning complete svd results.        
   *
   */
  public static void main(String[] args){

    // data vector must provide all matrix components in col major order
    double[] data = {1., 4, 7, 10, 2, 5, 8, 11, 3, 6, 9, 12};
    int nrows = 4;
    int ncols = 3;

    java_julia_svd mysvder = new java_julia_svd(data,nrows,ncols);

    double[] s_array = mysvder.get_julia_singularvals();

    System.out.println("\n\nCheck singular values in java:");
    for(int i=0;i<s_array.length;i++){
      System.out.println("s[" + i + "]=" + s_array[i]);
    }

    System.out.println("\n\n\nTest complete results:");
    //    svd_results myRes = new svd_results();
    svd_results my_results = mysvder.get_julia_svd();

    System.out.println("Check U:");
    double[][] Umatrix = my_results.get_u();
    for(int i=0;i<Umatrix.length;i++){
      for(int j=0;j<Umatrix[0].length;j++){
        System.out.print(Umatrix[i][j]+"\t");
      }
      System.out.println("");
    }

    System.out.println("\nSingular values:");
    double[] Sarray = my_results.get_s();
    for(int i=0;i<Sarray.length;i++)
      System.out.println(Sarray[i]);

    System.out.println("\nCheck Vt:");
    double[][] Vtmatrix = my_results.get_vt();
    for(int i=0;i<Vtmatrix.length;i++){
      for(int j=0;j<Vtmatrix[0].length;j++){
        System.out.print(Vtmatrix[i][j]+"\t");
      }
      System.out.println("");
    }

  }

}
