package kz.som.task.controllers;

import kz.som.task.dto.ContactRequestDTO;
import kz.som.task.dto.ContactResponseDTO;
import kz.som.task.services.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @GetMapping
    public List<ContactResponseDTO> getAllContacts() {
        return contactService.findAllContacts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactResponseDTO> getContactById(@PathVariable String id) {
        return contactService.getContactById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ContactResponseDTO createContact(@RequestBody ContactRequestDTO contactRequestDTO) {
        return contactService.createContact(contactRequestDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContactResponseDTO> updateContact(
            @PathVariable String id,
            @RequestBody ContactRequestDTO contactRequestDTO
    ) {
        try {
            return ResponseEntity.ok(contactService.updateContact(id, contactRequestDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContact(@PathVariable String id) {
        contactService.deleteContactById(id);
        return ResponseEntity.noContent().build();
    }
}
