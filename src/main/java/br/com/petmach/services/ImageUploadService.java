package br.com.petmach.services;

import javax.enterprise.context.ApplicationScoped;

import br.com.petmach.model.PetRequestBody;
import br.com.petmach.model.PetRequestDTO;
import br.com.petmach.model.PetResponseDTO;

import java.io.IOException;
import java.util.Base64;

@ApplicationScoped
public class ImageUploadService {
    
    public PetResponseDTO createResponse(PetRequestDTO petRequestDTO){
        
        PetResponseDTO petResponseDTO = new PetResponseDTO();

        String encodedMime = Base64.getEncoder().encodeToString(petRequestDTO.getFile());

        petResponseDTO.setId(petRequestDTO.getId());
        petResponseDTO.setFile(encodedMime);
        petResponseDTO.setFileName(petRequestDTO.getFileName());
        petResponseDTO.setFileExtension(petRequestDTO.getFileExtension());

        return petResponseDTO;
    }

    public PetRequestDTO createRequest(PetRequestBody petRequestBody) throws IOException{

        PetRequestDTO petRequestDTO = new PetRequestDTO();

        byte[] fileContent = petRequestBody.getFile().readAllBytes();
        byte[] encodedAsBytes = java.util.Base64.getMimeEncoder().encode(fileContent);
                
        petRequestDTO.setFile(encodedAsBytes);
        petRequestDTO.setFileName(petRequestBody.getFileName());
        petRequestDTO.setFileExtension(petRequestBody.getFileExtension());

        return petRequestDTO;
    }
}
