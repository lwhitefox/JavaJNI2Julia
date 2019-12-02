

#include <stdio.h>
#include <julia.h>
#include "svd_fcns.h"



#define DATA(i,j)( *(data + (j)*nrows + (i)*1) )
#define INDATA(i,j)( *(indata + (j)*nrows + (i)*1) )


// copied from julia/test/embedded/embedding.c
JULIA_DEFINE_FAST_TLS() // only define this once, in an executable


// copied from julia/test/embedded/embedding.c
jl_value_t *checked_eval_string(const char* code)
{
  jl_value_t *result = jl_eval_string(code);
  if (jl_exception_occurred()) {
    // none of these allocate, so a gc-root (JL_GC_PUSH) is not necessary
    jl_call2(jl_get_function(jl_base_module, "showerror"),
	     jl_stderr_obj(),
	     jl_exception_occurred());
    jl_printf(jl_stderr_stream(), "\n");
    jl_atexit_hook(1);
    exit(1);
  }
  assert(result && "Missing return value but no exception occurred!");
  return result;
}


// not really necessary, but may create more readable code for newbies in
// calling C code
void start_julia()
{
  jl_init();
}


// not really necessary, but may create more readable code for newbies in
// calling C code
void stop_julia()
{
  jl_atexit_hook(0);
}


/** apply SVD to input matrix, returning a struct with the svd_results
 *
 * input params:
 *
 * in_matrix pointer to double array with the matrix components. in col major order
 * nrows number of rows in input matrix
 * ncols nubmer of columns in input matrix
 *
 */
svd_results julia_svd(double *in_matrix, int nrows, int ncols)
{
  svd_results the_svd;  // function populates this struct and returns it
  
  if(!jl_is_initialized())
    start_julia();

  checked_eval_string("include(\"svd_tasks.jl\")");

  // create 2D array for Float64 in Julia
  jl_value_t *array_type = jl_apply_array_type((jl_value_t*)jl_float64_type, 2);
  jl_array_t *jdata  = jl_alloc_array_2d(array_type, nrows, ncols);

  double *data = (double *)jl_array_data(jdata);
    
  for(int i=0;i<nrows;i++){
    for(int j=0;j<ncols;j++){
      DATA(i,j) = in_matrix[j*nrows+i];
#ifdef DEBUG
	fprintf(stdout,"D[%d,%d]=%f\t",i,j,DATA(i,j));
#endif
    }
#ifdef DEBUG
      fprintf(stdout,"\n");
#endif
  }

  JL_GC_PUSH1(&jdata);


  jl_function_t *func = jl_get_function(jl_main_module, "apply_svd");
  jl_call1(func, (jl_value_t*)jdata);

  jl_array_t* jl_svd = (jl_array_t*)jl_call1(func, (jl_value_t*)jdata);
  
  func = jl_get_function(jl_main_module, "get_svd_s");
  jl_array_t* jl_svd_s = (jl_array_t*)jl_call1(func, (jl_value_t*)jl_svd);
  the_svd.s = (double *)jl_array_data(jl_svd_s);
  the_svd.n_singvals = jl_array_len(jl_svd_s);

  func = jl_get_function(jl_main_module, "get_svd_u");
  jl_array_t* jl_svd_u = (jl_array_t*)jl_call1(func, (jl_value_t*)jl_svd);
  the_svd.u = (double *)jl_array_data(jl_svd_u);
  the_svd.u_nrows = (int)(jl_array_dim(jl_svd_u,0));
  the_svd.u_ncols = (int)(jl_array_dim(jl_svd_u,1));

  func = jl_get_function(jl_main_module, "get_svd_vt");
  jl_array_t* jl_svd_vt = (jl_array_t*)jl_call1(func, (jl_value_t*)jl_svd);
  the_svd.vt = (double *)jl_array_data(jl_svd_vt);
  the_svd.vt_nrows = (int)(jl_array_dim(jl_svd_vt,0));
  the_svd.vt_ncols = (int)(jl_array_dim(jl_svd_vt,1));

  JL_GC_POP();

  return(the_svd);
  
}



/* int main(int argc, char **argv) */
/* { */
/*   double *indata = (double *)malloc(12*sizeof(double)); */
/*   for(int i=0;i<4;i++){ */
/*     for(int j=0;j<3;j++){ */
/*       INDATA(i,j) = j+i*3 + 1; */
/*     } */
/*   } */

/*   svd_results my_svd = julia_svd(indata,4,3); */

/*   fprintf(stdout,"Matrix U\n"); */
/*   for(int i=0;i<4;i++){ */
/*     for(int j=0;j<3;j++){ */
/*       fprintf(stdout,"%f\t",*(my_svd.u + (j)*4 + (i))); */
/*     } */
/*     fprintf(stdout,"\n"); */
/*   } */

/*   for(int i=0;i<3;i++) */
/*     { */
/*       fprintf(stdout,"s[%d]=%f\n",i,my_svd.s[i]); */
/*     } */

/*   fprintf(stdout,"Matrix Vt\n"); */
/*   for(int i=0;i<3;i++){ */
/*     for(int j=0;j<3;j++){ */
/*       fprintf(stdout,"%f\t",*(my_svd.vt + (j)*3 + (i))); */
/*     } */
/*     fprintf(stdout,"\n"); */
/*   } */

/*   stop_julia(); */

/*   return 0; */

/* } */
