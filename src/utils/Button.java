package utils;

import panels.PopulationController;

import javax.swing.JButton;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Button extends JButton {

    TextField textField;
    Integer lowerLimit, upperLimit, intChange;
    Double doubleChange;
    PopulationController pop;

    public Button(String text, TextField textField, Integer lowerLimit, Integer upperLimit,
                  Double changeValue){
        Button.super.setText(text);
        this.textField = textField;
        this.lowerLimit = lowerLimit;
        this.upperLimit = upperLimit;
        this.doubleChange = changeValue;
    }

    public Button(String text, TextField textField, Integer lowerLimit, Integer upperLimit,
                  Integer intChange){
        Button.super.setText(text);
        this.textField = textField;
        this.lowerLimit = lowerLimit;
        this.upperLimit = upperLimit;
        this.intChange = intChange;
    }

    public Button(String text, PopulationController pop, TextField textField, Integer lowerLimit, Integer upperLimit){
        Button.super.setText(text);
        this.pop = pop;
        this.textField = textField;
        this.lowerLimit = lowerLimit;
        this.upperLimit = upperLimit;
    }

    public void incrementDouble() {
        this.addActionListener(e -> {
            if(Double.parseDouble(textField.getText()) < upperLimit)
                textField.setText(
                        String.valueOf(BigDecimal.valueOf(Double.parseDouble(textField.getText()) + doubleChange)
                                .setScale(2, RoundingMode.HALF_UP)
                                .doubleValue()));
            textField.colorTextFieldDouble();
        });
    }

    public void incrementInteger() {
        this.addActionListener(e -> {
            if(Integer.parseInt(textField.getText()) < upperLimit)
                textField.setText(
                        String.valueOf(Integer.parseInt(textField.getText()) + intChange));
            textField.colorTextFieldInteger();
        });
    }

    public void incrementSpeed() {
        this.addActionListener(e -> {
            if(Integer.parseInt(textField.getText()) < upperLimit) {
                pop.incrementSpeed();
                textField.setText(String.valueOf(Integer.parseInt(textField.getText()) + 1));
            }
        });
    }

    public void decrementDouble() {
        this.addActionListener(e -> {
            if(Double.parseDouble(textField.getText()) > lowerLimit)
                textField.setText(
                        String.valueOf(BigDecimal.valueOf(Double.parseDouble(textField.getText()) - doubleChange)
                                .setScale(2, RoundingMode.HALF_UP)
                                .doubleValue()));
            textField.colorTextFieldDouble();
        });
    }

    public void decrementInteger() {
        this.addActionListener(e -> {
            if(Integer.parseInt(textField.getText()) > lowerLimit)
                textField.setText(
                        String.valueOf(Integer.parseInt(textField.getText()) - intChange));
            textField.colorTextFieldInteger();
        });
    }

    public void decrementSpeed() {
        this.addActionListener(e -> {
            if(lowerLimit < Integer.parseInt(textField.getText())) {
                pop.decrementSpeed();
                textField.setText(String.valueOf(Integer.parseInt(textField.getText()) - 1));
            }
        });
    }

}
