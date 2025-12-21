package com.Thunderlight11jk.bedwars.generator;

import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;

@Getter
public enum GeneratorType {

    IRON(XMaterial.IRON_INGOT, 20, 48),
    GOLD(XMaterial.GOLD_INGOT, 160, 16),
    DIAMOND(XMaterial.DIAMOND, 600, 4),
    EMERALD(XMaterial.EMERALD, 1200, 4);

    private final XMaterial material;
    private final int baseDelay;
    private final int maxItems;

    GeneratorType(XMaterial material, int baseDelay, int maxItems) {
        this.material = material;
        this.baseDelay = baseDelay;
        this.maxItems = maxItems;
    }
}
