package org.vaadin.petter.devday.pollingsample;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import javax.servlet.annotation.WebServlet;
import java.util.Optional;

public class MyUI extends UI {

    private VerticalLayout layout;

    @Override
    protected void init(VaadinRequest request) {
        layout = new VerticalLayout();
        setContent(layout);
        addPollListener(event -> pollBackend());
        setPollInterval(1000);
    }

    private void pollBackend() {
        System.out.println("Polling backend...");
        Optional<String> latestMessage = MyBackend.getLatestMessage();
        if (latestMessage.isPresent()) {
            layout.addComponent(new Label(latestMessage.get()));
        }
    }

    @WebServlet(urlPatterns = "/*")
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false, heartbeatInterval = 5)
    public static class Servlet extends VaadinServlet {
    }
}
