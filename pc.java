package com.example.threads;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

class semaphore {
    protected int value = 0 ;
    protected semaphore() { value = 0 ; }
    protected semaphore(int initial) { value = initial ; }
    public synchronized void P() {
        value-- ;
        if (value < 0)
            try { wait() ; } catch( InterruptedException e ) { }
    }
    public synchronized void V() {
        value++ ; if (value <= 0) notify() ;
    }
}


class buffer {
    protected static  int size=100;

   public buffer(int size) {
   this.size = size;

    }
    private Queue<Integer> q =  new LinkedList<Integer>() ;
    private int inptr = 0;
    private int outptr = 0;


    public int getCounter() {
        return counter;
    }

    protected int counter=0;
    semaphore spaces = new semaphore(size);
    semaphore elements = new semaphore(0);
    public void produce(int value) {
        spaces.P();
        q.add(value);
        counter++;
        inptr = (inptr + 1) % size;
        elements.V();
    }
    public int consume() {
        int value;
        elements.P();
        value = q.remove();
        outptr = (outptr + 1) % size;
        spaces.V();
        return value;
    }
}




class producer extends Thread {
    buffer buf;

    int num;
    public producer(buffer buf,int num) {
        this.buf = buf;
        this.num =num;

    }
    public void run() {
        int count=0;
        for (int n = 1; n <= num; n++){
            for (int i = 2 ; i <= n / 2; i++,count=0) {
                if (n % i == 0) {
                   count=1;
                   break;
                }

            }
            if(count==0){
               // buf.counter++;
                buf.produce(new Integer(n));



            }

        }

    }
}



class consumer extends Thread {
    buffer buf;
    String address;



     int Greatest;
    public int getGreatest() {
        return Greatest;
    }
    int num;
    public consumer(buffer buf, int num, String address) {
        this.buf = buf;
        this.num =num;
        this.address=address;
    }
    public void run() {

        for (int i = 1; i <= buf.counter; i++){
            Greatest= buf.consume();
            List<String> lines = Arrays.asList(""+Greatest);

            Path file = Paths.get(address);
            try {
                Files.write(file, lines, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }





        }

    }









public class pc {



    public static int convert(String str) {
        int val = 0;
        System.out.println("String = " + str);

        // Convert the String
        try {
            val = Integer.parseInt(str);
        }
        catch (NumberFormatException e) {

            // This is thrown when the String
            // contains characters other than digits
            System.out.println("Invalid String");
        }
        return val;
    }

        public static void main(String[] args) {


            //Create the Frame


            JFrame jframe = new JFrame("Prime Number calculator");


            jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);



            jframe.setSize(500, 580);











            //Create the panel at bottom and add label, textArea and buttons


            JPanel panel1= new JPanel(); // this panel is not visible in output
            JPanel panel2= new JPanel();
            JPanel panel3= new JPanel();
            JPanel panel4= new JPanel();
            JPanel panel5= new JPanel();
            JPanel panel6= new JPanel();
            Color myColor1 = new Color(76,78,82);
            panel1.setBackground(myColor1);
            panel2.setBackground(myColor1);
            Color myColor2 = new Color(204, 204, 204);
            panel3.setBackground(myColor2);
            panel4.setBackground(myColor1);
            panel5.setBackground(myColor1);




            JLabel label = new JLabel("                                     N");
            Font newFont = new Font("Serif", Font.BOLD,22);

            label.setFont(newFont);

            JLabel label2 = new JLabel("                     Buffer Size");
            label2.setFont(newFont);

            JLabel label3 = new JLabel("                    Output File");
            label3.setFont(newFont);




            JTextField textField = new JTextField(20);
            JTextField textField2 = new JTextField(20);
            JTextField textField3 = new JTextField(20);

            JButton btn_send=new JButton("Start Procedure");




            //Adding Components to the frame.


            jframe.getContentPane().add(BorderLayout.NORTH, panel1);
            jframe.getContentPane().add(BorderLayout.SOUTH, panel2);
            jframe.getContentPane().add(BorderLayout.CENTER, panel3);
            jframe.getContentPane().add(BorderLayout.WEST, panel4);
            jframe.getContentPane().add(BorderLayout.EAST, panel5);

            panel3.add(textField);
            panel3.add(label);

            label.setLayout(new BorderLayout(10,10));
            label.setBorder(BorderFactory.createEmptyBorder(20,10,10,10));

            panel3.add(textField2);
            panel3.add(label2);

            label2.setLayout(new BorderLayout(10,10));
            label2.setBorder(BorderFactory.createEmptyBorder(20,10,10,10));

            panel3.add(textField3);
            panel3.add(label3);

            label3.setLayout(new BorderLayout(10,10));
            label3.setBorder(BorderFactory.createEmptyBorder(20,10,10,10));




            panel3.add(btn_send);
            btn_send.setLayout(new BorderLayout(10,10));
            btn_send.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));


            panel3.add(panel6,BorderLayout.AFTER_LINE_ENDS);
            panel6.setBackground(myColor1);
            panel6.setLayout(new BorderLayout(10,10));
            panel6.setBorder(BorderFactory.createEmptyBorder(15,250,15,250));




            jframe.setVisible(true);







            btn_send.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    int num=convert(textField.getText());
                    int b = convert(textField2.getText());
                    String address=textField3.getText();
                    buffer buf=new buffer(b);
                    producer P = new producer(buf,num);
                    consumer C = new consumer(buf,num,address);
                    long start = System.currentTimeMillis();
                    P.start();
                    C.start();
                    try {
                        P.join();
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                    try {
                        C.join();
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }

                    long end = System.currentTimeMillis();
                    JLabel label4 = new JLabel("The Greatest Prime Number");
                    JLabel output1 = new JLabel(String.valueOf(C.getGreatest()));
                    label4.setFont(newFont);
                    output1.setFont(newFont);
                    JLabel label5 = new JLabel("# of Prime Numbers Generated");
                    JLabel output2 = new JLabel(String.valueOf(buf.getCounter()));
                    label5.setFont(newFont);
                    output2.setFont(newFont);
                    long result= end-start;
                    JLabel label6 = new JLabel("Time Elapsed since the start of Processing");
                    JLabel output3 = new JLabel(""+result+"  ns");
                    label6.setFont(newFont);
                    output3.setFont(newFont);
                    panel3.add(label4);
                    panel3.add(output1);
                    label4.setLayout(new BorderLayout(10,10));
                    label4.setBorder(BorderFactory.createEmptyBorder(20,2,10,10));


                    panel3.add(label5);
                    panel3.add(output2);
                    label5.setLayout(new BorderLayout(10,10));
                    label5.setBorder(BorderFactory.createEmptyBorder(20,40,10,10));

                    panel3.add(label6);
                    panel3.add(output3);
                    label6.setLayout(new BorderLayout(10,10));
                    label6.setBorder(BorderFactory.createEmptyBorder(20,5,10,10));


                }
            });


        }


}



















