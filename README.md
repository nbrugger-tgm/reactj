

<img src="D:\Users\Nils\Desktop\Workspaces\libs\reactj\media\Thumbnail.png" alt="Unbenannt" style="zoom:25%;" />

This library introduces **easy** reactive Bindings in Java, very useful to create a MVC UI without back draws. 

### Example

Create a View

```java
public class CustomComponent extends JPanel implements ReactiveComponent<CustomController> {
	
    //components that need bindings
	private JTextField surnameInput = new JTextField()
	private JComboBox<Gender> genderJComboBox = new JComboBox<>(Gender.values())
	private JButton selectButton = new JButton("Reset");
    
	public PersonalInformationComponent() {
		add(surnameInput); //method from JPanel, nothing custom
		add(genderJComboBox);
		add(selectButton);
	}

	@Override
	public void createBindings(ReactiveBinder bindings){
		//bind surename bidirectional
		bindings.bindEdit("surename",surnameInput::setText,surnameInput::getText);
		//react to change in UI
        surnameInput.addActionListener(bindings::react);

		
        //Bind is only bound one directionals so changes in the UI wont affect the model
		bindings.bind("gender",genderJComboBox::setSelectedItem);
        //react to changes in many and different ways
		bindings.bind("gender",this::adaptColorToGender);
	}

	@Override
	public void registerListeners(CustomController controller){
		selectButton.addActionListener(controller::submitInformation);
	}
	public void adaptColorToGender(Gender g){
		System.out.println("Adapt color for "+g);
	}

    //This adds automatic binding
	@Reactive("surename")
	public void setNameAsWindowTitle(String name){
		System.out.println("Set the window title to "+name);
	}
}
```

Then we need a Pojo/Model to sync the View with

```java
//optional
//FLAT = Only members of this class are reactive
//DEEP = also members from subclasses are resolved
@ReactivResolution(FLAT)
//If you dont like extending you can also do it a different way
public class Person extends ReactiveObject { 
	//change reactive name
	@Reactive("surename")
	private String name;
	private Gender gender;

	//This will not be reacted to
	@Unreactive
	private String address;

	//Getters as needed, cut out here

	public void setName(String name) {
		this.name = name;
		react();
	}

	public void setGender(Gender gender) {
		this.gender = gender;
		react();
	}
}
```

Now we need to bind the view to a Person object

```java
Person information = new Person();
CustomComponent component = new CustomComponent();
CustomController controller = new CustomController(information); //This one only custom events.
ReactiveController<CustomController> reactor = new ReactiveController<>(component,controller);
reactor.bind(information); //you can allways bind a new Object
```

