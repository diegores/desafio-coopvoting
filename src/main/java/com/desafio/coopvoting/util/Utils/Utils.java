package com.desafio.coopvoting.util.Utils;

public class Utils {
    public static boolean isValidCpf(String cpf) {
        // Remove pontos, espaços e traços do CPF
        cpf = cpf.replaceAll("[.\\s-]", "");

        // Verifica se o CPF tem 11 dígitos
        if (cpf.length() != 11) {
            return false;
        }

        // Verifica se todos os caracteres do CPF são números
        if (!cpf.matches("\\d+")) {
            return false;
        }

        // Calcula os dígitos verificadores
        int[] digits = new int[11];
        for (int i = 0; i < 11; i++) {
            digits[i] = Integer.parseInt(cpf.substring(i, i + 1));
        }

        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += digits[i] * (10 - i);
        }

        int mod = sum % 11;
        int expectedDigit1 = (mod < 2) ? 0 : 11 - mod;

        if (digits[9] != expectedDigit1) {
            return false;
        }

        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += digits[i] * (11 - i);
        }

        mod = sum % 11;
        int expectedDigit2 = (mod < 2) ? 0 : 11 - mod;

        return digits[10] == expectedDigit2;
    }

    public static String cleanCpf(String cpf) {
        // Remove pontos, espaços e traços do CPF
        return cpf.replaceAll("[.\\s-]", "");
    }
}
