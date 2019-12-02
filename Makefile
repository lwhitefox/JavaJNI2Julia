
# START BLOCK ################################################################
## set values in this block for your local setup, compiler, OS, etc.:
CC = clang
JAVA_HOME = /Library/Java/JavaVirtualMachines/jdk1.8.0_202.jdk/Contents/Home
JAVAC = javac
AR = $(CC)

INC = -I$(JAVA_HOME)/include \
	-I$(JAVA_HOME)/include/darwin
LIBDIR = -L.
# END BLOCK ##################################################################


LIBSO = -ljava_julia_svd

## comment/uncomment the next two as desired
#OPT = -O3 -DDEBUG
OPT = -O3 


LIBPREFIX = lib
LIBSUFFIX = dylib


CFLAGS = $(INC) $(OPT)
LDFLAGS = $(OPT) -dynamiclib


## these lines are from Julia documentation - automatically set params for 
## embedding julia in c code
JL_SHARE = $(shell julia -e 'print(joinpath(Sys.BINDIR, Base.DATAROOTDIR, "julia"))')
CFLAGS   += $(shell $(JL_SHARE)/julia-config.jl --cflags)
CXXFLAGS += $(shell $(JL_SHARE)/julia-config.jl --cflags)
LDFLAGS  += $(shell $(JL_SHARE)/julia-config.jl --ldflags)
LDLIBS   += $(shell $(JL_SHARE)/julia-config.jl --ldlibs) 


# below we defined compile targets etc.:

COBJS = java_julia_svd.o svd_fcns.o


libsvd:	$(COBJS)
	$(AR) -o $(LIBPREFIX)java_julia_svd.$(LIBSUFFIX) $(LDFLAGS) $(LIBDIR) $(LDLIBS) $(COBJS)


java_julia_svd: libsvd 
	$(JAVAC) $(JAVAFFLAGS) java_julia_svd.java svd_results.java


clean:
	rm *.o *$(LIBSUFFIX) *.class



