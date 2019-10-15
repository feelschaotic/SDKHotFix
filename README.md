# SDKHotFix（5分钟即可接入）

[![Download](https://img.shields.io/badge/SDKHotFix-version%3A1.2-green)](https://github.com/feelschaotic/SDKHotFix/releases)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](https://github.com/feelschaotic/SDKHotFix/pulls)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://raw.githubusercontent.com/Meituan-Dianping/Robust/master/LICENSE)  

在用户规模千万级别的app中验证过，稳定、无兼容问题，满足你的SDK快速拥有热修复能力的需求。

---
## 目录

- 一、介绍
  * 作用
  * 优点
  * 待改进
- 二、接入指南
- 三、使用指南和演示
- 四、原理
- 五、相关文章
- 六、鸣谢
    * 改进说明
    
[查看演示 Demo](https://github.com/feelschaotic/SDKHotFix/tree/master/business_sdk) | [文档](https://github.com/feelschaotic/SDKHotFix/wiki)


## 一、介绍

相信 APP 热修复大家都很熟练了，那如果 SDK 想要实现热修复呢？

这就是本项目诞生的背景，让 SDK 开发者能快速赋予 SDK 热修复的能力，不要踩我踩过的坑。（捂脸哭）

### 作用

如果你是 SDK 开发者，你能得到什么？
- 5 分钟即可让你的 SDK 拥有热修复的能力
- 节省踩坑和开发成本

如果你是一个学习者，你能通过这个项目学到什么？
- hook and create Gradle Task
- Kotlin + groovy
- 热修复
- 非对称加密+对称加密
- 扩展美团 Robust 开源库，使其具有其他热更库不具备的补丁回滚功能
- 数据和监控思维

### 优点

1. 无兼容问题
2. 实时生效
3. 修复成功率高达99.9%
4. 在用户规模千万级别的app中验证过，可信赖
5. 支持补丁异常自动失效，不用再担心补丁造成crash啦~

### 待改进

1. 在编译阶段插件侵入了产品代码，对运行效率、方法数、包体积还是产生了一些副作用。（可指定某些class无需插入方法，减少插桩数）
2. so和资源的替换目前暂未实现 
3. 对于只有字段访问的函数无法直接修复，可通过调用处间接修复
4. 尚未在MAC环境下测试
5. 上传的GradleTask支持补丁按包名存档

## 二、接入指南 

点击[SDK热修复SDK接入文档](https://github.com/feelschaotic/SDKHotFix/wiki/SDK%E7%83%AD%E4%BF%AE%E5%A4%8DSDK%E6%8E%A5%E5%85%A5%E6%96%87%E6%A1%A3)，开始接入吧！

关于常见问题的解决，请参看 [Wiki](https://github.com/feelschaotic/SDKHotFix/wiki)

## 三、使用指南和演示

- [SDK热修复使用文档](https://github.com/feelschaotic/SDKHotFix/wiki/SDK%E7%83%AD%E4%BF%AE%E5%A4%8D%E4%BD%BF%E7%94%A8%E6%96%87%E6%A1%A3)
> 用于接入后SDK的打包、补丁代码的编写、下发、上传。

- [SDK热修复使用常见问题](https://github.com/feelschaotic/SDKHotFix/wiki/%E5%B8%B8%E8%A7%81%E9%97%AE%E9%A2%98%E4%B8%8E%E5%BC%82%E5%B8%B8%E8%87%AA%E6%9F%A5)
> 常见问题和异常汇总，遇到问题前请先自查

![](https://github.com/feelschaotic/SDKHotFix/blob/master/gif/%E6%BC%94%E7%A4%BAdemo%E5%B9%B6%E7%BC%96%E5%86%99%E8%A1%A5%E4%B8%81.gif)

## 四、原理

- [如何5分钟让你的 SDK 拥有热修复能力（原理篇）](https://juejin.im/post/5da546b35188254796426ae3)

## 五、相关文章

- [APP 热修复都懂了，你会 SDK 热修复吗？最全方案在这里！](https://juejin.im/post/5d299aaae51d45105e021367)

## 六、鸣谢

灵感来源于oubowu大大的 [RobustForSdk](https://github.com/oubowu/RobustForSdk)，感谢！

#### 改进说明
相比于 RobustForSdk 库，本库：
1. 稳定性提升：
- fix 若干 bug
- fix 与业务方的资源 ID 冲突问题
2. 便捷性提高：
- 使用 gardle Task 脚本上传补丁，比起 Window 程序更方便
- 抽取成库，业务 SDK 只需要最小步骤即可快速接入
3. 支持度提高：
- 使用了阿里云 oss 管理补丁 
- 支持了打包 SDK 后上传 jcenter



