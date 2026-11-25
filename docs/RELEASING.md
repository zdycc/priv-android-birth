# 发布 APK

APK 通过 **GitHub Releases** 分发，不提交进 Git 仓库。

## 1. 准备签名 keystore（只做一次）

```bash
keytool -genkeypair -v \
  -keystore ~/AndroidProjects/Birthdate/keystore.jks \
  -alias birthdate \
  -keyalg RSA -keysize 2048 -validity 10000
```

- 记住 `-alias`（这里是 `birthdate`）和两个密码；
- `keystore.jks` 必须备份，丢失后无法给已安装用户发升级包；
- 不要提交 keystore，仓库的 `.gitignore` 已忽略 `*.jks`。

## 2. 配置仓库 Secrets

仓库 → Settings → Secrets and variables → Actions → New repository secret，创建 4 条：

| Name | 值 |
| --- | --- |
| `KEYSTORE_BASE64` | `base64 -i keystore.jks \| tr -d '\n'` 的一行输出 |
| `KEYSTORE_PASSWORD` | keystore 存储密码 |
| `KEY_ALIAS` | `birthdate` |
| `KEY_PASSWORD` | key 密码（PKCS12 下与存储密码相同） |

macOS 可直接复制到剪贴板：

```bash
base64 -i ~/AndroidProjects/Birthdate/keystore.jks | tr -d '\n' | pbcopy
```

粘贴后值里只应有 base64 字符（`A–Z a–z 0–9 + / =`），不能带引号、空格、换行或终端提示符。

## 3. 打 tag 发布

1. 修改 `app/build.gradle.kts` 中的 `versionCode` 和 `versionName`；
2. 提交并打 tag：

```bash
git add -A
git commit -m "release v1.0.0"
git tag v1.0.0
git push origin main --tags
```

3. `.github/workflows/release.yml` 自动执行：
   - 解码 keystore；
   - `./gradlew assembleRelease`；
   - 生成 `Birthdate-<tag>.apk` 与 `SHA256SUMS.txt`；
   - 创建 Release 并上传附件。

## 4. 本地构建签名包（可选）

```bash
KEYSTORE_FILE=keystore.jks \
KEYSTORE_PASSWORD='...' \
KEY_ALIAS=birthdate \
KEY_PASSWORD='...' \
./gradlew assembleRelease
```

## 常见报错

| 报错 | 处理 |
| --- | --- |
| `base64: error: invalid input` | `KEYSTORE_BASE64` 含非法字符或换行，用 `tr -d '\n'` 重新生成并更新 secret |
| `Keystore was tampered with, or password was incorrect` | `KEYSTORE_PASSWORD` / `KEY_PASSWORD` 不对 |
| `Alias <xxx> does not exist` | `KEY_ALIAS` 与 keytool 的 `-alias` 不一致 |
| 没触发 workflow | tag 必须以 `v` 开头，且推送时带 `--tags` |
| Release 已存在导致失败 | 先删除旧 tag/Release，或换一个新版本号 |

工作流文件：`.github/workflows/release.yml`。
