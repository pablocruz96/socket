package SocketsChat;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;//paquete que importa el Serversocket
import java.util.ArrayList;

public class Servidor  {
    
    public static void main(String[] args) {
        MarcoServidor mimarco=new MarcoServidor();
        mimarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
}

class MarcoServidor extends JFrame implements Runnable {
    
    public MarcoServidor(){
        setBounds(1200,300,280,350);				
	JPanel milamina= new JPanel();
        milamina.setLayout(new BorderLayout());
        areatexto=new JTextArea();
        milamina.add(areatexto,BorderLayout.CENTER);
	add(milamina);
        setVisible(true);
        Thread mihilo = new Thread(this);
        mihilo.start();
    }

    @Override
    public void run() {//metodo con el que comenzamos a crear los hilos
        try {
            //instncia el servidor del socket y se le asigna un puerto
            ServerSocket servidor = new ServerSocket(9999);
            String nick, ip, mensaje;//variables a utilizar
            //creamos un arrayList 
            ArrayList<String> listaIp = new ArrayList<String>();
            PaqueteEnvio paquete_recibido;
            while(true){//ejecución del bucle con while
                //acepta las conexiones del servidor
                Socket misocket = servidor.accept();
                //creamos el flujo de datos de entrada
                ObjectInputStream paquete_datos = new ObjectInputStream
                (misocket.getInputStream());
                //paquete  recibe el flujo de red y guarde el paquete recibido
                paquete_recibido = (PaqueteEnvio) paquete_datos.readObject();
                //pasamos lo que hay escrito en TextField get y lo almacenamos
                //para el nick, ip y mensaje en paquete recibido
                nick = paquete_recibido.getNick();
                ip = paquete_recibido.getIp();
                mensaje = paquete_recibido.getMensaje();
                if(!mensaje.equals(" online")){
                    //area texto concatena , el nick de la persona  y el mesnaje
                    //y desintario que viene determinado por la ip
                    areatexto.append("\n" + nick + ": " + mensaje + " para "+ ip);
                    //se crea un socket para que pase la información al destinatario
                    //como parametros van la ip y el puerto
                    Socket enviaDestinatario = new Socket(ip,9090);
                    ObjectOutputStream paqueteReenvio = new ObjectOutputStream
                    (enviaDestinatario.getOutputStream());
                    paqueteReenvio.writeObject(paquete_recibido);
                    paqueteReenvio.close();
                    enviaDestinatario.close();//se cierra el flujo de datos
                    misocket.close();//cierre del socket
                }else{
    //---------------------------DETECTA ONLINE-----------------------------
                    InetAddress localizacion=misocket.getInetAddress();	
                    String IpRemota=localizacion.getHostAddress();
                    System.out.println("Online " + IpRemota);
                    listaIp.add(IpRemota);//guarda las  drecciones ipip
                    paquete_recibido.setIps(listaIp);
                    for(String z:listaIp){
                        System.out.println("Array: "+z);
                        Socket enviaDestinatario = new Socket(z,9090);
                        ObjectOutputStream paqueteReenvio = new ObjectOutputStream
                        (enviaDestinatario.getOutputStream());
                        paqueteReenvio.writeObject(paquete_recibido);
                        paqueteReenvio.close();
                        enviaDestinatario.close();
                        misocket.close();
                    }
    //----------------------------------------------------------------------
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
    
    private JTextArea areatexto;

}

