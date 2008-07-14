package edu.pdx.cs399J.family.gwt.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import edu.pdx.cs399J.family.Person;
import edu.pdx.cs399J.family.FamilyTree;

/**
 * The entry point of the Family Tree GWT Web Application
 */
public class FamilyTreeGWT extends SimplePanel implements EntryPoint {
  private FamilyTreeList treeList;
  private FamilyTree tree = new FamilyTree();
  private boolean isDirty = false;

  public FamilyTreeGWT() {
    treeList = new FamilyTreeList();
    treeList.addChangeListener(new ChangeListener() {

      public void onChange(Widget widget) {
        Person person = treeList.getSelectedPerson();
        showPerson(person);
      }
    });

    HorizontalPanel buttons = new HorizontalPanel();
    Button newPerson = new Button("New Person");
    newPerson.addClickListener(new ClickListener() {
      public void onClick(Widget widget) {
        newPerson();
      }
    });
    buttons.add(newPerson);

    DockPanel left = new DockPanel();
    left.setSize("100%", "100%");
    left.add(treeList, DockPanel.CENTER);
    left.setCellHeight(treeList, "100%");
    left.add(buttons, DockPanel.SOUTH);
    left.setCellHorizontalAlignment(buttons, DockPanel.ALIGN_CENTER);

    PersonPanel personPanel = new PersonPanel(this);
    personPanel.setSize("100%", "100%");

    HorizontalSplitPanel split = new HorizontalSplitPanel();
    split.setLeftWidget(left);
    split.setRightWidget(personPanel);
    split.setSplitPosition("200px");
    split.setSize("100%", "100%");

    DockPanel panel = new DockPanel();
    panel.setSize("100%", "100%");

    MenuBar menuBar = new MenuBar();
    addFileMenu(menuBar);
    addPersonMenu(menuBar);
    panel.add(menuBar, DockPanel.NORTH);

    panel.add(split, DockPanel.CENTER);
    panel.setCellHeight(split, "100%");


    setWidget(panel);
  }

  private void addPersonMenu(MenuBar menuBar) {

  }

  private void addFileMenu(MenuBar menuBar) {
    MenuBar file = new MenuBar(true);
    menuBar.addItem("File", file);

    file.addItem("Open", new Command() {
      public void execute() {
        open();
      }
    });

    file.addItem("Save", new Command() {
      public void execute() {
        save();
      }
    });
  }

  private void save() {
    Window.alert("Saving family tree");
  }

  private void open() {
    Window.alert("Opening an family tree file");
  }

  private void newPerson() {
    EditPersonDialog dialog = new EditPersonDialog(this);
    dialog.center();
  }

  private void showPerson(Person person) {
    //To change body of created methods use File | Settings | File Templates.
  }

  public void onModuleLoad() {
    RootPanel.get().add(new FamilyTreeGWT());
  }

  public void displayFather() {
    //To change body of created methods use File | Settings | File Templates.
  }

  public void displayMother() {
    //To change body of created methods use File | Settings | File Templates.
  }

  public void update(Person person) {
    this.setDirty(true);
    this.showPerson(person);
    this.treeList.fillInList(this.tree);
  }

  private void setDirty(boolean isDirty) {
    this.isDirty = isDirty;
  }
}
