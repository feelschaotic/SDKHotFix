# SDKHotFix（已封装为库，5分钟即可接入使用）

在用户规模千万级别的app中验证过，稳定、无兼容问题，满足你的SDK快速拥有热修复能力的需求。

## 一、介绍

相信 APP 热修复大家都很熟练了，那如果 SDK 想要实现热修复呢？

这就是本项目诞生的背景，让 SDK 开发者能快速赋予 SDK 热修复的能力，不要踩我踩过的坑。（捂脸哭）

#### 优点

1. 无兼容问题
2. 实时生效
3. 成功率最高
4. 在用户规模千万级别的app中验证过，可信赖

#### 待改进
1. 在编译阶段插件侵入了产品代码，对运行效率、方法数、包体积还是产生了一些副作用。（可指定某些class无需插入方法，减少插桩数）
2. so和资源的替换目前暂未实现
3. 无法新增变量与类，只能新增方法 

## 二、使用方法

## 三、原理

## 四、鸣谢