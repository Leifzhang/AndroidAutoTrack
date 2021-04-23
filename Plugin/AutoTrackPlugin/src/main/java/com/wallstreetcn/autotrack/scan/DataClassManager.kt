package com.wallstreetcn.autotrack.scan

/**
 * @Author LiABao
 * @Since 2021/4/22
 */
class DataClassManager private constructor() {

    val datList = mutableListOf<String>()

    companion object {
        val INSTANCE = DataClassManager()
    }
}
