package io.github.makbn.vaadin.demo.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Matt Akbarian  (@makbn)
 */
public class DialogBuilder {

    private final Dialog dialog;
    private final FormLayout formLayout;
    private final Map<String, Object> fieldComponents = new LinkedHashMap<>();

    private DialogBuilder() {
        this.dialog = new Dialog();
        this.dialog.setModal(true);

        this.formLayout = new FormLayout();
        dialog.add(formLayout);
    }

    public static DialogBuilder builder() {
        return new DialogBuilder();
    }

    public DialogBuilder textField(String label) {
        TextField field = new TextField(label);
        fieldComponents.put(label, field);
        formLayout.add(field);
        return this;
    }

    public DialogBuilder numberField(String label) {
        IntegerField field = new IntegerField(label);
        fieldComponents.put(label, field);
        formLayout.add(field);
        return this;
    }

    public DialogBuilder decimalField(String label) {
        NumberField field = new NumberField(label);
        fieldComponents.put(label, field);
        formLayout.add(field);
        return this;
    }

    public void get(Consumer<Map<String, Object>> okHandler) {
        Button ok = new Button("OK", e -> {
            Map<String, Object> values = new LinkedHashMap<>();
            fieldComponents.forEach((label, comp) -> {
                if (comp instanceof TextField tf) {
                    values.put(label, tf.getValue());
                } else if (comp instanceof IntegerField nf) {
                    values.put(label, nf.getValue());
                } else if (comp instanceof NumberField df) {
                    values.put(label, df.getValue());
                }
            });
            okHandler.accept(values);
            dialog.close();
        });

        Button cancel = new Button("Cancel", e -> dialog.close());
        dialog.getFooter().add(ok, cancel);

        dialog.open();
    }
}
