package br.com.petmach.repository;

import javax.enterprise.context.ApplicationScoped;

import br.com.petmach.model.PetRequestDTO;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class PetImageRepository implements PanacheRepository<PetRequestDTO>{

    public PetRequestDTO findByName(String fileName) {
        return find("fileName", fileName).firstResult();
    }
    
}
