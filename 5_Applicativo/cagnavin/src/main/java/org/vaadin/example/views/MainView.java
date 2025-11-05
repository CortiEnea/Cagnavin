package org.vaadin.example.views;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.example.GreetService;
import org.vaadin.example.entities.Users;

/**
 * A sample Vaadin view class.
 * <p>
 * To implement a Vaadin view just extend any Vaadin component and use @Route
 * annotation to announce it in a URL as a Spring managed bean.
 * <p>
 * A new instance of this class is created for every new user and every browser
 * tab/window.
 * <p>
 * The main view contains a text field for getting the user name and a button
 * that shows a greeting message in a notification.
 */
@Route("old")
public class MainView extends VerticalLayout {

    private Grid<Users> grid = new Grid<>(Users.class);

    /**
     * Construct a new Vaadin view.
     * <p>
     * Build the initial UI state for the user accessing the application.
     *
     * @param service The message service. Automatically injected Spring managed bean.
     */
    public MainView(@Autowired GreetService service) {


        TextField textField = new TextField("Your name");
        textField.addClassName("bordered");

        Button button = new Button("Say hello", e -> {
            add(new Paragraph(service.greet(textField.getValue())));
            getUI().ifPresent(ui -> ui.navigate(LoginView.class));
        });

        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        button.addClickShortcut(Key.ENTER);

        addClassName("centered-content");

        add(textField, button);
        Button a = new Button("");
    }

}
