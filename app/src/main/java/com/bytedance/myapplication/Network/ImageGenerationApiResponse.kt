package com.bytedance.myapplication.Network

data class ImageGenerationApiResponse(
    val model: String,
    val created: Long,
    val data: List<ImageData>,
    val usage: ImageUsage
)

data class ImageData(
    val url: String,
    val size: String
)

data class ImageUsage(
    val generated_images: Int,
    val output_tokens: Int,
    val total_tokens: Int
)
