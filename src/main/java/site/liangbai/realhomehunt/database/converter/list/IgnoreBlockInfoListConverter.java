package site.liangbai.realhomehunt.database.converter.list;

import site.liangbai.realhomehunt.api.residence.Residence;
import site.liangbai.realhomehunt.util.Ref;

import javax.persistence.Converter;

@Converter
public class IgnoreBlockInfoListConverter extends JsonListConverter<Residence.IgnoreBlockInfo> {
    @Override
    public Residence.IgnoreBlockInfo covertJsonToEntityAttribute(String jsonText) {
        Residence.IgnoreBlockInfo info = Ref.newInstance(Residence.IgnoreBlockInfo.class);

        return info.convertToEntityAttribute(jsonText);
    }
}
