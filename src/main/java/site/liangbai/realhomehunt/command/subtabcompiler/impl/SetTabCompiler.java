package site.liangbai.realhomehunt.command.subtabcompiler.impl;

import org.bukkit.command.CommandSender;
import site.liangbai.realhomehunt.command.subtabcompiler.ISubTabCompiler;
import site.liangbai.realhomehunt.manager.ResidenceManager;
import site.liangbai.realhomehunt.residence.Residence;
import site.liangbai.realhomehunt.residence.attribute.IAttributable;
import site.liangbai.realhomehunt.residence.attribute.map.AttributeMap;

import java.util.List;

public final class SetTabCompiler implements ISubTabCompiler {
    @Override
    public List<String> handle(CommandSender sender, int length, String[] args) {
        if (length == 2) {
            return AttributeMap.getTypes();
        }

        if (length == 3) {
            String type = args[1].toLowerCase();

            Residence residence = ResidenceManager.getResidenceByOwner(sender.getName());

            if (residence == null) return null;

            Class<? extends IAttributable<?>> attributeClass = AttributeMap.getMap(type);

            try {
                IAttributable<?> attribute = residence.getAttributeWithoutType(attributeClass);

                return attribute.allowValues();
            } catch (Throwable ignored) {
                return null;
            }
        }

        return null;
    }
}
