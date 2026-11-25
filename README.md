# Birthdate · 生日提醒

一个**完全离线**的 Android 生日提醒应用：记录好友的公历 / 农历生日，按「距离今天还有几天」排序，
每天在设定时间提醒**今天**和**明天**的生日。

[![最新版本](https://img.shields.io/github/v/release/zdycc/priv-android-birth?label=release)](https://github.com/zdycc/priv-android-birth/releases/latest)

| 下载 | 系统要求 | 隐私 |
| --- | --- | --- |
| [最新版 APK](https://github.com/zdycc/priv-android-birth/releases/latest) | Android 7.0（API 24）及以上 | 无网络权限，数据只存本机 |

> 首次安装会提示「未知来源应用」，允许当前浏览器或文件管理器安装即可。
> 每个 Release 都附带 `SHA256SUMS.txt`，下载后可执行 `shasum -a 256 Birthdate-*.apk` 校验。

---

## 功能

**生日管理**

- 公历 / 农历两种录入方式，农历支持闰月。
- 自动换算下一次生日，按剩余天数排序。
- 可选出生年份：填写后显示年龄，不填则只记生日。
- 好友头像、按姓名搜索、批量删除。

**提醒**

- 每天在设定时间检查生日，通知「今天」和「明天」过生日的人。
- Android 12+ 支持精确闹钟；开机、改时间、换时区后自动重排。
- 可自定义每天的提醒时间（默认 09:00）。

**外观**

- 多套主题色；深色模式支持跟随系统、浅色、深色。

---

## 使用

### 1. 首次启动

1. 打开应用，Android 13 及以上会请求通知权限，选择「允许」。
2. 进入右上角「设置」：
   - 若提示「未开启精确提醒权限」，点「去开启」，在系统设置里允许「闹钟和提醒」；
   - 在「后台运行」中点「去开启」，允许后台活动 / 忽略电池优化。
3. 国产系统建议把应用设为「无限制」并允许自启动，避免提醒被延迟。

### 2. 添加好友

1. 点击首页右下角「＋」。
2. 填写姓名（必填），可选一张头像。
3. 选择「公历」或「农历」，再选年 / 月 / 日；农历可勾选「闰月」。
4. 出生年份可「不填」：不填只记生日，不显示年龄。
5. 点「保存」。

### 3. 首页

- 顶部显示今天的公历与农历日期。
- 好友按最近生日排序，分组为「今天」「7 天内」和之后的月份。
- 卡片显示头像、姓名、农历生日、对应公历、剩余天数和年龄。
- 顶部搜索框可按姓名过滤。
- 点右上角「管理」进入多选，支持全选、取消全选、批量删除。

### 4. 编辑与删除

- 点好友卡片进入编辑页，可修改姓名、头像、生日类型和日期。
- 编辑页底部可删除该好友，二次确认后不可恢复。

### 5. 提醒设置

| 设置项 | 说明 |
| --- | --- |
| 每天提醒时间 | 默认 09:00，滚轮选择小时 / 分钟 |
| 精确提醒权限 | Android 12+ 需要，未开启时可能不准时 |
| 主题色 | 多套颜色，立即生效 |
| 深色模式 | 跟随系统 / 浅色 / 深色 |
| 允许后台运行 | 跳转系统电池优化设置 |

### 6. 通知行为

- 每天在设定时间检查一次：
  - 今天有人生日 → 通知「今天（x月x日）生日：…」；
  - 明天有人生日 → 通知「明天（x月x日）生日：…」。
- 通知渠道为「生日提醒」（高重要性），点击可打开应用。
- 手机重启、修改时间或切换时区后会自动重新安排提醒。

---

## 权限

| 权限 | 用途 |
| --- | --- |
| `POST_NOTIFICATIONS` | 发送生日提醒（Android 13+） |
| `SCHEDULE_EXACT_ALARM` | 在设定时间精确触发（Android 12+） |
| `RECEIVE_BOOT_COMPLETED` | 开机后重新安排提醒 |
| `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` | 引导关闭电池优化 |

应用**没有** `INTERNET` 权限，不会联网。

---

## 数据与隐私

- 好友数据：应用私有目录 `files/friends.json`；
- 头像：`files/avatars/`；
- 设置：`SharedPreferences`；
- 卸载应用即清除全部数据；
- 无账号、无云同步、无网络请求。

---

## 已知限制

- **公历生日会换算为农历保存**：录入公历后按对应农历日期存储，之后按农历计算下一次生日；
  如果需要严格按公历每年提醒，需要把日历类型一并保存后再计算。
- **闰月**：该年没有对应闰月时，自动回退到平月。
- **农历三十**：遇到只有 29 天的小月时，自动取该月最后一天。

---

## 构建

环境：Android Studio（AGP 9.2）+ JDK 17；Gradle 9.4.1 由 wrapper 提供。

```bash
git clone https://github.com/zdycc/priv-android-birth.git
cd priv-android-birth
```

在项目根目录创建 `local.properties`（不要提交）：

```properties
sdk.dir=/Users/<你>/Library/Android/sdk
```

```bash
./gradlew assembleDebug      # app/build/outputs/apk/debug/app-debug.apk
./gradlew installDebug       # 连接手机后直接安装
```

Release 包需要签名，发布流程见 [docs/RELEASING.md](docs/RELEASING.md)。

---

## 项目结构

```text
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

核心类：

- `logic/LunarConverter`：农历 ↔ 公历换算（基于 [6tail/lunar-java](https://github.com/6tail/lunar-java)）；
- `logic/BirthdayCalculator`：下一次生日、剩余天数、年龄；
- `notifications/BirthdayNotifications`：通知渠道与每日闹钟；
- `data/FriendRepository`：JSON 持久化与头像文件。

## 技术栈

Kotlin 2.2 · Jetpack Compose (Material 3) · ViewModel/StateFlow · Gson · Coil · lunar-java · AlarmManager

## License

[MIT](LICENSE)
