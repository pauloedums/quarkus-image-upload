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
import javax.ws.rs.core.Response.Status;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import br.com.petmach.model.PetRequestBody;
import br.com.petmach.model.PetRequestDTO;
import br.com.petmach.model.PetResponseDTO;
import br.com.petmach.repository.PetImageRepository;
import br.com.petmach.resources.PetImageResource;
import br.com.petmach.services.ImageUploadService;


@Path("/image")
public class ImageUploadResource {

    @Inject
    PetImageRepository petImageRepository;

    @Inject
    PetImageResource petImageResource;

    @Inject
    ImageUploadService imageUploadService;

    @POST
    @Transactional
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadFile(
        @MultipartForm PetRequestBody petRequestBody) throws IOException {
        
        PetRequestDTO petRequestDTO = imageUploadService.createRequest(petRequestBody);

        petImageRepository.persist(petRequestDTO);
        
        PetResponseDTO petResponseDTO = imageUploadService.createResponse(petRequestDTO);
        
        return petImageRepository
                .findByIdOptional(petRequestDTO.getId())
                .map(pet -> Response.ok(petResponseDTO).build())
                .orElse(Response.status(Status.NOT_FOUND).build());
    }

    @GET
    @Path("/download/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response downloadFile(
        @PathParam("id") Long id) throws IOException {

        if(petImageResource.get(id) == null){
            return Response.ok(Response.Status.BAD_REQUEST).build();
        }

        PetRequestDTO petRequestDTO = petImageRepository.findById(id);
        
        PetResponseDTO petResponseDTO = imageUploadService.createResponse(petRequestDTO);

        return petImageRepository
                .findByIdOptional(petRequestDTO.getId())
                .map(pet -> Response.ok(petResponseDTO).build())
                .orElse(Response.status(Status.NOT_FOUND).build());
    }

    @PUT
    @Path("/update/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response updateFile(
        @PathParam("id") Long id,
        @MultipartForm PetRequestBody petRequestBody) throws IOException {
        
        if(petImageResource.get(id) == null){
            return Response.ok(Response.Status.BAD_REQUEST).build();
        }
        
        PetRequestDTO petRequestDTO = petImageRepository.findById(id);
        PetResponseDTO petResponseDTO = imageUploadService.createResponse(petRequestDTO);

        petImageRepository.persist(petRequestDTO);
        
        PetRequestDTO petRequestDTO2 = petImageResource.update(id, petRequestDTO);

        return petImageRepository
                .findByIdOptional(petRequestDTO2.getId())
                .map(pet -> Response.ok(petResponseDTO).build())
                .orElse(Response.status(Status.NOT_FOUND).build());

    }

    @DELETE
    @Transactional
    @Path("/delete/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public String deleteFile(
        @PathParam("id") Long id) {
        
        try {
            if(petImageResource.get(id) == null){
                return "Esta imagem não existe.";
            }
            PetRequestDTO petRequestDTO = petImageRepository.findById(id);
            petImageRepository.delete(petRequestDTO);
            return "Imagem deletada com sucesso!";
        
        } catch (Exception e) {
            e.printStackTrace();
            return "Erro ao deletar a imagem";
        }

    }
    

}
