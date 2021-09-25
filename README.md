> This README is about version 3! While most of the content remains valid, the code examples are most certainly outdated. Version 4 will be a breaking release.
> version 3 is a stable DEVELOPER release, version 4 will be the public release. To track progress go to the "Projects" tab

<center>
	<h1 align="center">React J</h1>
</center>
<p align="center">
<img src="media/logo.png" alt="Unbenannt" height="180pt"><br/>
</p>
<p align="center">
<a href="https://app.codacy.com/gh/nbrugger-tgm/reactj?utm_source=github.com&utm_medium=referral&utm_content=nbrugger-tgm/reactj&utm_campaign=Badge_Grade_Settings"><img src="https://api.codacy.com/project/badge/Grade/f0aa98c14a794c419f8400de14e3dbc8"></a><br/>
    <a href="https://www.conventionalcommits.org/en/v1.0.0/"><img src="https://img.shields.io/badge/conventional%20commits-✔-brightgreen"/></a>
</p>
<p align="center">
This library introduces <b>easy</b> reactive Bindings in Java, very useful to create a MVC UI without backdraws.<br>
Its just like <b>Vue js</b> for java
</p>

Documentation:

- [Wiki](https://github.com/nbrugger-tgm/reactj/wiki)
- [JavaDoc](https://niton.jfrog.io/artifactory/java-libs/com/niton/reactj/4.0.0b6/reactj-4.0.0b6-javadoc.jar!/index.html)
- [Changelog](CHANGELOG.md)

## Framework support implementation

- [ ] Swing : *in work*
- [ ] JavaFx
- [ ] Qt
- [ ] Vaadin

> Feel free to contribute custom implementations (they are not hard to create)

*Note* : You can use ReactJ with any UI framework without doing the implementation, its just a little more code

## Usage

[![Maven metadata URL](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fniton.jfrog.io%2Fartifactory%2Fjava-libs%2Fcom%2Fniton%2Freactj%2Fmaven-metadata.xml)](https://niton.jfrog.io/ui/packages/gav:%2F%2Fcom.niton:reactj?name=react&type=packages)

### Gradle

```groovy
repositories {
    maven {
        url "https://niton.jfrog.io/artifactory/java-libs/"
    }
}
```

Adding the dependency

*plain*

```groovy
implementation 'com.niton.reactj:core:4.0.0b6'
implementation 'com.niton.reactj:lists:4.0.0b6'
```

*framework*

```groovy
implementation 'com.niton.reactj:swing:4.0.0b6'
```

### Maven

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
    <groupId>com.niton.reactj</groupId>
    <!--For artifactId use your UI framework (swing,javafx,qt or vaadin)-->
    <artifactId>swing</artifactId>
    <version>4.0.0b6</version>
</dependency>
```

### Example

> All functional examples can be found at https://github.com/nbrugger-tgm/reactj/tree/master/core/src/test/java/com/niton/reactj/examples

Create a View (Component)

```java
public class DataView extends ReactiveView<JPanel, ReactiveProxy<Data>> {
	private JPanel panel; // the panel itself

	private JTextField        nameInput;
	private JComboBox<Gender> genderJComboBox;
	private JButton           selectButton;

	//Events this view can emitt
	public final EventManager<Person> resetEvent = new EventManager<>();

	@Override
	protected JPanel createView() {
		panel = new JPanel();

		nameInput = new JTextField();
		genderJComboBox = new JComboBox<>(Gender.values());
		selectButton = new JButton("Reset");

		panel.add(nameInput);
		panel.add(genderJComboBox);
		panel.add(selectButton);

		return panel;
	}

	//the reactj swing implementation makes this method a lot easier
	@Override
	public void createBindings(ReactiveBinder bindings) {
		bindings.bindBi("name", nameInput::setText, nameInput::getText);
		bindings.bindBi("gender", genderJComboBox::setSelectedItem, genderJComboBox::getSelectedItem);

		//add actions to react to
		nameInput.getDocument().addUndoableEditListener(bindings::react);
		genderJComboBox.addActionListener(bindings::react);

		selectButton.addActionListener(() -> resetEvemt.fire(getModel()));
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

	//getter and setters
}
```

Now we need to bind the view to a Person object

```java
ReactiveProxy<Data> proxy=ReactiveObject.create(new Data());
		Data model=proxy.object;

		DataView view=new DataView();
		view.setData(proxy);

//this will cause the UI to update
		model.setGender(FEMALE);
```

### Full runnable example

https://github.com/nbrugger-tgm/todo-list
