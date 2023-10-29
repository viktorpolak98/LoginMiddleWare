package Controller;

import Model.Request;

import java.sql.SQLException;
import java.util.LinkedList;

public class RequestHandler {
    private static final LinkedList<Request> queue = new LinkedList<>();
    private final DbCaller dbCaller = new DbCaller();

    public static void addRequest(Request request){
        queue.add(request);
    }

    private class RequestListener implements Runnable {
        @Override
        public void run(){
            boolean running = true;
            while(running){
                try {
                    running = dbCaller.isConnected();
                } catch (SQLException e){
                    e.printStackTrace();
                }
                if(!queue.isEmpty()){
                    Request request = queue.pop();
                    dbCaller.execute(request);
                }
            }
        }
    }
}
