package org.vaadin.example.views;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.vaadin.example.services.LoginService;

@Route("login")
public class LoginView extends VerticalLayout {

    public LoginView(LoginService loginService) {
        getStyle()
                .set("min-height", "100vh")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("margin", "0")
                .set("padding", "0");

        Div formContainer = new Div();
        formContainer.addClassName("login-container");
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
        monkey.getStyle()
                .set("position", "absolute")
                .set("top", "-172px")
                .set("right", "0")
                .set("width", "150px")
                .set("height", "auto")
                .set("z-index", "1");
        formContainer.add(monkey);

        H1 title = new H1("Accedi");
        title.getStyle()
                .set("color", "rgb(74, 14, 44)")
                .set("text-align", "center")
                .set("margin-bottom", "2rem");

        TextField username = new TextField("Nome utente");
        username.addClassName("custom-field");

        PasswordField password = new PasswordField("Password");
        password.addClassName("custom-field");

        Button loginButton = new Button("Accedi");
        loginButton.addClassName("login-button");
        password.addKeyPressListener(Key.ENTER, event -> loginButton.click());

        loginButton.addClickListener(e -> {
            String user = username.getValue();
            String pwd = password.getValue();

            if (user.isEmpty() || pwd.isEmpty()) {
                Notification.show("Inserisci username e password", 3000, Notification.Position.TOP_CENTER);
                return;
            }

            if (loginService.authenticate(user, pwd)) {
                UserDetails userDetails = loginService.loadUserByUsername(user);
                Authentication auth = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(auth);

                VaadinSession.getCurrent().setAttribute("user", userDetails);

                String redirectTo = (String) VaadinSession.getCurrent().getAttribute("redirectAfterLogin");
                if (redirectTo != null) {
                    VaadinSession.getCurrent().setAttribute("redirectAfterLogin", null);
                    getUI().ifPresent(ui -> ui.navigate(redirectTo));
                } else {
                    getUI().ifPresent(ui -> ui.navigate(HomeView.class));
                }
            } else {
                Notification.show("Credenziali non valide", 3000, Notification.Position.TOP_CENTER);
                password.setValue("");
            }
        });


        Anchor forgotPassword = new Anchor("#", "Password dimenticata?");
        forgotPassword.addClassName("forgot-password");

        Anchor noAccount = new Anchor("register", "Non hai un account? Registrati!");
        noAccount.addClassName("no-account");

        formContainer.add(title, username, password, loginButton, forgotPassword, noAccount);
        add(formContainer);
    }
}
