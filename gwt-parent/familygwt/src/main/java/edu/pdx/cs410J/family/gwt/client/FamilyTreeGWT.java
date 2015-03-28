package edu.pdx.cs410J.family.gwt.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import edu.pdx.cs410J.family.Person;
import edu.pdx.cs410J.family.FamilyTree;

/**
 * The entry point of the Family Tree GWT Web Application
 */
public class FamilyTreeGWT extends SimplePanel implements EntryPoint {
  private FamilyTreeList treeList;
  private FamilyTree tree = new FamilyTree();
  private boolean isDirty = false;
  private final FamilyTreeServiceAsync service;
  private final PersonPanel personPanel;

  public FamilyTreeGWT() {
    service = FamilyTreeServiceAsync.Factory.create();

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

    personPanel = new PersonPanel(this);
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

    getFamilyTreeService().getFamilyTree(new AsyncCallback<FamilyTree>() {

      public void onFailure(Throwable throwable) {
        Window.alert("While invoking remote method: " + throwable.getMessage());
      }

      public void onSuccess(FamilyTree tree) {
        if (tree != null) {
          FamilyTreeGWT.this.tree = tree;
          treeList.fillInList(tree);
        }
      }
    });

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
    final DialogBox open = new DialogBox(false, true);
    open.setText("Open Family Tree XML File");

    final FormPanel form = new FormPanel();
    form.setAction("upload");
    form.setMethod(FormPanel.METHOD_POST);
    form.setEncoding(FormPanel.ENCODING_MULTIPART);

    VerticalPanel panel = new VerticalPanel();
    panel.add(new Label("Choose a file to upload"));

    final FileUpload upload = new FileUpload();
    upload.setName("xmlFile");
    panel.add(upload);

    HorizontalPanel buttons = new HorizontalPanel();

    buttons.add(new Button("Upload", new ClickListener() {
      public void onClick(Widget sender) {
        form.submit();
      }
    }));
    buttons.add(new Button("Cancel", new ClickListener() {
      public void onClick(Widget widget) {
        open.hide();
      }
    }));

    panel.add(buttons);

    form.addFormHandler(new FormHandler() {

      public void onSubmit(FormSubmitEvent event) {
        if (upload.getFilename().length() == 0) {
          Window.alert("No file name specified");
          event.setCancelled(true);
        }
      }

      public void onSubmitComplete(FormSubmitCompleteEvent event) {
        getFamilyTreeService().getFamilyTree(new AsyncCallback<FamilyTree>() {

          public void onFailure(Throwable throwable) {
            Window.alert("While invoking remote method: " + throwable.getMessage());
          }

          public void onSuccess(FamilyTree tree) {
            FamilyTreeGWT.this.tree = tree;
            treeList.fillInList(tree);
            open.hide();
          }
        }); 
      }
    });

    form.add(panel);

    open.add(form);

    open.center();
    open.show();
  }

  private FamilyTreeServiceAsync getFamilyTreeService() {
    return service;
  }

  private void newPerson() {
    EditPersonDialog dialog = new EditPersonDialog(this);
    dialog.center();
  }

  private void showPerson(Person person) {
    personPanel.showPerson(person); 
  }

  public void onModuleLoad() {
    RootPanel.get().add(new FamilyTreeGWT());
  }

  public void displayFather() {
    Person person = this.treeList.getSelectedPerson();
    if (person != null) {
      Person father = person.getFather();
      if (father != null) {
        this.treeList.setSelectedPerson(father);
        showPerson(father);
      }
    }
  }

  public void displayMother() {
    Person person = this.treeList.getSelectedPerson();
    if (person != null) {
      Person mother = person.getMother();
      if (mother != null) {
        this.treeList.setSelectedPerson(mother);
        showPerson(mother);
      }
    }
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
