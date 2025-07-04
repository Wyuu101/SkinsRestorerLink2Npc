# SkinsRestorerLink2Npc
基于SkinsRestorerAPI开发的皮肤商城NPC命令链接器

## 一、背景、原理与用途
Citizens中NPC设置的指令在后端服务器执行时无法被工作在代理模式下的SkinsRestorer识别。
本插件直接监听后端服务器命令执行事件，并通过调用skinsrestorer内部的API实现Citizens与SkinsRestorer无缝衔接。


## 二、版本与依赖
- 依赖：SkinsRestorer
- 运行环境：Jre21
- 使用版本: 1.8x-1.21x



## 三、命令用法 
![image](https://github.com/user-attachments/assets/30f5cb7d-a9a5-4544-a88b-6a3c3cec4670)


## 四、权限说明
- 所有命令仅管理员与终端可用
- `dxzskin update <player>`命令执行时会先检查目标玩家是否有`zbverify.skin`权限，如果无，则会引导玩家使用zbverify插件进行正版验证。

## 五、使用示例

```
/npc creare test
/npc cmdadd -c dxzskins update %player_name%
```

**注意**: /npc cmdadd 命令需要安装CommandNPC扩展插件


