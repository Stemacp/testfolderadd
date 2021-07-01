import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class AssemblyLine implements Runnable {
  private final int MAX_CAPACITY_OF_ASSEMBLY_LINE = 5;
  private Queue<Component> neededPartsQueue = new LinkedList<Component>();
  protected List<Component> componentsBeingBuilt = new ArrayList<Component>();
  private static final AssemblyLine INSTANCE = new AssemblyLine();
  private Queue<Component> componentsToBeBuilt = new LinkedList<Component>();
  private List<Plane> planesBuilt = new ArrayList<Plane>();
  public boolean assemblyLineWorking = false;
  ThreadPoolExecutor robots = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
  public int time = 0;

  private AssemblyLine() 
  {
  }

  public static AssemblyLine getInstance() 
  {
    return INSTANCE;
  }

  public void buildPlane() {
    Thread thread = new Thread(AssemblyLine.getInstance());
    thread.start();
    assemblyLineWorking = true;
  }

  public synchronized void addComponentToQueue(Component component) 
  {
    componentsToBeBuilt.add(component);
    addComponentToBeBuilt(componentsToBeBuilt);
  }

  public synchronized void addComponentToBeBuilt(Queue<Component> queue) 
  {
    assemblyLineWorking = true;
    Component component = queue.remove();
    if (component == null) {
      System.out.println("YOU'RE INSERTING A NULL COMPONENT INTO THE LINE");
    }
    if (componentsBeingBuilt.size() < MAX_CAPACITY_OF_ASSEMBLY_LINE) {
      componentsBeingBuilt.add(component);
      component.build();
    } else {
      neededPartsQueue.add(component);
    }
  }

  public synchronized void notifyComponentDoneAssembly(Component component) 
  {
    System.out.println("Adding " + component.getComponentType() + " to plane");
    componentsBeingBuilt.remove(component);
    System.out.println(componentsBeingBuilt.size() + " Components currently being built.");
    Component nextComponent = neededPartsQueue.poll();
    if (nextComponent != null)
      addComponentToQueue(nextComponent);
    else {
      System.out.println("Next component in queue is null, queue size is: " + neededPartsQueue.size());
    }
    if (componentsBeingBuilt.size() == 0 && neededPartsQueue.size() == 0) {
      System.out.println("Assembly line has stopped.");
      assemblyLineWorking = false;
    }
  }

  public synchronized void incrementTime(int time)
  {
    time ++;
  }

  @Override
  public void run() {

    System.out.println("Planes coming in");
    List<Plane> planeQueue = new ArrayList<Plane>();
    for (int i = 1; i <= 10; i++) {
      System.out.println("Plane " + i + " coming in");
      Plane plane = new Plane();
      if(plane.timer <= time)
      {
        planeQueue.add(plane);
        incrementTime(time);
      }
      else
      {
        incrementTime(time);
      }
    }
    for (Plane workingPlane : planeQueue) {
      while (!workingPlane.isBuilt()) {

      }
      planesBuilt.add(workingPlane);
      System.out.println("Done building plane " + planesBuilt.size() + ".");
    }
  }

  public List<Plane> getCarsBuilt() {
    return planesBuilt;
  }

  public void setCarsBuilt(List<Plane> planesBuilt) {
    this.planesBuilt = planesBuilt;
  }

  public static void main(String[] args) {
    try {
      PrintStream fileOut = new PrintStream("./output.dat");
      System.setOut(fileOut);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    AssemblyLine al = new AssemblyLine();
    al.run();
    }
    
  }

  