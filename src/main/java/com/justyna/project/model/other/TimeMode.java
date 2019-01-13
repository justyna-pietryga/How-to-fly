package com.justyna.project.model.other;

import com.justyna.project.Exceptions.WrongTimeModeException;

public enum TimeMode {
    LOCAL, UTC;

    public boolean isUTC() {
        return TimeMode.valueOf("UTC").equals(this);
    }

    public static TimeMode convert(String timeMode) throws WrongTimeModeException {
        if (TimeMode.valueOf(timeMode).equals(UTC)) return UTC;
        else if (TimeMode.valueOf(timeMode).equals(LOCAL)) return LOCAL;
        else throw new WrongTimeModeException("Wrong time mode exception");
    }
}
