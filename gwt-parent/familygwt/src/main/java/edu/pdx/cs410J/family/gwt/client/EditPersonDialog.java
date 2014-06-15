package edu.pdx.cs410J.family.gwt.client;

import com.google.gwt.user.client.ui.*;
import edu.pdx.cs410J.family.Person;

/**
 * A dialog for editing or creating a {@link Person}.
 */
public class EditPersonDialog extends DialogBox {
  private final FamilyTreeGWT parent;
  private Person person;

  private Person mother;
  private Person father;
  private Person child;

  private TextBox idField = new TextBox();
  private TextBox firstNameField = new TextBox();
  private TextBox middleNameField = new TextBox();
  private TextBox lastNameField = new TextBox();
  private TextBox dobField = new TextBox();
  private TextBox dodField = new TextBox();
  private TextBox fatherField = new TextBox();
  private TextBox motherField = new TextBox();
  private TextBox childField = new TextBox();
  private RadioButton male = new RadioButton("gender", "male");
  private RadioButton female = new RadioButton("gender", "female");


  /**
   * Creates a new <code>EditPersonDialog</code>
   * @param parent
   */
  public EditPersonDialog(FamilyTreeGWT parent) {
    super(false, true);
    setText("Add New Person");

    this.parent = parent;
    this.fatherField.setText("Click to choose");
    this.motherField.setText("Click to choose");
    this.childField.setText("Click to choose");

    setupWidgets();
  }

  /**
   * Lays out the widgets
   */
  private void setupWidgets() {
    DockPanel panel = new DockPanel();

    Grid grid = new Grid(10, 2);
    grid.setText(0, 0, "id:");
    grid.setWidget(0, 1, idField);
    grid.setWidget(1, 0, male);
    grid.setWidget(1, 1, female);
    
  }

  /**
   * Creates or updates the being edited in this dialog
   */
  private void update() {
    this.parent.update(this.person);
  }
}
