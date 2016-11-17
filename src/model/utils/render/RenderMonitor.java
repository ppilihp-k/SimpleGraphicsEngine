package model.utils.render;

import multithreadedGeometrie.geometricCalculus.model.VectorTaskFactory;
import multithreadedGeometrie.geometricCalculus.model.vectoroperations.VectorSubtraction;
import singlethreadedGeometrie.geometricCalc.model.Vector;
import utils.Tupel;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.*;

/**
 * Created by PhilippKroll on 10.11.2016.
 */
public class RenderMonitor {

    private ExecutorService pool;

    private ConcurrentLinkedQueue<RenderTask> running;

    private ConcurrentLinkedQueue<RenderTask> waiting;

    private LinkedList<Tupel<RenderTask,Future<Tupel<Exception,RenderTask>>>> penting;

    public RenderMonitor(){
        running = new ConcurrentStrategyQueue<>();
        waiting = new ConcurrentStrategyQueue<>();
        penting = new LinkedList<>();
        pool = Executors.newCachedThreadPool();
        pool.submit(new Scheduler());
    }

    public void submit(RenderTask task){
        System.out.println("submitted task!");
        waiting.add(task);
    }

    private class Scheduler implements Callable<Integer> {

        private boolean TERMINATE;

        public Scheduler(){
            TERMINATE = false;
        }

        public void terminate(){
            TERMINATE = true;
        }

        public Integer call(){
            System.out.println("start scheduler");
            while(!TERMINATE){
                System.out.println("start checking!");
                if(running.size() > 0){
                    System.out.println("check waiting");
                   while(waiting.size() > 0){
                       RenderTask rt = waiting.poll();
                       SchedulTask task = new SchedulTask(rt,running);
                       Tupel<RenderTask,Future<Tupel<Exception,RenderTask>>> t = new Tupel<>();
                       t.first = rt;
                       Future<Tupel<Exception,RenderTask>> f = pool.submit(task);
                       t.second = f;
                       penting.add(t);
                   }
                    System.out.println("check penting");
                   while(penting.size() > 0){
                       Tupel<RenderTask,Future<Tupel<Exception,RenderTask>>> t = new Tupel<>();
                       try{
                           Tupel<Exception,RenderTask> t2 = t.second.get();
                           if(t2.first != null){
                               System.out.println("ab zu running !!");
                                running.add(t.first);
                           }
                           if(t2.second == null){
                               System.out.println("zurück zu waiting!");
                               waiting.add(t.first);
                           }
                       } catch (Exception e){
                           System.out.println("Exception!!");
                           waiting.add(t.first);
                       }
                   }
                } else {
                    System.out.println("keine wartenden, direkt ausführen!");
                    if(waiting.size() > 0){
                        running.add(waiting.poll());
                    }
                }
                System.out.println("scheck running");
                for (RenderTask r:running) {
                    pool.submit(r);
                    System.out.println("submitting !!!!");
                }
            }
            return 0;
        }
    }

    private class SchedulTask implements Callable<Tupel<Exception,RenderTask>>{
        private ConcurrentLinkedQueue<RenderTask> run;
        private  RenderTask check;

        public SchedulTask(RenderTask task,ConcurrentLinkedQueue<RenderTask> running){
            run = running;
            check = task;
        }

        public Tupel<Exception,RenderTask> call(){
            Iterator<RenderTask> iter = run.iterator();
            /*
            try{
                run.semaphoreRead.acquire();
            } catch (InterruptedException ie){
                System.out.println("interrupted exception in RenderMonitor!");
            }
            */
            RenderTask rt = null;
            Tupel<Exception,RenderTask> t = new Tupel<>();
            while(iter.hasNext()){
                rt = iter.next();
                boolean overlaps = true;
                try{
                     overlaps = overlaps(check,rt);
                } catch (InterruptedException ie){
                    t.first = ie;
                    t.second = null;
                } catch (ExecutionException ex){
                    t.first = ex;
                    t.second = null;
                } catch (Exception e){
                    t.first = e;
                    t.second = null;
                } finally {
                    if(t.first == null){
                        return t;
                    }
                }
                if(overlaps){
                    t.first = null;
                    t.second = null;
                    return t;
                }
            }
            //run.semaphoreRead.release();
            t.first = null;
            t.second = check;
            return t;
        }

        /**
         * this method checks, whether the polygon from r2 has one point located inside the polygon of r1
         * @param r1 a task, which should perform the rendering for its polygon
         * @param r2 a task, which should perform the rendering for its polygon
         * @return true, if the tasks rendering action takes place in different cutouts of the picturebuffer, false otherwise
         */
        private boolean overlaps(RenderTask r1,RenderTask r2) throws Exception,InterruptedException,ExecutionException{
            VectorTaskFactory taskFactory = new VectorTaskFactory();
            Vector[] verts1 = r1.getPolygon().getVertices();
            Vector[] verts2 = r2.getPolygon().getVertices();

            //prepare the subtractions (like in clip)
            LinkedList<Future<Object>> list = new LinkedList<>();
            for (int i = 0;i < verts1.length;i++){
                Future<Object> f = pool.submit(taskFactory.createVectorVectorSubtraction(verts1[i],verts1[(i+1)%verts1.length]));
                list.add(f);
            }
            for (Future<Object> f:list) {
                Tupel<Exception,Vector> t = (Tupel<Exception,Vector>)f.get();
                if(t.first != null){
                    throw t.first;
                }
                Vector v = t.second;
                //for each vector from the second polygon test, if one vertex is located on the right of the actual edge v of the polygon from t1
                for (Vector v2:verts2) {
                    Tupel<Exception,Double> t2 = (Tupel<Exception,Double>)(pool.submit(taskFactory.createVectorVectorScalarproduct(v,v2)).get());
                    if(t2.first != null){
                        throw t2.first;
                    }
                    //if r1.polygon.v2 is located clockwise, it is located inside r1.p => the overlap
                    if(t2.second <= 0){
                        return true;
                    }
                }
            }
            //if no overlapping was found, return false
            return false;
        }
    }

    private class ConcurrentStrategyQueue<E> extends ConcurrentLinkedQueue<E>{
        private Semaphore semaphoreRead;
        private Semaphore semaphoreWrite;
        public ConcurrentStrategyQueue(){
            super();
            semaphoreRead = new Semaphore(10000,true);
            semaphoreWrite = new Semaphore(1);
        }
        public LinkedList<E> filter(Filter f){
            try{
                semaphoreRead.acquire();
            } catch (InterruptedException ie){
                System.out.println("interrupted exception in RenderMonitor!");
            }
            LinkedList<E> list = new LinkedList<E>();
            Iterator<E> iter = super.iterator();
            while(iter.hasNext()){
                E next = iter.next();
                if(f.filter(next)){
                    list.add(next);
                }
            }
            semaphoreRead.release();
            return list;
        }

        public boolean add(E o){
            try{
                semaphoreWrite.acquire();
            } catch (InterruptedException ie){
                return false;
            }
            super.add(o);
            semaphoreWrite.release();
            return true;
        }

        public E poll(){
            try{
                semaphoreRead.acquire();
            } catch (InterruptedException ie){
                return null;
            }
            E o = super.poll();
            semaphoreRead.release();
            return o;
        }

        public boolean remove(Object o){
            try{
                semaphoreRead.acquire();
            } catch (InterruptedException ie){
                return false;
            }
            boolean b = super.remove(o);
            semaphoreRead.release();
            return b;
        }

        public boolean addAll(Collection<? extends E> c){
            try{
                semaphoreRead.acquire();
                semaphoreWrite.acquire();
            } catch (InterruptedException ie){
                return false;
            }
            super.addAll(c);
            semaphoreRead.release();
            semaphoreWrite.release();
            return true;
        }
    }

    private abstract class Filter<E>{
        public abstract boolean filter(E o);
    }
}
