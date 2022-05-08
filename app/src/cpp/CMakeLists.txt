cmake_minimum_required(VERSION 3.0)
project(OMPEval)
set(CMAKE_CXX_STANDARD 11)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -pthread")
set(SOURCE_FILES ompeval.cpp benchmark.cpp omp/CardRange.cpp omp/CardRange.h omp/CombinedRange.cpp omp/CombinedRange.h omp/Constants.h omp/EquityCalculator.cpp omp/Hand.h omp/HandEvaluator.cpp omp/HandEvaluator.h omp/Util.h libdivide/libdivide.h)
#add_executable (ompeval  ${SOURCE_FILES})



add_library(
        # Specifies the name of the library.
        ompeval

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        ${SOURCE_FILES})

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
        ompeval

        # Links the target library to the log library
        # included in the NDK.
        ${log-lib} )