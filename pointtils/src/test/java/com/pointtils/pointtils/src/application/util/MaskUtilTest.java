package com.pointtils.pointtils.src.application.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class MaskUtilTest {

    @Test
    void shouldNotMaskNullCpf() {
        assertNull(MaskUtil.maskCpf(null));
    }

    @Test
    void shouldNotMaskCpfLongerThan11Digits() {
        assertEquals("111111111111", MaskUtil.maskCpf("111111111111"));
    }

    @Test
    void shouldNotMaskCpfSmallerThan11Digits() {
        assertEquals("123", MaskUtil.maskCpf("123"));
    }

    @Test
    void shouldMaskCpf() {
        assertEquals("123.***.***-01", MaskUtil.maskCpf("12345678901"));
    }
}
