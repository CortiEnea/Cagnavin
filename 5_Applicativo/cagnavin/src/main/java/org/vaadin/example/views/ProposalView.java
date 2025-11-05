package org.vaadin.example.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.vaadin.example.entities.Proposal;
import org.vaadin.example.services.ProposalService;
import org.vaadin.example.services.UsersService;


public class ProposalView extends VerticalLayout {

    private final ProposalService proposalService;

    public ProposalView(ProposalService proposalService, UsersService usersService) {
        this.proposalService = proposalService;
        addComponent();
    }

    private void addComponent() {
        removeAll();
        setSizeFull();
        getStyle().set("background", "linear-gradient(to bottom right, #4a0e2c, #2c0735)").set("padding", "1rem 2rem").setAlignItems(Style.AlignItems.CENTER).set("display", "flex");
        VerticalLayout proposalContainer = new VerticalLayout();
        proposalContainer.setWidth("80%");
        proposalContainer.getStyle().set("background-color", "white").set("padding", "1.5rem").set("border-radius", "0.75rem").setHeight("100%").set("box-shadow", "0 5px 10px rgba(0, 0, 0, 0.1)");
        H1 title = new H1("Proposte per nuove gite");
        title.getStyle().set("text-align", "center").set("margin-bottom", "3rem").set("color", "#4a0e2c");
        proposalContainer.add(title);
        Grid<Proposal> proposalTable = createProposalTable();
        proposalContainer.add(proposalTable);

        add(proposalContainer);
    }

    private Grid<Proposal> createProposalTable() {
        Grid<Proposal> grid = new Grid<>(Proposal.class, false);
        grid.getStyle().set("padding", "10px");
        grid.getStyle().set("border-spacing", "10px");

        grid.addColumn(Proposal::getDestination)
                .setHeader("Destinazione")
                .setAutoWidth(true);
        grid.addColumn(Proposal::getWineCellarName)
                .setHeader("Nome Cantina")
                .setAutoWidth(true);
        grid.addColumn(Proposal::getWineCellarAddress)
                .setHeader("Indirizzo Cantina")
                .setAutoWidth(true);
        grid.addColumn(proposal -> proposal.getUsername().getUsername())
                .setHeader("Proposto da")
                .setAutoWidth(true);

        Span actionsHeader = new Span("Azioni");
        actionsHeader.getStyle()
                .set("width", "100%")
                .set("text-align", "right")
                .set("padding-right", "10px");

        grid.addComponentColumn(proposal -> {
                    HorizontalLayout buttonLayout = new HorizontalLayout();
                    buttonLayout.setWidthFull();
                    buttonLayout.setJustifyContentMode(JustifyContentMode.END);

                    Button acceptButton = new Button("Accetta", event ->
                            acceptRequest(proposal.getUsername().getUsername(), proposal));
                    acceptButton.getStyle().set("background", "#4CAF50")
                            .set("color", "white");

                    Button rejectButton = new Button("Rifiuta", event ->
                            openConfirmDeleteDialog(proposal.getUsername().getUsername(), proposal));
                    rejectButton.getStyle().set("background", "#E74C3C")
                            .set("color", "white");

                    buttonLayout.add(acceptButton, rejectButton);
                    return buttonLayout;
                })
                .setAutoWidth(true);

        grid.setItems(proposalService.all());
        grid.setWidthFull();

        return grid;
    }

    private void openConfirmDeleteDialog(String username, Proposal proposal) {
        Dialog dialog = new Dialog();
        dialog.setHeight("200px");
        dialog.setWidth("500px");
        dialog.setHeaderTitle("Confermi di voler rifiutare la richiesta di " + username + "?");
        Button cancelButton = new Button("Annulla", e -> dialog.close());
        cancelButton.addClassName("cancel-button");

        Button saveButton = new Button("Conferma", e -> {
            proposalService.delete(proposal);
            addComponent();
            dialog.close();
        });
        saveButton.addClassName("create-button");
        dialog.getFooter().add(cancelButton, saveButton);
        dialog.open();
    }

    private void acceptRequest(String username, Proposal proposal) {
        Dialog dialog = new Dialog();
        dialog.setHeight("200px");
        dialog.setWidth("500px");
        dialog.setHeaderTitle("Confermi di voler accettare la richiesta di " + username + "?");
        Button cancelButton = new Button("Annulla", e -> dialog.close());
        cancelButton.addClassName("cancel-button");
        Button saveButton = new Button("Conferma", e -> {

        });
        saveButton.addClassName("create-button");
        dialog.getFooter().add(cancelButton, saveButton);
        dialog.open();
    }
}
