package SocketsChat;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Cliente {
    
    public static void main(String[] args) {
        MarcoCliente mimarco=new MarcoCliente();
        mimarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}

class MarcoCliente extends JFrame{
    
    public MarcoCliente(){		
        setBounds(600,300,280,350);
        LaminaMarcoCliente milamina=new LaminaMarcoCliente();
        add(milamina);
        setVisible(true);
        addWindowListener(new EnvioOnline());
    }
    
}

//-----------------------ENVIO SEÑAL ONLINE--------------------------
class EnvioOnline extends WindowAdapter{
    public void windowOpened(WindowEvent e){
        try{
            Socket misocket = new Socket("192.168.56.1",9999);
            PaqueteEnvio datos = new PaqueteEnvio();
            datos.setMensaje(" online");
            ObjectOutputStream paquete_datos=new ObjectOutputStream
            (misocket.getOutputStream());
            paquete_datos.writeObject(datos);
            misocket.close();
        }catch(Exception e2){}	
    }
}
//clase que contiene la interfaz se ejecuta con hilos
class LaminaMarcoCliente extends JPanel implements Runnable {
    
    public LaminaMarcoCliente(){
        String nick_usuario = JOptionPane.showInputDialog("Nick: ");
        //instancia de los componentes para la creación de la interfaz
        JLabel n_nick = new JLabel("Nick: ");
        nick = new JLabel();
        nick.setText(nick_usuario);
        JLabel texto = new JLabel("Online: ");
        ip = new JComboBox();
        campochat = new JTextArea(12,20);
        campo1 = new JTextField(20);       		
        miboton = new JButton("Enviar");
        EnviaTexto mievento = new EnviaTexto();
        miboton.addActionListener(mievento);
        
        //agregan los componentes a la lamina
        add(nick);
        add(texto);
        add(ip);
        add(campochat);
        add(campo1);
        add(miboton);
        
        //instancia del hilo
        Thread mihilo = new Thread(this);
        mihilo.start();//comienza la ejecución del hilo
        
    }

    @Override
    public void run() {
        try{ 
            ServerSocket servidor_cliente = new ServerSocket(9090);
            Socket cliente;
            //crea instancia para construir el objeto serializar
            PaqueteEnvio paqueteRecibido;
            while(true){
                cliente = servidor_cliente.accept();
                //crea un flujo paquete de entrada
                ObjectInputStream flujoentrada = new ObjectInputStream
                (cliente.getInputStream());
                paqueteRecibido = (PaqueteEnvio) flujoentrada.readObject();
                if(!paqueteRecibido.getMensaje().equals(" online")){
                    campochat.append("\n" + paqueteRecibido.getNick() + ": " + 
                    paqueteRecibido.getMensaje());
                }else{
                    //
                    ArrayList <String> IpsMenu = new ArrayList <String>();
                    IpsMenu=paqueteRecibido.getIps();
                    ip.removeAllItems();
                    for(String z:IpsMenu){
                        ip.addItem(z);
                    }
                }
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    
    //clase que implementa la accion del boton enviar
    private class EnviaTexto implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            //agrega a campochat lo que se ha escrito en el campo de texto
            campochat.append("\n " + campo1.getText());
            try {
                //instancia del socket a la que le asignamos la ip del servidor 
                //y un puerto
                Socket misocket = new Socket("192.168.56.1",9999);
                //instancia del la clase paqueteEnvio para empaquetar los daros
                //poder enviar y recibir
                PaqueteEnvio datos = new PaqueteEnvio();
                //pasamos lo que hay escrito en TextField get y lo almacenamos
                //para el nick, ip y campo1
                datos.setNick(nick.getText());
                datos.setIp(ip.getSelectedItem().toString());
                datos.setMensaje(campo1.getText());
                //instancia ObjectOutputStream para crear un flujo de datos de  
                //salida  del texto en paquete_Datos y enviarlo pro la red
                ObjectOutputStream paquete_datos = new ObjectOutputStream
                (misocket.getOutputStream());
                paquete_datos.writeObject(datos);//indica que escriba en el 
           //flujo de datos
            } catch (IOException ex) {
                System.out.println(ex.getMessage());//mensaje que lanza la 
                //excepcion si no conecta
            }
        }
    }
    
    //creamos los componentes necesarios para la interfaz del usuario
    private JTextField campo1;
    private JComboBox ip;
    private JLabel nick;
    private JButton miboton;
    private JTextArea campochat;
	
}

class PaqueteEnvio implements Serializable {//implementamos la serialización para
    //que todas las instancias se conviertan en bits y mandarlas vía internet
    
// enviar los datos necesarios
    private String nick, ip, mensaje;
    private ArrayList<String> Ips;
    //ArrayList que almacenara las ips
    public ArrayList<String> getIps(){
        return Ips;
    }
    //se generan los setters y getters correpondientes para guardar y obtener datos
    public void setIps(ArrayList<String> ips){
        this.Ips = ips;
    }

    public String getNick() {
        return nick;
    }
    
    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getIp() {
        return ip;
    }
    
    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
            
}
