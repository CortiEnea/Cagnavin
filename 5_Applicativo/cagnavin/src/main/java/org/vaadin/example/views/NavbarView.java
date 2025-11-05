package org.vaadin.example.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.VaadinSession;
import org.vaadin.example.security.CustomUserDetails;

public class NavbarView extends HorizontalLayout {
    private boolean isMobileMenuOpen = false;
    private final VerticalLayout mobileMenu;

    public NavbarView() {
        setWidthFull();
        setPadding(false);
        addClassName("navbar");

        Div container = new Div();
        container.addClassName("navbar-container");
        container.setWidthFull();

        H1 title = new H1("Gruppo Cagnavin");
        title.addClassName("navbar-title");

        // Menu desktop
        HorizontalLayout desktopMenu = new HorizontalLayout();
        desktopMenu.addClassName("navbar-links");
        desktopMenu.setSpacing(true);
        desktopMenu.setJustifyContentMode(JustifyContentMode.END);

        CustomUserDetails userDetails = (CustomUserDetails) VaadinSession.getCurrent().getAttribute("user");

        String[][] menuItems = getMenuItems(userDetails);

        for (String[] item : menuItems) {
            Anchor link = new Anchor(item[1], item[0]);
            link.addClassName("nav-link");
            desktopMenu.add(link);
        }

        // Pulsante menu mobile
        Button mobileMenuButton = new Button(new Icon(VaadinIcon.MENU));
        mobileMenuButton.addClassName("mobile-menu-button");
        
        // Menu mobile
        mobileMenu = new VerticalLayout();
        mobileMenu.addClassName("mobile-menu");
        mobileMenu.setVisible(false);
        mobileMenu.setPadding(true);
        mobileMenu.setSpacing(true);

        for (String[] item : menuItems) {
            Anchor link = new Anchor(item[1], item[0]);
            link.addClassName("mobile-nav-link");
            mobileMenu.add(link);
        }

        // Add click listener to toggle menu
        mobileMenuButton.addClickListener(e -> toggleMobileMenu());

        container.add(title, desktopMenu, mobileMenuButton);
        add(container, mobileMenu);
    }

    private static String[][] getMenuItems(CustomUserDetails userDetails) {
        String[][] menuItems;

        if (userDetails == null) {
            menuItems = new String[][]{
                    {"Home", "/"},
                    {"Gite", "/trip"},
                    {"Accedi", "/login"}
            };
        }else if (userDetails.getUser().getUsername().equals("Admin")){
            menuItems = new String[][]{
                    {"Home", "/"},
                    {"Gite", "/trip"},
                    {"Aggiungi gita", "/add-trip"},
                    {"Richieste", "/request"},
                    {"Utenti", "/users"},
                    {userDetails.getUser().getUsername(), "/profile"}
            };
        }else {
            menuItems = new String[][]{
                    {"Home", "/"},
                    {"Gite", "/trip"},
                    {userDetails.getUser().getUsername(), "/profile"}
            };
        }
        return menuItems;
    }

    private void toggleMobileMenu() {
        mobileMenu.setVisible(!mobileMenu.isVisible());
        if (mobileMenu.isVisible()) {
            mobileMenu.addClassName("visible");
        } else {
            mobileMenu.removeClassName("visible");
        }
    }
}
