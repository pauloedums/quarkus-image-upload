package br.com.petmach.resources;

import br.com.petmach.model.PetRequestDTO;
import br.com.petmach.repository.PetImageRepository;
import io.quarkus.hibernate.orm.rest.data.panache.PanacheRepositoryResource;
import io.quarkus.rest.data.panache.ResourceProperties;

@ResourceProperties(exposed = false)
public interface PetImageResource extends PanacheRepositoryResource<PetImageRepository, PetRequestDTO, Long>  {
    
}
