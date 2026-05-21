package cn.refinex.jinfra.id;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class IdGeneratorsTest {

    @Test
    @DisplayName("UUID 默认应返回无连字符字符串")
    void shouldGenerateUuidWithoutHyphen() {
        String id = IdGenerators.uuid().nextId();

        assertThat(id).hasSize(32).doesNotContain("-");
    }

    @Test
    @DisplayName("UUID with hyphen 应返回标准 UUID 字符串")
    void shouldGenerateUuidWithHyphen() {
        String id = IdGenerators.uuidWithHyphen().nextId();

        assertThat(id).hasSize(36).contains("-");
    }
}
