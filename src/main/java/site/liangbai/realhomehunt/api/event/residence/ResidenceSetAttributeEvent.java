package site.liangbai.realhomehunt.api.event.residence;

import org.bukkit.entity.Player;
import site.liangbai.realhomehunt.api.event.EventCancellable;
import site.liangbai.realhomehunt.api.residence.Residence;
import site.liangbai.realhomehunt.api.residence.attribute.IAttributable;

public class ResidenceSetAttributeEvent extends EventCancellable<ResidenceSetAttributeEvent> {
    private final Player owner;
    private final Residence residence;
    private final IAttributable<?> attributable;

    private String value;

    public ResidenceSetAttributeEvent(Player owner, Residence residence, IAttributable<?> attributable, String value) {
        this.owner = owner;
        this.residence = residence;
        this.attributable = attributable;
        this.value = value;
    }

    public Player getOwner() {
        return owner;
    }

    public Residence getResidence() {
        return residence;
    }

    public IAttributable<?> getAttributable() {
        return attributable;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
