# Maven Central 发布手册

本文记录 JInfra 第一次发布到 Maven Central 的人工操作步骤。当前工程已经配置 Central Publisher Portal 发布链路，但账号、命名空间、Token 和 GPG 私钥属于运营信息，不写入仓库。

## 当前状态

- 发布 groupId：`cn.refinex`
- 发布插件：`org.sonatype.central:central-publishing-maven-plugin`
- 发布 server id：`central`
- 发布策略：`autoPublish=false`，先上传并等待校验，再到 Central Portal 人工发布
- CI 预演：`-Dgpg.skip=true -Dcentral.skip=true`，只校验 release profile 附件生成，不签名、不上传
- 未指定：Central Portal 账号、命名空间验证信息、Portal Token、GPG 私钥、GPG passphrase、GitHub Secrets

## 官方资料

- Central 发布要求：https://central.sonatype.org/publish/requirements/
- Central Portal Maven 发布：https://central.sonatype.org/publish/publish-portal-maven/
- Portal Token：https://central.sonatype.org/publish/generate-portal-token/
- GPG 指南：https://central.sonatype.org/publish/requirements/gpg/
- GitHub Actions Java with Maven：https://docs.github.com/en/actions/tutorials/build-and-test-code/java-with-maven
- `actions/setup-java`：https://github.com/actions/setup-java

## 1. 注册并验证命名空间

1. 打开 Central Portal：https://central.sonatype.com/
2. 注册或登录你的账号。
3. 创建 namespace。JInfra 当前使用 `cn.refinex`，所以需要确认你是否拥有该命名空间的发布权。
4. 按 Portal 提示完成所有权验证。常见方式是 DNS TXT 记录；如果使用 GitHub 相关命名空间，则按 Portal 页面要求完成对应验证。
5. 验证完成后，在 Portal 中确认 namespace 状态可用于发布。

待你补充：

- Central Portal 登录账号：未指定
- namespace 验证方式：未指定
- namespace 是否已验证：未指定

## 2. 生成 Portal Token 并配置 Maven

1. 在 Central Portal 中进入 Account 或 Publishing Token 页面。
2. 生成用于发布的 Portal Token。
3. 在本机 `~/.m2/settings.xml` 中加入 `central` server。不要把真实 Token 写进仓库。

```xml
<settings>
  <servers>
    <server>
      <id>central</id>
      <username>未指定</username>
      <password>未指定</password>
    </server>
  </servers>
</settings>
```

说明：

- `<id>` 必须与根 `pom.xml` 中的 `<publishingServerId>central</publishingServerId>` 一致。
- `username` 和 `password` 使用 Portal Token 生成结果，不是你的网页登录密码。
- 如果以后做 GitHub Actions 自动发布，应改用 GitHub Secrets 注入，不要提交 `settings.xml`。

## 3. 准备 GPG 签名

先检查本机是否已有可用于发布的私钥：

```bash
gpg --version
gpg --list-secret-keys --keyid-format LONG
```

如果没有私钥，生成一把新密钥：

```bash
gpg --full-generate-key
```

建议：

- 密钥类型：RSA 或 ECC 均可，优先使用当前 GPG 推荐选项
- 用户名：使用你的发布身份
- 邮箱：使用你希望公开关联的发布邮箱
- passphrase：必须记录在安全位置，后续 CI 签名会用到

导出公钥并发布到公共 keyserver：

```bash
gpg --armor --export <KEY_ID> > public-key.asc
gpg --keyserver keyserver.ubuntu.com --send-keys <KEY_ID>
```

待你补充：

- GPG Key ID：未指定
- GPG 公钥是否已发布到 keyserver：未指定
- GPG passphrase：未指定，且不能写入仓库

## 4. 本地发布前验证

普通构建验证：

```bash
mvn -DskipTests validate
mvn test
mvn verify
```

release profile 预演，不签名、不上传：

```bash
mvn -P release -DskipTests -Dgpg.skip=true -Dcentral.skip=true clean verify
```

验证关键附件：

```bash
test -f jinfra-core/target/jinfra-core-0.1.0-SNAPSHOT-sources.jar
test -f jinfra-core/target/jinfra-core-0.1.0-SNAPSHOT-javadoc.jar
test -f jinfra-all/target/jinfra-all-0.1.0-SNAPSHOT-sources.jar
test -f jinfra-all/target/jinfra-all-0.1.0-SNAPSHOT-javadoc.jar
```

配置好 GPG 后，再验证签名附件生成能力，但仍然跳过 Central 上传：

```bash
mvn -P release -DskipTests -Dcentral.skip=true clean verify
```

签名验证示例：

```bash
test -f jinfra-core/target/jinfra-core-0.1.0-SNAPSHOT.jar.asc
test -f jinfra-core/target/jinfra-core-0.1.0-SNAPSHOT-sources.jar.asc
test -f jinfra-core/target/jinfra-core-0.1.0-SNAPSHOT-javadoc.jar.asc
```

注意：当前仓库版本是 `0.1.0-SNAPSHOT`。正式发布 Maven Central release 前，需要先把版本改为非 `-SNAPSHOT`，例如 `0.1.0`。

## 5. 正式发布到 Central

正式发布前检查：

- Central namespace 已验证
- `~/.m2/settings.xml` 已配置 `central` server
- GPG 私钥在本机可用
- GPG 公钥已发布到 keyserver
- 工程版本不是 `-SNAPSHOT`
- `mvn -P release -DskipTests -Dcentral.skip=true clean verify` 已通过

执行上传：

```bash
mvn -P release -DskipTests deploy
```

因为当前配置为 `autoPublish=false`：

1. Maven 会把构件上传到 Central Publisher Portal。
2. 插件会等待 Portal 校验到 `VALIDATED`。
3. 登录 Central Portal，检查 deployment。
4. 确认无误后，在 Portal 页面手动点击 Publish。
5. 等待同步完成，再到 Maven Central 搜索或用测试项目拉取依赖。

## 6. GitHub Actions 发布凭证

当前 CI 只做 dry run，不发布。以后如果要自动发布，建议先新增单独 workflow，并配置以下 Secrets：

- `CENTRAL_TOKEN_USERNAME`：未指定
- `CENTRAL_TOKEN_PASSWORD`：未指定
- `MAVEN_GPG_PRIVATE_KEY`：未指定
- `MAVEN_GPG_PASSPHRASE`：未指定

`actions/setup-java` 可以帮助写入 Maven `settings.xml` 并导入 GPG 私钥。发布 workflow 必须限制触发条件，建议只允许 tag 或手工 `workflow_dispatch`。

## 7. 常见失败处理

- `401` 或 `403`：检查 Portal Token、`server id=central`、namespace 权限。
- namespace 校验失败：回到 Portal 检查 DNS TXT 或所有权验证状态。
- GPG 签名失败：检查本机私钥、passphrase、`gpg --list-secret-keys --keyid-format LONG`。
- 缺少 `-sources.jar` 或 `-javadoc.jar`：先跑 `mvn -P release -DskipTests -Dgpg.skip=true -Dcentral.skip=true clean verify`，再检查对应模块 `target/`。
- 版本拒绝：Central release 不能使用 `-SNAPSHOT` 版本。

## 8. 发布后检查

1. 在 Central Portal 确认 deployment 已发布。
2. 等待 Maven Central 搜索可见。
3. 新建临时消费项目，引用发布版本：

```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>cn.refinex</groupId>
      <artifactId>jinfra-bom</artifactId>
      <version>0.1.0</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>
```

4. 验证 `jinfra-all` 可解析，且不会引入 Spring Boot Starter 聚合边界外模块。
