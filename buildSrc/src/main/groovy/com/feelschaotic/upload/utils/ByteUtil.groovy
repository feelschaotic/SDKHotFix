package com.feelschaotic.upload.utils

class ByteUtil {

    static def bytesToFile(byte[] bfile, String filePath, String fileName) {
        BufferedOutputStream bos = null
        FileOutputStream fos = null
        File file = null
        try {
            File dir = new File(filePath)
            if (!dir.exists() && dir.isDirectory()) {//判断文件目录是否存在
                dir.mkdirs()
            }
            file = new File(filePath + "\\" + fileName)
            fos = new FileOutputStream(file)
            bos = new BufferedOutputStream(fos)
            bos.write(bfile)
        } catch (Exception e) {
            e.printStackTrace()
        } finally {
            if (bos != null) {
                bos.close()
            }
            if (fos != null) {
                fos.close()
            }
        }
    }

    static byte[] fileToBytes(filePath) {
        byte[] buffer = null
        try {
            File file = new File(filePath)
            FileInputStream fis = new FileInputStream(file)
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000)
            byte[] b = new byte[1024]
            int n
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n)
            }
            fis.close()
            bos.close()
            buffer = bos.toByteArray()
        } catch (FileNotFoundException e) {
            e.printStackTrace()
        } catch (IOException e) {
            e.printStackTrace()
        }
        return buffer
    }
}