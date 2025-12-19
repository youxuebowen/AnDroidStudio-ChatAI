package com.bytedance.myapplication.Network

data class ImageGenerationApiRequest(
    val model: String,
    val prompt: String,
    val size: String = "2K",
    val watermark: Boolean = false
)
