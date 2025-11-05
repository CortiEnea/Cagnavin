package org.vaadin.example.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import org.vaadin.example.entities.Participant;
import org.vaadin.example.entities.Proposal;
import org.vaadin.example.entities.Request;
import org.vaadin.example.entities.Users;
import org.vaadin.example.services.ParticipantService;
import org.vaadin.example.services.ProposalService;
import org.vaadin.example.services.RequestService;
import org.vaadin.example.services.UsersService;

import java.io.ByteArrayInputStream;
import java.util.List;

@Route("users")
@PageTitle("Lista utenti")
public class UsersListView extends VerticalLayout {

    private final UsersService usersService;
    private RequestService requestService;
    private ProposalService proposalService;
    private ParticipantService participantService;
    private List<Request> requestList;
    private List<Proposal> proposalList;
    private List<Participant> participantList;


    public UsersListView(UsersService usersService, RequestService requestService, ProposalService proposalService, ParticipantService participantService) {
        this.usersService = usersService;
        this.requestService = requestService;
        this.proposalService = proposalService;
        this.participantService = participantService;
        addComponent();
    }

    private void addComponent() {
        removeAll();

        HorizontalLayout navbar = new NavbarView();
        add(navbar);
        setSizeFull();
        getStyle().set("background", "linear-gradient(to bottom right, #4a0e2c, #2c0735)").setAlignItems(Style.AlignItems.CENTER).set("display", "flex");

        VerticalLayout usersContainer = new VerticalLayout();
        usersContainer.setWidth("80%");
        usersContainer.getStyle().set("background-color", "white").set("padding", "1.5rem").set("border-radius", "0.75rem").setHeight("100%").set("box-shadow", "0 5px 10px rgba(0, 0, 0, 0.1)").set("margin-top", "100px");

        H1 title = new H1("Lista utenti");
        title.getStyle().set("text-align", "center").set("margin-bottom", "1rem").set("color", "#4a0e2c");
        usersContainer.add(title);

        Grid<Users> usersTable = createUserTable();
        usersContainer.add(usersTable);
        add(usersContainer);

        HorizontalLayout footer = new FooterView();
        footer.getStyle().set("margin-top", "auto");
        footer.setWidthFull();
        add(footer);
    }

    private Grid<Users> createUserTable() {
        Grid<Users> grid = new Grid<>(Users.class, false);
        grid.getStyle().set("padding", "10px");
        grid.getStyle().set("border-spacing", "10px");

        grid.addComponentColumn(user -> {
            if (user.getProfilePicture() != null && user.getProfilePicture().length > 0) {
                StreamResource resource = new StreamResource("profile-picture",
                        () -> new ByteArrayInputStream(user.getProfilePicture()));
                Image image = new Image(resource, "Immagine profilo");
                image.setWidth("50px");
                image.setHeight("50px");
                image.getStyle().set("border-radius", "50%");
                return image;
            } else {
                Image defaultImage = new Image("images/default-profile.jpg", "Immagine di default");
                defaultImage.setWidth("50px");
                defaultImage.setHeight("50px");
                defaultImage.getStyle().set("border-radius", "50%");
                return defaultImage;
            }
        }).setHeader("Foto Profilo").setAutoWidth(true);

        grid.addColumn(Users::getUsername)
                .setHeader("Username")
                .setAutoWidth(true);
        grid.addColumn(Users::getEmail)
                .setHeader("Indirizzo email")
                .setAutoWidth(true);
        Span actionsHeader = new Span("Azioni");
        actionsHeader.getStyle()
                .set("width", "100%")
                .set("text-align", "right")
                .set("padding-right", "10px");

        grid.addComponentColumn(user -> {
                    HorizontalLayout buttonLayout = new HorizontalLayout();
                    buttonLayout.setWidthFull();
                    buttonLayout.setJustifyContentMode(JustifyContentMode.END);

                    Button rejectButton = new Button(VaadinIcon.TRASH.create(), event ->
                            openConfirmDeleteDialog(user));
                    rejectButton.getStyle().set("background-color", "#ad0000")
                            .set("color", "white");

                    buttonLayout.add(rejectButton);
                    return buttonLayout;
                })
                .setAutoWidth(true);

        grid.setItems(usersService.all());
        grid.setWidthFull();

        return grid;
    }

    private void openConfirmDeleteDialog(Users user) {
        Dialog dialog = new Dialog();
        dialog.setHeight("200px");
        dialog.setWidth("500px");
        dialog.setHeaderTitle("Confermi di voler eliminare l'account di " + user.getUsername() + "?");
        Paragraph message = new Paragraph("Tutte le tue eventuali iscrizioni e proposte di gita verranno eliminate");
        dialog.add(message);
        Button cancelButton = new Button("Annulla", e -> dialog.close());
        cancelButton.addClassName("cancel-button");

        Button saveButton = new Button("Conferma", e -> {
            requestList = requestService.findByUser(user);
            for (Request request : requestList) {
                requestService.delete(request);
            }

            proposalList = proposalService.findByUser(user);
            for (Proposal proposal : proposalList) {
                proposalService.delete(proposal);
            }

            participantList = participantService.findByUser(user);
            for (Participant participant : participantList) {
                participantService.delete(participant);
            }
            usersService.delete(user);
            addComponent();
            dialog.close();
        });
        saveButton.addClassName("create-button");
        dialog.getFooter().add(cancelButton, saveButton);
        dialog.open();
    }
}
