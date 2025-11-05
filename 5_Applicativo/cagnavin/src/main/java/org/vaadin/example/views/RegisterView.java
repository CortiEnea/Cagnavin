package org.vaadin.example.views;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.vaadin.example.entities.Users;
import org.vaadin.example.services.MailService;
import org.vaadin.example.services.UsersService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@Route("register")
public class RegisterView extends VerticalLayout {

    public RegisterView(UsersService usersService, MailService mailService, BCryptPasswordEncoder passwordEncoder) {
        getStyle()
                .set("min-height", "100vh")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("margin", "0")
                .set("padding", "0");


        Div formContainer = new Div();
        formContainer.getStyle()
                .set("background", "#fff")
                .set("backdrop-filter", "blur(10px)")
                .set("border", "1px solid rgba(168, 85, 247, 0.1)")
                .set("border-radius", "16px")
                .set("padding", "2rem")
                .set("box-shadow", "0 25px 50px -12px rgba(0, 0, 0, 0.25)")
                .set("width", "100%")
                .set("max-width", "400px")
                .set("position", "relative");

        Image monkey = new Image("images/logo.png", "Monkey");
        monkey.addClassName("monkey");
        formContainer.add(monkey);

        H1 title = new H1("Registrazione");
        title.getStyle()
                .set("color", "rgb(74, 14, 44)")
                .set("text-align", "center")
                .set("margin-bottom", "2rem");

        TextField username = new TextField("Nome utente");
        username.setRequiredIndicatorVisible(true);
        username.addClassName("custom-field");

        PasswordField password = new PasswordField("Password");
        password.setRequiredIndicatorVisible(true);
        password.addClassName("custom-field");

        PasswordField confirmPassword = new PasswordField("Conferma Password");
        confirmPassword.setRequiredIndicatorVisible(true);
        confirmPassword.addClassName("custom-field");

        EmailField email = new EmailField("Email");
        email.setErrorMessage("Inserisci una mail valida!");
        email.setRequiredIndicatorVisible(true);
        email.addClassName("custom-field");

        Button registerButton = new Button("Registrati");
        registerButton.addClassName("login-button");

        confirmPassword.addKeyPressListener(Key.ENTER, enter -> registerButton.click());

        registerButton.addClickListener(e -> {
            String user = username.getValue();
            String pwd = password.getValue();
            String confirmPwd = confirmPassword.getValue();
            String emailValue = email.getValue();

            if (user.isEmpty() || pwd.isEmpty() || confirmPwd.isEmpty() || emailValue.isEmpty()) {
                Notification.show("Compila tutti i campi", 3000, Notification.Position.TOP_CENTER);
                return;
            }

            if (!pwd.equals(confirmPwd)) {
                Notification.show("Le password non coincidono", 3000, Notification.Position.TOP_CENTER);
                return;
            }

            if (usersService.findByUsername(user) != null) {
                Notification.show("Username già in uso", 3000, Notification.Position.TOP_CENTER);
                return;
            }

            Users nuovoUtente = new Users();
            nuovoUtente.setUsername(user);
            nuovoUtente.setPassword(passwordEncoder.encode(pwd));
            nuovoUtente.setEmail(emailValue);

            usersService.aggiungiUtente(nuovoUtente);

            try{
                mailService.sendRegisterEmail(emailValue, user);
            }catch (Exception ex){
                System.out.println("Errore --> " + ex.getMessage());
            }

            Notification.show("Registrazione completata con successo!", 3000, Notification.Position.TOP_CENTER);
            getUI().ifPresent(ui -> ui.navigate(LoginView.class));


        });

        Button loginButton = new Button("Hai già un account? Accedi");
        loginButton.addClassName("forgot-password");
        loginButton.getStyle().set("margin-top", "1rem");
        loginButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(LoginView.class)));

        formContainer.add(title, username, email, password, confirmPassword, registerButton, loginButton);
        add(formContainer);
    }
}
