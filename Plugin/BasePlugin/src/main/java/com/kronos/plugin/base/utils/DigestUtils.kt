package com.kronos.plugin.base.utils

import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils.getMd5Digest
import java.nio.charset.Charset
import kotlin.text.Charsets.UTF_8

/**
 * @Author LiABao
 * @Since 2021/7/31
 */
object DigestUtils {

    fun md5Hex(data: String?): String {
        return Hex.encodeHexString(md5(data)) ?: ""
    }

    fun md5(data: String?): ByteArray? {
        return md5(data?.getBytes())
    }

    fun md5(data: ByteArray?): ByteArray? {
        return getMd5Digest().digest(data)
    }

    private fun String.getBytes(charset: Charset = UTF_8): ByteArray = this.toByteArray(charset)

}