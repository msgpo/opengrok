/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License (the "License").
 * You may not use this file except in compliance with the License.
 *
 * See LICENSE.txt included in this distribution for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at LICENSE.txt.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */

/*
 * Copyright (c) 2008, 2010, Oracle and/or its affiliates. All rights reserved.
 */

package org.opensolaris.opengrok.management.client;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.opensolaris.opengrok.management.OGAgent;

/**
 *
 * @author  Jan S Berg
 */
@SuppressWarnings("PMD.SingularField")
public class SettingsFrame extends javax.swing.JFrame {

    private static final long serialVersionUID = -5229330170574558105L;
    private final SettingsPersistence properties;

    /** 
     * Creates new form SettingsFrame 
     * @param sp settings to start and save with
     */
    public SettingsFrame(SettingsPersistence sp) {
        properties = sp;
        java.net.URL imageUrl = OGAgent.class.getResource("opengrok.gif");
        Image image = Toolkit.getDefaultToolkit().getImage(imageUrl);
        this.setIconImage(image);
        initComponents();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension mySize = getPreferredSize();
        setLocation(screenSize.width / 2 - (mySize.width / 2), screenSize.height / 2 - (mySize.height / 2));
        loadSettings();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                doClose();
            }
        });
    }

    private void loadSettings() {
        //userTextField.setText(properties.getProperty(SettingsPersistence.USERNAMEKEY));
        //passwordField.setText(properties.getProperty(SettingsPersistence.PASSWORDKEY));
        agentURLField.setText(properties.getAgentUrl());
        indexingTimeoutField.setText(properties.getProperty(SettingsPersistence.INDEXTIMEOUTKEY));
        connTimeoutField.setText(properties.getProperty(SettingsPersistence.CONNECTIONTIMEOUTKEY));
    }

    private void saveSettings() {
        //properties.setProperty(SettingsPersistence.USERNAMEKEY, userTextField.getText());
        //properties.setProperty(properties.PASSWORDKEY, 
        //        String.copyValueOf(passwordField.getPassword()));
        //properties.setPassword(passwordField.getPassword());
        properties.setProperty(SettingsPersistence.JMXURL, agentURLField.getText());
        properties.setProperty(SettingsPersistence.INDEXTIMEOUTKEY, indexingTimeoutField.getText());
        properties.setProperty(SettingsPersistence.CONNECTIONTIMEOUTKEY, connTimeoutField.getText());

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        closeButton = new javax.swing.JButton();
        headerLabel = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        agentURLLabel = new javax.swing.JLabel();
        agentURLField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        userTextField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        passwordField = new javax.swing.JPasswordField();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        indexingTimeoutField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        connTimeoutField = new javax.swing.JTextField();
        seconds = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("OpenGrok");
        setIconImages(null);

        closeButton.setText("Close");
        closeButton.setToolTipText("Close and save settings");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        headerLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/opensolaris/opengrok/management/client/opengrok.gif"))); // NOI18N
        headerLabel.setText("OpenGrok Client Settings");

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        agentURLLabel.setText("Agent URL");

        agentURLField.setText("localhost:9296");
        agentURLField.setToolTipText("Agent URL: <hostname>:<port>");

        jLabel1.setText("User Name");

        userTextField.setEditable(false);
        userTextField.setToolTipText("Username for connecting to the agent");

        jLabel2.setText("Password");

        passwordField.setEditable(false);
        passwordField.setText("jPasswordField1");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(agentURLLabel)
                    .addComponent(jLabel2))
                .addGap(43, 43, 43)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(passwordField, javax.swing.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE)
                    .addComponent(userTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE)
                    .addComponent(agentURLField, javax.swing.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(agentURLField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(agentURLLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(userTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel4.setText("Indexing Warning Timeout");

        indexingTimeoutField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        indexingTimeoutField.setText("10");

        jLabel5.setText("seconds");

        jLabel6.setText("Connection Timeout");

        connTimeoutField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        connTimeoutField.setText("120");

        seconds.setText("seconds");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(connTimeoutField, 0, 0, Short.MAX_VALUE)
                    .addComponent(indexingTimeoutField, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(seconds))
                .addContainerGap(44, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(indexingTimeoutField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(connTimeoutField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(seconds, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(47, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(headerLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 313, Short.MAX_VALUE)
                    .addComponent(closeButton, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(headerLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(closeButton)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

@SuppressWarnings("unused")
private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
    doClose();
}//GEN-LAST:event_closeButtonActionPerformed

    /**
     * Save the settings and close the frame.
     */
    private void doClose() {
        saveSettings();
        this.dispose();
    }

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
            try {
               new SettingsFrame(new SettingsPersistence(null)).setVisible(true);
            } catch (IOException ex) {
               Logger.getLogger(SettingsFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField agentURLField;
    private javax.swing.JLabel agentURLLabel;
    private javax.swing.JButton closeButton;
    private javax.swing.JTextField connTimeoutField;
    private javax.swing.JLabel headerLabel;
    private javax.swing.JTextField indexingTimeoutField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JLabel seconds;
    private javax.swing.JTextField userTextField;
    // End of variables declaration//GEN-END:variables

}
