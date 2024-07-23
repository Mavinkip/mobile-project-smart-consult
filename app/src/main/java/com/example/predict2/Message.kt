package com.example.predict2

import java.util.Date

data class Message(
    var senderId: String = "",
    var receiverId: String = "",
    var text: String = "",
    var timestamp: Long = Date().time  // Current time in milliseconds as Long
)
