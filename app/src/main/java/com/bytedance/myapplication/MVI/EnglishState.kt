package com.bytedance.myapplication.MVI
import android.net.Uri
import com.bytedance.myapplication.data.database.EnglishEntity

data class EnglishState(
//    图片地址
    val capturedImageUri: Uri? = null,
    val voiceUri: String? = "",
//    英文单词和
    val recognizedWord: String = "",
//    汉语
    val recognizedCn : String = "",
    val isGeneratingAudio: Boolean = false,
    val isAnalyzing: Boolean = false,
    val isCameraOpen: Boolean = false,
//    存入单词图片后返回的行序号
    val currentRecordId : Long = -1L,
//    用于加载历史数据
    val historyList: List<EnglishEntity> = emptyList(), // 历史数据
    val isShowingHistory: Boolean = false,              // 是否显示历史页面
)
