package assistente;

import jandl.wizard.Data;
import jandl.wizard.WizardBase;
import jandl.wizard.WizardFactory;
import jandl.wizard.WizardText;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import java.sql.ResultSet;


public class Assistente {

    public static void main(String[] args) {
        // Pagina 01 Menu com GIF
        WizardBase pg1 = WizardFactory.createBase("Pagina 01","!/fotos/assistant_4.gif");
        
         WizardBase historico= WizardFactory.createText("HISTORICO", 
				"!/fotos/buzz.png", true);
        // Pagina 02 Primeira coleta de dados
        String[] tag = { "Nome", "Semana", "Destinatario"};
        String[] label = { "Nome", "Dia da semana", "Destinatario"};
        WizardBase pg2 = WizardFactory.createField("Pagina 02", tag, label, label);
        pg2.setImage("!/fotos/potter.png");
        // Pagina 03 Pré visualização da coleta de dados
        WizardBase pg3 = WizardFactory.createText("Pagina 03", 
				"!/fotos/naruto_1.png", true);
        // Pagina 04 Segunda coleta de dados
        String[] opcoes = {"Aniversario", "Desculpa", "Falta"};
        WizardBase pg4 = WizardFactory.createList("Pagina 04", "opt", "Opcoes", opcoes);
        pg4.setImage("!/fotos/heroes_1.png");
        // Pagina 05 Resultado final
         WizardBase pg5 = WizardFactory.createText("Pagina 05", 
				"!/fotos/buzz.png", true);
        // Transição de pagina
        pg1.nextWizard(historico);
        historico.nextWizard(pg2);
        pg2.nextWizard(pg3);
        pg3.nextWizard(pg4);
        pg4.nextWizard(pg5);
        
        //Processos
        historico.addPreProcessor((wiz) -> historicoPreProcessor(wiz));
     
        pg3.addPreProcessor((wiz) -> pg3PreProcessor(wiz));
        pg5.addPreProcessor((wiz) -> pg4PreProcessor(wiz));
        pg5.addPreProcessor((wiz) -> pg5PreProcessor(wiz));
        // Ativa a tela
        SwingUtilities.invokeLater(() -> pg1.setVisible(true));
    }
    //Passando a primeira coleta de dados para visualização
    private static void pg3PreProcessor(WizardBase wiz) {
        System.out.println("wb4PreProcessor");
        WizardText wizardText = (WizardText)wiz;
        wizardText.setText("Pre-visualizacao\n\n");
        Data data = Data.instance();
        wizardText.append("\nNome: " + data.get("Wizard3.fieldPane0.Nome"));
	wizardText.append("\nData: " + data.get("Wizard3.fieldPane0.Semana"));
	wizardText.append("\nDestinatario: " + data.get("Wizard3.fieldPane0.Destinatario"));
    }
    // Coleta o dado da segunda coleta para definir o tipo de mensagem e concatenando com os dados da primeira coleta
    private static void pg4PreProcessor(WizardBase wiz) {
        String resultado;
        System.out.println("wb4PreProcessor");
        WizardText wizardText = (WizardText)wiz;
        wizardText.setText("Mensagem\n\n");
        Data data = Data.instance();
        if ("[Aniversario]".equals(data.get("Wizard5.listPane0.opt.selectedValues").toString())) {
            resultado =data.get("Wizard3.fieldPane0.Destinatario") + ", Tenha um feliz aniversario cheio\n" +
            "de sorrisos e gargalhadas, repleto\n" +
            "de paz, amor e muita alegria.\n" +
            "Parabens do seu amigo " + data.get("Wizard3.fieldPane0.Nome");
            wizardText.append("\n" + resultado );
            data.put("resultado", resultado);
        } else if("[Desculpa]".equals(data.get("Wizard5.listPane0.opt.selectedValues").toString())){
            resultado =data.get("Wizard3.fieldPane0.Destinatario") + ", me perdoa por duvidar da sua lealdade.\n"
            + " Hoje sei que a sua amizade e verdadeira\n"
            + " e prezo pela sua presenca em minha vida." +
            "Desculpa do seu amigo " + data.get("Wizard3.fieldPane0.Nome");
            wizardText.append("\n" + resultado);
            data.put("resultado", resultado);            
            }else if("[Falta]".equals(data.get("Wizard5.listPane0.opt.selectedValues").toString())){
                resultado ="Senhor " + data.get("Wizard3.fieldPane0.Destinatario") + "\n Nao poderei comparecer no trabalho na "+
                data.get("Wizard3.fieldPane0.Semana")
                + ".\n Ja venho ter avisar com antecedencia\n"
                +"\nAtenciosamente " + data.get("Wizard3.fieldPane0.Nome");
                wizardText.append("\n"+ resultado);
                data.put("resultado", resultado);   
                }else {
                    wizardText.append("Dado nao selecionado" );
                }
    }
    //Pega os dados e coloca no BD
    private static void pg5PreProcessor(WizardBase wiz) {
         try {
            java.sql.Timestamp now = new java.sql.Timestamp (System.currentTimeMillis ());
            System.out.println("wb5PreProcessor");
            Data data = Data.instance();
            Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/Assistente","app","app");
            Statement stmt = con.createStatement();
            
            stmt.executeUpdate("insert into RESULTADO (TEXTO,HORA)\n"+
            "values ('"+Data.instance().get("resultado")+"','"+now+"')");
            
            stmt.executeUpdate("insert into COLETA (NOME,SEMANA,DESTINATARIO,HORA)\n"+
            "values ('"+data.get("Wizard3.fieldPane0.Nome")+"', '"+data.get("Wizard3.fieldPane0.Semana")+"', '"+data.get("Wizard3.fieldPane0.Destinatario")+"', '"+now+"')");
            
            stmt.executeUpdate("insert into COLETA2 (TIPO,HORA)\n"+
            "values ('"+data.get("Wizard5.listPane0.opt.selectedValues").toString()+"', '"+now+"')");
            
                     
            stmt.close();
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(Assistente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    //Pega os dados do banco e gera o histórico
    private static void historicoPreProcessor(WizardBase wiz) {
        try {   
            WizardText wizardText = (WizardText)wiz;
            wizardText.setText("Historico\n\n");
            Data data = Data.instance();
            Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/Assistente","app","app");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select IDCOLETA,nome,semana,destinatario,tipo from coleta,coleta2,resultado where idcoleta=idresultado and idcoleta2=idresultado");
            String resultado="";
            while(rs.next()){
                resultado+=rs.getInt("IDCOLETA")+ " | "+rs.getString("nome") + " | "+rs.getString("semana") + " | "+rs.getString("destinatario")+ " | "+rs.getString("tipo")+ "\n";
            }
            wizardText.append(resultado);
            rs.close();
            stmt.close();
            con.close();
        } catch (SQLException ex) {
            Logger.getLogger(Assistente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    
}
