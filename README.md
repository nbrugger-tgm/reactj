

<img src="media/Thumbnail.png" alt="Unbenannt" style="zoom:25%;" />

This library introduces **easy** reactive Bindings in Java, very useful to create a MVC UI without back draws. 

### Example

Create a View

```java
public class DataView extends ReactiveView<DataController, JPanel, ReactiveModel<Data>> {
    private JPanel            panel;
    
    private JTextField        nameInput;
    private JComboBox<Gender> genderJComboBox;
    private JButton           selectButton ;
    
    public DataView(DataController controller) {
        super(controller);
    }
    
    @Override
    protected JPanel createView() {
        panel           = new JPanel();
        
        nameInput       = new JTextField();
        genderJComboBox = new JComboBox<>(Gender.values());
        selectButton    = new JButton("Reset");
        
        nameInput.setColumns(10);
        
        panel.add(nameInput);
        panel.add(genderJComboBox);
        panel.add(selectButton);
        
        return panel;
    }
    
    @Override
    public void createBindings(ReactiveBinder bindings) {
        bindings.bindBi("name", nameInput::setText, nameInput::getText);
        bindings.bindBi("gender", genderJComboBox::setSelectedItem, genderJComboBox::getSelectedItem);
        
        //add actions to react to
        nameInput.getDocument().addUndoableEditListener(bindings::react);
        genderJComboBox.addActionListener(bindings::react);
    }
    
    @Override
    public void registerListeners(PersonController controller) {
        selectButton.addActionListener(controller::reset);
    }
}
```

Then we need a Pojo/Model to sync the View with

```java
public class Data { 
	//change reactive name
	@Reactive("gender")
	private Gender personsGender;
	private String name;

	//This will not be reacted to
	@Unreactive
	private String address;

	public void setName(String name) {
		this.name = name;
	}

	public void setGender(Gender gender) {
		this.personsGender = gender;
	}
}
```

Now we need to bind the view to a Person object

```java
ReactiveProxy<Data> proxy = ReactiveObject.create(Data.class);
Data model = proxy.object;
DataController controller = new DataController(model);
DataView view = new DataView();
view.setData(proxy.reactive);

//now you just need to display the view on a JFrame
```

