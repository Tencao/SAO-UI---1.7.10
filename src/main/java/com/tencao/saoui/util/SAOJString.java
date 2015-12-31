package com.tencao.saoui.util;

public final class SAOJString implements SAOString {

    private final String string;

    public SAOJString(Object object) {
        string = object instanceof String ? (String) object : String.valueOf(object);
    }

    public SAOJString() {
        string = "";
    }

    @Override
    public final String toString() {
        return string;
    }

}
