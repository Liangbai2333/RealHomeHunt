package site.liangbai.realhomehunt.api.residence.attribute.impl;

import java.util.Map;

public final class GlowAttribute extends BooleanAttribute {
    public GlowAttribute() {
        set(true);
    }

    public GlowAttribute(Map<String, Object> map) {
        super(map);
    }

    @Override
    public String getName() {
        return "glow";
    }
}
