package com.bytedance.myapplication.MVI

// 密封类：统一管理所有“聊天页面的副作用”
/*Sealed Class（密封类）*/
sealed class ChatEffect {
    // 数据类子类：具体的副作用指令——显示Toast提示
    /*ShowToast 继承自 ChatEffect 类。括号 () 表示调用了父类 ChatEffect 的主构造函数（即使父类没有参数，也需要加上）。*/
    data class ShowToast(val message: String) : ChatEffect()
}