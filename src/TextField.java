import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.List;

public class TextField extends JTextField {

    List<JButton> mainButtons;
    List<Button> controlButtons;
    Integer lowerLimit, upperLimit, intParameter;
    Double doubleParameter;
    PopulationController pop;
    double value;

    public TextField(Double parameter, String text, List<JButton> mainButtons,
                     Integer lowerLimit, Integer upperLimit){
        TextField.super.setText(text);
        this.mainButtons = mainButtons;
        this.lowerLimit = lowerLimit;
        this.upperLimit = upperLimit;
        this.doubleParameter = parameter;
    }

    public TextField(Integer parameter, String text, List<JButton> mainButtons,
                     Integer lowerLimit, Integer upperLimit){
        TextField.super.setText(text);
        this.mainButtons = mainButtons;
        this.lowerLimit = lowerLimit;
        this.upperLimit = upperLimit;
        this.intParameter = parameter;
    }

    public TextField(PopulationController pop, String text, List<JButton> mainButtons,
                     Integer lowerLimit, Integer upperLimit){
        this.pop = pop;
        TextField.super.setText(text);
        this.mainButtons = mainButtons;
        this.lowerLimit = lowerLimit;
        this.upperLimit = upperLimit;
    }

    public void addInputCheckerSpeed(){
        this.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                tryInsert();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                tryInsert();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }

            private void tryInsert() {
                try {
                    if (lowerLimit <= Integer.parseInt(TextField.super.getText()) &&
                            Integer.parseInt(TextField.super.getText()) <= upperLimit) {
                        pop.setSpeed(Integer.parseInt(TextField.super.getText()));
                        setTrue();
                    } else
                        setFalse();

                    colorTextFieldSpeed();
                } catch (NumberFormatException e1) {
                    TextField.super.setBackground(Color.red);
                    setFalse();
                }
            }

            private void setTrue() {
                enableButtons(true);
                enableControlButtons(true);
            }

            private void setFalse() {
                enableButtons(false);
                enableControlButtons(false);
            }
        });
    }

    public void addInputCheckerDouble(){
        this.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                tryInsert();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                tryInsert();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }

            private void tryInsert() {
                try {
                    if (value != Double.parseDouble(TextField.super.getText()) &&
                            lowerLimit <= Double.parseDouble(TextField.super.getText()) &&
                            Double.parseDouble(TextField.super.getText()) <= upperLimit) {
                        value = Double.parseDouble(TextField.super.getText());
                        setTrue();
                    } else
                        setFalse();

                    colorTextFieldDouble();
                } catch (NumberFormatException e1) {
                    TextField.super.setBackground(Color.red);
                    setFalse();
                }
            }

            private void setTrue() {
                enableButtons(true);
                enableControlButtons(true);
            }

            private void setFalse() {
                enableButtons(false);
                enableControlButtons(false);
            }
        });
    }

    public void addInputCheckerInteger(){
        this.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                tryInsert();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                tryInsert();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }

            private void tryInsert() {
                try {
                    if (value != Double.parseDouble(TextField.super.getText()) &&
                            lowerLimit <= Double.parseDouble(TextField.super.getText()) &&
                            Double.parseDouble(TextField.super.getText()) <= upperLimit) {
                        value = Double.parseDouble(TextField.super.getText());
                        setTrue();
                    } else
                        setFalse();

                    colorTextFieldInteger();
                } catch (NumberFormatException e1) {
                    TextField.super.setBackground(Color.red);
                    setFalse();
                }
            }

            private void setTrue() {
                enableButtons(true);
                enableControlButtons(true);
            }

            private void setFalse() {
                enableButtons(false);
                enableControlButtons(false);
            }
        });
    }

    private void enableButtons(boolean isEnabled){
        for(JButton button : mainButtons)
            button.setEnabled(isEnabled);
    }

    public void colorTextFieldDouble() {
        if (Double.parseDouble(this.getText()) > upperLimit)
            this.setBackground(Color.red);
        else if (Double.parseDouble(this.getText()) < lowerLimit)
            this.setBackground(Color.red);
        else if (doubleParameter != Double.parseDouble(this.getText()))
            this.setBackground(Color.yellow);
        else
            this.setBackground(Color.white);
    }

    public void colorTextFieldInteger() {
        if (Integer.parseInt(this.getText()) > upperLimit)
            this.setBackground(Color.red);
        else if (Integer.parseInt(this.getText()) < lowerLimit)
            this.setBackground(Color.red);
        else if (intParameter != Integer.parseInt(this.getText()))
            this.setBackground(Color.yellow);
        else
            this.setBackground(Color.white);
    }

    public void colorTextFieldSpeed() {
        if (Integer.parseInt(this.getText()) > upperLimit)
            this.setBackground(Color.red);
        else if (Integer.parseInt(this.getText()) < lowerLimit)
            this.setBackground(Color.red);
        else
            this.setBackground(Color.white);
    }

    private void enableControlButtons(boolean isEnabled){
        for(JButton button : controlButtons)
            button.setEnabled(isEnabled);
    }

    public void addButtons(List<Button> controlButtons){
        this.controlButtons = controlButtons;
    }
}
