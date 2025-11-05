package org.vaadin.example.views;

import com.google.zxing.WriterException;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import jakarta.mail.MessagingException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.vaadin.example.entities.Request;
import org.vaadin.example.entities.Trip;
import org.vaadin.example.entities.Users;
import org.vaadin.example.security.CustomUserDetails;
import org.vaadin.example.services.MailService;
import org.vaadin.example.services.RequestService;
import org.vaadin.example.services.TripService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/*
public class OldTripView extends VerticalLayout {
    private final List<Trip> nextTrips;
    private final List<Trip> oldTrips;
    private final TripService tripService;
    private final RequestService requestService;
    private final MailService mailService;
    private final TextField destinationField = new TextField();
    private final TextField lunchField = new TextField();
    private final DatePicker datePicker = new DatePicker();
    private final IntegerField priceField = new IntegerField();
    private final TextField nParticipants = new TextField();
    private final TextArea descriptionArea = new TextArea();
    private final Image detailsImage = new Image();
    private CustomUserDetails userDetails;


    public OldTripView(TripService tripService, RequestService requestService, MailService mailService) {
        this.requestService = requestService;
        this.mailService = mailService;
        this.tripService = tripService;
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        nextTrips = tripService.giteFuture();
        oldTrips = tripService.gitePassate();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {

        } else {
            userDetails = (CustomUserDetails) SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getPrincipal();
        }
        addComponent();
    }

    public void addComponent() {
        addClassName("home-view");
        getStyle()
                .set("margin", "0")
                .set("padding", "0");
        // Navbar
        HorizontalLayout navbar = new NavbarView();
        add(navbar);

        VerticalLayout mainContent = new VerticalLayout();
        mainContent.setWidthFull();
        mainContent.setMaxWidth("1200px");
        mainContent.getStyle()
                .set("margin", "0 auto")
                .set("padding", "2rem");

        mainContent.add(createTripSection("Prossime gite", false, nextTrips));
        mainContent.add(createTripSection("Gite passate", true, oldTrips));

        setFlexGrow(1, mainContent);
        add(mainContent);

        // Footer
        FooterView footer = new FooterView();
        add(footer);
    }


    private VerticalLayout createTripSection(String title, boolean isPast, List<Trip> tripList) {
        VerticalLayout section = new VerticalLayout();
        section.setPadding(false);
        section.setSpacing(true);
        HorizontalLayout tripGrid = new HorizontalLayout();

        H2 sectionTitle = new H2(title);
        sectionTitle.getStyle()
                .set("color", "white")
                .set("margin-top", "3rem");

        if (!tripList.isEmpty()) {
            tripGrid.setWidthFull();
            tripGrid.getStyle()
                    .set("flex-wrap", "wrap")
                    .set("gap", "1rem")
                    .set("justify-content", "space-between");

            for (Trip trip : tripList) {
                tripGrid.add(createTripCard(trip.getId(), trip.getUrl_immagine(), trip.getData(), trip.getNPartecipanti(), trip.getNMaxPartecipanti()));
            }

        } else {
            Div noTripsMessage = new Div();
            noTripsMessage.setText("Non è ancora stata pianificata nessuna gita");
            noTripsMessage.getStyle()
                    .set("color", "white");

            tripGrid.add(noTripsMessage);
            tripGrid.setWidthFull();
            tripGrid.setJustifyContentMode(JustifyContentMode.CENTER);
            tripGrid.setAlignItems(Alignment.CENTER);
        }

        section.add(sectionTitle, tripGrid);
        return section;
    }

    private Div createTripCard(long id, String url, LocalDate date, int nPartecipanti, int nMaxPartecipanti) {
        Trip trip;
        Optional<Trip> optionalTrip = tripService.findById(id);
        trip = optionalTrip.orElseGet(Trip::new);
        Div card = new Div();
        card.getStyle()
                .set("background", "rgba(255, 255, 255, 0.05)")
                .set("border-radius", "16px")
                .set("padding", "1rem")
                .set("width", "300px");

        Image tripImage;
        if (url != null && !url.isEmpty()) {
            tripImage = new Image(url, "Trip preview");
        } else {
            tripImage = new Image("images/placeholder.png", "Placeholder");
        }
        tripImage.setWidth("100%");
        tripImage.getStyle().set("border-radius", "12px");

        HorizontalLayout participantLayout = new HorizontalLayout();
        participantLayout.setAlignItems(Alignment.CENTER);
        participantLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        Div participantsCount = new Div("Partecipanti: " + nPartecipanti + "/" + nMaxPartecipanti);
        participantsCount.getStyle().set("color", "white");
        participantLayout.add(participantsCount);

        HorizontalLayout cardFooter = new HorizontalLayout();
        Button detailsButton = new Button("Dettagli");
        detailsButton.addClickListener(e -> createDetailsDialog(trip));
        detailsButton.addClassName("create-button");

        if (!date.isBefore(LocalDate.now())) {
            Button requestJoinButton = new Button("Partecipa");
            requestJoinButton.addClickListener(e -> requestJoinDialog(trip));
            requestJoinButton.addClassName("create-button");
            cardFooter.add(detailsButton, requestJoinButton);
        } else {
            cardFooter.add(detailsButton);
        }

        cardFooter.setJustifyContentMode(JustifyContentMode.BETWEEN);
        cardFooter.setWidthFull();

        card.add(tripImage, participantLayout, cardFooter);
        return card;
    }

    private void createDetailsDialog(Trip trip) {
        Dialog detailsDialog = new Dialog();

        detailsDialog.setHeight("800px");
        detailsDialog.setWidth("600px");

        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setAlignItems(Alignment.CENTER);
        dialogLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        detailsDialog.setHeaderTitle("Dettagli della gita");

        destinationField.setLabel("Destinazione");
        destinationField.setValue(trip.getDestinazione());
        destinationField.addClassName("custom-field");

        datePicker.setLabel("Data");
        datePicker.addClassName("custom-field");
        datePicker.setValue(trip.getData());

        priceField.setLabel("Quota");
        priceField.addClassName("custom-field");
        priceField.setValue((int) trip.getQuota());

        descriptionArea.setLabel("Descrizione");
        descriptionArea.addClassName("custom-field");
        descriptionArea.setValue(trip.getDestinazione());

        nParticipants.setLabel("Numero di partecipanti");
        nParticipants.addClassName("custom-field");
        nParticipants.setEnabled(false);
        nParticipants.setValue(trip.getNPartecipanti() + "/" + trip.getNMaxPartecipanti());

        detailsImage.setSrc(trip.getUrl_immagine());
        detailsImage.setMaxWidth("100%");

        lunchField.setLabel("Pranzo");
        lunchField.addClassName("custom-field");
        lunchField.setValue(trip.getPranzo());
        dialogLayout.add(detailsImage, destinationField, datePicker, priceField, descriptionArea, nParticipants, lunchField);

        Button cancelButton = new Button("Annulla", e -> detailsDialog.close());
        cancelButton.addClassName("cancel-button");

        detailsDialog.add(dialogLayout);
        detailsDialog.getFooter().add(cancelButton);
        detailsDialog.open();
    }

    private void requestJoinDialog(Trip trip) {
        Dialog request = new Dialog();
        request.setHeaderTitle("Confermi di voler partecipare?");

        HorizontalLayout msg = new HorizontalLayout();
        Paragraph paragraph = new Paragraph("Vuoi inviare la tua richiesta di partecipazione alla gita verso " + trip.getDestinazione() + "in data " + trip.getData());
        msg.add(paragraph);

        Button cancelButton = new Button("Annulla", e -> request.close());
        cancelButton.addClassName("cancel-button");
        Button saveButton = new Button("Invia richiesta", e -> {
            try {
                saveRequest(trip);
            } catch (MessagingException | WriterException | IOException ex) {
                throw new RuntimeException(ex);
            }
            request.close();
        });
        saveButton.addClassName("create-button");

        request.add(msg);
        request.getFooter().add(cancelButton, saveButton);
        request.open();
    }

   private void saveRequest(Trip trip) throws MessagingException, IOException, WriterException {
    CustomUserDetails userDetails = (CustomUserDetails) VaadinSession.getCurrent().getAttribute("user");
    
    if (userDetails == null) {
        String currentRoute = UI.getCurrent().getInternals().getActiveViewLocation().getPath();
        VaadinSession.getCurrent().setAttribute("redirectAfterLogin", currentRoute);
        getUI().ifPresent(ui -> ui.navigate(LoginView.class));
    } else {
        Request newRequest = new Request();
        newRequest.setTrip(trip);
        Users user = userDetails.getUser();
        newRequest.setUsername(user);
        newRequest.setStatus(false);
        requestService.addRequest(newRequest);
    }
}



}
*/