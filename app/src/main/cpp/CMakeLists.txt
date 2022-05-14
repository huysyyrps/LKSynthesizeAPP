#project(scrfdncnn)
#
#cmake_minimum_required(VERSION 3.18.1)
#
#set(OpenCV_DIR ${CMAKE_SOURCE_DIR}/opencv-mobile-4.5.4-android/sdk/native/jni)
#find_package(OpenCV REQUIRED core imgproc)
#
#set(ncnn_DIR ${CMAKE_SOURCE_DIR}/ncnn-20211208-android-vulkan/${ANDROID_ABI}/lib/cmake/ncnn)
#find_package(ncnn REQUIRED)
#
#add_library(scrfdncnn SHARED scrfdncnn.cpp scrfd.cpp ndkcamera.cpp CallJava.cpp)
#
#target_link_libraries(scrfdncnn ncnn ${OpenCV_LIBS} camera2ndk mediandk)


# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.18.1)

# Declares and names the project.

project("rtmtscreenlive")

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_subdirectory(librtmp)
#add_subdirectory(opencv-mobile-4.5.4-android)
#add_subdirectory(ncnn-20211208-android-vulkan)

add_library(native-lib SHARED native-lib.cpp)

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
        native-lib
        ${log-lib}
        rtmp)