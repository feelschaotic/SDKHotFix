package com.feelschaotic.upload

import com.alibaba.fastjson.annotation.JSONField
import org.gradle.api.Project

class Patch {
    @JSONField(name = "bundlename")
    public def bundlename

    @JSONField(name = "packagename")
    public def packagename

    @JSONField(name = "version")
    public def version

    @JSONField(name = "bundledescribe")
    public def bundledescribe

    @JSONField(name = "fromDate")
    public def fromDate = 1520904373

    @JSONField(name = "toDate")
    public def toDate = 1585953200

    @JSONField(name = "fromSystem")
    public def fromSystem = "8.0"

    @JSONField(name = "bundleurl")
    public def bundleurl

    @JSONField(name = "platform")
    public def platform = "android"

    @JSONField(name = "size")
    public def size

    @JSONField(name = "pKey")
    public def pKey

    Patch(Project project) {
        version = project.properties.get("patchVersion")
        bundlename = project.properties.get("sdkPackageName") + "_" + version
        packagename = bundlename
        bundledescribe = project.properties.get("patchDesc")
        //转码
        bundledescribe = new String(bundledescribe.getBytes("ISO8859-1"), "utf-8")
    }
}