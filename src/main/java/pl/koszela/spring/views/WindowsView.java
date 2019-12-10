package pl.koszela.spring.views;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToDoubleConverter;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;
import pl.koszela.spring.entities.main.Windows;
import pl.koszela.spring.repositories.WindowsRepository;
import pl.koszela.spring.service.GridInteraface;

import java.util.*;
import java.util.stream.Collectors;

@Route(value = WindowsView.WINDOWS, layout = MainView.class)
public class WindowsView extends VerticalLayout implements GridInteraface<Windows>, BeforeLeaveObserver {

    static final String WINDOWS = "windows";

    private WindowsRepository windowsRepository;

    private Set<Windows> setWindows = (Set<Windows>) VaadinSession.getCurrent().getSession().getAttribute("windows");

    private TreeGrid<Windows> treeGrid = new TreeGrid<>();
    private Binder<Windows> binder;

    @Autowired
    public WindowsView(WindowsRepository windowsRepository) {
        this.windowsRepository = Objects.requireNonNull(windowsRepository);

        if (setWindows == null) {
            setWindows = allWindows();
        }
        add(createGrid());
    }

    public TreeGrid<Windows> createGrid() {
        treeGrid.addHierarchyColumn(Windows::getName).setHeader("Nazwa");
        treeGrid.addColumn(Windows::getSize).setHeader("Rozmiar");
        treeGrid.addColumn(Windows::getManufacturer).setHeader("Producent");
        Grid.Column<Windows> quantityColumn = treeGrid.addColumn(Windows::getQuantity).setHeader("Ilość");
        Grid.Column<Windows> discountColumn = treeGrid.addColumn(Windows::getDiscount).setHeader("Rabat");
        treeGrid.addColumn(Windows::getUnitDetalPrice).setHeader("Cena detal");
        treeGrid.addColumn(Windows::getUnitPurchasePrice).setHeader("Cena zakupu");
        treeGrid.addColumn(Windows::getAllpriceAfterDiscount).setHeader("Cena razem detal");
        treeGrid.addColumn(Windows::getAllpricePurchase).setHeader("Cena razem zakup");
        treeGrid.addColumn(Windows::getAllprofit).setHeader("Zysk");
        treeGrid.addColumn(createComponent()).setHeader("Opcje");

        binder = new Binder<>(Windows.class);
        treeGrid.getEditor().setBinder(binder);
        readBeans(setWindows);

        TextField discountEditField = bindTextFieldToInteger(binder, new StringToIntegerConverter("Błąd"), Windows::getDiscount, Windows::setDiscount);
        itemClickListener(treeGrid, discountEditField);
        discountColumn.setEditorComponent(discountEditField);

        TextField quantityField = bindTextFieldToDouble(binder, new StringToDoubleConverter("Błąd"), Windows::getQuantity, Windows::setQuantity);
        itemClickListener(treeGrid, quantityField);
        quantityColumn.setEditorComponent(quantityField);

        closeListener(treeGrid, binder);

        treeGrid.setDataProvider(new TreeDataProvider<>(addItems(new ArrayList())));

        settingsGrid(treeGrid);
        return treeGrid;
    }

    @Override
    public TreeData<Windows> addItems(List list) {
        TreeData<Windows> treeData = new TreeData<>();
        Set<Windows> collect = setWindows.stream().filter(e -> e.getSize().equals("55x78")).collect(Collectors.toSet());
        for (Windows parent : collect) {
            List<Windows> childrens = setWindows.stream().filter(e -> !e.getSize().equals("55x78")).collect(Collectors.toList());
            for (int i = 0; i < childrens.size(); i++) {
                if (i == 0) {
                    treeData.addItem(null, parent);
                } else if (childrens.get(i).getName().equals(parent.getName())) {
                    treeData.addItem(parent, childrens.get(i));
                }
            }
        }
        return treeData;
    }

    @Override
    public ComponentRenderer<VerticalLayout, Windows> createComponent() {
        return new ComponentRenderer<>(windows -> {
            Checkbox mainCheckBox = new Checkbox("Dodać ?");
            mainCheckBox.setValue(windows.isOffer());
            mainCheckBox.addValueChangeListener(event -> {
                windows.setOffer(event.getValue());
            });
            return new VerticalLayout(mainCheckBox);
        });
    }

    private Set<Windows> allWindows() {
        return new HashSet<>(windowsRepository.findAll());
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
        BeforeLeaveEvent.ContinueNavigationAction action = event.postpone();
        Set<Windows> trueOffer = new HashSet<>();
        for (Windows windows : setWindows) {
            if (windows.isOffer()) {
                trueOffer.add(windows);
            }
        }
        VaadinSession.getCurrent().getSession().setAttribute("windows", setWindows);
        VaadinSession.getCurrent().getSession().setAttribute("windowsAfterChoose", trueOffer);
        action.proceed();
    }
}