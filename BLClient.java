/*
Brendan Lauterborn
COSC 650 Socket Programming Project-CLient
UDP server implementing stop-and-wait protocol
Fetches webpage and sends it to Client in 1024-byte chunks
*/

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

public class BLClient {
    public static void main(String args[]) throws Exception {

        /*
        Create an input reader that allows the user to type the web address
        Create a UDP client socket that allows communication with the server
        Translate the server hostname to IP add.
        */

        //create inpurt stream
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

        //create client socket
        DatagramSocket clientSocket = new DatagramSocket();
        
        //translate hostname to IP address
        InetAddress IPAddress = InetAddress.getByName("localhost");


        byte[] sendData = new byte[1024];
        byte[] receiveData = new byte[1024];


        /* 
        Prompt user to enter the web server
        COnvert the string into bytes to send to the server
        */
        System.out.print("Enter a web address: ");
        String sentence = inFromUser.readLine();
        sendData = sentence.getBytes();


        //create datagram with the webaddress to send to server port 11122
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 11122);

        //send datagram to server
        clientSocket.send(sendPacket);
        
        /* 
        We Prepare to recieve the webpage data in multiple 1024 byte chunks
        We initialize a buffer to accumulate all chunks into one complete 
        response
        */

        //Prepare to recieve webpage in chunks 
        boolean done = false;
        ByteArrayOutputStream fullPage = new ByteArrayOutputStream();
        final int chunkSize = 1024;


        /*
        We loop until all chunks are recieved
            recieve a UDP packet that has 
                i)sequence number(int),
                ii)payload length(int),
                iii)and payload data (bytes)
            store the recieved payload into full repsonse
            send the ACK packet to the server with toggled sequence number
            finish recieving when the last chunk is detected
        */

        while(!done){
            //create buffer for incoming packet
            byte[] receiveDataPacket = new byte[(Integer.BYTES * 2) + chunkSize];
            DatagramPacket receivePacket = new DatagramPacket(receiveDataPacket, receiveDataPacket.length);
            clientSocket.receive(receivePacket);

            //extract data from packet
            ByteBuffer buffer = ByteBuffer.wrap(receivePacket.getData(), 0, receivePacket.getLength());
            int seqNum = buffer.getInt();
            int payloadLength = buffer.getInt();
            
            byte[] payload = new byte[payloadLength];
            buffer.get(payload);
            fullPage.write(payload);

            System.out.println("Received chunk seq=" + seqNum + " size=" + payloadLength);

            /* 
            Prepare the ACK packet
                i) the ACK must be the toggled seq number
                ii) send it to the servers ephemeral port so that it does not think
                    that its a webserver
            
            */

            //Send ACK back to server
            ByteBuffer ackBuffer = ByteBuffer.allocate(4);
            ackBuffer.putInt(seqNum ^ 1);
            DatagramPacket ackPacket = new DatagramPacket(
                ackBuffer.array(),
                ackBuffer.array().length,
                receivePacket.getAddress(),
                receivePacket.getPort() //gets the ephemeral port that the clientHandler thread is using
            );
            clientSocket.send(ackPacket);
            
            /*
                If the payload size is less than the full chunk size,
                final chunk was recieved
                stop
            */


            if(payloadLength < chunkSize){
                done = true;
            }
        }

        /* 
        Print the total size of the webpage data that we recieved
        close the UDP socket and terminate
        */
        System.out.println("Received full page: " + fullPage.size() + " bytes");

        clientSocket.close();
    }
}
