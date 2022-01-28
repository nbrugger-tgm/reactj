package com.niton.reactj.examples.swing;

import com.niton.reactj.api.event.EventEmitter;
import com.niton.reactj.api.event.GenericEventEmitter;
import com.niton.reactj.objects.ReactiveObjectComponent;
import com.niton.reactj.objects.annotations.ReactiveListener;
import com.niton.reactj.objects.dsl.ObjectDsl;
import com.niton.reactj.objects.observer.PropertyObservation;

import javax.swing.*;
import java.awt.*;

import static com.niton.reactj.api.binding.Converters.parseInt;

//For swing components we recommend the com.niton.reactj:swing implementation
public class PersonComponent extends ReactiveObjectComponent<Person, JPanel> {

    public final  EventEmitter<Person> onReset            = new EventEmitter<>();
    private final GenericEventEmitter  surnameFieldChange = new GenericEventEmitter();
    private final GenericEventEmitter  changeGender       = new GenericEventEmitter();
    private final GenericEventEmitter  ageChange          = new GenericEventEmitter();
    private       JPanel               panel;
    private       JTextField           surnameInput;
    private       JTextField           ageInput;
    private       JTextField           iqField;
    private       JComboBox<Gender>    genderJComboBox;
    private JButton resetButton;


    @Override
    protected JPanel createView() {
        panel           = new JPanel();
        surnameInput    = new JTextField();
        ageInput        = new JTextField();
        iqField         = new JTextField();
        genderJComboBox = new JComboBox<>(Gender.values());
        resetButton = new JButton("Reset");


        panel.add(surnameInput);
        surnameInput.setColumns(10);

        panel.add(ageInput);
        ageInput.setColumns(10);

        panel.add(iqField);
        iqField.setColumns(10);

        panel.add(genderJComboBox);

        panel.add(resetButton);

        return panel;
    }

    @Override
    protected void createBindings(
            ObjectDsl<Person> binder,
            EventEmitter<Person> onModelChange,
            EventEmitter<PropertyObservation<Person>> onPropertyChange
    ) {
        surnameInput.getDocument().addUndoableEditListener(surnameFieldChange::fire);
        //surnameInput.addActionListener(bindings::react); This one requires enter to be pressed
        genderJComboBox.addActionListener(changeGender::fire);
        ageInput.addActionListener(ageChange::fire);

        binder.call(surnameInput::setText)
                .onObjectChange(Person::getName);

        binder.call(iqField::setText)
                .with(String::valueOf)
                .onModelChange(Person::getIq);

        binder.call(genderJComboBox::setSelectedItem)
                .onObjectChange(Person::getGender);

        binder.call(Person::setName)
                .with(surnameInput::getText)
                .on(surnameFieldChange);

        binder.call(ageInput::setText)
                .with(Object::toString)
                .onModelChange(Person::getAge);

        binder.call(Person::setGender)
              .withCasted(genderJComboBox::getSelectedItem)
              .on(changeGender);

        //react to changes in many and different ways
        binder.call(this::adaptColorToGender)
              .onObjectChange(Person::getGender);

        binder.call(Person::setAge)
                .with(parseInt)
                .from(ageInput::getText)
                .on(ageChange);

    }

    public void adaptColorToGender(Gender g) {
        Color c = Color.WHITE;
        if (g == Gender.MALE)
            c = Color.BLUE;
        if (g == Gender.FEMALE)
            c = Color.PINK;
        panel.setBackground(c);
    }

    @ReactiveListener("age")
    public void adaptSizeToAge(int age) {
        System.out.println("adapt font size for age of " + age + " years");
    }
}
