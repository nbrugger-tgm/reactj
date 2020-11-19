
<center><h1 align="center">React J</h1></center>
<p align="center">
<img src="media/logo.png" alt="Unbenannt" height="180pt"><br/>
	This library introduces <b>easy</b> reactive Bindings in Java, very useful to create a MVC UI without backdraws.<br>
	Its just like <b>Vue js</b> for java
</p> 

[Wiki](https://github.com/nbrugger-tgm/reactj/wiki)

### Usage

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/f0aa98c14a794c419f8400de14e3dbc8)](https://app.codacy.com/gh/nbrugger-tgm/reactj?utm_source=github.com&utm_medium=referral&utm_content=nbrugger-tgm/reactj&utm_campaign=Badge_Grade_Settings)
[![Maven metadata URL](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fniton.jfrog.io%2Fartifactory%2Fjava-libs%2Fcom%2Fniton%2Freactj%2Fmaven-metadata.xml)](https://niton.jfrog.io/ui/packages/gav:%2F%2Fcom.niton:reactj?name=react&type=packages)

#### Gradle

```groovy
repositories {
    maven {
        url "https://niton.jfrog.io/artifactory/java-libs/"
    }
}
```

Adding the dependency

```groovy
implementation 'com.niton:reactj:2.0.1'
```

#### Maven

```xml
<repositories>
  <repository>
    <id>niton</id>
    <name>niton</name>
    <url>https://niton.jfrog.io/artifactory/java-libs/</url>
  </repository>
</repositories>
```

Adding the dependency

```xml
<dependency>
  <groupId>com.niton</groupId>
  <artifactId>reactj</artifactId>
  <version>2.0.1</version>
</dependency>
```

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
view.setData(proxy);

//now you just need to display the view on a JFrame
```

### Full runnable example

https://github.com/nbrugger-tgm/todo-list

### Getting Started

I have a wiki ready to be read
