package Server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.concurrent.ThreadPoolExecutor;

public class Worker2 implements Runnable {

    private String myName;
    private Socket socket;
    BufferedReader in;
    BufferedWriter out;

    public static ArrayList<String> room = new ArrayList<>();
    LinkedHashMap<String, Socket> map = new LinkedHashMap<>();
//    LinkedHashMap<String, String> mapWorker = new LinkedHashMap<>();
//    public static LinkedHashMap<Integer, ArrayList> mapChat = new LinkedHashMap<>();
//    ArrayList<LinkedHashMap> chatRoom = new ArrayList<>();

    public Worker2(Socket s, String name) throws IOException {
        this.socket = s;
        this.myName = name;
        this.in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        this.out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
    }

    public void run() {
        System.out.println("Client " + socket.toString() + " accepted");
        try {
            int id = 0;
            String input = "";
            while (true) {
                input = in.readLine();
                System.out.println("Server received: " + input + " from " + socket.toString() + " # Client " + myName);

                if (input.equals("BYEBYEBYE")) {
                    ServerFrame.listName.remove(myName);
                    ServerFrame.mapSocket.remove(myName);
                    for (Worker2 w : ServerFrame.workers) {
                        if (w.myName.equals(myName)) {
                            ServerFrame.workers.remove(w);
                            ServerFrame.workerTemp.remove(w);
                            break;
                        }
                    }
                    for (Worker2 w : ServerFrame.workers) {
                        w.out.write("ONLINE:" + ServerFrame.workers.size());
                        w.out.newLine();
                        w.out.flush();
                    }
                    break;
                }
                Worker2 w2 = null;
                if (input.equals("CREATE:" + myName)) {
                    w2 = new Worker2(socket, myName);
                    if (ServerFrame.workers.size() < 2) {
                        out.write("WAIT" + '\n');
                        out.flush();
                        System.out.println("Server write: WAIT" + " to " + myName);
                    } else {
                        Random r = new Random();
                        int i = r.nextInt(ServerFrame.workers.size());
                        Worker2 w1 = ServerFrame.workers.get(i);
                        while (w1.myName.equals(myName) || ServerFrame.workerTemp.contains(w1)) {
                            i = r.nextInt(ServerFrame.workers.size());
                            w1 = ServerFrame.workers.get(i);
                            System.out.println("Serve23");
                            if (!w1.myName.equals(myName) && !map.containsKey(w1.myName)) {
                                break;
                            }
                        }
                        if (ServerFrame.workerTemp.contains(w1)) {
                            out.write("USERISCHATTING" + '\n');
                            out.flush();
                        } else {
                            ServerFrame.workerTemp.add(w1);
                            w1 = ServerFrame.workers.get(i); // random ra người chat là B
                            w1.out.write(myName + " WANT TO CHAT:" + socket.getPort() + ":" + myName + '\n'); //gửi đến B là A WANT TO CHAT
                            w1.out.flush();
                            map.putIfAbsent(myName, socket);
                            map.putIfAbsent(w1.myName, w1.socket);
                            room.add(myName);
                            room.add(w1.myName);
                            System.out.println("Server write123: " + input + " to " + ServerFrame.workers.get(i).myName);
                        }
                    }
                }
                if (input.contains("NO:")) {
                    String temp[] = input.split(":");
                    System.out.println("Server.Worker2.run()" + temp[1]);
                    for (Worker2 w : ServerFrame.workers) {
                        if (w.socket.getPort() == socket.getPort()) {
                            ServerFrame.workerTemp.remove(w);
                        }
                        if (w.socket.getPort() == Integer.parseInt(temp[1])) {
                            w.out.write("DECLINE" + '\n');
                            w.out.flush();
                            System.out.println("Server write: " + "NO" + " to " + w.myName);
                        }
                    }
                }
                if (input.contains("IACCEPT:")) {
                    String temp[] = input.split(":");
                    System.out.println("Server.Worker2.run()" + temp[1]);
                    for (Worker2 w : ServerFrame.workers) {
                        if (w.socket.getPort() == Integer.parseInt(temp[1])) {
                            ServerFrame.workerTemp.add(w);
                            w.out.write("ACCEPTED:" + socket.getPort() + ":" + myName + '\n');
                            w.out.flush();
                            System.out.println("Server write: " + "ACCEPTED" + " to " + w.myName);
                        }
                    }
                }
                System.out.println("room: " + room.size());
                if (input.contains("TEXTINPUT:")) {
                    String split[] = input.split(":");
                    for (Worker2 worker : ServerFrame.workers) {
                        if (worker.socket.getPort() == Integer.parseInt(split[1])) {
                            worker.out.write("TEXT;From: " + myName + ": " + split[2] + '\n');
                            worker.out.flush();
                            System.out.println("Server write: " + split[2] + " to " + worker.myName);
                            break;
                        }
                    }
                }
                if (input.contains("EXITCHAT__:")) {
                    String split[] = input.split(":");
                    for (Worker2 worker : ServerFrame.workers) {
                        if (worker.socket.getPort() == socket.getPort()) {
                            ServerFrame.workerTemp.remove(worker);
                            System.out.println("map ten=mp: " + ServerFrame.workerTemp.size());
                        }
                    }
                    for (Worker2 worker : ServerFrame.workers) {

                        if (worker.socket.getPort() == Integer.parseInt(split[1])) {
                            worker.out.write("EXIT: " + myName + '\n');
                            worker.out.flush();
                            ServerFrame.workerTemp.remove(worker);
                            System.out.println("Server write: " + "EXIT" + " to " + worker.myName);
                            out.write("BACKTOLOGIN: " + myName + '\n');
                            out.flush();

                            break;
                        }
                    }
                }

            }
            System.out.println("Closed socket for client " + myName + " " + socket.toString());
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
