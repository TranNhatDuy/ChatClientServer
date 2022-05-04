/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Server;

import Client.ChatFrame;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Win 10
 */
public class ServerFrame extends javax.swing.JFrame {

    static ServerSocket server;
    Socket socket;
    BufferedWriter out;
    BufferedReader in;

    public static LinkedHashMap<String, Socket> mapSocket = new LinkedHashMap<>();

    public static ArrayList<String> listName = new ArrayList<>();
    public static ArrayList<Worker2> workerTemp = new ArrayList<>();

    public static LinkedHashMap<String, String> map = new LinkedHashMap<>();
//    public static LinkedHashMap<String, ChatFrame> maptemp = new LinkedHashMap<>();
    public static ArrayList<Worker2> workers = new ArrayList<>();

    /**
     * Creates new form ServerFrame
     */
    public ServerFrame() {
        initComponents();
        this.setResizable(false);
        this.setTitle("Server");
    }

    public void startServer() {
        try {
//            server = new ServerSocket(1234);
            if (!isNumeric(txtPort.getText())) {
                JOptionPane.showMessageDialog(null, "Nhập sai port!!!", "Thông báo", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int port = Integer.parseInt(txtPort.getText());
            System.out.println("Start server success!");
            Thread t = new Thread() {
                public void run() {
                    try {
                        server = new ServerSocket(port);
                        requestFromClient();
                    } catch (IOException ex) {
                        Logger.getLogger(ServerFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            };
            t.start();
            btnStart.setEnabled(false);
            String temp = txtPort.getText();
            txtPort.setText("PORT: " + temp);

        } catch (Exception e) {
        }
    }

    public void requestFromClient() throws IOException {
        while (true) {
            socket = server.accept();
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            String message = in.readLine();

            String mode = "";
            String name = "";

            if (message.contains("CREATE") || message.contains("EXITFORMCHAT") || message.contains("TEXT")) {
                String split[] = message.split(":");
                mode = split[0];
                name = split[1];
            }
            if ((mapSocket.containsKey(message)) && (checkMODE(mode) == false)) {
                System.out.println("server send back 0 to client"); //code này để gửi tin từ server về client
                out.write("0");
                out.newLine();
                out.flush();
                System.out.println(".run()" + mapSocket.toString());

            } else {
                if (!mapSocket.containsKey(message) && (checkMODE(mode) == false)) {
                    String temp = "1";
                    out.write(temp);
                    out.newLine();
                    out.flush();
                    mapSocket.putIfAbsent(message, socket);
                    System.out.println("message " + message);
                    listName.add(message);
                    Worker2 client = new Worker2(socket, message);
                    workers.add(client);
                    for (Worker2 w : workers) {
                        w.out.write("ONLINE:" + workers.size());
                        w.out.newLine();
                        w.out.flush();
                    }
                    Thread t = new Thread(client);
                    t.start();
                }
            }
        }
    }

    public static boolean checkMODE(String mode) {
        if (mode.equals("CREATE") || mode.equals("EXITFORMCHAT") || mode.equals("TEXT")) {
            return true;
        }
        return false;
    }

    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtPort = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        btnStart = new javax.swing.JButton();

        jLabel1.setText("jLabel1");

        jLabel2.setText("jLabel2");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        txtPort.setText("1234");
        txtPort.setDisabledTextColor(new java.awt.Color(0, 0, 0));

        jLabel3.setText("PORT:");

        btnStart.setText("Start");
        btnStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStartActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(txtPort, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnStart)
                .addContainerGap(50, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(136, 136, 136)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnStart)
                    .addComponent(txtPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addContainerGap(141, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStartActionPerformed
        // TODO add your handling code here:
        startServer();
    }//GEN-LAST:event_btnStartActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ServerFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ServerFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ServerFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ServerFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ServerFrame().setVisible(true);
            }
        });

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnStart;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JTextField txtPort;
    // End of variables declaration//GEN-END:variables

}