package org.vaadin.petter.devday.threadsample;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import javax.servlet.annotation.WebServlet;
import java.util.Optional;

@Push
public class MyUI extends UI {

    private VerticalLayout layout;

    @Override
    protected void init(VaadinRequest request) {
        layout = new VerticalLayout();
        setContent(layout);
        new Thread(this::pollBackend).start();
    }

    private void pollBackend() {
        while (true) {
            try {
                System.out.println("Polling backend...");
                Optional<String> latestMessage = MyBackend.getLatestMessage();
                if (latestMessage.isPresent()) {
                    access(() -> layout.addComponent(new Label(latestMessage.get())));
                }

                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    @WebServlet(urlPatterns = "/*")
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class Servlet extends VaadinServlet {
    }
}
