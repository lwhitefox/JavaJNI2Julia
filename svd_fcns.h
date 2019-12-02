typedef struct
{
  double *u, *s, *vt;
  int u_ncols, u_nrows;
  int vt_ncols, vt_nrows;
  int n_singvals;
} svd_results;

void start_julia();
void stop_julia();
svd_results julia_svd(double *in_matrix, int nrows, int ncols);
