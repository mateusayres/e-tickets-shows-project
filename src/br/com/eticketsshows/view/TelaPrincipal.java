package br.com.eticketsshows.view;

import br.com.eticketsshows.controller.Conexao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class TelaPrincipal extends javax.swing.JFrame {

    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    public TelaPrincipal() {
        initComponents();

        cbTipo.removeAllItems();
        cbTipo.addItem("");
        cbTipo.addItem("Arquibancada");
        cbTipo.addItem("Pista");
        cbTipo.addItem("Camarote");

        conexao = Conexao.conector();

        if (conexao != null) {
            lblStatus.setText("CONECTADO!");
        } else {
            lblStatus.setText("NÃO CONECTADO!");
        }

        consultar();
    }

    private void consultar() {
        DefaultTableModel dtmTabela = (DefaultTableModel) tbTabela.getModel();
        try {
            String sql = "SELECT * FROM ingressos";
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String nome = rs.getString("nome");
                String tipo = rs.getString("tipo");
                double valor = rs.getDouble("valor");
                String pagamento = rs.getString("pagamento");

                dtmTabela.addRow(new Object[]{id, nome, tipo, valor, pagamento});
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void incluir() {
        DefaultTableModel dtmTabela = (DefaultTableModel) tbTabela.getModel();
        int contagemRow = dtmTabela.getRowCount() + 1;

        if (contagemRow > 1) {
            contagemRow = Integer.parseInt(dtmTabela.getValueAt(contagemRow - 2, 0).toString()) + 1;
        }

        if ("".equals(txNome.getText()) || cbTipo.getSelectedIndex() == 0
                || (!rbBoleto.isSelected() && !rbCartao.isSelected() && !rbPix.isSelected())) {

            JOptionPane.showMessageDialog(rootPane, "Insira os dados primeiro.");
        } else {
            String resultadoPagamento = null;
            if (rbPix.isSelected()) {
                resultadoPagamento = "PIX";
            } else if (rbCartao.isSelected()) {
                resultadoPagamento = "Cartão de Crédito";
            } else {
                resultadoPagamento = "Boleto";
            }

            Object incluirLinha[] = {contagemRow, txNome.getText(), cbTipo.getSelectedItem(), txValor.getText(), resultadoPagamento};

            int confirmacao = JOptionPane.showConfirmDialog(rootPane, "Confirma essa inclusão?");
            if (confirmacao == 0) {

                dtmTabela.addRow(incluirLinha);

                try {
                    String sql = "INSERT INTO ingressos (id, nome, tipo, valor, pagamento) VALUES (?, ?, ?, ?, ?)";
                    pst = conexao.prepareStatement(sql);
                    pst.setInt(1, contagemRow);
                    pst.setString(2, txNome.getText());
                    pst.setString(3, cbTipo.getSelectedItem().toString());
                    pst.setDouble(4, Double.parseDouble(txValor.getText()));
                    pst.setString(5, resultadoPagamento);

                    pst.executeUpdate();

                    txNome.setText("");
                    cbTipo.setSelectedIndex(0);
                    txValor.setText("");
                    buttonGroup1.clearSelection();

                    JOptionPane.showMessageDialog(null, "Dados inseridos com sucesso.");

                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(null, "Erro ao inserir dados: " + e.getMessage());
                }
            }
        }
    }
    
    public void alterar(){
        DefaultTableModel dtmTabela = (DefaultTableModel) tbTabela.getModel();
        int linha = tbTabela.getSelectedRow();

        if (linha == -1) {
            JOptionPane.showMessageDialog(rootPane, "Selecione uma opção primeiro.");
        } else {
            if ("".equals(txNome.getText()) || cbTipo.getSelectedIndex() == 0
                    || (rbBoleto.isSelected() == false && rbCartao.isSelected() == false && rbPix.isSelected() == false)) {
                JOptionPane.showMessageDialog(rootPane, "Insira os dados primeiro.");
            } else {
                int confirmacao = JOptionPane.showConfirmDialog(rootPane, "Confirma essa alteração?");

                if (confirmacao == 0) {
                    String resultadoPagamento = null;
                    if (rbPix.isSelected()) {
                        resultadoPagamento = "PIX";
                    } else if (rbCartao.isSelected()) {
                        resultadoPagamento = "Cartão de Crédito";
                    } else {
                        resultadoPagamento = "Boleto";
                    }

                    String sql = "UPDATE ingressos SET nome=?, tipo=?, valor=?, pagamento=? WHERE id=?";
                    try {

                        pst = conexao.prepareStatement(sql);
                        pst.setString(1, txNome.getText());
                        pst.setString(2, cbTipo.getSelectedItem().toString());
                        pst.setDouble(3, Double.parseDouble(txValor.getText()));
                        pst.setString(4, resultadoPagamento);
                        pst.setInt(5, (int) dtmTabela.getValueAt(linha, 0));

                        int adicionado = pst.executeUpdate();
                        if (adicionado > 0) {
                            JOptionPane.showMessageDialog(null, "Dados alterados com sucesso.");

                            dtmTabela.setValueAt(txNome.getText(), linha, 1);
                            dtmTabela.setValueAt(cbTipo.getSelectedItem(), linha, 2);
                            dtmTabela.setValueAt(txValor.getText(), linha, 3);
                            dtmTabela.setValueAt(resultadoPagamento, linha, 4);

                            txNome.setText("");
                            cbTipo.setSelectedIndex(0);
                            txValor.setText("");
                            buttonGroup1.clearSelection();
                        }
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(null, e);
                    }
                }
            }
        }

        txNome.setText("");
        cbTipo.setSelectedIndex(0);
        txValor.setText("");
        buttonGroup1.clearSelection();
    }
    
    public void deletar(){
        DefaultTableModel dtmTabela = (DefaultTableModel) tbTabela.getModel();
        int linha = tbTabela.getSelectedRow();

        if (linha == -1) {
            JOptionPane.showMessageDialog(rootPane, "Selecione uma opção primeiro.");
        } else {
            int confirmacao = JOptionPane.showConfirmDialog(rootPane, "Confirma essa exclusão?");
            if (confirmacao == 0) {

                int idIngresso = (int) dtmTabela.getValueAt(linha, 0);

                String sql = "DELETE FROM ingressos WHERE id=?";
                try {
                    pst = conexao.prepareStatement(sql);
                    pst.setInt(1, idIngresso);
                    int apagado = pst.executeUpdate();

                    if (apagado > 0) {
                        JOptionPane.showMessageDialog(null, "Dados deletados com sucesso");
                        dtmTabela.removeRow(linha);

                        txNome.setText("");
                        cbTipo.setSelectedIndex(0);
                        txValor.setText("");
                        buttonGroup1.clearSelection();
                    }

                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(null, e);
                }
            }
        }
    }
    
    public void setarCampos() {
        DefaultTableModel dtmTabela = (DefaultTableModel) tbTabela.getModel();
        int linha = tbTabela.getSelectedRow();

        txNome.setText(dtmTabela.getValueAt(linha, 1).toString());
        cbTipo.setSelectedItem(dtmTabela.getValueAt(linha, 2).toString());
        txValor.setText(dtmTabela.getValueAt(linha, 3).toString());

        String resultadoPagamento = dtmTabela.getValueAt(linha, 4).toString();
        if (resultadoPagamento.equals("PIX")) {
            rbPix.setSelected(true);
        } else if (resultadoPagamento.equals("Cartão de Crédito")) {
            rbCartao.setSelected(true);
        } else {
            rbBoleto.setSelected(true);
        }
    }
    
    public void validarCbTipo(){
        String resultado = null;

        if (cbTipo.getSelectedItem() == "Arquibancada") {
            resultado = "150.0";
        } else if (cbTipo.getSelectedItem() == "Pista") {
            resultado = "200.0";
        } else if (cbTipo.getSelectedItem() == "Camarote") {
            resultado = "350.0";
        } else {
            resultado = "";
        }

        txValor.setText(resultado);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        txNome = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        cbTipo = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        txValor = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        rbPix = new javax.swing.JRadioButton();
        rbCartao = new javax.swing.JRadioButton();
        rbBoleto = new javax.swing.JRadioButton();
        btIncluir = new javax.swing.JButton();
        btAlterar = new javax.swing.JButton();
        btExcluir = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbTabela = new javax.swing.JTable();
        lblStatus = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(800, 600));
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(132, 180, 255));
        jPanel1.setMaximumSize(new java.awt.Dimension(800, 600));
        jPanel1.setMinimumSize(new java.awt.Dimension(800, 600));
        jPanel1.setPreferredSize(new java.awt.Dimension(800, 600));

        jLabel1.setFont(new java.awt.Font("sansserif", 1, 36)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("E-Tickets Shows");

        jLabel2.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        jLabel2.setText("Nome:");

        jLabel3.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        jLabel3.setText("Tipo:");

        cbTipo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbTipo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbTipoItemStateChanged(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        jLabel4.setText("Valor:");

        txValor.setEditable(false);

        jLabel5.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        jLabel5.setText("Pagamento:");

        rbPix.setBackground(jPanel1.getBackground());
        buttonGroup1.add(rbPix);
        rbPix.setFont(new java.awt.Font("sansserif", 1, 13)); // NOI18N
        rbPix.setText("PIX");

        rbCartao.setBackground(jPanel1.getBackground());
        buttonGroup1.add(rbCartao);
        rbCartao.setFont(new java.awt.Font("sansserif", 1, 13)); // NOI18N
        rbCartao.setText("Cartão de Crédito");

        rbBoleto.setBackground(jPanel1.getBackground());
        buttonGroup1.add(rbBoleto);
        rbBoleto.setFont(new java.awt.Font("sansserif", 1, 13)); // NOI18N
        rbBoleto.setText("Boleto");

        btIncluir.setFont(new java.awt.Font("sansserif", 1, 13)); // NOI18N
        btIncluir.setText("Incluir");
        btIncluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btIncluirActionPerformed(evt);
            }
        });

        btAlterar.setFont(new java.awt.Font("sansserif", 1, 13)); // NOI18N
        btAlterar.setText("Alterar");
        btAlterar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btAlterarActionPerformed(evt);
            }
        });

        btExcluir.setFont(new java.awt.Font("sansserif", 1, 13)); // NOI18N
        btExcluir.setText("Excluir");
        btExcluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btExcluirActionPerformed(evt);
            }
        });

        tbTabela.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nº", "Nome", "Tipo", "Valor (R$)", "Pagamento"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbTabela.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbTabelaMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tbTabela);
        if (tbTabela.getColumnModel().getColumnCount() > 0) {
            tbTabela.getColumnModel().getColumn(0).setPreferredWidth(2);
            tbTabela.getColumnModel().getColumn(1).setPreferredWidth(120);
            tbTabela.getColumnModel().getColumn(2).setPreferredWidth(50);
            tbTabela.getColumnModel().getColumn(3).setPreferredWidth(20);
            tbTabela.getColumnModel().getColumn(4).setPreferredWidth(60);
        }

        lblStatus.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        lblStatus.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblStatus.setText("Status da Conexão com BD");

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/bd.png"))); // NOI18N

        jLabel7.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel7.setText("R$:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(142, 142, 142)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btIncluir)
                                .addGap(18, 18, 18)
                                .addComponent(btAlterar)
                                .addGap(18, 18, 18)
                                .addComponent(btExcluir))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(rbPix)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(rbCartao)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(rbBoleto))
                            .addComponent(jLabel5)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 516, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel2)
                                        .addGap(114, 114, 114)
                                        .addComponent(jLabel3))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(txNome, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(cbTipo, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4)
                                    .addComponent(txValor, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 106, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(lblStatus)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addComponent(jLabel6)
                .addGap(16, 16, 16))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSeparator1)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(62, 62, 62)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel7))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rbPix)
                            .addComponent(rbCartao)
                            .addComponent(rbBoleto))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btIncluir)
                            .addComponent(btAlterar)
                            .addComponent(btExcluir))
                        .addGap(30, 30, 30)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel6))
                    .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btIncluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btIncluirActionPerformed
        incluir();
    }//GEN-LAST:event_btIncluirActionPerformed

    private void tbTabelaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbTabelaMouseClicked
        setarCampos();
    }//GEN-LAST:event_tbTabelaMouseClicked

    private void cbTipoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbTipoItemStateChanged
        validarCbTipo();
    }//GEN-LAST:event_cbTipoItemStateChanged

    private void btAlterarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btAlterarActionPerformed
        alterar();
    }//GEN-LAST:event_btAlterarActionPerformed

    private void btExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btExcluirActionPerformed
        deletar();
    }//GEN-LAST:event_btExcluirActionPerformed

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
            java.util.logging.Logger.getLogger(TelaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TelaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TelaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TelaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TelaPrincipal().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btAlterar;
    private javax.swing.JButton btExcluir;
    private javax.swing.JButton btIncluir;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<String> cbTipo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JRadioButton rbBoleto;
    private javax.swing.JRadioButton rbCartao;
    private javax.swing.JRadioButton rbPix;
    private javax.swing.JTable tbTabela;
    private javax.swing.JTextField txNome;
    private javax.swing.JTextField txValor;
    // End of variables declaration//GEN-END:variables
}
