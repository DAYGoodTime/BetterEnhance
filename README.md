<h1 align="center">BetterGTExperience</h1>
<p align="center"><strong><em>GTNH 非官方扩展模组</em></strong></p>

<p align="center">
  <img alt="Minecraft 1.7.10" src="https://img.shields.io/badge/Minecraft-1.7.10-blue">
  <img alt="Forge 10.13.4.1614" src="https://img.shields.io/badge/Forge-10.13.4.1614-blue">
  <a href="https://github.com/DAYGoodTime/BetterEnhance/releases"><img alt="GitHub Releases" src="https://img.shields.io/badge/GitHub-Releases-green"></a>
</p>

扩展更好用的GT机器

## 下载与版本需求

- [GitHub Releases](https://github.com/DAYGoodTime/BetterEnhance/releases)
- **开发基准：** GTNH 2.9.0（Minecraft 1.7.10 / Forge 10.13.4.1614）。其他 GTNH 版本未验证。
- **基础依赖：** GregTech、GTNHLib、StructureLib；ME 相关功能使用整合包内的 Applied Energistics 2。
- **合成材料：** 单元毁灭者系列的制造配方会用到 BuildCraft Factory 和 Iron Tanks 的储罐。缺少对应储罐时，该档制造配方不会注册。
- **可选联动：** 两台增强机器只在安装 [Twist Space Technology（TST）](https://github.com/Nxer/Twist-Space-Technology-Mod) 时加载。不装 TST 不影响其余机器。

## 内容简介

### 蒸汽多方块与仓室

**蒸汽大型电解机**

使用多方块电解机的无单元配方。青铜结构基础 **16 并行**，钢结构基础 **32 并行、1.5 倍速度**。默认可处理 MV 及以下配方；在控制器槽放入不锈钢齿轮即可处理 HV 配方，齿轮不会消耗。

可在界面手动设置 0–4 次超频，每次将每 tick 耗汽量乘以 4、处理时间减半。支持输入分离、批处理，也可用配置了普通蒸汽的 ME 输入仓直接供汽。

**大型蒸汽仓**

存放 **4,096,000 L** 普通蒸汽，可用于蒸汽大型电解机。

### 单方块机器

- **过胶机（青铜蒸汽、高压蒸汽、LV–UEV）：** 使用线缆绝缘配方。基础处理时间减半，每 tick 耗电或耗汽为原来的 75%。
- **单元毁灭者电解机（LV–EV）：** 在单方块里运行多方块电解机的无单元配方。提供 1 个流体输入槽和 6 个独立流体输出槽，产物可以直接用管道抽走。
- **单元毁灭者搅拌机（青铜蒸汽、高压蒸汽、LV–EV）：** 使用多方块搅拌机的无单元配方。提供 9 个物品输入槽、9 个物品输出槽，以及各 6 个独立的流体输入、输出槽。青铜蒸汽版可处理 LV 配方，高压蒸汽版可处理 MV 配方。

### ME 输出

**ME输出总成**

用高级胶水把 ME 输出总线和 ME 输出仓粘在一起。物品与流体共用一个 ME 节点，但各自保留缓存和设置。组装时继承原仓口的对应基础容量，之后也可用物品或流体存储组件分别调整容量。

### TST 联动

以下是独立的控制器，不替换 TST 原机。两台机器均支持普通能源仓、多安能源仓和激光仓。

- **地狱接口（增强版）：** 保留原机结构，用蒸馏水运行，随机取得地狱资源；并行上限沿用 TST 配置。
- **精密高能光量子掌控者（增强版）：** 可在光子掌控者和激光蚀刻机模式间切换。背面的光量子增幅器可以叠加加速；使用同级增幅器时，并行倍率随等级翻倍，混装则没有并行加成。

## 致谢

- [Twist Space Technology](https://github.com/Nxer/Twist-Space-Technology-Mod) — 感谢 Nxer 与 TST 贡献者，两台联动机器源自该项目。
- [GT: Not Leisure](https://github.com/ABKQPO/GT-Not-Leisure) 与 [GT-Steam-Reborn](https://github.com/MIAOKATZE/GT-Steam-Reborn) — 内容展示与 README 写法参考；两者都不是本模组的依赖。

[开发文档](docs/README.md) · [第三方来源与许可](THIRD_PARTY_NOTICES.md)
