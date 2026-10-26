# 第三方来源与许可

## Twist Space Technology

- 原作者：Nxer 与 Twist Space Technology contributors。
- 项目：<https://github.com/Nxer/Twist-Space-Technology-Mod>。
- 核对版本：`0.8.0-RC2.6` 的 sources JAR 及用户指定本地源码。
- 原文件：`src/main/java/com/Nxer/TwistSpaceTechnology/common/machine/TST_NetherInterface.java`。
- 衍生文件：`src/main/java/com/day/betterxeperience/common/machine/BetterTSTNetherInterface.java`，包括原结构数组与处理算法。
- 修改：改用 BGTE 基类与注册，原生 GT Tooltip、本项目语言键、边界算术与输入提交顺序；保留原结构、配置、产物与抽样规则。
- 上游许可为 GNU General Public License version 3；许可原文见 <https://www.gnu.org/licenses/gpl-3.0.txt>，上游仓库 `LICENSE` 与运行依赖 JAR 内的 `LICENSE` 亦提供全文。

移植文件按 GPL-3.0 发布，而不是按仓库的 MIT 占位模板发布。分发包含该衍生代码的组合程序须遵守 GPLv3，包括提供对应源码、许可全文与保留声明；发布前需统一项目发行许可，不能仅附 MIT 模板。该代码不提供任何担保。

本项目通过依赖引用 TST 原方块、纹理、随机产物池与展示配方，不复制其资源文件。

## GT-Not-Leisure

只读参考 `common/machine/multiMachineBase/MultiMachineBase.java` 的职责组织，不复制其实现、不添加 GTNL 依赖。其许可为 LGPLv3；本项目基类直接使用 GT5U 公共 API。
