package site.liangbai.realhomehunt.storage;

import site.liangbai.realhomehunt.residence.Residence;

import java.util.List;

public interface IStorage {
    void save(Residence residence);

    void remove(Residence residence);

    List<Residence> loadAll();

    int count();
}
