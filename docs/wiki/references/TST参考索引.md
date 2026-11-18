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
- 同一 Java 根目录下的 `recipe/craftRecipe/machine/GTCMMachineRecipes.java`：TST 0.8.2 的 `Nether Interface` 段已注释控制器组装机配方。本项目沿用该配方的材料、传送器回退顺序、LuV 电压与 360 秒耗时，仅替换产物；原增强版装配线配方停用并保留为注释。

本项目两台 TST 增强机器的主机说明存于自身 `assets/betterexperience/lang/{zh_CN,en_US}/tooltip/{nether-interface,photonic-quantum-master}.md`，不覆盖或调用 TST Tooltip 语言键。地狱接口配置及光量子掌控者模式常量作为 Markdown 变量传入，结构／状态使用本项目语言键；英文地狱接口旧拼写 `betterxeperience.machine.nether_interface.*` 已对齐 Java 使用的 `betterexperience`。

两台机器统一使用 GT5U 原生 `MultiblockTooltipBuilder.addMarkdown` 与结构 API，光量子掌控者不再引用 `TSTMultiblockTooltipBuilder`、`TSTSharedLocalization` 或 `TSTUtils`。结构字符串与运行逻辑保持不变，结构页补全固定外壳数量及维护仓位置，提示编号仍为维护 1、物料 2、能源 3。API 按本地 GT5U `5.09.54.265` sources JAR 核对，未重新解析依赖；格式及静态检查不代表游戏内显示与结构投影已验收。

## 修订记录

- 2026-11-15：按用户决定迁移两台增强机器的中英 Markdown，替代原先复用 TST 语言键的约定。此前地狱接口复用 `machine_type`、`info.01`–`info.03` 和蓝图提示；光量子掌控者复用 TST 专用 Builder 与共享结构文本，该方式不再生效。
