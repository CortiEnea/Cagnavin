package org.vaadin.example.views;

import com.google.zxing.WriterException;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import jakarta.mail.MessagingException;
import org.vaadin.example.entities.*;
import org.vaadin.example.security.CustomUserDetails;
import org.vaadin.example.services.ParticipantService;
import org.vaadin.example.services.ProposalService;
import org.vaadin.example.services.RequestService;
import org.vaadin.example.services.TripService;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Route("trip")
@PageTitle("Gite")
public class TripView extends VerticalLayout {
    private final RequestService requestService;
    private CustomUserDetails userDetails;
    private final ProposalService proposalService;
    private final TripService tripService;
    private final ParticipantService participantService;

    private Grid<Trip> grid;
    private Grid<Proposal> proposalGrid;
    private List<Trip> tutte;
    private List<Trip> future;
    private List<Trip> passate;
    private List<Proposal> bozze;
    private Tab tabTutte, tabFuture, tabPassate, tabBozze;
    private final TextField destinationField = new TextField();
    private final TextField lunchField = new TextField();
    private final DatePicker datePicker = new DatePicker();
    private final IntegerField priceField = new IntegerField();
    private final IntegerField minParticipants = new IntegerField();
    private final IntegerField maxParticipants = new IntegerField();
    private final TextField nParticipants = new TextField();
    private final TextArea descriptionArea = new TextArea();
    private final Image detailsImage = new Image();
    private Tabs tabs;
    private Trip trip = new Trip();

    public TripView(TripService tripService, ProposalService proposalService, ParticipantService participantService, RequestService requestService) {
        this.tripService = tripService;
        this.proposalService = proposalService;
        this.participantService = participantService;
        this.requestService = requestService;

        setSizeFull();
        setFlexGrow(1, this);

        userDetails = (CustomUserDetails) VaadinSession.getCurrent().getAttribute("user");

        addComponent();

        tabBozze.setVisible(false);
    }

    public void addComponent() {
        getStyle()
                .set("margin", "0")
                .set("padding", "0");
        // Navbar
        HorizontalLayout navbar = new NavbarView();
        add(navbar);

        H1 title = new H1("Gite");
        title.getStyle().set("align-self", "center")
                .set("margin-top", "100px")
                .set("color", "white");
        add(title);

        VerticalLayout mainContent = new VerticalLayout();
        mainContent.setMaxWidth("1200px");
        mainContent.setHeightFull();
        mainContent.getStyle()
                .set("margin", "0 auto");
        tabTutte = new Tab("Tutte");
        tabFuture = new Tab("Future");
        tabPassate = new Tab("Passate");
        tabBozze = new Tab("Bozze");
        tabs = new Tabs(tabTutte, tabFuture, tabPassate, tabBozze);

        tabs.getStyle()
                .set("background", "white")
                .set("border-radius", "3px")
                .set("margin-bottom", "0.5rem");

        mainContent.setSpacing(false);
        mainContent.setPadding(false);

        tabTutte.getStyle().set("margin-right", "20px");
        tabFuture.getStyle().set("margin-right", "20px");
        tabPassate.getStyle().set("margin-right", "20px");

        grid = new Grid<>();
        grid.getStyle().set("border-radius", "4px");
        grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
        proposalGrid = new Grid<>();
        grid.addColumn(Trip::getDestinazione).setHeader("Nome");
        grid.addColumn(Trip::getData).setHeader("Data");
        grid.addColumn(Trip::getDescrizione).setHeader("Luogo");
        grid.addComponentColumn(trip -> {
            HorizontalLayout buttonLayout = new HorizontalLayout();
            buttonLayout.setWidthFull();
            buttonLayout.setJustifyContentMode(JustifyContentMode.END);
            buttonLayout.setSpacing(false);

            Button detailsButton = new Button(VaadinIcon.EYE.create(), event -> createDetailsDialog(trip));
            detailsButton.getStyle()
                    .set("background-color", "purple")
                    .set("color", "white")
                    .set("min-width", "32px")
                    .set("padding", "4px");

            Button requestJoinButton = new Button(VaadinIcon.USER.create(), event -> requestJoinDialog(trip));
            requestJoinButton.getStyle()
                    .set("color", "purple")
                    .set("min-width", "32px")
                    .set("padding", "4px");

            Button listParticipantButton = new Button(VaadinIcon.LINES_LIST.create(), event -> participantListDialog(trip));
            listParticipantButton.getStyle()
                    .set("color", "purple")
                    .set("min-width", "32px")
                    .set("padding", "4px");

            Button rejectButton = new Button(VaadinIcon.TRASH.create(), event -> deleteTrip(trip));
            rejectButton.getStyle()
                    .set("background-color", "#ad0000")
                    .set("color", "white")
                    .set("min-width", "32px")
                    .set("padding", "4px");

            buttonLayout.add(detailsButton, requestJoinButton, listParticipantButton, rejectButton);

            rejectButton.setVisible(false);
            requestJoinButton.setVisible(false);
            listParticipantButton.setVisible(false);

            if (userDetails != null && userDetails.getUser().getUsername().equals("Admin")) {
                tabBozze.setVisible(true);
                rejectButton.setVisible(true);
                listParticipantButton.setVisible(true);
            } else if (userDetails != null && !userDetails.getUser().getUsername().equals("Admin") && !trip.getData().isBefore(LocalDate.now())) {
                requestJoinButton.setVisible(true);
            }

            return buttonLayout;
        }).setHeader("Azioni")
                .setTextAlign(ColumnTextAlign.END)
                .setWidth("150px");

        proposalGrid.addColumn(Proposal::getDestination).setHeader("Destinazione");
        proposalGrid.addColumn(Proposal::getWineCellarName).setHeader("Nome cantina");
        proposalGrid.addColumn(Proposal::getWineCellarAddress).setHeader("Indirizzo");

        proposalGrid.addComponentColumn(proposal -> {
                    HorizontalLayout buttonLayout = new HorizontalLayout();
                    buttonLayout.setWidthFull();
                    buttonLayout.setJustifyContentMode(JustifyContentMode.END);

                    Button acceptButton = new Button("Accetta", event ->
                            createDetailsProposalDialog(proposal));
                    acceptButton.getStyle().set("background", "#4CAF50")
                            .set("color", "white");

                    Button rejectPropose = new Button("Rifiuta", event ->
                            deletePropose(proposal));
                    rejectPropose.getStyle().set("background", "#E74C3C")
                            .set("color", "white");

                    buttonLayout.add(acceptButton, rejectPropose);
                    return buttonLayout;
                })
                .setAutoWidth(true);

        proposalGrid.getStyle()
                .set("background", "white")
                .set("padding", "1rem");

        refreshList();

        tabs.addSelectedChangeListener(event -> {
            Tab selectedTab = tabs.getSelectedTab();
            if (selectedTab.equals(tabTutte)) {
                mainContent.remove(proposalGrid);
                updateGrid(tutte);
                mainContent.add(grid);
            } else if (selectedTab.equals(tabFuture)) {
                mainContent.remove(proposalGrid);
                updateGrid(future);
                mainContent.add(grid);
            } else if (selectedTab.equals(tabPassate)) {
                mainContent.remove(proposalGrid);
                updateGrid(passate);
                mainContent.add(grid);
            } else if (selectedTab.equals(tabBozze)) {
                mainContent.remove(grid);
                updateGridProposal(bozze);
                mainContent.add(proposalGrid);
            }
        });

        mainContent.add(tabs, grid);
        add(mainContent);
        setupDownloadButton();

        FooterView footer = new FooterView();

        add(footer);

    }

    private void updateGrid(List<Trip> items) {
        grid.setItems(items);
    }

    private void updateGridProposal(List<Proposal> items) {
        proposalGrid.setItems(items);
    }

    private void createDetailsDialog(Trip trip) {
        Dialog detailsDialog = new Dialog();

        detailsDialog.setHeight("800px");
        detailsDialog.setWidth("600px");
        detailsDialog.addClassName("responsive-dialog"); 


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
        descriptionArea.setValue(trip.getDescrizione());

        nParticipants.setLabel("Numero di partecipanti");
        nParticipants.addClassName("custom-field");
        nParticipants.setEnabled(false);
        nParticipants.setValue(trip.getNPartecipanti() + "/" + trip.getNMaxPartecipanti());

        detailsImage.setMaxWidth("100%");
        byte[] imageBytes = trip.getUrl_immagine();
        StreamResource resource = new StreamResource("profile-picture",
                () -> new ByteArrayInputStream(imageBytes));
        detailsImage.setSrc(resource);

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

    private void createDetailsProposalDialog(Proposal proposal) {
        Dialog proposalDetails = new Dialog();
        proposalDetails.setHeight("800px");
        proposalDetails.setWidth("800px");
        proposalDetails.addClassName("custom-dialog");

        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.setWidthFull();
        mainLayout.setSpacing(true);

        // Form Container (lato sinistro)
        VerticalLayout formContainer = new VerticalLayout();
        formContainer.setWidth("60%");
        formContainer.setSpacing(true);
        formContainer.setPadding(false);

        destinationField.setLabel("Destinazione");
        destinationField.setWidthFull();
        destinationField.setValue(proposal.getDestination());
        destinationField.addClassName("custom-field");

        datePicker.setLabel("Data");
        datePicker.addClassName("custom-field");
        datePicker.setWidthFull();

        HorizontalLayout participantsLayout = new HorizontalLayout();
        minParticipants.setLabel("Partecipanti Min");
        minParticipants.addClassName("custom-field");
        minParticipants.setWidth("130px");

        maxParticipants.setLabel("Partecipanti Max");
        maxParticipants.setWidth("130px");
        maxParticipants.addClassName("custom-field");
        participantsLayout.add(minParticipants, maxParticipants);

        priceField.setLabel("Prezzo");
        priceField.setSuffixComponent(new Div("CHF"));
        priceField.setWidth("130px");
        priceField.addClassName("custom-field");

        descriptionArea.setLabel("Descrizione");
        descriptionArea.setWidthFull();
        descriptionArea.setHeight("150px");
        descriptionArea.addClassName("custom-field");

        lunchField.setLabel("Pranzo");
        lunchField.setWidthFull();
        lunchField.setVisible(false);
        lunchField.addClassName("custom-field");

        Checkbox lunchIncluded = new Checkbox("Pranzo incluso?");
        lunchIncluded.addValueChangeListener(e -> lunchField.setVisible(lunchIncluded.getValue()));

        formContainer.add(destinationField, datePicker, participantsLayout, priceField,
                descriptionArea, lunchIncluded, lunchField);

        VerticalLayout imageContainer = new VerticalLayout();
        imageContainer.setWidth("40%");
        imageContainer.setPadding(false);
        imageContainer.setSpacing(true);

        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.addSucceededListener(event -> {
            try (InputStream fileData = buffer.getInputStream()) {
                byte[] imageBytes = fileData.readAllBytes();
                trip.setUrl_immagine(imageBytes);
                StreamResource resource = new StreamResource("profile-picture",
                        () -> new ByteArrayInputStream(imageBytes));
                detailsImage.setSrc(resource);
                detailsImage.setVisible(true);
                Notification.show("Immagine profilo caricata correttamente", 3000, Notification.Position.TOP_CENTER);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        detailsImage.setVisible(false);
        detailsImage.addClassName("preview-image");

        Div uploadContainer = new Div();
        uploadContainer.addClassName("upload-container");
        uploadContainer.add(upload, detailsImage);

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        buttonLayout.setWidthFull();

        Button saveButton = new Button("Salva", e -> {

            trip.setDescrizione(descriptionArea.getValue());
            trip.setData(datePicker.getValue());
            trip.setDestinazione(destinationField.getValue());
            trip.setPranzo(lunchField.isVisible() ? lunchField.getValue() : "Non incluso");
            trip.setQuota(priceField.getValue());
            trip.setNMaxPartecipanti(maxParticipants.getValue());
            trip.setNMinPartecipanti(minParticipants.getValue());
            trip.setNPartecipanti(0);

            tripService.addTrip(trip);
            proposalService.delete(proposal);
            bozze = proposalService.all();
            updateGridProposal(bozze);
            proposalDetails.close();
        });
        saveButton.addClassName("create-button");

        Button cancelButton = new Button("Annulla", e -> proposalDetails.close());
        cancelButton.addClassName("cancel-button");

        buttonLayout.add(cancelButton, saveButton);
        imageContainer.add(uploadContainer, buttonLayout);

        mainLayout.add(formContainer, imageContainer);
        proposalDetails.add(mainLayout);
        proposalDetails.open();
    }

    private void deletePropose(Proposal proposal) {
        Dialog dialog = new Dialog();
        dialog.setHeight("200px");
        dialog.setWidth("500px");
        dialog.setHeaderTitle("Confermi di voler rifiutare la proposta?");
        Button cancelButton = new Button("Annulla", e -> dialog.close());
        cancelButton.addClassName("cancel-button");

        Button saveButton = new Button("Conferma", e -> {
            proposalService.delete(proposal);
            bozze = proposalService.all();
            updateGridProposal(bozze);
            dialog.close();
        });
        saveButton.addClassName("create-button");
        dialog.getFooter().add(cancelButton, saveButton);
        dialog.open();
    }

    private void deleteTrip(Trip trip) {
        Dialog tripDialog = new Dialog();
        tripDialog.setHeight("210px");
        tripDialog.setWidth("500px");
        tripDialog.addClassName("responsive-dialog");
        tripDialog.setHeaderTitle("Confermi di voler eliminare la gita?");
        Paragraph paragraph = new Paragraph("Eliminando la gita verranno eliminati anche i partecipanti già iscritti e le richieste di partecipazione");
        tripDialog.add(paragraph);
        Button cancelButton = new Button("Annulla", e -> tripDialog.close());
        cancelButton.addClassName("cancel-button");

        Button saveButton = new Button("Conferma", e -> {
            List<Participant> participants = participantService.findByTrip(trip);
            List<Request> requests = requestService.findByTrip(trip);
            deleteReferences(participants, requests);
            tripService.delete(trip);
            refreshList();
            tripDialog.close();
            tabs.setSelectedTab(tabs.getSelectedTab());
        });
        saveButton.addClassName("create-button");
        tripDialog.getFooter().add(cancelButton, saveButton);
        tripDialog.open();
    }

    private void deleteReferences(List<Participant> participantList, List<Request> requestList) {
        for (Participant participant : participantList) {
            participantService.delete(participant);
        }
        for (Request request : requestList) {
            requestService.delete(request);
        }
    }

    private String saveImageToServer(String fileName, byte[] imageBytes) throws IOException {
        String folderPath = "src/main/resources/static/images/uploads/";
        File directory = new File(folderPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String UI = String.valueOf(UUID.randomUUID());
        String filePath = folderPath + UI + "_" + fileName;
        File imageFile = new File(filePath);
        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            fos.write(imageBytes);
        }

        // Return the full URL path
        return "/images/uploads/" + UI + "_" + fileName;
    }

    private void refreshList() {

        tutte = tripService.all();
        future = tripService.giteFuture();
        passate = tripService.gitePassate();
        bozze = proposalService.all();

        Tab selectedTab = tabs.getSelectedTab();
        if (selectedTab.equals(tabTutte)) {
            updateGrid(tutte);
        } else if (selectedTab.equals(tabFuture)) {
            updateGrid(future);
        } else if (selectedTab.equals(tabPassate)) {
            updateGrid(passate);
        }
    }

    private void requestJoinDialog(Trip trip) {
        Dialog request = new Dialog();
        request.setHeaderTitle("Confermi di voler partecipare?");

        HorizontalLayout msg = new HorizontalLayout();
        Paragraph paragraph = new Paragraph("Vuoi inviare la tua richiesta di partecipazione alla gita verso " + trip.getDestinazione() + " in data " + trip.getData());
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

    private void participantListDialog(Trip trip) {
        Dialog participantDialog = new Dialog();
        participantDialog.setWidth("70%");
        participantDialog.setHeight("60%");
        participantDialog.setHeaderTitle("Lista di partecipanti");
        Grid<Participant> grid = new Grid<>(Participant.class, false);
        grid.getStyle().set("padding", "10px");
        grid.getStyle().set("border-spacing", "10px");

        grid.addComponentColumn(participant -> {
            if (participant.getUsername().getProfilePicture() != null && participant.getUsername().getProfilePicture().length > 0) {
                StreamResource resource = new StreamResource("profile-picture",
                        () -> new ByteArrayInputStream(participant.getUsername().getProfilePicture()));
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

        grid.addColumn(participant -> participant.getUsername().getUsername())
                .setHeader("Username")
                .setAutoWidth(true);

        grid.addColumn(Participant::isHasPay)
                .setHeader("Pagato")
                .setAutoWidth(true);

        grid.addComponentColumn(participant -> {
                    HorizontalLayout buttonLayout = new HorizontalLayout();
                    buttonLayout.setWidthFull();
                    buttonLayout.setJustifyContentMode(JustifyContentMode.END);

                    Button rejectButton = new Button(VaadinIcon.TRASH.create(), event ->
                            openConfirmDeleteDialog(participant));
                    rejectButton.getStyle().set("background-color", "#ad0000")
                            .set("color", "white");

                    buttonLayout.add(rejectButton);
                    return buttonLayout;
                })
                .setAutoWidth(true);

        grid.setItems(participantService.findByTrip(trip));
        grid.setWidthFull();
        participantDialog.add(grid);
        participantDialog.open();
    }

    private void saveRequest(Trip trip) throws MessagingException, IOException, WriterException {
        CustomUserDetails userDetails = (CustomUserDetails) VaadinSession.getCurrent().getAttribute("user");
        Request newRequest = new Request();
        newRequest.setTrip(trip);
        Users user = userDetails.getUser();
        newRequest.setUsername(user);
        newRequest.setStatus(false);
        requestService.addRequest(newRequest);
    }

    private void openConfirmDeleteDialog(Participant participant) {
        Dialog dialog = new Dialog();
        dialog.setHeight("200px");
        dialog.setWidth("500px");
        dialog.setHeaderTitle("Confermi di voler disicrivere " + participant.getUsername() + "?");
        Paragraph message = new Paragraph("Tutte le tue eventuali iscrizioni e proposte di gita verranno eliminate");
        dialog.add(message);
        Button cancelButton = new Button("Annulla", e -> dialog.close());
        cancelButton.addClassName("cancel-button");

        Button saveButton = new Button("Conferma", e -> {
            participantService.delete(participant);

            dialog.close();
        });
        saveButton.addClassName("create-button");
        dialog.getFooter().add(cancelButton, saveButton);
        dialog.open();
    }


    private void setupDownloadButton() {
        List<Trip> futureTrips = tripService.giteFuture();

        StreamResource resource = new StreamResource("future-trips.ics", () -> {
            String icsContent = generateICS(futureTrips);
            return new ByteArrayInputStream(icsContent.getBytes(StandardCharsets.UTF_8));
        });

        resource.setContentType("text/calendar;charset=utf-8");

        Anchor downloadLink = new Anchor(resource, "");
        downloadLink.getElement().setAttribute("download", true);

        Button downloadButton = new Button("Scarica gite future (.ics)");
        downloadButton.setIcon(VaadinIcon.DOWNLOAD.create());
        downloadButton.getStyle().set("color", "white");
        downloadLink.add(downloadButton);

        add(downloadLink);
    }

    private String generateICS(List<Trip> trips) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        String events = trips.stream().map(trip ->
                "BEGIN:VEVENT\n" +
                        "UID:" + UUID.randomUUID().toString() + "\n" +
                        "SUMMARY:" + sanitizeICSField(trip.getDestinazione()) + "\n" +
                        "DTSTART;VALUE=DATE:" + trip.getData().format(formatter) + "\n" +
                        "DTEND;VALUE=DATE:" + trip.getData().plusDays(1).format(formatter) + "\n" +
                        "DESCRIPTION:" + sanitizeICSField(trip.getDescrizione()) + "\n" +
                        "END:VEVENT"
        ).collect(Collectors.joining("\n"));

        return "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:-//Cagnavin//Trip Calendar//IT\r\n" +
                "CALSCALE:GREGORIAN\r\n" +
                events + "\r\n" +
                "END:VCALENDAR";
    }

    private String sanitizeICSField(String input) {
        if (input == null) return "";
        return input.replace("\n", "\\n")
                .replace(",", "\\,")
                .replace(";", "\\;")
                .replace("\"", "'");
    }
}

