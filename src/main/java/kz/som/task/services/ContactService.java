package kz.som.task.services;

import kz.som.task.dto.ContactRequestDTO;
import kz.som.task.dto.ContactResponseDTO;
import kz.som.task.models.Contact;
import kz.som.task.repositories.ContactRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContactService {
    private final ContactRepository contactRepository;

    public List<ContactResponseDTO> findAllContacts() {
        log.info("FIND ALL CONTACTS");
        return contactRepository.findAll()
                .stream()
                .map(contact -> new ContactResponseDTO(
                        contact.getId(),
                        contact.getName(),
                        contact.getEmail(),
                        contact.getPhoneNumber(),
                        contact.getAddress()))
                .collect(Collectors.toList());
    }

    public Optional<ContactResponseDTO> getContactById(String id) {
        log.info("FIND CONTACT BY ID");

        return contactRepository.findById(id)
                .map(contact -> new ContactResponseDTO(
                        contact.getId(),
                        contact.getName(),
                        contact.getPhoneNumber(),
                        contact.getEmail(),
                        contact.getAddress()));
    }

    public ContactResponseDTO createContact(ContactRequestDTO request) {
        Contact contact = Contact.builder()
                .name(request.name())
                .phoneNumber(request.phoneNumber())
                .email(request.email())
                .address(request.address())
                .build();
        Contact savedContact = contactRepository.save(contact);

        log.info("CREATE CONTACT");

        return new ContactResponseDTO(
                savedContact.getId(),
                savedContact.getName(),
                savedContact.getPhoneNumber(),
                savedContact.getEmail(),
                savedContact.getAddress());
    }

    public ContactResponseDTO updateContact(String id, ContactRequestDTO request) {
        return contactRepository.findById(id).map(contact -> {
            contact.setName(request.name());
            contact.setPhoneNumber(request.phoneNumber());
            contact.setEmail(request.email());
            contact.setAddress(request.address());
            Contact updatedContact = contactRepository.save(contact);

            log.info("UPDATE CONTACT");

            return new ContactResponseDTO(
                    updatedContact.getId(),
                    updatedContact.getName(),
                    updatedContact.getPhoneNumber(),
                    updatedContact.getEmail(),
                    updatedContact.getAddress());
        }).orElseThrow(() -> new RuntimeException("Contact not found with id " + id));
    }

    public void deleteContactById(String id) {
        contactRepository.deleteById(id);

        log.info("DELETE USER");
    }
}
