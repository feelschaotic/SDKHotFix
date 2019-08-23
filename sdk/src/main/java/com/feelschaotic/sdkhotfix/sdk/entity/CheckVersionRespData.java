package com.feelschaotic.sdkhotfix.sdk.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.feelschaotic.sdkhotfix.sdk.HotfixManager;

import java.io.File;

/**
 * 由于后端没有遵守驼峰，所以只能这么命名
 * @author feelschaotic
 * @create 2019/6/21.
 */

public class CheckVersionRespData {

    //补丁描述
    @JSONField(name = "bundledescribe")
    public String bundledescribe;
    //补丁名
    @JSONField(name = "bundlename")
    public String bundlename;
    //补丁下载 url
    @JSONField(name = "bundleurl")
    public String bundleurl;

    @JSONField(name = "bundleversion")
    public int bundleversion;

    @JSONField(name = "createtime")
    public String createtime;

    @JSONField(name = "fromDate")
    public String fromDate;

    @JSONField(name = "fromSystem")
    public String fromSystem;

    @JSONField(name = "pKey")
    public String pKey;

    @JSONField(name = "packagename")
    public String packagename;

    @JSONField(name = "platform")
    public String platform;

    @JSONField(name = "size")
    public long size;

    @JSONField(name = "toDate")
    public String toDate;

    @JSONField(name = "patchPath")
    public String patchPath;

    /**
     * 进行版本比对
     */
    public boolean needDownloadPatch(CheckVersionRespData localPatchInfo) {
        return (localPatchInfo == null
                || localPatchInfo.bundleversion != bundleversion
                || !fileIsExists(localPatchInfo.patchPath)
                || localPatchInfo.size != size) && bundleurl != null && !bundleurl.isEmpty();
    }

    private boolean fileIsExists(String patchPath) {
        return patchPath != null && !patchPath.isEmpty() && new File(patchPath).exists();
    }

    public boolean patchCanLoad() {
        int index = packagename.lastIndexOf("_");
        return patchPath != null && !patchPath.isEmpty()
                && HotfixManager.config.getAppVersion().equals(packagename.substring(index + 1, packagename.length()))
                && HotfixManager.config.getPackageName().equals(packagename.substring(0, index));
    }
}
