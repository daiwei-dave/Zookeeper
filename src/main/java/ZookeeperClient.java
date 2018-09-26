import org.apache.zookeeper.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by chenjun on 2017/3/28.
 */
public class ZookeeperClient {
    private Watcher watcher;
    private AsyncCallback.StringCallback stringCallback;
    private final CountDownLatch cdl=new CountDownLatch(1);
    private final String connStr="localhost:2181/newbee";
    private void initServer()throws Exception{
         this.watcher=new Watcher() {
            public void process(WatchedEvent watchedEvent) {
                System.out.println("rec watch event is---"+watchedEvent);
                if(watchedEvent.getState()== Event.KeeperState.SyncConnected){
                    cdl.countDown();//123
                }
            }
        };

         stringCallback=new AsyncCallback.StringCallback() {
             public void processResult(int rc, String path, Object ctx, String name) {
                 System.out.println(String.format("the ret code is %d,the input path is %s,the ctx is %s,the real path is %s",
                         new Object[]{rc,path,ctx,name}
                 ));
             }
         };


        ZooKeeper zk=new ZooKeeper(connStr, 5000, watcher);
        System.out.println(zk.getState());
        cdl.await();
        System.out.println("the connection succeed!!!");
       zk.create("/too","too".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.PERSISTENT,stringCallback,"I am a context!!!");
        zk.create("/too","too12".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.PERSISTENT,stringCallback,"I am a context!!!");
        zk.create("/too","too12333".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.PERSISTENT_SEQUENTIAL,stringCallback,"I am a context!!!");

//        System.out.println("the create node path is--"+path);
        /*long sid=zk.getSessionId();
        byte[]skey=zk.getSessionPasswd();
        zk=new ZooKeeper(connStr,5000,watcher,99L,"byte".getBytes());
        Thread.sleep(100);
        zk=new ZooKeeper(connStr,5000,watcher,sid,skey);
        */

        Thread.sleep(Integer.MAX_VALUE);
    }


    public static void main(String[] args) {
        try {
            new ZookeeperClient().initServer();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("produce exception!!!");
        }
    }

}
