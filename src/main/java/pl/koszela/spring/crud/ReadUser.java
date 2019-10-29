package pl.koszela.spring.crud;

import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.koszela.spring.entities.*;
import pl.koszela.spring.repositories.*;

import java.util.Objects;

@Service
public class ReadUser {

    private UsersRepo usersRepo;

    @Autowired
    public ReadUser(UsersRepo usersRepo) {
        this.usersRepo = Objects.requireNonNull(usersRepo);
    }

    public EntityUser getUser(String name, String surname) {
        EntityUser find = usersRepo.findEntityUserByEntityPersonalDataNameAndEntityPersonalDataSurname(name, surname);

        VaadinSession.getCurrent().getSession().setAttribute("personalData", find.getEntityPersonalData());
        VaadinSession.getCurrent().getSession().setAttribute("inputData", find.getInputData());
        VaadinSession.getCurrent().getSession().setAttribute("entityWindowsFromRepo", find.getEntityWindows());
        VaadinSession.getCurrent().getSession().setAttribute("entityKolnierzFromRepo", find.getEntityKolnierz());
        VaadinSession.getCurrent().getSession().setAttribute("tiles", find.getTiles());
        VaadinSession.getCurrent().getSession().setAttribute("accesories", find.getUserAccesories());
        VaadinSession.getCurrent().getSession().setAttribute("gutter", find.getEntityUserGutter());

        return find;
    }
}