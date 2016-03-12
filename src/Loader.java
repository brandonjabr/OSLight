
import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.io.InputStream;
import javax.imageio.ImageIO;
import java.util.HashMap;
import java.util.List;
import rsreflection.Reflector;


public class Loader extends JFrame implements AppletStub{
    //insane declarations
    public enum Game{OSRS, RS3, CLASSIC};
    public static final HashMap<String, String> Parameters = new HashMap<String, String>();
    public Game game;
    public URL GamePack;
    private static Applet applet;
    public String gameUrl;
    public String MClass;
    public Reflector loader;

    public Loader(Game g)
    {
        Color KratosColor = new Color(0xFFCC00);
        Color bgColor = new Color(0x2B2B2B);
        game = g;
        if(game == Game.OSRS)
            gameUrl = "http://oldschool1.runescape.com/";
        else if(game == Game.RS3)
            gameUrl = "http://world1.runescape.com/";
        else
            gameUrl = "http://classic2.runescape.com/plugin.js?param=o0,a0,s0";

        setTitle("OSLight v1.2");
        setSize(768, 528);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setBackground(bgColor);

            // Loading GUI

            JPanel panel = new JPanel();
            panel.setAlignmentX(CENTER_ALIGNMENT);
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            JLabel jlabel = new JLabel("Loading OSRS Gamepack", JLabel.CENTER);
            jlabel.setFont(new Font("Lato-Light",Font.PLAIN,18));
            jlabel.setForeground(Color.WHITE);
            jlabel.setAlignmentX(CENTER_ALIGNMENT);
            jlabel.setAlignmentY(CENTER_ALIGNMENT);

            panel.add(jlabel);

            ClassLoader cldr = this.getClass().getClassLoader();
            java.net.URL imageURL = cldr.getResource("resources/ajax-loader.gif");
            ImageIcon imageIcon = new ImageIcon(imageURL);
            JLabel iconLabel = new JLabel();
            iconLabel.setIcon(imageIcon);
            imageIcon.setImageObserver(iconLabel);
            iconLabel.setAlignmentX(CENTER_ALIGNMENT);
            iconLabel.setAlignmentY(CENTER_ALIGNMENT);

            panel.add(iconLabel);

            panel.setBackground(bgColor);

            Box box = new Box(BoxLayout.Y_AXIS);
            box.setOpaque(true);
            box.setBackground(bgColor);
            box.setBorder(new LineBorder(KratosColor));
            box.add(Box.createVerticalGlue());
            box.add(panel);
            box.add(Box.createVerticalGlue());


            setBackground(bgColor);

            add(box);

            setVisible(true);

        try {
            GetParams(new URL(gameUrl));

            // URLClassLoader loader = new URLClassLoader(new URL[]{GamePack});
            loader  = new Reflector(new URL[]{GamePack});

            applet = (Applet)loader.loadClass(MClass).newInstance();

            jlabel.setText("Initialising OSLight Client");

            applet.setStub(this);
            applet.init();
            applet.start();
            JPopupMenu.setDefaultLightWeightPopupEnabled(false);
            add(applet);
            box.setVisible(false);
        } catch (IOException | InstantiationException | IllegalAccessException | ClassNotFoundException e1) {
            e1.printStackTrace();
        }

        setVisible(true);
    }

    public void GetParams(URL url) throws IOException
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        String line;
        List<String> params = new ArrayList<String>();
        while((line = reader.readLine()) != null)
        {
            if(line.contains("param name"))
                params.add(line);
            if(GamePack == null) //retarted block
                if(line.contains("archive"))
                    if(game != Game.CLASSIC)
                        GamePack = new URL(gameUrl + line.substring(line.indexOf("archive=") + 8, line.indexOf(" ');")));
                    else
                        GamePack = new URL("http://classic2.runescape.com/" + line.substring(line.indexOf("archive=") + 8, line.indexOf(" code")));
            if(MClass == null)
                if(line.contains("code="))
                    MClass = line.substring(line.indexOf("code=") + 5, line.indexOf(".class"));
        }
        reader.close();

        for(String s : params)
        {
            Parameters.put(GetParamName(s), GetParamValue(s));
        }
    }

    public String GetParamName(String param)
    {
        try{
            int StartIndex = param.indexOf("<param name=\"") + 13;
            int EndIndex = param.indexOf("\" value");
            return param.substring(StartIndex, EndIndex);
        }catch(StringIndexOutOfBoundsException e)//classic handles some differently so why not just catch it =P
        {
            int StartIndex = param.indexOf("<param name=") + 12;
            int EndIndex = param.indexOf(" value");
            return param.substring(StartIndex, EndIndex);
        }
    }

    public String GetParamValue(String param)
    {
        try{
            int StartIndex = param.indexOf("value=\"") + 7;
            int EndIndex = param.indexOf("\">');");
            return param.substring(StartIndex, EndIndex);
        }catch(StringIndexOutOfBoundsException e)//and again :D
        {
            int StartIndex = param.indexOf("value=") + 6;
            int EndIndex = param.indexOf(">');");
            return param.substring(StartIndex, EndIndex);
        }
    }

    @Override
    public void appletResize(int arg0, int arg1) {
    }

    @Override
    public AppletContext getAppletContext() {
        return null;
    }

    @Override
    public URL getCodeBase() {
        try
        {
            if(game == Game.OSRS)
                return new URL("http://oldschool1.runescape.com/");
            else if(game == Game.RS3)
                return new URL("http://world1.runescape.com/");
            else
                return new URL("http://classic2.runescape.com/");
        }catch(MalformedURLException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public URL getDocumentBase() {
        try
        {
            if(game == Game.OSRS)
                return new URL("http://oldschool1.runescape.com/");
            else if(game == Game.RS3)
                return new URL("http://world1.runescape.com/");
            else
                return new URL("http://classic2.runescape.com/");
        }catch(MalformedURLException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getParameter(String arg0) {
        return Parameters.get(arg0);
    }

    @Override
    public boolean isActive() {
        return false;
    }

    public static void main(String[] args) {
        Loader loader = new Loader(Game.OSRS);
    }


}