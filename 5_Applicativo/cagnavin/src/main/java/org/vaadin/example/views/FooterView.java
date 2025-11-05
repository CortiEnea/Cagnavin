package org.vaadin.example.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.theme.lumo.Lumo;
import com.vaadin.flow.theme.lumo.LumoIcon;

public class FooterView extends HorizontalLayout {
    public FooterView() {
        setWidthFull();
        getStyle()
                .set("background", "rgba(26, 11, 46, 0.3)");
        VerticalLayout socialLinks = new VerticalLayout();
        socialLinks.setPadding(false);
        socialLinks.getStyle()
                .set("margin-top", "1rem")
                .set("margin-left", "1rem")
                .set("color", "white");

        H4 social = new H4("Social");
        social.getStyle().set("color", "white");

        Anchor facebookLink = new Anchor("https://www.facebook.com/gruppo.cagnavin/", "Facebook");
        Anchor instagramLink = new Anchor("https://www.instagram.com/cagnavin/", "Instagram");
        facebookLink.getStyle()
                .set("color", "white");
        instagramLink.getStyle()
                .set("color", "white");


        socialLinks.add(social, facebookLink, instagramLink);

        VerticalLayout copyright = new VerticalLayout();
        copyright.setPadding(false);
        copyright.setAlignItems(Alignment.CENTER);
        copyright.setJustifyContentMode(JustifyContentMode.CENTER);
        Paragraph myCopyright = new Paragraph(" Copyright © Gruppo Cagnavin - 2025 ");
        myCopyright.getStyle()
                        .set("color", "white");

        copyright.add(myCopyright);

        VerticalLayout logo = new VerticalLayout();
        logo.getStyle().set("margin-top", "0px")
                        .set("margin-bottom", "0px")
                        .set("margin-right", "20px");

        logo.setPadding(false);
        logo.setAlignItems(Alignment.END);
        Image monkey = new Image("images/logo.png", "Monkey");
        monkey.getStyle()
                .set("height", "100px");
        logo.add(monkey);

        // Allinea la sezione newsletter a destra
        setJustifyContentMode(JustifyContentMode.BETWEEN);
        add(socialLinks, copyright, logo);
    }
}

