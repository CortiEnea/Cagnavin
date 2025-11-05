package org.vaadin.example.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.vaadin.example.entities.Participant;
import org.vaadin.example.entities.Proposal;
import org.vaadin.example.entities.Request;
import org.vaadin.example.entities.Users;
import org.vaadin.example.security.CustomUserDetails;
import org.vaadin.example.services.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Route("profile")
@PageTitle("Profilo")
public class ProfileView extends VerticalLayout {

    private final CustomUserDetails userDetails;
    private final UsersService usersService;
    private final RequestService requestService;
    private final ProposalService proposalService;
    private final ParticipantService participantService;
    private final LoginService loginService;
    private final Users user;
    private final BCryptPasswordEncoder passwordEncoder;

    private final TextField emailField = new TextField();
    private final PasswordField passwordField = new PasswordField();
    private final PasswordField newPasswordField = new PasswordField();
    private final PasswordField confirmPasswordField = new PasswordField();

    public ProfileView(UsersService usersService, RequestService requestService, ProposalService proposalService, ParticipantService participantService, LoginService loginService, BCryptPasswordEncoder passwordEncoder) {
        this.usersService = usersService;
        this.requestService = requestService;
        this.proposalService = proposalService;
        this.participantService = participantService;
        this.loginService = loginService;
        this.passwordEncoder = passwordEncoder;
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        userDetails = (CustomUserDetails) VaadinSession.getCurrent().getAttribute("user");
        user = usersService.findByUsername(userDetails.getUser().getUsername());

        addComponents();
    }

    private void addComponents() {

        setSizeFull();
        getStyle()
                .set("font-family", "\"Inter\", -apple-system, BlinkMacSystemFont, \"Segoe UI\", Roboto, sans-serif");

        NavbarView navbarView = new NavbarView();
        add(navbarView);

        VerticalLayout mainContent = new VerticalLayout();
        mainContent.setMaxWidth("900px");
        mainContent.getStyle()
                .set("background-color", "white")
                .set("border-radius", "16px")
                .set("box-shadow", "0 10px 30px rgba(0, 0, 0, 0.08)")
                .set("padding", "2.5rem")
                .set("margin-top", "100px")
                .set("margin", "0 auto");
        mainContent.setSpacing(false);
        mainContent.setPadding(false);

        // Header section
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);
        header.setAlignItems(Alignment.CENTER);
        header.getStyle()
                .set("padding-bottom", "1.5rem")
                .set("border-bottom", "1px solid #f0f0f0");

        H3 username = new H3(user.getUsername());
        username.getStyle()
                .set("font-size", "1.2rem")
                .set("font-weight", "600")
                .set("color", "#333")
                .set("margin", "0");

        H2 title = new H2("Gestisci il tuo profilo");
        title.getStyle()
                .set("font-size", "1.8rem")
                .set("font-weight", "700")
                .set("color", "#7b2cbf")
                .set("margin", "0");

        Image profileImage = createProfileImage();
        profileImage.setWidth("80px");
        profileImage.setHeight("80px");
        profileImage.getStyle()
                .set("border-radius", "50%")
                .set("border", "3px solid #7b2cbf");

        header.add(username, title, profileImage);
        mainContent.add(header);

        // Email and Upload section
        HorizontalLayout emailSection = new HorizontalLayout();
        emailSection.setWidthFull();
        emailSection.setJustifyContentMode(JustifyContentMode.BETWEEN);

        emailField.setLabel("Modifica la tua email");
        emailField.setWidth("400px");
        emailField.addClassName("custom-field");
        emailField.getStyle()
                .set("background-color", "#f9f5fc")
                .set("border-radius", "8px");
        emailField.setValue(user.getEmail());
        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes("image/jpeg", "image/png", "image/gif");
        upload.setMaxFiles(1);
        upload.addSucceededListener(event -> {
            try (InputStream fileData = buffer.getInputStream()) {
                byte[] imageBytes = fileData.readAllBytes();
                user.setProfilePicture(imageBytes);
                StreamResource resource = new StreamResource("profile-picture",
                        () -> new ByteArrayInputStream(imageBytes));
                profileImage.setSrc(resource);
                Notification.show("Immagine profilo caricata correttamente", 3000, Notification.Position.TOP_CENTER);
            } catch (IOException e) {
                e.printStackTrace();
                Notification.show("Errore nel caricamento dell'immagine", 3000, Notification.Position.TOP_CENTER);
            }
        });
        upload.getStyle()
                .set("background-color", "#f0f0f0")
                .set("border-radius", "8px")
                .set("padding", "0.8rem 1.5rem");

        emailSection.add(emailField, upload);
        mainContent.add(emailSection);

        // Password section
        VerticalLayout passwordSection = new VerticalLayout();
        passwordSection.setSpacing(false);
        Paragraph resetPassword = new Paragraph("Cambia la password");
        resetPassword.getStyle()
                .set("font-size", "1.1rem")
                .set("font-weight", "600")
                .set("color", "#333")
                .set("margin", "0.5rem 0");

        configurePasswordField(passwordField, "Inserisci la vecchia password");
        configurePasswordField(newPasswordField, "Inserisci la nuova password");
        configurePasswordField(confirmPasswordField, "Conferma la password");
        passwordField.addClassName("custom-field");
        newPasswordField.addClassName("custom-field");
        confirmPasswordField.addClassName("custom-field");

        passwordSection.add(resetPassword, passwordField, newPasswordField, confirmPasswordField);
        mainContent.add(passwordSection);

        // Button section
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);

        Button deleteButton = new Button("Elimina Profilo", event -> openDeleteDialog(user));
        deleteButton.getStyle()
                .set("background-color", "#e63946")
                .set("color", "white")
                .set("padding", "0.8rem 1.5rem")
                .set("border-radius", "8px")
                .set("font-weight", "500");

        Button logoutButton = new Button("Logout", event -> logout());
        logoutButton.getStyle()
                .set("background-color", "#7b2cbf")
                .set("color", "white")
                .set("padding", "0.8rem 1.5rem")
                .set("border-radius", "8px")
                .set("font-weight", "500");

        buttonLayout.add(deleteButton, logoutButton);
        mainContent.add(buttonLayout);

        // Save button
        Button saveButton = new Button("Salva modifiche", event -> handleSave());
        saveButton.getStyle()
                .set("background-color", "#7b2cbf")
                .set("color", "white")
                .set("padding", "1rem 2.5rem")
                .set("border-radius", "30px")
                .set("font-weight", "600")
                .set("box-shadow", "0 4px 12px rgba(123, 44, 191, 0.2)")
                .set("align-self", "center");

        mainContent.add(saveButton);
        add(mainContent);

        FooterView footer = new FooterView();
        footer.getStyle().set("margin-top", "auto");
        add(footer);
    }

    private void configurePasswordField(PasswordField field, String label) {
        field.setLabel(label);
        field.setWidth("400px");
        field.setRevealButtonVisible(true);
        field.getStyle()
                .set("background-color", "#f9f5fc")
                .set("border-radius", "8px");
    }

    private void handleSave() {
        user.setEmail(emailField.getValue());

        if (!passwordField.getValue().isEmpty() || !newPasswordField.getValue().isEmpty() || !confirmPasswordField.getValue().isEmpty()) {
            if (passwordField.getValue().isEmpty() || newPasswordField.getValue().isEmpty() || confirmPasswordField.getValue().isEmpty()) {
                Notification.show("Per cambiare la password, compila tutti i campi password", 3000, Notification.Position.TOP_CENTER);
                return;
            }

            if (!loginService.authenticate(user.getUsername(), passwordField.getValue())) {
                Notification.show("La password attuale non è corretta", 3000, Notification.Position.TOP_CENTER);
                return;
            }

            if (!newPasswordField.getValue().equals(confirmPasswordField.getValue())) {
                Notification.show("Le nuove password non coincidono", 3000, Notification.Position.TOP_CENTER);
                return;
            }

            user.setPassword(passwordEncoder.encode(newPasswordField.getValue()));

            passwordField.clear();
            newPasswordField.clear();
            confirmPasswordField.clear();

        }

        usersService.aggiungiUtente(user);
        Notification.show("Profilo aggiornato", 3000, Notification.Position.TOP_CENTER);
    }

    private Image createProfileImage() {
        if (user.getProfilePicture() != null && user.getProfilePicture().length > 0) {
            StreamResource resource = new StreamResource("profile-picture",
                    () -> new ByteArrayInputStream(user.getProfilePicture()));
            return new Image(resource, "Immagine profilo");
        } else {

            return new Image("images/default-profile.jpg", "Immagine profilo");
        }
    }

    private void openDeleteDialog(Users user) {
        Dialog dialog = new Dialog();
        dialog.setHeight("200px");
        dialog.setWidth("500px");
        dialog.setHeaderTitle("Confermi di voler eliminare il tuo account?");
        dialog.setHeaderTitle("Confermi di voler eliminare il tuo account?");
        Paragraph message = new Paragraph("Tutte le tue eventuali iscrizioni e proposte di gita verranno eliminate");
        dialog.add(message);
        Button cancelButton = new Button("Annulla", e -> dialog.close());
        cancelButton.addClassName("cancel-button");

        Button confirmButton = new Button("Conferma", e -> {
            deleteUserInfo(user);
            dialog.close();
            VaadinSession.getCurrent().close();
            getUI().ifPresent(ui -> ui.navigate(HomeView.class));

        });
        confirmButton.addClassName("create-button");
        dialog.getFooter().add(cancelButton, confirmButton);
        dialog.open();
        Notification.show("Profilo eliminato", 3000, Notification.Position.BOTTOM_END);
    }

    private void deleteUserInfo(Users user) {
        List<Request> requestList = requestService.findByUser(user);
        for (Request request : requestList) {
            requestService.delete(request);
        }

        List<Proposal> proposalList = proposalService.findByUser(user);
        for (Proposal proposal : proposalList) {
            proposalService.delete(proposal);
        }

        List<Participant> participantList = participantService.findByUser(user);
        for (Participant participant : participantList) {
            participantService.delete(participant);
        }
        usersService.delete(user);
    }

    private void logout() {
        VaadinSession.getCurrent().close();
        getUI().ifPresent(ui -> ui.navigate(HomeView.class));
    }
}
