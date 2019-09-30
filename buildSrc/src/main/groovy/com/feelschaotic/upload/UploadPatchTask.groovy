package com.feelschaotic.upload

import com.alibaba.fastjson.JSONObject
import com.feelschaotic.crypto.crypto.EncryptManager
import com.feelschaotic.upload.utils.EncryptUtil
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class UploadPatchTask extends DefaultTask {
    def rootDir = project.rootDir.path
    def patchName = "patch.jar"
    def patchPath

    def byteUtil = new com.feelschaotic.upload.utils.ByteUtil()
    def uploadService = new UploadService()

    def ossConfig
    def config

    @TaskAction
    def upload() {
        init()
        checkPatch()
    }

    def init() {
        /*发布脚本和测试脚本的唯一区别：常量配置的取值不同*/
        def buildConfigUtil = new com.feelschaotic.upload.utils.BuildConfigUtil(project)
        ossConfig = new OssConfig()
        ossConfig.accessKeyId = buildConfigUtil.getReleaseBuildType("ACCESS_ID")
        ossConfig.buckName = buildConfigUtil.getReleaseBuildType("BUCK_NAME")
        ossConfig.accessKeySecret = buildConfigUtil.getReleaseBuildType("SECRET_KEY")
        ossConfig.endpoint = buildConfigUtil.getReleaseBuildType("END_POINT")

        config = new Config()
        config.serverUrl = buildConfigUtil.getReleaseBuildType("HOTFIX_SERVER_URL") + "create"
        config.applicationId = buildConfigUtil.getReleaseBuildType("BMOB_APPLICATION_ID")
        config.restApiKey = buildConfigUtil.getReleaseBuildType("REST_API_KEY")

        patchPath = rootDir + File.separator + patchName
    }

    def checkPatch() {
        def file = new File(patchPath)
        if (!file.exists()) {
            println '--【警告】本地补丁文件不存在:' + patchPath
            return
        }
        encryptPatch()
    }

    def encryptPatch() {
        def clientAesKey = new EncryptUtil().getRandomString(16)
        println "--clientAesKey: ${clientAesKey}"

        byte[] encryptBytes = EncryptManager.getInstance().encryptByAes(byteUtil.fileToBytes(patchPath), clientAesKey.getBytes())
        byteUtil.bytesToFile(encryptBytes, rootDir, patchName)
        def encryptAesKey = EncryptManager.getInstance().encryptByRsaPublicKey(clientAesKey.getBytes())
        uploadPatchToAliYun(new String(Base64.encoder.encode(encryptAesKey)))
    }

    def uploadPatchToAliYun(String encryptKey) {
        def file = new File(patchPath)
        uploadService.uploadPatchToAliYun(ossConfig, file, new OnResponseListener<String>() {
            @Override
            def onSuccess(String objectName) {
                updateServerPatchInfo(objectName, file.size(), encryptKey)
            }

            @Override
            def onError(Exception e) {
                println "--【警告】上传补丁到阿里云失败，错误信息如下："
                e.printStackTrace()
            }
        })
    }

    /**
     * 更新补丁信息到后端服务
     */
    def updateServerPatchInfo(String objectName, long fileSize, String encryptKey) {

        uploadService.updatePatchInfo(config
                , createPatch(objectName, fileSize, encryptKey)
                , new OnResponseListener<String>() {
            @Override
            def onSuccess(String response) {
                JSONObject jsonObj = JSONObject.parse(response)
                if (jsonObj.getJSONObject("result").get("code") != 0) {
                    println "--【警告】上传补丁信息到服务端失败,response如下:"
                    println response
                    return
                }
                println "--上传补丁信息到服务端成功,response如下:"
                println response
            }

            @Override
            def onError(Exception e) {
                println "--【警告】上传补丁信息到服务端失败,失败信息如下:"
                println e.message
            }
        })
    }

    Patch createPatch(String objectName, long fileSize, String encryptKey) {
        def patch = new Patch(project)
        patch.bundleurl = objectName
        patch.size = fileSize
        patch.pKey = encryptKey
        println "--AesKeyBase64: ${encryptKey}"
        return patch
    }
}