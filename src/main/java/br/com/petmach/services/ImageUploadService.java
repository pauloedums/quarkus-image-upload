package br.com.petmach.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.zip.GZIPOutputStream;

import javax.enterprise.context.ApplicationScoped;

import br.com.petmach.model.PetRequestBody;
import br.com.petmach.model.PetRequestDTO;
import br.com.petmach.model.PetResponseDTO;

@ApplicationScoped
public class ImageUploadService {
    
    public PetResponseDTO createResponse(PetRequestDTO petRequestDTO) throws IOException{
        
        PetResponseDTO petResponseDTO = new PetResponseDTO();

        byte[] compressed = compress(petRequestDTO.getFile());

        String encodedMime = Base64.getEncoder().encodeToString(compressed);
        String imageFile = new String("data:image/"+ petRequestDTO.getFileExtension() +";base64," + encodedMime.substring(0, 51) + "..."+ encodedMime.substring(Math.max(0, encodedMime.length() - 3)));

        petResponseDTO.setId(petRequestDTO.getId());
        petResponseDTO.setFile(imageFile);
        petResponseDTO.setFileName(petRequestDTO.getFileName());
        petResponseDTO.setFileExtension(petRequestDTO.getFileExtension());

        return petResponseDTO;
    }

    public PetRequestDTO createRequest(PetRequestBody petRequestBody) throws IOException{

        PetRequestDTO petRequestDTO = new PetRequestDTO();

        byte[] fileContent = petRequestBody.getFile().readAllBytes();
        byte[] encodedAsBytes = Base64.getMimeEncoder().encode(fileContent);

        byte[] compressed = compress(encodedAsBytes);
                
        petRequestDTO.setFile(compressed);
        petRequestDTO.setFileName(petRequestBody.getFileName());
        petRequestDTO.setFileExtension(petRequestBody.getFileExtension());

        return petRequestDTO;
    }
    
    public static byte[] compress(final byte[] bytesImage) throws IOException {
        if ((bytesImage == null) || (bytesImage.toString().length() == 0)) {
          return null;
        }
        ByteArrayOutputStream obj = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(obj);
        gzip.write(bytesImage);
        gzip.flush();
        gzip.close();
        return obj.toByteArray();
      }
}
