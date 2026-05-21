package cn.refinex.jinfra.id.ulid;

/**
 * ULID 常量与编码工具。
 *
 * @author refinex
 * @since 0.1.0
 */
public final class Ulid {

    /**
     * ULID 字符串长度。
     */
    public static final int STRING_LENGTH = 26;

    /**
     * ULID 时间戳最大值，48 bit 毫秒时间。
     */
    public static final long MAX_TIMESTAMP = (1L << 48) - 1;

    private static final char[] ENCODING = "0123456789ABCDEFGHJKMNPQRSTVWXYZ".toCharArray();

    private Ulid() {
        throw new AssertionError("No cn.refinex.jinfra.id.ulid.Ulid instances");
    }

    /**
     * 将时间戳和随机数编码为 ULID 字符串。
     *
     * @param timestampMillis 毫秒时间戳
     * @param randomness 80 bit 随机数
     * @return ULID 字符串
     */
    public static String encode(long timestampMillis, byte[] randomness) {
        char[] chars = new char[STRING_LENGTH];
        long timestamp = timestampMillis;
        for (int index = 9; index >= 0; index--) {
            chars[index] = ENCODING[(int) (timestamp & 0x1F)];
            timestamp >>>= 5;
        }

        int accumulator = 0;
        int bits = 0;
        int charIndex = 10;
        for (byte value : randomness) {
            accumulator = (accumulator << 8) | (value & 0xFF);
            bits += 8;
            while (bits >= 5) {
                bits -= 5;
                chars[charIndex] = ENCODING[(accumulator >>> bits) & 0x1F];
                charIndex++;
            }
        }
        return new String(chars);
    }
}
