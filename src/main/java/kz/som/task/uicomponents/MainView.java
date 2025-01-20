package kz.som.task.uicomponents;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import kz.som.task.dto.ContactRequestDTO;
import kz.som.task.dto.ContactResponseDTO;
import kz.som.task.services.ContactService;
import kz.som.task.utils.PdfGenerator;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Route
public class MainView extends VerticalLayout {

    private final ContactService contactService;
    private final Grid<ContactResponseDTO> grid = new Grid<>(ContactResponseDTO.class);

    private final TextField nameField = new TextField("Name");
    private final TextField phoneNumberField = new TextField("Phone Number");
    private final EmailField emailField = new EmailField("Email");
    private final TextField addressField = new TextField("Address");

    private ContactResponseDTO selectedContact;
    private final Set<ContactResponseDTO> selectedContacts = new HashSet<>();

    @Autowired
    public MainView(ContactService contactService) {
        this.contactService = contactService;

        // Загрузка кастомного шрифта
        UI.getCurrent().getPage().addStyleSheet("https://fonts.googleapis.com/css2?family=Smooch+Sans:wght@100..900&display=swap");

        // Установка шрифта для корневого контейнера
        getStyle().set("font-family", "'Smooch-Sans'");

        configureGrid();
        configureForm();

        HorizontalLayout buttonsLayout = new HorizontalLayout(
                createSaveButton(),
                createDeleteButton(),
                createDownloadPdfButton()
        );

        add(grid, createFormLayout(), buttonsLayout);
        updateGrid();
    }

    private void configureGrid() {
        grid.setColumns("name", "phoneNumber", "email", "address");
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.asMultiSelect().addValueChangeListener(event -> {
            selectedContacts.clear();
            selectedContacts.addAll(event.getValue());
        });
        grid.addSelectionListener(selection -> {
            selectedContact = selection.getFirstSelectedItem().orElse(null);
            if (selectedContact != null) {
                populateForm(selectedContact);
            }
        });

        // Установка шрифта для таблицы
        grid.getStyle().set("font-family", "'Smooch-Sans'");
    }

    private void configureForm() {
        nameField.setPlaceholder("Enter name");
        phoneNumberField.setPlaceholder("Enter phone number");
        emailField.setPlaceholder("Enter email");
        addressField.setPlaceholder("Enter address");

        // Установка шрифта для текстовых полей
        nameField.getStyle().set("font-family", "'Smooch-Sans'");
        phoneNumberField.getStyle().set("font-family", "'Smooch-Sans'");
        emailField.getStyle().set("font-family", "'Smooch-Sans'");
        addressField.getStyle().set("font-family", "'Smooch-Sans'");

        nameField.getStyle().set("border-radius", "0");
        phoneNumberField.getStyle().set("border-radius", "0");
        emailField.getStyle().set("border-radius", "0");
        addressField.getStyle().set("border-radius", "0");

    }

    private VerticalLayout createFormLayout() {
        return new VerticalLayout(nameField, phoneNumberField, emailField, addressField);
    }

    private Button createSaveButton() {
        Button saveButton = new Button("Save", event -> {
            if (selectedContact == null) {
                ContactRequestDTO newContact = new ContactRequestDTO(
                        nameField.getValue(),
                        phoneNumberField.getValue(),
                        emailField.getValue(),
                        addressField.getValue()
                );
                contactService.createContact(newContact);
                Notification.show("Contact saved.");
            } else {
                ContactRequestDTO updatedContact = new ContactRequestDTO(
                        nameField.getValue(),
                        phoneNumberField.getValue(),
                        emailField.getValue(),
                        addressField.getValue()
                );
                contactService.updateContact(selectedContact.id(), updatedContact);
                Notification.show("Contact updated.");
            }
            clearForm();
            updateGrid();
        });
        saveButton.getStyle().set("background-color", "green");
        saveButton.getStyle().set("color", "white");
        saveButton.getStyle().set("font-family", "'Smooch-Sans'");
        saveButton.getStyle().set("border-radius", "0");
        saveButton.getStyle().set("padding", "10px");
        return saveButton;
    }

    private Button createDeleteButton() {
        Button deleteButton = new Button("Delete", event -> {
            if (!selectedContacts.isEmpty()) {
                selectedContacts.forEach(contact -> contactService.deleteContactById(contact.id()));
                Notification.show(selectedContacts.size() + " contact(s) deleted.");
            } else if (selectedContact != null) {
                contactService.deleteContactById(selectedContact.id());
                Notification.show("Contact deleted.");
            } else {
                Notification.show("No contact selected.");
            }
            clearForm();
            updateGrid();
        });
        deleteButton.getStyle().set("background-color", "red");
        deleteButton.getStyle().set("color", "white");
        deleteButton.getStyle().set("font-family", "'Smooch-Sans'");
        deleteButton.getStyle().set("border-radius", "0");
        deleteButton.getStyle().set("padding", "10px");
        return deleteButton;
    }

    private Button createDownloadPdfButton() {
        Button downloadPdfButton = new Button("Download PDF");
        downloadPdfButton.addClickListener(event -> {
            byte[] pdfContent = PdfGenerator.generateContactsPdf(contactService.findAllContacts());
            String base64Content = java.util.Base64.getEncoder().encodeToString(pdfContent);
            String fileName = "contacts.pdf";

            // Создание ссылки с использованием JavaScript
            UI.getCurrent().getPage().executeJs(
                    "const link = document.createElement('a');" +
                            "link.href = 'data:application/pdf;base64,' + $0;" +
                            "link.download = $1;" +
                            "link.click();",
                    base64Content, fileName
            );
        });
        downloadPdfButton.getStyle().set("background-color", "green");
        downloadPdfButton.getStyle().set("color", "white");
        downloadPdfButton.getStyle().set("font-family", "'Smooch-Sans'");
        downloadPdfButton.getStyle().set("border-radius", "0");
        downloadPdfButton.getStyle().set("padding", "10px");
        return downloadPdfButton;
    }

    private void populateForm(ContactResponseDTO contact) {
        nameField.setValue(contact.name());
        phoneNumberField.setValue(contact.phoneNumber());
        emailField.setValue(contact.email());
        addressField.setValue(contact.address());
    }

    private void clearForm() {
        nameField.clear();
        phoneNumberField.clear();
        emailField.clear();
        addressField.clear();
        selectedContact = null;
        selectedContacts.clear();
    }

    private void updateGrid() {
        List<ContactResponseDTO> contacts = contactService.findAllContacts();
        grid.setItems(contacts);
    }
}

