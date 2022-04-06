#ifndef SCRFD_H
#define SCRFD_H

#include "layer.h"
#include "net.h"

#if defined(USE_NCNN_SIMPLEOCV)
#include "simpleocv.h"
#else
#include <opencv2/core/core.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#endif
#include <float.h>
#include <stdio.h>
#include <vector>
#include <iostream>

#define YOLOV5_V60 0 //YOLOv5 v6.0

using namespace std;
using namespace ncnn;

struct Object
{
    cv::Rect_<float> rect;
    int label;
    float prob;
};

class Yolov5{
public:
    Yolov5();
    ~Yolov5();
private:
    float intersection_area(const Object& a, const Object& b);
    void qsort_descent_inplace(std::vector<Object>& faceobjects, int left, int right);
    void qsort_descent_inplace(std::vector<Object>& faceobjects);
    void nms_sorted_bboxes(const std::vector<Object>& faceobjects, std::vector<int>& picked, float nms_threshold);
    void generate_proposals(const ncnn::Mat& anchors, int stride, const ncnn::Mat& in_pad, const ncnn::Mat& feat_blob, float prob_threshold, std::vector<Object>& objects);
    float sigmoid(float x);

public:
    int detect_yolov5(const cv::Mat& image, std::vector<Object> &objects);
    int draw_objects(cv::Mat& image, const std::vector<Object>& objects);
    int init_model(const char* param_file, const char* bin_file);
    int load(AAssetManager* mgr, const char* modeltype, bool use_gpu = false);

private:
    ncnn::Net m_yolov5;

    bool has_kps;
    const int m_target_size = 640;
    const float m_prob_threshold = 0.25f;
    const float m_nms_threshold = 0.45f;
};
#endif //NDKCAMERA_H