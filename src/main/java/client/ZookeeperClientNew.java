package client;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Administrator on 2017/3/29.
 */
public class ZookeeperClientNew {
    private Watcher watcher;
    private AsyncCallback.StringCallback stringCallback;
    private AsyncCallback.ChildrenCallback childrenCallback;
    private ZooKeeper zk;
    private final String connStr="154.127.52.173:2181/zk-book";
    private final CountDownLatch cdl=new CountDownLatch(1);

    public ZookeeperClientNew() {
        init();
    }
    private void init(){
        initWatcher();
        initStringCallBack();
        try {
            zk=new ZooKeeper(connStr,5000,watcher);
            System.out.println(zk.getState());
            cdl.await();
            System.out.println("初次连接成功!!!");

        } catch (IOException e) {
            e.printStackTrace();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
    private void initWatcher(){
        watcher=new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                System.out.println(">>>>>>>>>>"+ Thread.currentThread().getName());
                System.out.println(" rec envent is--"+watchedEvent);
                if(Event.KeeperState.SyncConnected==watchedEvent.getState()){
                    if(watchedEvent.getType()== Event.EventType.None&&watchedEvent.getPath()==null){
                        cdl.countDown();
                    }else if(watchedEvent.getType()== Watcher.Event.EventType.NodeChildrenChanged&&watchedEvent.getPath()!=null){
                        try {
                            List<String > childNodeList=zk.getChildren(watchedEvent.getPath(),watcher);
                            System.out.println(String.format("new child node list is--{{%s}}",childNodeList));
                        } catch (KeeperException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
    }

    private void initStringCallBack(){
        stringCallback=new AsyncCallback.StringCallback() {
            @Override
            public void processResult(int i, String s, Object o, String s1) {
                String pattern="the ret code is %d,the input path is %s,the ctx is %s,the real path is %s";
                Object[] params=new Object[]{i,s,o,s1};
                System.out.println(">>>>>"+Thread.currentThread().getName());
                System.out.println(String.format(pattern,params));
            }
        };
    }

    public void createNode(String path,String value){
       zk.create(path,value.getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT,stringCallback,"I am a ctx");

    }
    private List<String> getChildNodeList(String path){
        List<String > childNodeList=null;
        try {
           childNodeList=zk.getChildren(path,true);
            return childNodeList;
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return childNodeList;
    }


    public static void main(String[] args) {
        System.out.println(">>>>>>>>>>>>>>"+Thread.currentThread().getName());
            ZookeeperClientNew client=new ZookeeperClientNew();
            client.createNode("/books","bookList");
            client.createNode("/books","bookLL");

        try {
            Thread.sleep(12333);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
