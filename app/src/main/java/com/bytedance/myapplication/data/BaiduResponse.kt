package com.bytedance.myapplication.data

data class BaiduResponse(
    val result: List<BaiduResult>,
    val result_num: Int,
    val log_id: String
)

data class BaiduResult(
    val score: Double,
    val root: String,
    val keyword: String
)