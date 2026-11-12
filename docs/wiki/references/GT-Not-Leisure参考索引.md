# GT-Not-Leisure 本地代码参考索引

> 创建日期: 2026-09-30 12:35
> 作者: DAYGood_Time
> 状态: 长期维护

## 参考项目与使用约定

用户指定只读仓库 `D:\DEV\mcmod\GT-Not-Leisure`（GTNL）。

*如果本地仓库不存在，则读取远程仓库* : https://github.com/DAYGoodTime/GT-Not-Leisure

**功能开发前先查相关实现**，覆盖机器、仓口、配方、流体、GUI、同步、NBT 与兼容；纯文档／格式整理不要求查机器实现。它不是本项目源码、子模块或依赖，不自动复制内容、复用 ID／数值、添加依赖或运行其构建。

## 大项目的按需检索流程

1. 按功能主题从下表定位，内容检索限定相关子目录／`*.java`，资源限定 `R`；排除 `.git/build/run/temp/bin`，不整库通读或建全量镜像。
2. 阅读具体方法并追父类、工具、调用、注册和资源，不能只凭类名照搬；对照本项目准确依赖，区分 GTNL 扩展与上游 API。
3. 实现前说明参考路径、可复用思路及版本差异；无对应实现则说明范围与结果再继续。实现后将采用入口与版本写入 Wiki，命令／结果写入验证记录，不借用 GTNL 验收结论。

## 源码导航

下表路径均相对参考仓库根目录。定义 `J = src/main/java/com/science/gtnl/`，`R = src/main/resources/assets/sciencenotleisure/`；`J`、`R` 只是本文缩写，不是代码变量。

「本项目采用」列指向集中记录的参考条目；主题文档通过这些锚点引用做法，不再各自保留路径清单。

| 主题 | 已核对存在的入口 | 检索用途与边界 | 本项目采用 |
| --- | --- | --- | --- |
| 模组生命周期 | `J/ScienceNotLeisure.java`、`J/CommonProxy.java` | 追踪初始化和代理分工；注册阶段以真实调用链为准 | — |
| 机器注册与物品引用 | `J/loader/MachineLoader.java`、`J/utils/enums/GTNLItemList.java` | 机器构造、物品枚举和引用入口；不得照搬注册 ID | [物品引用与方块注册](#物品引用与方块注册) |
| 方块、物品、材料加载 | `J/loader/BlockLoader.java`、`J/loader/ItemLoader.java`、`J/loader/MaterialLoader.java` | 检查材料与跨模组资源的加载顺序 | [物品引用与方块注册](#物品引用与方块注册) |
| 多方块公共基类 | `J/common/machine/multiMachineBase/MultiMachineBase.java` | 继承 `MTEExtendedPowerMultiBlockBase`，含自有仓口、处理逻辑和 GUI 依赖；不是可直接放入本项目的独立类 | — |
| 蒸汽机器基类 | `J/common/machine/multiMachineBase/SteamMultiMachineBase.java` | 蒸汽机器的公共实现入口，具体算法在使用时逐方法核验 | — |
| 无线能源机器基类 | `J/common/machine/multiMachineBase/WirelessEnergyMultiMachineBase.java` | 无线模式相关案例，不自动成为本项目需求 | — |
| 单方块蒸汽涡轮 | `J/common/machine/basicMachine/SteamTurbine.java` | 继承 `MTEBasicGenerator`；含蒸汽判定、燃料值、消费量、容量、纹理和 GUI 入口，不是多方块转子涡轮模板 | — |
| 单方块配方与 GUI | `J/common/machine/basicMachine/Replicator.java`、`J/common/gui/modularui/GTNLBasicMachineGui.java` | 实例重建、配方检查扩展点及 GT 原生 MUI2 基类继承；不是多流体输出实现 | [单方块多流体输出](#单方块多流体输出) |
| 多方块发电与同步案例 | `J/common/machine/multiblock/SiphonTurbine.java`、`J/common/gui/modularui/SiphonTurbineGui.java` | 无线蒸汽网络取用、EU 缓冲、发电仓、NBT、GUI/Waila 案例；不能据名称视为传统蒸汽涡轮 | — |
| 锅炉案例 | `J/common/machine/multiblock/structuralReconstructionPlan/LargeBoiler.java` | 包含多个材料等级子类、工作处理与蒸汽输出；不等同于 IC2 热冷却液热交换设备 | — |
| 太阳能锅炉案例 | `J/common/machine/multiblock/steam/MegaSolarBoiler.java`、`J/common/gui/modularui/MegaSolarBoilerGui.java` | 锅炉与界面实现参考；太阳能不是本项目规划的主热源 | — |
| 蒸汽分类 | `J/utils/enums/SteamTypes.java` | 流体识别和网络转换映射；其倍率不代表 Thermonuclear 的 EU/L 设计 | — |
| 仓口 | `J/common/machine/hatch/` | 根据输入、输出、并行、维护等功能继续限定检索 | — |
| 配方注册与处理逻辑 | `J/loader/RecipeLoader.java`、`J/loader/LoadCompleteRecipeScheduler.java`、`J/common/material/GTNLRecipeMaps.java`、`J/utils/recipes/GTNLProcessingLogic.java` | 追踪配方池、注册调用、处理逻辑与注册时机 | [配方注册](#配方注册) |
| 配方实现 | `J/common/recipe/gtnl/`、`J/common/recipe/gregtech/` | 分别检索附属模组配方和上游相关配方 | [配方注册](#配方注册) |
| 多方块 GUI | `J/common/gui/modularui/GTNLMultiBlockBaseGui.java`、`J/common/gui/modularui/GTNLSteamMultiBlockBaseGui.java` | 基类界面和同步组织；同时核验 GTNL 自有组件依赖 | — |
| 结构资源 | `J/utils/StructureUtils.java`、`R/multiblock/` | 资源读取、`.mbs` 结构定义、转置与控制器偏移 | [多方块组织与多批燃料](GTNL多方块组织与多批燃料.md) |
| 多方块组织 | `J/common/machine/multiblock/PCBFactory.java`、`J/common/machine/multiblock/NaquadahReactor.java` | 主／附加结构片段、分段检查与建造、输入处理与输出提交分离 | [多方块组织与多批燃料](GTNL多方块组织与多批燃料.md) |
| Tooltip | `J/common/machine/multiblock/NuclearReactor.java`、`J/common/machine/multiblock/wireless/FastNeutronBreederReactor.java`、`J/common/machine/multiblock/WhiteNightGenerator.java` | 结构／仓口提示组织与主题分隔写法；签名以本项目依赖为准 | [Tooltip 与仓口](#tooltip-与仓口) |
| 语言与配置 | `R/lang/zh_CN.lang`、`R/lang/en_US.lang`、`J/config/MainConfig.java` | 本地化键、提示和配置入口；数值不自动采用 | — |
| 兼容与注入 | `J/asm/GTNLEarlyCoreMod.java`、`J/mixins/LateMixinLoader.java`、`J/mixins/Mixins.java`、`R/mixins.sciencenotleisure{,.early,.late}{,.j17,.j21}.json` | 只有具体功能确需注入时才追踪目标、加载条件与资源配置 | [兼容与注入](#兼容与注入) |

## 集中参考条目

以下各节收录本项目已从 GTNL 采用的参考做法。主题文档通过锚点引用这些结论，不再各自保存路径清单；新建条目须在[参考索引](README.md) 登记。

### 物品引用与方块注册

- 方块／物品注册分层：`J/common/block/casings/{base,column}/` 与 `J/common/item/items/fuelRod/RadioactiveItem.java`。只采用职责划分，不复制其元数据表、构造时注册或辐射机制。
- 统一物品引用：`J/utils/enums/GTNLItemList.java` 与 `J/loader/MachineLoader.java` 的 Loader 绑定与副本读取。本项目同样由 Loader 绑定统一枚举，但拒绝备用物品、公开模板与未绑定读取，不新增 GT API。
- 方块提示列表：`J/common/block/casings/base/ItemBlockBase.java` 与 `J/utils/item/MetaTooltipUtils.java`。本项目按语言键存储说明，不复制其预翻译字符串和元数据映射。
- 扳手采集与机器更新：`J/common/block/casings/casing/MetaBlockCasingBase.java`。实际采用 GT5U `.205` 的 `BlockCasingsAbstract` 二级采集要求，以及 `BehaviourWrench` 调用 Forge 旋转接口的原生路径，不复制工具交互逻辑。

### 配方注册

- 配方池、注册调用与处理逻辑入口：`J/loader/RecipeLoader.java`、`J/loader/LoadCompleteRecipeScheduler.java`、`J/common/material/GTNLRecipeMaps.java`、`J/utils/recipes/GTNLProcessingLogic.java`。本项目沿用 load-complete 注册。
- 电路区分与螺丝刀转换：`J/common/recipe/gregtech/AssemblerRecipes.java`、`J/common/recipe/gtnl/CraftingTableRecipes.java`，用于增殖输入／输出总线的组装机与合成台配方。
- 双流体搅拌机配方：`J/common/recipe/gregtech/MixerRecipes.java`，用于复合冷却液制造；配方表与机器调用按本项目选中的 GT5U 核验。
- 换热类型组织：`J/utils/enums/SteamTypes.java` 只采用组织思路，不复用其蒸汽体系、倍率或自有 API。
- 不迁入参考库的自有配方工具类、`RecipeBuilder` 变体或工具箱扩展。
- 单元毁灭者电解机制造配方参考 `J/common/recipe/gtnl/CraftingTableRecipes.java` 的 `LargeCircuitAssembler` 合成台注册：采用原生 `GTModHandler.addCraftingRecipe`、机器枚举及等级电路矿辞描述，不复制配方内容。包裹线缆材质按本项目 GT5U `5.09.54.265` 缓存源码 `MTERecipeLoader.registerElectrolyser()` 与 `GTModHandler.addMachineCraftingRecipe()` 的 `WIRE` 映射核对，改用 `cableGt16`；不采用电极用的裸金／银／琥珀金／铂线。
- 单元毁灭者搅拌机制造配方同样参考上述 `CraftingTableRecipes.java` 的原生注册与等级电路矿辞写法；材料及格子排列完全按用户指定，不复制 GTNL 配方、资源或 ID。搅拌机和砖砌外壳枚举、合成材料转换按本项目 GT5U `5.09.54.265` 缓存源码 `ItemList`／`GTModHandler` 核对；储罐沿用本项目电解机配方，蒸汽外壳沿用 `LatexRecipes`，不引入 GTNL 自有 API 或依赖。

### 长整型输入消耗

- 物品参考 `J/common/machine/multiblock/GrandAssemblyLine.java` 的 `consumeItemsUnordered`／`depleteFromRequirement`，流体参考同类 `consumeFluidsUnordered`：先将单次需求提升为 `long` 再乘并行数，跨多个 `int` 栈扣除，每次扣除量不超过该栈数量。
- 流体排液参考 `J/common/machine/multiblock/EyeOfHarmonyInjector.java` 的 `drainFluidLong`：总需求使用 `long`，单次仓口请求限制在 `Integer.MAX_VALUE` 内。GTNL `MultiMachineBase.depleteInputQuantity` 本身仍为 `int`，不是无限扣料接口。
- 本项目核对实际缓存 GT5U `5.09.54.205` sources JAR：`GTRecipe.consumeInput` 已按 `long` 计算物品／流体并行总耗料；`MTEMultiBlockBase` 的普通输入集合会去重 ME 材料，ME 仓口在 `startRecipeProcessing`／`endRecipeProcessing` 间暴露并提交原始快照引用。
- 本项目独立实现 `BGTEMultiblockBase.depleteInput(ItemStack/FluidStack, long, boolean)`：仅借鉴长整型余量与跨栈扣除思路，不复制 GTNL 代码或资源（参考仓库许可证为 LGPLv3）。先检查总量，足够后才扣除；模拟不改数量。物品按 GT 原生物品／通配元数据／NBT 规则匹配，不进行矿辞替代或容器转流体；流体匹配类型与 NBT。
- 只支持普通输入集合，不将独立合成缓存分组混为共享库存。单个物品栈／流体栈及 ME 单种材料快照仍为 `int`；本次能力是跨栈总消耗超过 `int`，不是解除 ME 快照上限、增加长整型配方栈或扩展输出。调用方自行协调多材料校验；本接口的不足不扣料仅覆盖单次调用，不覆盖 ME 最终提交失败。

### 长整型并行产出

- 本项目实际缓存 GT5U `5.09.54.205` 的 `gregtech.api.util.ParallelHelper` 已提供公开静态方法 `addItemsLong(ArrayList<ItemStack>, ItemStack, long)` 和 `addFluidsLong(ArrayList<FluidStack>, FluidStack, long)`；标准配方的并行输出计算也调用这两个方法。
- 总产量先提升为 `long` 再乘并行数，方法将正数总量拆成多个不超过 `Integer.MAX_VALUE` 的栈；零或负数不追加。单栈字段仍为 `int`，同一种产物可在 `mOutputItems`／`mOutputFluids` 数组中重复出现，数组总量不受单栈 `int` 上限约束。不得将拆分结果再合并回单个 `int` 栈。
- GTNL `J/utils/Utils.java` 的两个 `addStacksToList(..., long amount)` 使用相同分段思路；本项目直接复用 GT 原生方法，不复制 GTNL 工具代码。
- `.205` 的 `Materials.getFluid(long)`／`getMolten(long)` 内部会强转为 `int`，不会拆栈。调用拆分方法时使用小数量模板栈，总量单独传入；不能先创建已溢出的模板再拆分。
- 地狱接口自定义 `checkProcessing()` 不经过标准配方的自动输出计算，显式调用上述方法生成物品与流体数组；耗水总量及跨栈扣除余量同样使用 `long`。并行次数仍使用 GT 原生 `int`，本项扩展的是并行后的总数量，不是并行次数或单栈字段。
- 拆栈不等于输出空间校验或堵塞保护，也不解除单种 ME 输入快照的 `int` 上限。

### Tooltip 与仓口

- 机器署名参考 `J/loader/MachineLoader.java` 注册后调用 `J/utils/text/AnimatedTooltipHandler.java#addItemTooltip` 的物品级附加方式。本项目不复制其自有工具类，直接使用 GTNHLib `0.11.54` 的 `AnimatedTooltipHandler.addItemTooltip(ItemStack, Supplier<String>)`，在客户端初始化完成机器注册后遍历已绑定的 `BGEMachineList`。当前 `2.9.0` 清单指定的 GT5U `5.09.54.265` 中，`toolTipFinisher(String...)` 仅为二进制兼容保留，作者参数明确被忽略；无参收尾不承担署名功能。API 按缓存 sources JAR 核对，未重新解析依赖。
- 结构／仓口提示组织：`NuclearReactor.java`、`wireless/FastNeutronBreederReactor.java`；主题分隔与分组写法：`WhiteNightGenerator.java`。
- 编号语言键与逐条 `addInfo`：`SiphonTurbine.createTooltip()`。
- 解析受限的 `SiphonTurbineGui` 同步显示方式仅参考实现思路，本项目沿用既有 info 接口。以上签名均以本项目实际依赖为准，不复制数值、资源或扩展基类。
- 蒸汽电解机本轮核对：`J/common/machine/multiblock/steam/LargeSteamCentrifuge.java` 的 `getTierRecipes()` 固定为 MV；控制器不锈钢齿轮升级位于同目录 `LargeSteamMixer.java` 的 `checkProcessing()`／`getUpgradeTier()`，原说明为 `R/lang/zh_CN/tooltip/large-steam-mixer.md`。本项目只沿用物品比较与简洁提示写法，不复制搅拌机性能公式。Markdown 读取参考 `J/common/machine/tooltip/MultiblockTooltipReader.java`，本项目直接使用 GT5U `.265` 原生 `MultiblockTooltipBuilder.addMarkdown`，不迁入 GTNL 自动结构扫描器。当前 GTNL `dependencies.gradle` 声明并强制 `.265`，本项目当前 `2.9.0` 缓存清单也指定 `.265`；本轮未重跑依赖解析，下表 `.205` 保留为历史核验记录。

- 蒸汽电解机 GUI／仓口补全参考：`J/common/gui/modularui/GTNLSteamMultiBlockBaseGui.java` 的 `registerSyncValues`、`initPanelMap`、`createRightPanelGapRow` 与 `createRecipeOcPanel`，以及 `J/common/machine/multiMachineBase/SteamMultiMachineBase.java` 的手动超频倍率、存档及 WAILA。复用 0–4 次手动超频、每次耗汽 ×4／时长 ÷2 的行为与侧边输入框组织，使用本项目键名和 GT 原生 `ProcessingLogic`／`OverclockCalculator`，不迁入 GTNL 扩展处理器、纹理或无线网络。同步及倍率计算沿用 GTNL 的 `Math.min(4, value)` 上界限制，NBT 直接读取，不添加额外下限校验；GT5U `.265` 的同步 API、蒸汽 WAILA 跳过普通正文及普通总线集合需显式补齐的差异均按缓存 sources JAR 核对，未重新解析依赖。

蒸汽输入分离／批处理补全参考 `J/common/machine/multiMachineBase/SteamMultiMachineBase.java` 的 `supportsInputSeparation`、`supportsBatchMode`、`getDefaultInputSeparationMode` 与 `doCheckRecipe`：采用默认不分离、普通／蒸汽总线逐仓检查及按颜色共享流体的思路。GT5U `.265` 的普通分离路径仍不遍历蒸汽总线，本项目通过 `checkRecipeForCustomHatches` 补充，不复制 GTNL 整份配方循环；原生合成缓存优先级、普通总线检查、MUI2 同步、NBT 和批处理器继续复用 GT。未复制参考源码或引入 GTNL 处理器。

### ME输出总成与蒸汽输出接口

- 阅读 `J/common/machine/multiMachineBase/SteamMultiMachineBase.java#getOutputBusses()` 的普通／蒸汽总线集合，以及 `J/common/machine/hatch/OutputBusMEProxy.java` 的原生 ME provider 继承与实例构造；在 `J/common/gui/modularui/` 的输出 GUI 文件中未检索到直接对应的双输出排版。配方材料引用核对 `J/common/recipe/gregtech/AssemblerRecipes.java`。只采用接口分工与枚举材料引用思路，不复制 GTNL 的代理仓机制、自有配方 Builder、数值或资源。
- 本项目当前 `build/elytra_conventions/2.9.0.json` 指定 GT5U `5.09.54.265`、MUI2 `2.3.94-1.7.10`；本轮按对应缓存 sources JAR 核对 `MTESteamMultiBlockBase`、`MTEMultiBlockBase`、`HatchElement`、ME 仓口 GUI、`Materials`、`ItemList` 及 MUI2 `Flow`／`TextWidget`，未重新解析依赖。Context7 仅提供原生缓存 provider 的一般信息，不替代本地版本签名。
- 上游蒸汽基类重写输出总线 getter，绕过普通多方块基类上的注入。本项目在自身蒸汽层从有效流体仓集合补入总成物品接口视图，并用同一输出列表检查成型；不改动只读 GTNL 或扩大对附属模组自定义机器的兼容承诺。
- WAILA 核对 GTNL `OutputBusMEProxy.java`／`OutputHatchMEProxy.java` 的父类调用，以及 GT5U `.265` 的 `MTEHatchOutputBusME`／`MTEHatchOutputME`、`MTEHatchOutputMEBase` 和 `GTWaila`。两类 provider 使用相同的 WAILA 字段名，原生 `WailaHelper` 直接读取根节点；本项目保留根节点流体信息，将物品 provider 的原生序列化结果隔离到子节点，并按原生物品格式渲染普通缓存及高级存储元件明细，不以物品栈的 `int stackSize` 代替缓存的 `long Amount`。
- 本地 GT5U `.265` sources JAR 的 `assets/gregtech/lang/zh_CN.lang` 未包含物品缓存空状态、元件标题及省略提示的原生语言键；本项目为新增物品提示使用自己的中英语言键，不全局覆盖 GT 语言资源。
- WAILA 普通正文将流体与物品容量放在连续的独立行，再依次显示流体缓存明细、物品缓存明细和 GT 父类原生“刷新至 ME”提示；仅移动原生提示位置，不拼接物品刷新周期或倒计时，不并排显示。高级正文保留父类流体存储元件段落，紧接物品元件段落。GUI 的控件行只对齐行内控件；MUI2 `.94` 的 `IPositioned.verticalCenter()` 实际设置 `topRel(0.5f)`，`Flow.buildWrappedFlows()` 会忽略主轴位置已固定的子元素，因此不能用它把两条控制行放入纵向 Flow。

### 单方块多流体输出

- 单元毁灭者电解机先在 `J/common/machine/basicMachine/` 检索电解机、离心机及多流体关键词，并在 `J/common/machine/` 检索单方块基类和多流体缓冲入口，未定位到可直接复用的多流体单方块实现。阅读 `Replicator.java` 的 `newMetaEntity`、`checkRecipe`、`buildUI` 和 `GTNLBasicMachineGui.java` 的原生 GUI 继承方式，只采用扩展点组织，不复制其复制物品机制、数值、纹理或 GTNL 标识。
- 本项目按既有 `ElectricLatex.java` 扩展 GT5U `.265` 的 `MTEBasicMachineWithRecipe`；`MTEIndustrialElectrolyzer.getRecipeMap()` 确认为 `RecipeMaps.electrolyzerNonCellRecipes`，配方池最多 6 项物品和 6 项流体输出。GT 原生 `MTEBasicMachine` 的 `canOutput`、`checkRecipe`、`onPostTick` 仍仅处理第 0 项流体，`MTEBasicMachineBaseGui` 也只建立一个流体输出格，因此不能只改配方池和 UI 的数量。
- 本项目自行增加六个输出储槽及待产物数组，复用原生单方块的配方检查、概率第 0 项结果、超频与完成回调，补充全输出空间校验、剩余产物概率、NBT、按类型／模拟抽取和自动输出轮询；独立 MUI2 GUI 将六格分别绑定真实储槽。离心机槽位覆盖层、电解机进度条及机器外观均直接引用 GT 资源，不复制 GTNL 源码或资源。
- API 以本地 `2.9.0` 清单指定的 `5.09.54.265` sources JAR 核对；Context7 只查得配方池的一般组织说明，未提供本版本多流体单方块 API，未用其 master 分支示例替代缓存签名，也未重新解析依赖。

- 单元毁灭者搅拌机本轮在 `J/common/machine/basicMachine/` 按 Mixer、多流体、配方检查及 GUI 关键词检索，未找到可直接复用的多流体单方块搅拌机。阅读 `Replicator.java` 的实例重建／配方／GUI 扩展点，以及 `SteamAssemblerBronze.java` 的原生蒸汽基类和纹理组织；只借鉴扩展方式，不复制源码、资源或数值。另核对 `J/common/machine/multiblock/steam/LargeSteamMixer.java` 的配方池、等级与 Tooltip；当前只读仓库该类使用语言键，历史路径 `R/lang/zh_CN/tooltip/large-steam-mixer.md` 本轮不存在，不能当作已阅读的 Markdown。
- 本机多流体机制由本项目实现，GT5U `.265` 的 `MTEIndustrialMixer` 确认使用 `mixerNonCellRecipes`，配方池上限为 9／9 物品、6／6 流体；单方块原生配方检查仍只传入单一流体，因此必须同时补齐完整输入数组、真实输出储槽、配方提交、NBT 和流体接口，而非只更换 GUI。蒸汽机沿用原生青铜／钢超频描述器和排气，不采用 GTNL 多方块的并行、升级齿轮或速度公式。

### 无线与电网接入

`NaquadahReactor.java` 的无动力仓判定与所有者入网、`LargeEssentiaGenerator.java` 的右键升级，以及 `common/item/items/WirelessUpgradeChip.java` 的物品用途，用于无线升级芯片与自动入网。本项目直接按 GT5U 核对 `WirelessNetworkManager`、右键与掉落 NBT API 后调用 GT 电网，不依赖 GTNL，也不采用其批次入网与自有无线接口。

### 兼容与注入

- 加载入口：`J/asm/GTNLEarlyCoreMod.java` 提供 early；`J/mixins/LateMixinLoader.java`（`@LateMixin`／`ILateMixinLoader`）与 `J/mixins/Mixins.java`（带目标模组过滤的注册表）提供 late。
- 资源配置：`R/mixins.sciencenotleisure.json`、`.early`、`.late`，另有 `.early.j17`／`.late.j17`／`.late.j21` 等 Java 版本变体。
- 本项目只采用 early / late 分离与空引导模式，不复制功能注入、ASM 补丁、注册／配置体系或 Java 17/21 分支。

### 可选模组集成

- 参考 `J/utils/enums/ModList.java` 的 `Loader.isModLoaded(ID)`、`J/loader/MachineLoader.java` 的 `loadTSTItemTooltip()` 与 `J/loader/RecipeLoader.java` 的 TST 配方入口：先判定模组存在，再调用带 `@Optional.Method(modid = "TwistSpaceTechnology")` 的隔离方法。缺失模组时由 Forge 移除直接引用其 API 的方法。本项目直接使用 Forge 判定，不迁入 GTNL 枚举、Mixin 目标或缓存体系。
- 本项目 `registry/MachineLoader.java`、`registry/MachineRecipes.java` 对两台增强机器及其制造配方同时设防；未加载时保留原机器 ID 空位，其他机器仍使用原编号。开发依赖保留同一本地 TST 坐标，仅改为不发布的开发依赖。
- GTNL 主入口为 `before:TwistSpaceTechnology`；本项目增强机器使用 TST 注册的资源，保持原有后加载方向，改为可选的 `after:TwistSpaceTechnology`，不照搬其前加载顺序。API 按 Forge/FML `1.7.10` 官方 `Optional.java` 和 `Loader.java` 核对；未引入新 GT5U API、未运行依赖解析或游戏启动。

## 与当前热核规划相关的边界

快照检索在 `J` 未定位到名称／关键字直接对应 `ic2hotcoolant` 换热的实现，不断言全库不存在；开发时仍按功能、材料和上游类名核验。`SteamTypes` 的致密超临界／自有压缩蒸汽及网络倍率，不与本项目四级蒸汽一一对应，也不决定流体名或 EU/L。

## 依赖版本差异

| 项目 | GT5U 基线 | 证据 |
| --- | --- | --- |
| Thermonuclear | 已重跑解析，选中 `5.09.54.205` | [项目技术基线](../development/项目知识库.md#当前技术基线)；依赖清单对齐 GTNH `2.9.0-RC-2` 后由 `compileClasspath`／`runtimeClasspath` 确认 |
| GTNL 本地参考 | 声明并强制 `5.09.54.205` | 参考仓库 `dependencies.gradle`（`rc2` 提交起）；本次未运行其依赖解析 |

两个项目的依赖清单现同为 GTNH `2.9.0-RC-2` 基线，参考库还强制 ModularUI2 `2.3.92-1.7.10`、GTNHLib `0.11.52`。类／签名／结构／GUI 差异仍先查最终依赖，不升级全依赖掩盖问题。其他来源边界见[迁入资料使用说明](迁入资料使用说明.md)。
