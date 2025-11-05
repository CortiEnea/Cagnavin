package org.vaadin.example.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.vaadin.example.entities.Proposal;
import org.vaadin.example.entities.Users;
import org.vaadin.example.security.CustomUserDetails;
import org.vaadin.example.services.ProposalService;

import java.util.ArrayList;
import java.util.List;

@Route("")
@PageTitle("Home")

public class HomeView extends VerticalLayout {

    private final ProposalService proposalService;
    private int currentIndex = 0;
    private final List<Image> images = new ArrayList<>();
    private Div imageContainer;
    private final List<Button> dots = new ArrayList<>();
    private TextField destination, wineCellar, cellarAddress;
    private final CustomUserDetails userDetails;


    public HomeView(ProposalService proposalService) {
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        userDetails = (CustomUserDetails) VaadinSession.getCurrent().getAttribute("user");

        addComponent();
        this.proposalService = proposalService;
    }

    public void addComponent() {
        getStyle()
                .set("margin", "0")
                .set("padding", "0")
                .set("min-height", "100vh")
                .set("overflow-x", "hidden");

        // Navbar
        HorizontalLayout navbar = new NavbarView();
        add(navbar);

        // Aggiungi il Carousel
        add(createCarousel());

        add(createChiSiamoSection());

        add(createHistorySection());

        add(createProposalSection());

        // Newsletter Section
        add(createNewsletterSection());

        HorizontalLayout footer = new FooterView();
        add(footer);

    }

    private VerticalLayout createCarousel() {
        Div carouselContainer = new Div();
        carouselContainer.addClassName("carousel-container");

        imageContainer = new Div();
        imageContainer.addClassName("carousel-image-container");

        images.add(new Image("images/gruppone.jpg", "Image 1"));
        images.add(new Image("images/gruppo.jpg", "Image 2"));
        images.add(new Image("images/gruppozzo.jpg", "Image 3"));
        images.add(new Image("images/gita.jpg", "Image 3"));

        images.forEach(img -> {
            img.addClassName("carousel-image");
            imageContainer.add(img);
        });

        HorizontalLayout dotsContainer = new HorizontalLayout();
        dotsContainer.addClassName("carousel-dots");

        for (int i = 0; i < images.size(); i++) {
            final int index = i;
            Button dot = new Button("", e -> setCurrentSlide(index));
            dot.addClassName("carousel-dot");
            dots.add(dot);
            dotsContainer.add(dot);
        }
        updateDots();

        // Overlay con gradiente
        Div overlay = new Div();
        overlay.addClassName("carousel-overlay");

        // Assembla il carousel
        carouselContainer.add(imageContainer, dotsContainer, overlay);

        // Avvia l'autoplay
        UI.getCurrent().setPollInterval(5000);
        UI.getCurrent().addPollListener(e -> moveCarousel(1));

        return new VerticalLayout(carouselContainer);
    }

    private void setCurrentSlide(int index) {
        currentIndex = index;
        updateCarousel();
        updateDots();
    }

    private void moveCarousel(int direction) {
        currentIndex = (currentIndex + direction + images.size()) % images.size();
        updateCarousel();
        updateDots();
    }

    private void updateCarousel() {
        imageContainer.removeAll();
        imageContainer.add(images.get(currentIndex));
    }

    private void updateDots() {
        for (int i = 0; i < dots.size(); i++) {
            dots.get(i).removeClassName("active");
            if (i == currentIndex) {
                dots.get(i).addClassName("active");
            }
        }
    }

    private HorizontalLayout createChiSiamoSection() {
        HorizontalLayout chiSiamoSection = new HorizontalLayout();
        chiSiamoSection.setAlignItems(Alignment.CENTER);
        chiSiamoSection.setWidthFull();
        chiSiamoSection.addClassName("chisiamo-section");
        chiSiamoSection.addClassName("responsive-section");

        VerticalLayout form = new VerticalLayout();
        form.setAlignItems(Alignment.CENTER);
        form.setWidthFull();

        H2 title = new H2("Chi siamo?");
        title.addClassName("section-title");

        Paragraph description = new Paragraph("Quella che un tempo era semplicemente una gita tra colleghi di lavoro nel Piemonte, nel 2014 è diventata la celebre gita del Gruppo Cagnavin. Il nome \"Cagnavin\" nacque in modo spontaneo, ispirato dalla reazione stupita della gerente di un agriturismo, colpita dalla quantità di vino e cibo consumata dai partecipanti.\n" +
                "\n" +
                "Le gite del Gruppo Cagnavin sono aperte a chiunque desideri trascorrere una giornata in allegria, all'insegna del motto \"mangiare e bere in compagnia\". Il gruppo non ha uno statuto ufficiale ed è senza scopo di lucro: ogni partecipante contribuisce con una quota che copre i costi del bus e del ristorante.\n" +
                "\n" +
                "Ogni anno si organizzano due uscite: una in primavera e una in autunno. Le destinazioni principali sono Piemonte, Trentino, Aosta e Lombardia, ma ogni anno si esplorano anche nuove località.");
        description.addClassName("section-description");

        form.add(title, description);

        VerticalLayout image = new VerticalLayout();
        image.setAlignItems(Alignment.CENTER);

        Image monkey = new Image("images/logo.png", "Monkey");
        monkey.setWidth("150px");
        monkey.addClassName("responsive-image");
        image.add(monkey);

        chiSiamoSection.add(form, image);
        return chiSiamoSection;
    }

    private HorizontalLayout createHistorySection() {
        HorizontalLayout historySection = new HorizontalLayout();
        historySection.setAlignItems(Alignment.CENTER);
        historySection.setWidthFull();
        historySection.addClassName("responsive-section");

        VerticalLayout form = new VerticalLayout();
        form.setAlignItems(Alignment.CENTER);

        H2 title = new H2("Rivivi tutte le esperienze");
        title.getStyle().set("color", "white");

        Paragraph description = new Paragraph("Riguarda tutte le gite del gruppo Cagnavin grazie allo storico.");
        description.getStyle().set("color", "white");

        Button showHistory = new Button("Visualizza");
        showHistory.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(TripView.class)));

        showHistory.addClassName("newsletter-button");

        form.add(title, description, showHistory);

        VerticalLayout image = new VerticalLayout();
        image.setAlignItems(Alignment.CENTER);

        Image img = new Image("images/vino.jpg", "");
        img.setWidth("40%");
        img.getStyle()
            .set("border-radius", "22px")
            .set("margin", "20px");
        image.add(img);

        historySection.add(image, form);
        return historySection;
    }

    private HorizontalLayout createProposalSection() {
        HorizontalLayout proposalSection = new HorizontalLayout();
        proposalSection.setAlignItems(Alignment.CENTER);
        proposalSection.setWidthFull();
        proposalSection.addClassName("proposal-section");
        proposalSection.addClassName("responsive-section");

        VerticalLayout form = new VerticalLayout();
        form.setAlignItems(Alignment.CENTER);

        H2 title = new H2("Hai idee per gite future?");
        title.getStyle().set("color", "#4f0175");


        Paragraph description = new Paragraph("Conosci un luogo per una gita e vorresti proporla?");

        Paragraph desc = new Paragraph("Usa il form alla seguente pagina per fare una proposta!");

        Dialog proposalDialog = new Dialog();
        proposalDialog.addClassName("responsive-dialog");
        proposalDialog.setHeight("400px");
        proposalDialog.setWidth("500px");
        
        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setAlignItems(Alignment.CENTER);
        dialogLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        proposalDialog.setHeaderTitle("Proponi la tua idea");

        destination = new TextField("Citta di destinazione");
        wineCellar = new TextField("Nome della cantina");
        cellarAddress = new TextField("Indirizzo della cantina");
        
        destination.addClassName("custom-field");
        wineCellar.addClassName("custom-field");
        cellarAddress.addClassName("custom-field");
        
        Button cancelButton = new Button("Cancel", e -> {
            proposalDialog.close();
            wineCellar.setValue("");
            destination.setValue("");
            cellarAddress.setValue("");
        });
        Button saveButton = new Button("Invia Proposta", e -> saveProposal(proposalDialog));
        
        saveButton.addClassName("create-button");
        cancelButton.addClassName("cancel-button");

        dialogLayout.add(destination, wineCellar, cellarAddress);
        proposalDialog.add(dialogLayout);
        proposalDialog.getFooter().add(cancelButton, saveButton);

        Button btnProposal = new Button("Proponi");

        btnProposal.addClickListener(e -> openProposalDialog(proposalDialog));

        btnProposal.addClassName("newsletter-button");


        form.add(title, description, desc, btnProposal);

        VerticalLayout image = new VerticalLayout();
        image.setAlignItems(Alignment.CENTER);

        Image wine = new Image("images/a.jpg", "Wine");
        wine.setWidth("50%");
        wine.getStyle()
            .set("border-radius", "22px")
            .set("margin", "20px");

        image.add(wine);
        proposalSection.add(form, image);
        return proposalSection;
    }

    private VerticalLayout createNewsletterSection() {
        VerticalLayout newsletterSection = new VerticalLayout();
        newsletterSection.addClassName("newsletter-section");
        newsletterSection.addClassName("responsive-section");
        newsletterSection.setAlignItems(Alignment.CENTER);

        H2 title = new H2("Vuoi essere sempre aggiornato?");
        title.getStyle().set("color", "white");

        Paragraph description = new Paragraph("Iscriviti alla nostra newsletter per non perderti nessuna novità del gruppo.");
        description.getStyle().set("color", "white");

        HorizontalLayout form = new HorizontalLayout();
        form.addClassName("responsive-form");
        form.setAlignItems(Alignment.CENTER);
        form.setSpacing(true);

        EmailField emailField = new EmailField();
        emailField.setPlaceholder("Enter your email");
        emailField.addClassName("newsletter-input");
        emailField.getStyle().setBackgroundColor("white");

        Button subscribeButton = new Button("Iscriviti");
        subscribeButton.addClassName("newsletter-button");
        subscribeButton.addClickListener(e -> {

        });

        form.add(emailField, subscribeButton);
        newsletterSection.add(title, description, form);

        return newsletterSection;
    }

    private void saveProposal(Dialog proposalDialog) {

        Proposal proposal = new Proposal();
        if (!destination.getValue().isBlank() && !wineCellar.getValue().isBlank() && !cellarAddress.getValue().isBlank()) {
            Users user = userDetails.getUser();
            proposal.setDestination(destination.getValue());
            proposal.setUsername(user);
            proposal.setWineCellarName(wineCellar.getValue());
            proposal.setWineCellarAddress(cellarAddress.getValue());
            proposalService.addProposal(proposal);
            wineCellar.setValue("");
            destination.setValue("");
            cellarAddress.setValue("");
            proposalDialog.close();
            Notification.show("Proposta invita con successo", 3000, Notification.Position.TOP_CENTER);

        } else {
            Notification.show("Compila tutti i campi!", 3000, Notification.Position.TOP_CENTER);
            destination.setValue("");
            wineCellar.setValue("");
            cellarAddress.setValue("");
        }
    }

    private void openProposalDialog(Dialog proposalDialog) {
        if (userDetails == null) {
            String currentRoute = UI.getCurrent().getInternals().getActiveViewLocation().getPath();
            VaadinSession.getCurrent().setAttribute("redirectAfterLogin", currentRoute);
            getUI().ifPresent(ui -> ui.navigate(LoginView.class));
        } else {
            proposalDialog.open();
        }
    }
}
