package pl.koszela.spring.service;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.koszela.spring.entities.*;
import pl.koszela.spring.repositories.*;
import pl.koszela.spring.views.UsersView;

import java.util.*;

import static pl.koszela.spring.inputFields.ServiceNotification.getNotificationError;
import static pl.koszela.spring.inputFields.ServiceNotification.getNotificationSucces;

@Service
public class RemoveUsers {

    private PersonalDataRepository personalDataRepository;
    private UsersRepo usersRepo;
    private InputDataTilesRepository inputDataTilesRepository;
    private InputDataAccesoriesRespository inputDataAccesoriesRespository;
    private WindowsRepository windowsRepository;
    private KolnierzRepository kolnierzRepository;

    @Autowired
    public RemoveUsers(PersonalDataRepository personalDataRepository, UsersRepo usersRepo, InputDataTilesRepository inputDataTilesRepository, InputDataAccesoriesRespository inputDataAccesoriesRespository, WindowsRepository windowsRepository, KolnierzRepository kolnierzRepository) {
        this.personalDataRepository = Objects.requireNonNull(personalDataRepository);
        this.usersRepo = Objects.requireNonNull(usersRepo);
        this.inputDataTilesRepository = Objects.requireNonNull(inputDataTilesRepository);
        this.inputDataAccesoriesRespository = Objects.requireNonNull(inputDataAccesoriesRespository);
        this.windowsRepository = Objects.requireNonNull(windowsRepository);
        this.kolnierzRepository = Objects.requireNonNull(kolnierzRepository);
    }

    public void removeUser(ComboBox<String> comboBox) {
        String[] nameAndSurname = comboBox.getValue().split(" ");

        Optional<EntityPersonalData> personalData = personalDataRepository.findEntityPersonalDataByNameAndSurnameEquals(nameAndSurname[0], nameAndSurname[1]);

        if (personalData.isPresent()) {
            Optional<EntityUser> userFromRepo = usersRepo.findEntityUserByEntityPersonalDataEquals(personalData.get());
            if (userFromRepo.isPresent()) {
                EntityUser userToRemove = userFromRepo.get();
                usersRepo.delete(userToRemove);
                inputDataTilesRepository.delete(userToRemove.getEntityInputDataTiles());
                inputDataAccesoriesRespository.delete(userToRemove.getEntityInputDataAccesories());
                windowsRepository.delete(userToRemove.getEntityWindows());
                kolnierzRepository.delete(userToRemove.getEntityKolnierz());

                getNotificationSucces("Usunąłem użytkownika: " + userToRemove.getEntityPersonalData().getName() + " " + userToRemove.getEntityPersonalData().getSurname() + "   papa  :(");
            }
        } else {
            getNotificationError("Nie ma takiego użytkownika " + nameAndSurname[0] + " " + nameAndSurname[1] + " w bazie danych    :(");
        }

        VaadinSession.getCurrent().getSession().removeAttribute("personalDataFromRepo");
        VaadinSession.getCurrent().getSession().removeAttribute("tilesInputFromRepo");
        VaadinSession.getCurrent().getSession().removeAttribute("accesoriesInputFromRepo");
        VaadinSession.getCurrent().getSession().removeAttribute("entityWindowsFromRepo");
        VaadinSession.getCurrent().getSession().removeAttribute("entityKolnierzFromRepo");
        VaadinSession.getCurrent().getSession().removeAttribute("allTilesFromRepo");

        VaadinSession.getCurrent().getSession().removeAttribute("tilesInput");
        VaadinSession.getCurrent().getSession().removeAttribute("accesoriesInput");
        VaadinSession.getCurrent().getSession().removeAttribute("entityWindows");
        VaadinSession.getCurrent().getSession().removeAttribute("entityKolnierz");
        VaadinSession.getCurrent().getSession().removeAttribute("allTiles");
    }
}