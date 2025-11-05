package org.vaadin.example.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import org.apache.commons.io.IOUtils;
import org.vaadin.example.entities.Trip;
import org.vaadin.example.services.TripService;

import java.io.*;
import java.time.LocalDate;
import java.util.UUID;

@Route("add-trip")
@PageTitle("Crea Gita")
public class AddTripView extends VerticalLayout {

    private final TripService tripService;
    private TextField destinationField, lunchField;
    private DatePicker datePicker;
    private IntegerField priceField, minParticipants, maxParticipants;
    private TextArea descriptionArea;
    private Upload upload;
    private MemoryBuffer buffer;
    private Image previewImage;
    private Trip trip = new Trip();

    public AddTripView(TripService tripService) {
        this.tripService = tripService;

        addClassName("add-trip-view");
        setSizeFull();
        setFlexGrow(1, this);
        getStyle()
                .set("background", "linear-gradient(to bottom right, #4a0e2c, #2c0735)")
                .set("overflow-y", "auto")
                .set("margin", "0")
                .set("padding", "0");

        HorizontalLayout navbar = new NavbarView();
        add(navbar);
        VerticalLayout vLayout = new VerticalLayout();
        vLayout.setAlignItems(Alignment.CENTER);
        vLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        vLayout.addClassName("add-trip-content");

        Div mainContainer = new Div();
        mainContainer.addClassName("add-trip-container");
        mainContainer.getStyle()
                .set("max-width", "1200px")
                .set("margin-top", "100px")
                .set("background-color", "white")
                .set("padding", "1.5rem")
                .set("border-radius", "0.75rem")
                .set("box-shadow", "0 10px 15px -3px rgb(0 0 0 / 0.1)");

        H1 title = new H1("Creazione Gita");
        title.addClassName("add-trip-title");
        title.getStyle()
                .set("margin-bottom", "2rem")
                .set("font-size", "1.875rem")
                .set("font-weight", "700")
                .set("color", "#4f0175");

        HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.setWidthFull();
        mainLayout.setSpacing(true);
        mainLayout.addClassName("add-trip-layout");

        VerticalLayout formContainer = createFormLayout();
        formContainer.setWidth("60%");
        formContainer.addClassName("add-trip-form");

        VerticalLayout imageContainer = createImageUploadLayout();
        imageContainer.setWidth("40%");
        imageContainer.addClassName("add-trip-upload");
        imageContainer.getStyle()
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("justify-content", "space-between");

        mainLayout.add(formContainer, imageContainer);
        mainContainer.add(title, mainLayout);
        vLayout.add(mainContainer);
        add(vLayout);

        FooterView footer = new FooterView();
        footer.getStyle().set("margin-top", "auto");
        add(footer);
    }

    private VerticalLayout createFormLayout() {
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setPadding(false);

        destinationField = new TextField("Destinazione");
        destinationField.setWidthFull();
        destinationField.addClassName("custom-field");

        datePicker = new DatePicker("Data");
        datePicker.setWidthFull();
        datePicker.addClassName("custom-field");

        HorizontalLayout participantsLayout = new HorizontalLayout();
        participantsLayout.addClassName("participants-fields");
        
        minParticipants = new IntegerField("Partecipanti Min");
        minParticipants.getStyle().set("width", "130px");
        minParticipants.addClassName("custom-field");

        maxParticipants = new IntegerField("Partecipanti Max");
        maxParticipants.getStyle().set("width", "130px");
        maxParticipants.addClassName("custom-field");

        participantsLayout.add(minParticipants, maxParticipants);

        priceField = new IntegerField("Prezzo");
        priceField.setSuffixComponent(new Div("CHF"));
        priceField.getStyle().set("width", "130px");
        priceField.addClassName("custom-field");

        descriptionArea = new TextArea("Descrizione");
        descriptionArea.setWidthFull();
        descriptionArea.setHeight("150px");
        descriptionArea.addClassName("custom-field");

        lunchField = new TextField("Pranzo");
        lunchField.setWidthFull();
        lunchField.setVisible(false);
        lunchField.addClassName("custom-field");

        Checkbox lunchIncluded = new Checkbox("Pranzo incluso?");
        lunchIncluded.addValueChangeListener(e -> lunchField.setVisible(e.getValue()));

        layout.add(
            destinationField,
            datePicker,
            participantsLayout,
            priceField,
            descriptionArea,
            lunchIncluded,
            lunchField
        );

        return layout;
    }

    private VerticalLayout createImageUploadLayout() {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);
        layout.setSpacing(true);

        buffer = new MemoryBuffer();
        upload = new Upload(buffer);
        upload.setAcceptedFileTypes("image/jpeg", "image/png", "image/gif");
        upload.addClassName("trip-upload");

        upload.addSucceededListener(event -> {
            try (InputStream fileData = buffer.getInputStream()) {
                byte[] imageBytes = fileData.readAllBytes();
                trip.setUrl_immagine(imageBytes);
                StreamResource resource = new StreamResource("profile-picture",
                        () -> new ByteArrayInputStream(imageBytes));
                previewImage.setSrc(resource);
                previewImage.setVisible(true);
                Notification.show("Immagine profilo caricata correttamente", 3000, Notification.Position.TOP_CENTER);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        previewImage = new Image();
        previewImage.setVisible(false);
        previewImage.addClassName("preview-image");

        Div uploadContainer = new Div();
        uploadContainer.addClassName("upload-container");
        uploadContainer.add(upload, previewImage);

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        buttonLayout.setWidthFull();
        buttonLayout.addClassName("button-container");

        Button createButton = new Button("Crea", e -> saveTrip());
        createButton.addClassName("create-button");
        buttonLayout.add(createButton);

        layout.add(uploadContainer, buttonLayout);
        return layout;
    }

    private void saveTrip() {
        if (validateInputs()) {
            trip.setDescrizione(descriptionArea.getValue());
            trip.setData(datePicker.getValue());
            trip.setDestinazione(destinationField.getValue());
            trip.setPranzo(lunchField.isVisible() ? lunchField.getValue() : "Non incluso");
            trip.setQuota(priceField.getValue());
            trip.setNMaxPartecipanti(maxParticipants.getValue());
            trip.setNMinPartecipanti(minParticipants.getValue());
            trip.setNPartecipanti(0);
            tripService.addTrip(trip);
            Notification.show("Gita aggiunta con successo", 3000, Notification.Position.TOP_CENTER);
            getUI().ifPresent(ui -> ui.navigate(TripView.class));
        }
    }

    private boolean validateInputs() {
        return destinationField.getValue() != null && !destinationField.getValue().isEmpty()
                && datePicker.getValue() != null && !datePicker.getValue().isBefore(LocalDate.now())
                && minParticipants.getValue() != null && maxParticipants.getValue() != null
                && minParticipants.getValue() <= maxParticipants.getValue()
                && priceField.getValue() != null;
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
        fileName = "images/uploads/" + UI + "_" + fileName;

        return fileName;
    }
}
