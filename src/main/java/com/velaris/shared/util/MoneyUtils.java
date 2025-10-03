package com.velaris.shared.util;

import lombok.experimental.UtilityClass;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

@UtilityClass
public class MoneyUtils {

    public String formatCurrency(BigDecimal amount, Locale locale) {
        if (amount == null) return null;
        NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
        return formatter.format(amount);
    }

    public BigDecimal add(BigDecimal a, BigDecimal b) {
        return safe(a).add(safe(b));
    }

    public BigDecimal subtract(BigDecimal a, BigDecimal b) {
        return safe(a).subtract(safe(b));
    }

    public BigDecimal multiply(BigDecimal a, BigDecimal b) {
        return safe(a).multiply(safe(b));
    }

    public BigDecimal divide(BigDecimal a, BigDecimal b, int scale) {
        if (b == null || BigDecimal.ZERO.compareTo(b) == 0) throw new ArithmeticException("Division by zero");
        return safe(a).divide(b, scale, RoundingMode.HALF_UP);
    }

    public BigDecimal round(BigDecimal value, int scale) {
        return safe(value).setScale(scale, RoundingMode.HALF_UP);
    }

    public boolean isPositive(BigDecimal value) {
        return safe(value).compareTo(BigDecimal.ZERO) > 0;
    }

    public BigDecimal applyPercentage(BigDecimal value, BigDecimal percent) {
        return multiply(value, divide(percent, BigDecimal.valueOf(100), 4));
    }

    public long toMinorUnits(BigDecimal value) {
        return safe(value).multiply(BigDecimal.valueOf(100)).longValue();
    }

    private BigDecimal safe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
