/**
 * This is the second version of BeatBox as in Head First: Java
 * 
 * @author Saqib Nizam Shamsi
 * @version 2.3
 */

import javax.swing.*;
import javax.swing.event.*;
import javax.sound.midi.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

public class BeatBoxV2_3 implements Serializable
{
    static final long serialVersionUID = -4383073477395643401L;
    private JFrame frame;
    private String instNms[]={"Bass Drum","Closed Hi-Hat","Open Hi-Hat","Acoustic Snare","Crash Cymbal","Hand Clap","High Tom","High Bongo","Maracas","Whistle","Low Conga","Cowbell","Vibraslap","Low Mid-Tom", "High Agogo", "Open High Conga"};
    private int insts[]={35,42,46,38,49,39,50,60,70,72,64,56,58,47,67,63};
    private JButton start, stop, tempoUp, tempoDown, clear, credits, randomize;
    private JPanel rightPanel, leftPanel, centralPanel, creditsPanel, southPanel;
    private ArrayList<JCheckBox> cBoxList;
    private JCheckBox repeater;
    private JSeparator sepone, septwo, septhree, sepfour, sepfive;
    private Sequencer S;
    private Box creditsBox;
    private float t=120.0F;
    private JLabel tempo, Credits, headfirst, ks, bertb, tempoSliderL, tempoSliderR;
    private JMenuItem saveMenuItem, loadMenuItem, aboutMenuItem, exitMenuItem;
    private JSlider tempoSlider;
           
    public static void main(String args[])throws Exception
    {
        BeatBoxV2_3 bb=new BeatBoxV2_3();
        bb.gui();
        bb.createSequencer();
    }
    
    public void gui()throws Exception
    {
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        frame = new JFrame("BeatBox v2.3");
        frame.setIconImage(new ImageIcon("..//resources//Icon32.png").getImage());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BorderLayout layout=new BorderLayout();
        JPanel bckgrnd=new JPanel(layout);
        
        bckgrnd.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        GridLayout G=new GridLayout(16,1);
        
        rightPanel=new JPanel(G);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(0,10,0,0));
                
        southPanel=new JPanel();
        southPanel.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));
        
        start=new JButton("Start");
        start.addActionListener(new StartListener());
        stop=new JButton("Stop");
        stop.addActionListener(new StopListener());
        clear=new JButton("Clear");
        clear.addActionListener(new ClearListener());
        repeater=new JCheckBox("Repeat");
        repeater.setHorizontalAlignment(SwingConstants.CENTER);        
        repeater.addItemListener(new RepeaterListener());
        randomize = new JButton("Random Pattern");
        randomize.addActionListener(new RandomizeListener());
        
        repeater.setSelected(false);
        
        //Seperators
        sepone=new JSeparator();
        sepone.setOrientation(SwingConstants.HORIZONTAL);
        septwo=new JSeparator();
        septwo.setOrientation(SwingConstants.HORIZONTAL);
        septwo.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        septhree=new JSeparator();
        septhree.setOrientation(SwingConstants.HORIZONTAL);
        sepfour=new JSeparator();
        sepfour.setOrientation(SwingConstants.HORIZONTAL);
        sepfive = new JSeparator();
        sepfour.setOrientation(SwingConstants.HORIZONTAL);
        
        //Credits
        Credits = new JLabel("Credits");
        Credits.setHorizontalAlignment(SwingConstants.CENTER);
        headfirst = new JLabel("Head First: Java");
        headfirst.setHorizontalAlignment(SwingConstants.CENTER);
        ks = new JLabel("Kathy Sierra");
        ks.setHorizontalAlignment(SwingConstants.CENTER);
        bertb = new JLabel("Bert Bates");
        bertb.setHorizontalAlignment(SwingConstants.CENTER);
        creditsBox=new Box(BoxLayout.Y_AXIS);
        creditsBox.add(Credits);
        creditsBox.add(headfirst);
        creditsBox.add(ks);
        creditsBox.add(bertb);
        
        //Tempo Slider
        tempoSlider = new JSlider(0, 300, 120);
        tempoSlider.addChangeListener(new TempoSliderListener());
        
        //Right Panel
        rightPanel.add(start);
        rightPanel.add(stop);
        rightPanel.add(sepone);
        rightPanel.add(clear);
        rightPanel.add(septhree);
        rightPanel.add(repeater);
        
        rightPanel.add(sepfive);
        rightPanel.add(randomize);
                
        leftPanel=new JPanel(G);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(0,0,0,10));
                
        for(String s:instNms)
        {
            leftPanel.add(new JLabel(s));
        }
        
        GridLayout glayout=new GridLayout(16,16);
        glayout.setVgap(0);
        glayout.setHgap(0);
        centralPanel= new JPanel(glayout);
        cBoxList=new ArrayList<JCheckBox>();
        Dimension size=new Dimension(266,252);
        
        centralPanel.setMinimumSize(size);
        centralPanel.setMaximumSize(size);
        
        for(int i=0;i<256;i++)
        {
            JCheckBox cb=new JCheckBox();
            cb.setSelected(false);
            cBoxList.add(cb);
            centralPanel.add(cb);
        }
        
        //Tempo Labels
        tempoSliderL = new JLabel("Tempo:");
        tempoSliderR = new JLabel("120");
        
        
        //Menu bar Logic
        
        JMenuBar menu = new JMenuBar();
        
        //File Menu
        
        JMenu fileMenu = new JMenu("File");
        saveMenuItem = new JMenuItem("Save");
        loadMenuItem = new JMenuItem("Load");
        exitMenuItem = new JMenuItem("Exit");
        
        saveMenuItem.addActionListener(new SaveMenuListener());
        loadMenuItem.addActionListener(new LoadMenuListener());
        exitMenuItem.addActionListener(new ExitMenuListener());
        
        fileMenu.add(saveMenuItem);
        fileMenu.add(loadMenuItem);
        fileMenu.add(exitMenuItem);
        
        menu.add(fileMenu);
        
        //Help Menu
        
        JMenu helpMenu = new JMenu("Help");
        aboutMenuItem = new JMenuItem("About BeatBox");
        
        aboutMenuItem.addActionListener(new AboutMenuItemListener());
        
        helpMenu.add(aboutMenuItem);
        
        menu.add(helpMenu);
        
        frame.setJMenuBar(menu);
        
        //Menu bar Logic ends
        
        //South Panel
        southPanel.add(tempoSliderL);
        southPanel.add(tempoSlider);
        southPanel.add(tempoSliderR);
        //Main Panel
            
        bckgrnd.add(BorderLayout.EAST,rightPanel);
        bckgrnd.add(BorderLayout.WEST,leftPanel);
        bckgrnd.add(BorderLayout.CENTER,centralPanel);
        bckgrnd.add(BorderLayout.SOUTH,southPanel);
        frame.getContentPane().add(bckgrnd);
        
        frame.setBounds(300,100,600,400);
        frame.pack();
        frame.setVisible(true);
    }
    
    public void createSequencer()
    {
        try
        {
            S=MidiSystem.getSequencer();
            S.open();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public void createMelodyHFVM()
    {
        int []trackList = null;
               
        //Do this for each of the 16 rows, i.e., Bass Drum, Congo...
        try
        {
            //Get the sequence
            Sequence seq=new Sequence(Sequence.PPQ,4);
            //Create the track in the sequence
            Track track=seq.createTrack();
        
            for(int i=0;i<16;i++)
            {
                trackList = new int[16];
                
                int key = insts[i];
                
                //Do this for each of the beats for this row
                for(int j=0;j<16;j++)
                {
                    JCheckBox jc = (JCheckBox) cBoxList.get(j+(16*i));
                    if(jc.isSelected())
                        trackList[j] = key;
                        else
                        trackList[j]=0;
                }                                           //close the inner loop
                    
                //This makes events for one instrument at a time, for all 16 beats.
                for(int k=0;k<16;k++)
                {
                    int tmp = trackList[k];
                    if(tmp!=0)
                    {
                        track.add(makeEvent(144, 9, tmp, 100, k));
                        track.add(makeEvent(128, 9, tmp, 100, k+1));
                    }
                }
                
                track.add(makeEvent(176, 1, 127, 0, 16));
            }                                               //close the outer loop
            
            track.add(makeEvent(192, 9, 1, 0, 15));  //We always want to make sure that there is an event at beat 16. Otherwise the BeatBox may not go the full 16 beats the next time it starts over.
            
            S.setSequence(seq);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
    }
    
    public void createMelodyRemastered()
    {
        int inst, i, j, tick, channel;                                                                                //75
        tick=i=0;
        channel = 1;
        try
        {
            //Get the sequence
            Sequence seq=new Sequence(Sequence.PPQ,4);
            //Create the track in the sequence
            Track track=seq.createTrack();
            
            //Make the midiEvent (Sound data) to be read by the sequencer and add it to the track
            
            for(i=0;i<16;i++)
            {
                for(j=0;j<16*16;j+=16)
                {
                    JCheckBox temp = cBoxList.get(i+j);
                    inst = insts[j/16];
                    if(temp.isSelected())
                    {
                        track.add(makeEvent(192,9,inst,0,16));
                        track.add(makeEvent(144,9,inst,100,tick));
                        track.add(makeEvent(128,9,inst,100,tick+1));
                    }
                    channel++;
                    if(channel>10)
                    channel = 1;
                }
                tick+=4;
            }
            S.setSequence(seq);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public void createMelody()
    {
        int inst,i,j,tick;
        tick=i=0;
        j=1;
        try
        {            
            //Get the sequence
            Sequence seq=new Sequence(Sequence.PPQ,4);
            //Create the track in the sequence
            Track track=seq.createTrack();
            
            //Make the midiEvent (Sound data) to be read by the sequencer and add it to the track
            for(JCheckBox temp: cBoxList)
            {
                if(temp.isSelected())
                {
                    inst=insts[i/16];
                    track.add(makeEvent(192,1,inst,0,tick));
                    track.add(makeEvent(144,1,2+(j*7),100,tick));
                    track.add(makeEvent(128,1,2+(j*7),100,tick+2));
                    tick+=4;
                }
                i++;
                j++;
                if(i%16==0)
                j=1;
            }
            S.setSequence(seq);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public MidiEvent makeEvent(int one, int two, int three, int four, int five)
    {
        MidiEvent event =null;
        try
        {
            //Create a message
            ShortMessage S=new ShortMessage();
        
            //Put the instruction in the message
            S.setMessage(one, two, three, four);
            event=new MidiEvent(S, five);
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return event;
    }
    
    
    class StartListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            try
            {
                createMelodyHFVM();
                S.setTempoInBPM(t);
                S.start();
                //System.out.println(S.getTempoInBPM());
                //S.setTempoInBPM(200);
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }
    
    class StopListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            try
            {
                S.stop();
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }
    
    class TempoUpListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            t=t+5.0F;
            S.setTempoInBPM(t);
            int temp=(int)t;
            tempo.setText("Tempo: "+String.valueOf(temp)+" BPM");
            //System.out.println(t);
        }
    }
    
    class TempoDownListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            t=t-5.0F;
            if(t<0.0F)
            t=0.0F;
            S.setTempoInBPM(t);
            int temp=(int)t;
            tempo.setText("Tempo: "+String.valueOf(temp)+" BPM");
            assert(true) : "Tempo=" +t;
        }
    }
        
    class ClearListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            for(JCheckBox temp: cBoxList)
                temp.setSelected(false);
            // frame.repaint();
            
        }
    }
    
    class RepeaterListener implements ItemListener
    {
        public void itemStateChanged(ItemEvent e)
        {
            if(repeater.isSelected())
            S.setLoopCount(S.LOOP_CONTINUOUSLY);
            //System.out.println(S.LOOP_CONTINUOUSLY);
            else
            S.setLoopCount(0);
            //System.out.println("0");
        }
    }
    
    class SaveMenuListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            int state;
            JFileChooser dialog = new JFileChooser();
            dialog.setMultiSelectionEnabled(false);
            state = dialog.showSaveDialog(frame);
            dialog.setVisible(true);
            if(state!=JFileChooser.CANCEL_OPTION && state!=JFileChooser.ERROR_OPTION)
            saveFile(dialog.getSelectedFile());
        }
    }
    
    private void saveFile(File file)
    {
        boolean []list = new boolean[256];
        
        for(int i=0;i<256;i++)
        {
            JCheckBox box = cBoxList.get(i);
            list[i] = box.isSelected();
            
        }
        try
        {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(list);
        
            oos.close();
            fos.close();
                      
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
       
             
              
    }
    
    class LoadMenuListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            int state;
            JFileChooser dialog = new JFileChooser();
            dialog.setMultiSelectionEnabled(false);
            state = dialog.showDialog(frame, "Load");
            dialog.setVisible(true);
            if(state!=JFileChooser.CANCEL_OPTION && state!=JFileChooser.ERROR_OPTION)
            loadFile(dialog.getSelectedFile());
        }
    }

    
    private void loadFile(File file)
    {
        boolean errFlag = true;
        FileInputStream fis;
        ObjectInputStream ois;
        
        S.stop();
        boolean []list = new boolean[256];
                       
        try
        {
        
        fis = new FileInputStream(file);
        ois = new ObjectInputStream(fis);
        Object ob = ois.readObject();
            
        if(ob instanceof boolean[])
        list = (boolean[])ob;      
            
            
        ois.close();
        fis.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            
        }
        
        
        for(int i=0;i<256;i++)
        {
            JCheckBox box = cBoxList.get(i);
            if(list[i])
            box.setSelected(true);
            else
            box.setSelected(false);
        }
        createMelodyHFVM();
    
    }
        
    class AboutMenuItemListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            String info = String.format("BeatBox Version 2.3\n\nCoded and Designed by: Saqib Nizam Shamsi\nCredits: Kathy Sierra and Bert Bates (Head First: Java)");
            ImageIcon icon = new ImageIcon("..//resources//Icon64.png");
            JOptionPane about = new JOptionPane();
            about.showMessageDialog(frame, info, "About BeatBox", JOptionPane.INFORMATION_MESSAGE, icon);
        }
    }
    
    class AboutPanel extends JPanel
    {
        public void paintComponent(Graphics g)
        {
            g.setColor(Color.black);
            Font customFont = new Font(Font.SANS_SERIF, Font.BOLD, 28);
            g.setFont(customFont);
            g.drawString("BeatBox Version 2.3",5,24);
            //Font regularFont = new Font(Font.SERIF);
            
        }
    }
    
    class TempoSliderListener implements ChangeListener
    {
        public void stateChanged(ChangeEvent e)
        {
            t=tempoSlider.getValue();
            S.setTempoInBPM(t);
            tempoSliderR.setText(fix(String.valueOf((int)t),3));
            
        }
    }
    
    public String fix(String unfixed, int len)
    {
        String fixed;
        int l;
        l=unfixed.length();
        fixed=unfixed;
        while(l<len)
        {
            fixed="0"+fixed;
            l=fixed.length();
        }
        return fixed;
    }
    
    class RandomizeListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            for(JCheckBox temp: cBoxList)
                temp.setSelected(false);
            Random generator = new Random();
            int numberOfBoxes = generator.nextInt(256);
            int list[] = new int[numberOfBoxes];
            for(int i=0;i<numberOfBoxes;i++)
            list[i] = generator.nextInt(256);
            Arrays.sort(list);
            //System.out.println(Arrays.toString(list));
            int len = list.length;
            for(int i=0;i<len;i++)
            {
               JCheckBox tmp = cBoxList.get(list[i]);
               tmp.setSelected(true);
            }
               
            
        }
    }
    
    class ExitMenuListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            System.exit(0);
        }
    }
        
}
