package edu.ozyegin.ozuradyo.system;

import edu.ozyegin.ozuradyo.OzuRadyoApp;
import javax.swing.JOptionPane;

/**
 *
 * @author ugurk
 */
public class SystemStarter {

    public static void main(String[] args){
        
        try {
            String os = System.getProperty("os.name");
            String path = OzuRadyoApp.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            if(os.toLowerCase().indexOf("windows")!=-1)
                path = path.substring(1, path.length());
            String runpath = "java -Djava.net.preferIPv4Stack=true -cp "+path+" edu.ozyegin.ozuradyo.OzuRadyoApp";
            Process p = Runtime.getRuntime().exec(runpath);
            // JOptionPane.showMessageDialog(null, runpath,"Info",JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Failed when OzuRadyoApp running...","Error",JOptionPane.ERROR_MESSAGE);
        }
    }
    
}
