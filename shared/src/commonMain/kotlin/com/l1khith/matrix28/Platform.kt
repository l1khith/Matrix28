package com.l1khith.matrix28

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform