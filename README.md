# Birthdate · 生日提醒

一个**完全离线**的 Android 生日提醒应用：记录好友的公历 / 农历生日，自动按「距离今天还有几天」排序，
每天在设定时间提醒**今天**和**明天**的生日。

> 应用没有申请网络权限，所有数据只保存在本机，不需要账号，也不会上传任何信息。

---

## 下载安装

- **最新版 APK**：[GitHub Releases](https://github.com/zdycc/priv-android-birth/releases/latest)
- 首次安装时系统会提示「未知来源应用」，允许当前浏览器 / 文件管理器安装即可。
- 每个 Release 附带 `SHA256SUMS.txt`，下载后可校验：

  ```bash
  shasum -a 256 Birthdate-*.apk
  ```

---

## 功能

- **公历 / 农历生日**：农历支持闰月，自动换算成对应的公历日期。
- **按最近生日排序**：首页自动分组为「今天」「7 天内」和之后的月份。
- **年龄显示**：填写出生年份后显示「x 岁」；不填则只记生日、不算年龄。
- **好友头像**：从相册选择，本地保存。
- **搜索与批量删除**：按姓名搜索；「管理」模式支持全选、取消全选、批量删除。
- **每日提醒**：在设定时间检查今天与明天的生日，发送系统通知。
- **精确闹钟**：Android 12+ 可申请精确闹钟权限；开机、改时间、换时区后自动重排提醒。
- **主题色 / 深色模式**：内置多套主题色，支持跟随系统、浅色、深色。
- **无网络、无广告、无账号**。

---

## 环境要求

| 项目 | 要求 |
| --- | --- |
| 系统 | Android 7.0（API 24）及以上 |
| 编译 | Android Studio（支持 AGP 9.2）+ JDK 17 或更高（推荐 Android Studio 自带 JBR） |
| 构建 | Gradle 9.4.1（随 wrapper 提供） |

---

## 构建与安装

```bash
git clone https://github.com/zdycc/priv-android-birth.git
cd priv-android-birth
```

创建 `local.properties`（不要提交）：

```properties
sdk.dir=/Users/<你>/Library/Android/sdk
```

构建并安装：

```bash
./gradlew assembleDebug      # 产物：app/build/outputs/apk/debug/app-debug.apk
./gradlew installDebug       # 连接手机后直接安装
```

也可以直接用 Android Studio 打开项目，点击 Run。

> release 包需要自备签名配置；仓库不包含签名文件与 `local.properties`。

---

## 使用教程

### 第 1 步：首次启动

1. 打开应用，Android 13 及以上会请求**通知权限**，请选择「允许」。
2. 进入右上角「设置」：
   - Android 12 及以上，若提示「未开启精确提醒权限」，点「去开启」并在系统设置中允许「闹钟和提醒」；
   - 「后台运行」一栏点「去开启」，在系统设置中允许后台活动 / 忽略电池优化。
3. 部分国产系统的后台策略有多档（智能限制 / 无限制），建议把本应用设为「无限制」并允许自启动，保证提醒准时。

### 第 2 步：添加第一位好友

1. 点击首页右下角的「＋」。
2. 填写**姓名**（必填）。
3. 可选：点击头像区域，从相册选择一张图片。
4. 选择生日类型：
   - **公历**：按公历选择年 / 月 / 日；
   - **农历**：按农历选择月 / 日，闰月可勾选「闰月」。
5. 出生年份：
   - 填写 → 首页显示年龄；
   - 选「**不填**」→ 只记录生日，不显示年龄。
6. 输入公历时，下方会预览对应的农历日期；农历模式会提示「将保存为 农历×月×日」。
7. 点「保存」，好友会出现在首页。

### 第 3 步：首页

- 顶部显示今天的公历日期与农历日期。
- 好友按「距离下一个生日还有几天」从近到远排列，并自动分组：
  - **今天**：今天生日；
  - **7 天内**：一周内生日；
  - 其余按月份分组。
- 每张卡片显示：头像、姓名、农历生日 + 对应公历日期、还有几天、年龄（若填写了出生年份）。
- 顶部搜索框可按姓名过滤。
- 好友较多时，点右上角「管理」进入多选：
  - 「全选 / 取消全选」；
  - 点「删除」批量删除，二次确认后不可恢复。

### 第 4 步：编辑或删除好友

- 点击好友卡片进入编辑页，可修改姓名、头像、生日类型、日期与年份。
- 编辑页底部提供「删除好友」，二次确认后删除。

### 第 5 步：提醒设置

进入设置页：

| 设置项 | 说明 |
| --- | --- |
| 每天提醒时间 | 默认 **09:00**，点击后用滚轮选择小时 / 分钟 |
| 精确提醒权限 | Android 12+ 需要；未开启时提醒可能不准时 |
| 主题色 | 多套颜色可选，立即生效 |
| 深色模式 | 跟随系统 / 浅色 / 深色 |
| 允许后台运行 | 跳转系统电池优化设置，保证到点能收到通知 |

### 第 6 步：通知行为

- 应用每天在设定的时间检查一次：
  - 今天有人生日 → 通知「今天（x月x日）生日：张三、李四」；
  - 明天有人生日 → 通知「明天（x月x日）生日：王五」。
- 通知渠道名称：**生日提醒**（高重要性）。
- 点击通知打开应用。
- 手机重启、修改系统时间或切换时区后，提醒会自动重新安排。

---

## 权限说明

| 权限 | 用途 |
| --- | --- |
| `POST_NOTIFICATIONS` | 发送生日提醒通知（Android 13+） |
| `SCHEDULE_EXACT_ALARM` | 在设定时间精确触发提醒（Android 12+） |
| `RECEIVE_BOOT_COMPLETED` | 开机后重新安排提醒 |
| `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` | 引导关闭电池优化，避免提醒被系统延迟 |

应用**没有** `INTERNET` 权限，不会联网。

---

## 数据与隐私

- 好友数据保存在应用私有目录：`files/friends.json`；
- 头像复制到：`files/avatars/`；
- 设置保存在 `SharedPreferences`；
- 卸载应用即清除全部数据；
- 没有账号、没有云同步、没有网络请求。

---

## 已知限制

- **公历生日会换算为农历保存**：录入公历时，应用会把该日期换算成对应农历日期存储，之后按农历计算下一次生日；
  如果你需要「每年固定按公历同一天提醒」，目前建议留意这一点（可以改成保存日历类型后再计算）。
- **闰月**：农历闰月生日在该年没有闰月时，会自动回退到平月。
- **农历三十**：遇到只有 29 天的小月时，自动取该月最后一天。

---

## 项目结构

```
Birthdate/
├── app/src/main/
│   ├── java/com/birthdate/app/
│   │   ├── data/            # 好友数据、设置存储
│   │   ├── logic/           # 农历换算、生日计算
│   │   ├── notifications/   # 通知渠道与闹钟
│   │   └── ui/              # Compose 页面与组件
│   ├── res/                 # 主题、图标、字符串
│   └── AndroidManifest.xml
├── gradle/                  # wrapper 与版本目录
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

核心逻辑：

- `logic/LunarConverter`：农历 ↔ 公历换算（基于 [6tail/lunar-java](https://github.com/6tail/lunar-java)）；
- `logic/BirthdayCalculator`：计算下一次生日、剩余天数与年龄；
- `notifications/BirthdayNotifications`：通知渠道、每日闹钟调度；
- `data/FriendRepository`：JSON 持久化与头像文件管理。

## 技术栈

- Kotlin 2.2 + Jetpack Compose（Material 3）
- Navigation / ViewModel / StateFlow
- Gson（本地 JSON）
- Coil（头像加载）
- lunar-java（农历换算）
- AlarmManager（精确闹钟）+ NotificationChannel

## 发布新版本（维护者）

APK 通过 **GitHub Releases** 分发，不提交进仓库。

1. 修改 `app/build.gradle.kts` 中的 `versionCode` / `versionName`；
2. 提交后打 tag 并推送：

   ```bash
   git tag v1.0.0
   git push origin v1.0.0
   ```

3. `.github/workflows/release.yml` 会自动构建签名 APK，创建 Release 并上传
   `Birthdate-v1.0.0.apk` 与 `SHA256SUMS.txt`。

首次发布前，在仓库 **Settings → Secrets and variables → Actions** 添加：

| Secret | 内容 |
| --- | --- |
| `KEYSTORE_BASE64` | keystore 文件的 base64 |
| `KEYSTORE_PASSWORD` | keystore 密码 |
| `KEY_ALIAS` | key 别名 |
| `KEY_PASSWORD` | key 密码 |

本地生成 keystore（只做一次，务必备份，不要提交）：

```bash
keytool -genkeypair -v -keystore keystore.jks -alias birthdate \
  -keyalg RSA -keysize 2048 -validity 10000
base64 -i keystore.jks | pbcopy    # macOS：粘贴到 KEYSTORE_BASE64
```

本地构建签名包：

```bash
KEYSTORE_FILE=keystore.jks KEYSTORE_PASSWORD=... KEY_ALIAS=birthdate KEY_PASSWORD=... \
  ./gradlew assembleRelease
```

## License

[MIT](LICENSE)
