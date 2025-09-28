package com.pointtils.pointtils.src.application.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MaskUtil {

    public static String maskCpf(String cpf) {
        if (cpf == null || cpf.length() != 11) {
            return cpf;
        }
        return cpf.substring(0, 3) + ".***.***-" + cpf.substring(9);
    }
}
