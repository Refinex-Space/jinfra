package cn.refinex.jinfra.core.constant;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CoreConstantsTest {

    @Test
    @DisplayName("核心常量应提供模块名和空字符串")
    void shouldExposeCoreConstants() {
        assertThat(CoreConstants.MODULE_NAME).isEqualTo("jinfra-core");
        assertThat(CoreConstants.EMPTY).isEmpty();
    }
}
