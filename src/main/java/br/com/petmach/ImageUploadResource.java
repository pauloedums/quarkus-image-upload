package br.com.petmach;

import java.io.IOException;
import java.util.Base64;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import br.com.petmach.model.PetRequestBody;
import br.com.petmach.model.PetRequestDTO;
import br.com.petmach.model.PetResponseDTO;
import br.com.petmach.repository.PetImageRepository;
import br.com.petmach.resources.PetImageResource;


@Path("/image")
public class ImageUploadResource {

    @Inject
    PetImageRepository petImageRepository;

    @Inject
    PetImageResource petImageResource;

    @POST
    @Transactional
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadFile(
        @MultipartForm PetRequestBody petRequestBody) throws IOException {
        
        PetRequestDTO petRequestDTO = new PetRequestDTO();
        PetResponseDTO petResponseDTO = new PetResponseDTO();
        
        byte[] fileContent = petRequestBody.getFile().readAllBytes();
        byte[] encodedAsBytes = Base64.getMimeEncoder().encode(fileContent);
        
        String encodedMime = Base64.getMimeEncoder().encodeToString(encodedAsBytes);
        
        petRequestDTO.setFile(encodedAsBytes);
        petRequestDTO.setFileName(petRequestBody.getFileName());
        petRequestDTO.setFileExtension(petRequestBody.getFileExtension());

        petImageRepository.persist(petRequestDTO);

        petResponseDTO.setId(petRequestDTO.getId());
        petResponseDTO.setFile(encodedMime);
        petResponseDTO.setFileName(petRequestBody.getFileName());
        petResponseDTO.setFileExtension(petRequestBody.getFileExtension());
        
        if(!petImageRepository.isPersistent(petRequestDTO)){
            return Response.ok(Response.Status.BAD_REQUEST).build();
        }
        return Response.ok(petResponseDTO).build();
    }

    @GET
    @Path("/download/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response downloadFile(
        @PathParam("id") Long id) throws IOException {
        
        PetRequestDTO petRequestDTO = petImageRepository.findById(id);

        if(!petImageRepository.isPersistent(petRequestDTO)){
            return Response.ok(Response.Status.BAD_REQUEST).build();
        }

        PetResponseDTO petResponseDTO = new PetResponseDTO();

        String decodedMime = Base64.getEncoder().encodeToString(petRequestDTO.getFile());

        petResponseDTO.setId(petRequestDTO.getId());
        petResponseDTO.setFile(decodedMime);
        petResponseDTO.setFileName(petRequestDTO.getFileName());
        petResponseDTO.setFileExtension(petRequestDTO.getFileExtension());

        return Response.ok(petResponseDTO).build();
    }

    @PUT
    @Path("/update/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response updateFile(
        @PathParam("id") Long id,
        @MultipartForm PetRequestBody petRequestBody) throws IOException {
        
        if(petImageResource.get(id).getId() == null){
            return Response.ok(Response.Status.BAD_REQUEST).build();
        }
        
        PetRequestDTO petRequestDTO = petImageRepository.findById(id);
        PetResponseDTO petResponseDTO = new PetResponseDTO();
        
        byte[] fileContent = petRequestBody.getFile().readAllBytes();
        byte[] encodedAsBytes = Base64.getMimeEncoder().encode(fileContent);
                
        petRequestDTO.setFile(encodedAsBytes);
        petRequestDTO.setFileName(petRequestBody.getFileName());
        petRequestDTO.setFileExtension(petRequestBody.getFileExtension());

        petImageResource.update(id, petRequestDTO);

        String decodedMime = Base64.getEncoder().encodeToString(petRequestDTO.getFile());

        petResponseDTO.setId(petRequestDTO.getId());
        petResponseDTO.setFile(decodedMime);
        petResponseDTO.setFileName(petRequestDTO.getFileName());
        petResponseDTO.setFileExtension(petRequestDTO.getFileExtension());

        return Response.ok(petResponseDTO).build();
    }

    @DELETE
    @Path("/delete/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public String deleteFile(
        @PathParam("id") Long id) throws IOException {
        
        if(petImageResource.get(id).getId() == null){
            return "Esta imagem não existe";
        }
        
        petImageRepository.deleteById(id);

        return "Imagem deletada com sucesso!";
    }
    

}
