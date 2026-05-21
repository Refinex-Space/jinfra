package cn.refinex.jinfra.id.uuid;

import cn.refinex.jinfra.id.IdGenerator;
import java.util.UUID;

/**
 * UUID 生成器。
 *
 * @author refinex
 * @since 0.1.0
 */
public final class UuidGenerator implements IdGenerator<String> {

    private final boolean hyphenated;

    private UuidGenerator(boolean hyphenated) {
        this.hyphenated = hyphenated;
    }

    /**
     * 创建标准 UUID 生成器。
     *
     * @return UUID 生成器
     */
    public static UuidGenerator withHyphen() {
        return new UuidGenerator(true);
    }

    /**
     * 创建无连字符 UUID 生成器。
     *
     * @return UUID 生成器
     */
    public static UuidGenerator withoutHyphen() {
        return new UuidGenerator(false);
    }

    /**
     * 生成 UUID 字符串。
     *
     * @return UUID 字符串
     */
    @Override
    public String nextId() {
        String uuid = UUID.randomUUID().toString();
        if (hyphenated) {
            return uuid;
        }
        return uuid.replace("-", "");
    }
}
