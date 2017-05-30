package org.vaadin.petter.devday.commands;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.datefield.DateResolution;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import org.vaadin.petter.devday.commands.domain.CreatePatient;
import org.vaadin.petter.devday.commands.domain.FindAllPatients;
import org.vaadin.petter.devday.commands.domain.Patient;
import org.vaadin.petter.devday.commands.spi.Commands;
import org.vaadin.petter.devday.commands.spi.Queries;

import javax.servlet.annotation.WebServlet;
import java.util.List;

@Theme(ValoTheme.THEME_NAME)
public class MyUI extends UI {

    private DateField bornAfter;
    private TextField firstName;
    private TextField lastName;
    private DateField birthDate;
    private Grid<Patient> patientGrid;

    @Override
    protected void init(VaadinRequest request) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.setMargin(true);
        layout.setSpacing(true);
        setContent(layout);

        HorizontalLayout filterBar = new HorizontalLayout();
        filterBar.setWidth("100%");
        filterBar.setSpacing(true);
        layout.addComponent(filterBar);

        bornAfter = new DateField("Born after");
        bornAfter.setResolution(DateResolution.DAY);
        filterBar.addComponent(bornAfter);

        Button filter = new Button("Find Patients", this::find);
        filterBar.addComponent(filter);
        filterBar.setComponentAlignment(filter, Alignment.BOTTOM_LEFT);

        Button openLongRunning = new Button("Long Running Operation", evt -> getUI().addWindow(new MyLongRunningWindow()));
        filterBar.addComponent(openLongRunning);
        filterBar.setComponentAlignment(openLongRunning, Alignment.BOTTOM_RIGHT);
        filterBar.setExpandRatio(openLongRunning, 1.0f);

        patientGrid = new Grid<>(Patient.class);
        patientGrid.setSizeFull();
        layout.addComponent(patientGrid);
        layout.setExpandRatio(patientGrid, 1.0f);

        Label title = new Label("Add new patient");
        title.addStyleName(ValoTheme.LABEL_H2);
        layout.addComponent(title);

        HorizontalLayout form = new HorizontalLayout();
        form.setSpacing(true);
        layout.addComponent(form);

        firstName = new TextField("First name");
        form.addComponent(firstName);

        lastName = new TextField("Last name");
        form.addComponent(lastName);

        birthDate = new DateField("Birth date");
        birthDate.setResolution(DateResolution.DAY);
        form.addComponent(birthDate);

        Button add = new Button("Add patient", this::add);
        layout.addComponent(add);
    }

    private void find(Button.ClickEvent event) {
        List<Patient> result = Queries.getInstance().ask(new FindAllPatients(bornAfter.getValue()));
        patientGrid.setItems(result);
    }

    private void add(Button.ClickEvent event) {
        Patient createdPatient = Commands.getInstance()
            .tell(new CreatePatient(firstName.getValue(), lastName.getValue(), birthDate.getValue()));
        Notification.show(createdPatient + " was successfully created");
    }

    @WebServlet(urlPatterns = "/*")
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false, heartbeatInterval = 5)
    public static class Servlet extends VaadinServlet {
    }
}
