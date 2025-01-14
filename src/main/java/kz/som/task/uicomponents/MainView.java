package kz.som.task.uicomponents;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import kz.som.task.dto.ContactRequestDTO;
import kz.som.task.dto.ContactResponseDTO;
import kz.som.task.services.ContactService;
import kz.som.task.utils.PdfGenerator;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.util.List;

@Route
public class MainView extends VerticalLayout {

    private final ContactService contactService;
    private final Grid<ContactResponseDTO> grid = new Grid<>(ContactResponseDTO.class);

    private final TextField nameField = new TextField("Name");
    private final TextField phoneNumberField = new TextField("Phone Number");
    private final TextField emailField = new TextField("Email");
    private final TextField addressField = new TextField("Address");

    private ContactResponseDTO selectedContact;

    @Autowired
    public MainView(ContactService contactService) {
        this.contactService = contactService;

        configureGrid();
        configureForm();

        HorizontalLayout mainContent = new HorizontalLayout(grid, createFormLayout());
        mainContent.setSizeFull();
        grid.setSizeFull();

        Button downloadButton = createDownloadButton();

        add(mainContent, downloadButton);
        setSizeFull();

        updateGrid();
    }

    private void configureGrid() {
        grid.setColumns("name", "phoneNumber", "email", "address");
        grid.asSingleSelect().addValueChangeListener(event -> selectContact(event.getValue()));
    }

    private void configureForm() {
        Button saveButton = new Button("Save", event -> saveContact());
        Button deleteButton = new Button("Delete", event -> deleteContact());

        HorizontalLayout buttonsLayout = new HorizontalLayout(saveButton, deleteButton);
        buttonsLayout.setSpacing(true);

        VerticalLayout formLayout = createFormLayout();
        formLayout.add(buttonsLayout);

        add(formLayout);
    }

    private VerticalLayout createFormLayout() {
        VerticalLayout formLayout = new VerticalLayout(nameField, phoneNumberField, emailField, addressField);
        formLayout.setSpacing(true);
        formLayout.setPadding(true);
        return formLayout;
    }

    private void updateGrid() {
        grid.setItems(contactService.findAllContacts());
    }

    private void selectContact(ContactResponseDTO contact) {
        this.selectedContact = contact;
        if (contact != null) {
            nameField.setValue(contact.name());
            phoneNumberField.setValue(contact.phoneNumber());
            emailField.setValue(contact.email());
            addressField.setValue(contact.address());
        } else {
            clearForm();
        }
    }

    private void saveContact() {
        if (selectedContact == null) {
            ContactRequestDTO newContact = new ContactRequestDTO(
                    nameField.getValue(),
                    phoneNumberField.getValue(),
                    emailField.getValue(),
                    addressField.getValue()
            );
            contactService.createContact(newContact);
            Notification.show("Contact added");
        } else {
            ContactRequestDTO updatedContact = new ContactRequestDTO(
                    nameField.getValue(),
                    phoneNumberField.getValue(),
                    emailField.getValue(),
                    addressField.getValue()
            );
            contactService.updateContact(selectedContact.id(), updatedContact);
            Notification.show("Contact updated");
        }
        updateGrid();
        clearForm();
    }

    private void deleteContact() {
        if (selectedContact != null) {
            contactService.deleteContactById(selectedContact.id());
            Notification.show("Contact deleted");
            updateGrid();
            clearForm();
        }
    }

    private void clearForm() {
        selectedContact = null;
        nameField.clear();
        phoneNumberField.clear();
        emailField.clear();
        addressField.clear();
    }

    private Button createDownloadButton() {
        Button downloadButton = new Button("Download PDF");
        downloadButton.addClickListener(event -> {
            List<ContactResponseDTO> contacts = contactService.findAllContacts();
            byte[] pdfContent = PdfGenerator.generateContactsPdf(contacts);

            StreamResource resource = new StreamResource("contacts.pdf", () -> new ByteArrayInputStream(pdfContent));
            Anchor anchor = new Anchor(resource, "Download");
            anchor.getElement().setAttribute("download", true);
            add(anchor);
            Notification.show("PDF generated. Click the link to download.");
        });
        return downloadButton;
    }
}
