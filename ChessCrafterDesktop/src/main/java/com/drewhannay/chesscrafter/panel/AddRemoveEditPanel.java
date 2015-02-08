package com.drewhannay.chesscrafter.panel;

import com.drewhannay.chesscrafter.utility.GuiUtility;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import java.awt.Dimension;
import java.awt.event.ActionListener;

public class AddRemoveEditPanel extends ChessPanel {

    public final JButton mAdd;
    public final JButton mRemove;
    public final JButton mEdit;

    public AddRemoveEditPanel() {
        super(false);

        mAdd = new JButton(" ");
        mAdd.putClientProperty("JButton.buttonType", "gradient");
        mAdd.setDisabledIcon(GuiUtility.createSystemImageIcon(30, 30, "/add_button.png"));
        mAdd.setIcon(GuiUtility.createSystemImageIcon(30, 30, "/add_button.png"));
        mRemove = new JButton(" ");
        mRemove.putClientProperty("JButton.buttonType", "gradient");
        mRemove.setDisabledIcon(GuiUtility.createSystemImageIcon(30, 30, "/remove_button.png"));
        mRemove.setIcon(GuiUtility.createSystemImageIcon(30, 30, "/remove_button.png"));
        mEdit = new JButton(" ");
        mEdit.setDisabledIcon(GuiUtility.createSystemImageIcon(30, 30, "/edit_button.png"));
        mEdit.setIcon(GuiUtility.createSystemImageIcon(30, 30, "/edit_button.png"));
        initComponents();
    }

    private void initComponents() {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

        add(Box.createRigidArea(new Dimension(10, 0)));
        add(mAdd);
        add(Box.createRigidArea(new Dimension(5, 0)));
        add(mRemove);
        //add(Box.createHorizontalGlue());
        add(Box.createRigidArea(new Dimension(5, 0)));
        add(mEdit);
        add(Box.createRigidArea(new Dimension(10, 0)));
    }

    public void addAddActionListener(ActionListener actionListener) {
        mAdd.addActionListener(actionListener);
    }

    public void addRemoveActionListener(ActionListener actionListener) {
        mRemove.addActionListener(actionListener);
    }

    public void addEditActionListener(ActionListener actionListener) {
        mEdit.addActionListener(actionListener);
    }
}
