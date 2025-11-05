package org.vaadin.example.views;

import com.google.zxing.WriterException;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.mail.MessagingException;
import org.vaadin.example.entities.Participant;
import org.vaadin.example.entities.Request;
import org.vaadin.example.entities.Trip;
import org.vaadin.example.entities.Users;
import org.vaadin.example.services.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Route("request")
@PageTitle("Richieste Gite")
public class RequestView extends VerticalLayout {

    private final RequestService requestService;
    private final UsersService usersService;
    private final TripService tripService;
    private final ParticipantService participantService;
    private final MailService mailService;
    private List<Request> requestList;

    public RequestView(RequestService requestService, UsersService usersService, TripService tripService, ParticipantService participantService, MailService mailService) {
        this.requestService = requestService;
        this.usersService = usersService;
        this.tripService = tripService;
        this.participantService = participantService;
        this.mailService = mailService;
        addComponent();
    }

    private void addComponent() {
        removeAll();
        HorizontalLayout navbar = new NavbarView();
        add(navbar);
        requestList = requestService.all();
        setSizeFull();
        getStyle().set("background", "linear-gradient(to bottom right, #4a0e2c, #2c0735)").setAlignItems(Style.AlignItems.CENTER).set("display", "flex");
        VerticalLayout requestContainer = new VerticalLayout();
        requestContainer.setWidth("80%");
        requestContainer.getStyle().set("background-color", "white").set("padding", "1.5rem").set("border-radius", "0.75rem").setHeight("100%").set("box-shadow", "0 5px 10px rgba(0, 0, 0, 0.1)")
                .set("margin-top", "100px");
        H1 title = new H1("Richieste di partecipazione");
        title.getStyle().set("text-align", "center").set("margin-bottom", "3rem").set("color", "#4a0e2c");
        requestContainer.add(title);

        if (requestList != null && !requestList.isEmpty()) {
            for (Request request : requestList) {
                Optional<Users> user = usersService.findById(request.getUsername().getId());
                Optional<Trip> destination = tripService.findById(request.getTrip().getId());
                if (destination.isPresent() && user.isPresent()) {
                    Trip trip = destination.get();
                    Users username = user.get();
                    requestContainer.add(createRequestBlock(username.getUsername(), "Gita: " + trip.getDestinazione(), request));
                }
            }
        } else {
            VerticalLayout noRequestLayout = new VerticalLayout();
            H3 noRequest = new H3("Nessuna richiesta in sospeso");
            noRequestLayout.getStyle().setAlignItems(Style.AlignItems.CENTER)
                    .setJustifyContent(Style.JustifyContent.CENTER)
                    .set("text-align", "center");
            noRequestLayout.add(noRequest);
            requestContainer.add(noRequestLayout);
        }

        add(requestContainer);

        FooterView footer = new FooterView();

        add(footer);
    }

    private Div createRequestBlock(String username, String tripName, Request request) {
        Div requestBlock = new Div();
        requestBlock.setWidthFull();
        requestBlock.getStyle().set("display", "flex").set("align-items", "center").set("justify-content", "space-between").set("padding", "10px 15px").set("border", "1px solid #ccc")
                .set("border-radius", "8px").set("margin-bottom", "10px");

        Span userLabel = new Span(username);
        userLabel.getStyle().set("font-weight", "bold");

        Span tripLabel = new Span(tripName);

        HorizontalLayout buttonLayout = new HorizontalLayout();

        Button acceptButton = new Button("Accetta");
        acceptButton.getStyle().set("background", "#4CAF50").set("color", "white").set("border", "none").set("padding", "5px 10px").set("border-radius", "5px");

        Button rejectButton = new Button("Rifiuta");
        rejectButton.getStyle().set("background", "#E74C3C").set("color", "white").set("border", "none").set("padding", "5px 10px").set("border-radius", "5px").set("margin-left", "10px");

        rejectButton.addClickListener(e -> openConfirmDeleteDialog(username, request));
        acceptButton.addClickListener(e -> acceptRequest(username, request));

        buttonLayout.add(acceptButton, rejectButton);

        HorizontalLayout textLayout = new HorizontalLayout(userLabel, tripLabel, buttonLayout);
        textLayout.getStyle().set("display", "flex").set("align-items", "center").set("justify-content", "space-between").set("width", "100%");

        requestBlock.add(textLayout);
        return requestBlock;
    }

    private void openConfirmDeleteDialog(String username, Request request) {
        Dialog dialog = new Dialog();
        dialog.setHeight("200px");
        dialog.setWidth("500px");
        dialog.setHeaderTitle("Confermi di voler rifiutare la richiesta di " + username + "?");
        Button cancelButton = new Button("Annulla", e -> dialog.close());
        cancelButton.addClassName("cancel-button");

        Button saveButton = new Button("Conferma", e -> {
            requestService.delete(request);
            requestList.remove(request);
            addComponent();
            dialog.close();
        });
        saveButton.addClassName("create-button");
        dialog.getFooter().add(cancelButton, saveButton);
        dialog.open();
    }

    private void acceptRequest(String username, Request request) {
        Dialog dialog = new Dialog();
        dialog.setHeight("200px");
        dialog.setWidth("500px");
        dialog.setHeaderTitle("Confermi di voler accettare la richiesta di " + username + "?");
        Button cancelButton = new Button("Annulla", e -> dialog.close());
        cancelButton.addClassName("cancel-button");
        Button saveButton = new Button("Conferma", e -> {

            Participant newParticipant = new Participant();
            Users user = usersService.findByUsername(username);
            newParticipant.setTrip(request.getTrip());
            newParticipant.setUsername(user);
            participantService.addParticipant(newParticipant);
            Optional<Trip> newTrip = tripService.findById(request.getTrip().getId());

            if (newTrip.isPresent()) {
                Trip trip = newTrip.get();
                int actualParticipant = trip.getNPartecipanti();
                trip.setNPartecipanti(actualParticipant + 1);
                tripService.addTrip(trip);
            }

            try {
                mailService.sendEmailInvoice((int) request.getTrip().getQuota(), user.getEmail(), username, request.getTrip().getDestinazione());
            } catch (MessagingException | WriterException | IOException ex) {
                throw new RuntimeException(ex);
            }
            requestService.delete(request);
            addComponent();

            dialog.close();
        });
        saveButton.addClassName("create-button");
        dialog.getFooter().add(cancelButton, saveButton);
        dialog.open();
    }
}
