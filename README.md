# SDKHotFix（5分钟即可接入）

[![Download](https://img.shields.io/github/package-json/v/feelschaotic/SDKHotFix/master)]()
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](https://github.com/feelschaotic/SDKHotFix/pulls)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://raw.githubusercontent.com/Meituan-Dianping/Robust/master/LICENSE)  

在用户规模千万级别的app中验证过，稳定、无兼容问题，满足你的SDK快速拥有热修复能力的需求。

---

- 一、介绍
  * 作用
  * 优点
  * 待改进
- 二、接入指南
- 三、使用指南和演示
- 四、原理]
- 五、相关文章
- 六、鸣谢
    * 改进说明
    
[查看演示 Demo]() | [文档](https://github.com/feelschaotic/SDKHotFix/wiki)


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

### 待改进

1. 在编译阶段插件侵入了产品代码，对运行效率、方法数、包体积还是产生了一些副作用。（可指定某些class无需插入方法，减少插桩数）
2. so和资源的替换目前暂未实现 
3. 对于只有字段访问的函数无法直接修复，可通过调用处间接修复

## 二、接入指南 

点击[SDK热修复SDK接入文档]()，开始接入吧！

关于常见问题的解决，请参看 [Wiki](https://github.com/feelschaotic/SDKHotFix/wiki)

## 三、使用指南和演示
//TODO:gif

## 四、原理

见[SDK热修复原理文档]()

## 五、相关文章

- [APP 热修复都懂了，你会 SDK 热修复吗？最全方案在这里！](https://juejin.im/post/5d299aaae51d45105e021367)

## 六、鸣谢

灵感来源于oubowu大大的[RobustForSdk](https://github.com/oubowu/RobustForSdk)，感谢！

#### 改进说明
相比于 RobustForSdk 库，本库：
1. 修复了若干bug，更稳定
2. 使用了 gardle Task 脚本上传补丁，比起Window程序更方便
3. 使用了阿里云管理补丁
4. 解决了application方式打包，与业务方的资源冲突问题
5. 抽取成库，业务SDK只需要最小步骤即可快速接入
6. 支持了打包SDK后上传jcenter



