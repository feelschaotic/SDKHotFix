package com.feelschaotic.sdkhotfix.sdk.utils

import java.io.*

/**
 * @author feelschaotic
 * @create 2019/7/5.
 */
object ByteUtil {

    @Throws(Exception::class)
    fun bytesToFile(bfile: ByteArray, filePath: String, fileName: String) {
        var bos: BufferedOutputStream? = null
        var fos: FileOutputStream? = null
        try {
            val dir = File(filePath)
            if (!dir.exists()) {//判断文件目录是否存在
                dir.mkdirs()
            }
            val file = File(filePath + File.separator + fileName)
            fos = FileOutputStream(file)
            bos = BufferedOutputStream(fos)
            bos.write(bfile)
        } finally {
            bos?.close()
            fos?.close()
        }
    }

    @Throws(Exception::class)
    fun fileToBytes(filePath: String): ByteArray? {
        val buffer: ByteArray?
        var fis: FileInputStream? = null
        var bos: ByteArrayOutputStream? = null
        try {
            val file = File(filePath)
            fis = FileInputStream(file)
            bos = ByteArrayOutputStream(1000)
            val b = ByteArray(1024)
            var n = 0
            do {
                n = fis.read(b)
                if (n != -1) {
                    bos.write(b, 0, n)
                } else {
                    break
                }
            } while (true)
        } finally {
            fis?.close()
            bos?.close()
            buffer = bos?.toByteArray()
        }
        return buffer
    }
}