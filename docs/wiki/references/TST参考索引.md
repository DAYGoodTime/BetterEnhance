# Twist Space Technology 参考索引

> 创建日期: 2026-10-26
> 作者: DAYGood_Time
> 状态: 长期维护

## 仓库与使用边界

- 本地只读参考：`D:\DEV\mcmod\Twist-Space-Technology-Mod`。
- 远程仓库：[Nxer/Twist-Space-Technology-Mod](https://github.com/Nxer/Twist-Space-Technology-Mod)。本地不可访问时使用远程源码，并核对版本。
- 本项目将 TST 作为可选运行集成，开发 classpath 仍保留 TST；本地 Maven 坐标与加载条件见[项目知识库](../development/项目知识库.md#技术与依赖)。本地或远程工作区不代表已解析依赖，类、方法和语言键均以实际依赖 JAR 与对应源码为准。
- 不修改参考仓库、不运行其构建，不自动复用机器 ID、注册名或确定新的平衡数值。复制实现前核对许可证与署名要求，当前移植边界见[第三方声明](../../../THIRD_PARTY_NOTICES.md)。

## 地狱接口

以下路径均相对 TST 仓库根目录：

- `src/main/java/com/Nxer/TwistSpaceTechnology/common/machine/TST_NetherInterface.java`：原始结构、运行处理及 `createTooltip()`。
- `src/main/java/com/Nxer/TwistSpaceTechnology/util/text/TSTSharedLocalization.java`：通用结构与蓝图提示键。
- `src/main/resources/assets/gtnhcommunitymod/lang/{zh_CN,en_US}.lang`：中英文原始语言键。
- `src/main/java/com/Nxer/TwistSpaceTechnology/config/Config.java`：运行配置入口。

本项目地狱接口直接使用 `tst.common.machine.NetherInterface.tooltip.machine_type`、`info.01` 至 `info.03`，以及 `tst.common.shared.structure.text_use_blueprint`；不在自身语言文件中复制或覆盖这些键。原版说明之后追加本项目命名空间的配置、特性和结构提示。原版耗电说明固定为 IV，实际耗电以追加的当前配置公式为准；仓口提示保留实际提示编号和替换要求。

保留 GT5U 原生 `MultiblockTooltipBuilder`，不为复用文本引入 TST 的 Tooltip Builder、基类或署名机制。语言键已与实际依赖核对；格式及静态检查不代表游戏内显示与结构投影已验收。
