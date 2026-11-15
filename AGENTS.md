# BetterGTExperience 开发上下文

## 阅读入口

- 文档索引：`docs/README.md`。
- 当前项目事实：`docs/wiki/development/项目知识库.md`。
- 本地代码参考：`docs/wiki/references/GT-Not-Leisure参考索引.md`。
- 构建与排障：`docs/wiki/development/Gradle开发指南.md`。

## 项目约束

- 查询 GT5U API 时以本项目实际解析的依赖为准；迁入资料中的版本、签名和数值必须核对。
- 编写或修改功能前，优先先在 `D:\DEV\mcmod\GT-Not-Leisure` 检索并阅读相关实现。按参考索引限定目录，核对具体类、父类、注册调用和资源；不要整库通读或仅凭类名照搬。
- 实现前简要说明找到的参考路径、可复用思路和版本差异；未找到相关代码时明确说明检索范围，再继续本项目实现。参考目录不可访问时告知用户，不假装已完成参考核验。
- GT-Not-Leisure 仅作为只读参考，不自动添加构建依赖、不修改参考仓库、不复用其机器 ID 或平衡数值；复制代码或资源前核对来源许可证与署名要求。
- 不擅自确定规划中的冲突、机器 ID、流体注册名和最终平衡数值。
- 保持现有 GTNH convention / RetroFuturaGradle 构建方式。用户已允许全部格式检查及配套的 `spotlessApply`；除此之外，未经用户重新明确允许，不运行 Wrapper 的其他任务、系统 Gradle 或间接调用 Gradle 的脚本。
- 代码格式化仍以 Wrapper 的 `spotlessApply` 为项目标准，不手动维护格式化结果；格式任务已获许可，可运行 `spotlessApply`、`spotlessCheck` 及各语言格式检查。编译、测试和打包仍需另行许可；
- 不覆盖用户未提交的修改，不把生成的 `Tags.java` 当作手写源文件维护。
- 多方块结构直接使用原始 Shape 字符串数组，不通过循环填充、字符串拼接／替换或数组复制动态重建 Shape；仅在结构过于重复时允许抽取重复片段或动态生成，且须保留清晰的几何对应关系。
- 所有机器 Tooltip 遵循 GT／GTNL 风格：结构页仅描述所需方块、数量、等级／朝向／替换要求，仓口用原生 `add*Hatch`／`add*Bus`，专用仓口用 `addMiscHatch`，位置写实际提示方块编号且与 `.hint(n)` 一致；运行模式、容量、并行、机制及特性支持放入主机 `addInfo`，按主题用 `addSeparator()` 分组，数值局部着色后恢复正文颜色。不把功能说明加入结构提示编号，不宣称未经实现核验的支持。
- 蒸汽机器及两台 TST 增强机器 Tooltip 的主机说明使用 `assets/betterexperience/lang/{语言}/tooltip/*.md`，通过 GT 原生 Markdown Tooltip API 加载；编写前阅读 GTNL 对应机器现有的 Markdown、Tooltip 构造和实际机制，沿用其简洁措辞、信息顺序及局部着色，不自行添加口号、实现细节或重复说明。结构页继续使用原生 Tooltip Builder。两台 TST 增强机器的 Tooltip 不依赖 TST 语言键或专用 Builder。
