/*Author: Shenaz Narsidani*/
import java.io.*;
import java.util.*;
import java.lang.Math.*;
class homework{

    static HashMap<Integer, String> legalMoves;
    
    public static void main(String[] args){
        homework h=new homework();    
        legalMoves = new HashMap<>();
        legalMoves.put(1,"1 0 0");
        legalMoves.put(2,"-1 0 0") ;
        legalMoves.put(3,"0 1 0") ;
        legalMoves.put(4,"0 -1 0") ;
        legalMoves.put(5,"0 0 1") ;
        legalMoves.put(6,"0 0 -1") ;
        legalMoves.put(7,"1 1 0") ;
        legalMoves.put(8,"1 -1 0") ;
        legalMoves.put(9,"-1 1 0") ;
        legalMoves.put(10,"-1 -1 0") ;
        legalMoves.put(11,"1 0 1") ;
        legalMoves.put(12,"1 0 -1") ;
        legalMoves.put(13,"-1 0 1") ;
        legalMoves.put(14,"-1 0 -1") ;
        legalMoves.put(15,"0 1 1") ;
        legalMoves.put(16,"0 1 -1") ;
        legalMoves.put(17,"0 -1 1") ;
        legalMoves.put(18,"0 -1 -1") ;
        try{
        File f=new File("input.txt");
        BufferedReader br= new BufferedReader(new FileReader(f));

        String algo=br.readLine();
        Node gridBoundary=h.new Node(br.readLine());
        Node start=h.new Node(br.readLine());
        Node goal=h.new Node(br.readLine());
        int numOfNodes=Integer.valueOf(br.readLine());
        
        HashMap<String, NodesForQueue> explored= new HashMap<String, NodesForQueue>();
        HashMap<String, Vector<String>> adjacancyList = new HashMap<String, Vector<String>>();
        
            for(int i=0;i<numOfNodes;i++){
                String s=br.readLine();
                Node node=h.new Node(s);
                if(node.x >=0 && node.x<gridBoundary.x && node.y>=0 && node.y<gridBoundary.y && node.z >=0 && node.z<gridBoundary.z){  
                explored.put(node.nodeString, h.new NodesForQueue(node,0,null,false));
                adjacancyList.put(node.nodeString, h.getAdjacentNodes(node, s, gridBoundary));
                    
                }
                /**else{
                    explored.put(node.nodeString, null);
                     adjacancyList.put(node.nodeString, null);
                }**/
                
            }
            /**for (Map.Entry<String, Boolean> e : explored.entrySet())
            System.out.println("Key: " + e.getKey()
                               + " Value: " + e.getValue());
            for (Map.Entry<String, Vector<String>> e : adjacancyList.entrySet())
            System.out.println("Key: " + e.getKey()
                               + " Value: " + e.getValue().size());**/
            switch (algo) {
            case "UCS": h.UCS(start, goal, gridBoundary, explored, adjacancyList);
                        break;
            case "BFS": h.BFS(start, goal, gridBoundary, explored, adjacancyList);
                        break;
            case "A*":  h.AStar(start, goal, gridBoundary, explored, adjacancyList);
                        break;
            default: System.out.println("invalid case");
                        break;
        }
                
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
    
    void BFS(Node start,Node goal, Node gridBoundary, HashMap<String, NodesForQueue> explored, HashMap<String, Vector<String>> adjacancyList){
        
        //System.out.println("Executing BFS");
        Queue<NodesForQueue> queue = new LinkedList<>();
        
        int cost=0;
        //===========added the check===================
        if(explored.get(start.nodeString)!= null){
            explored.get(start.nodeString).distance=0;
            queue.add(explored.get(start.nodeString));
        }
        while(!queue.isEmpty()){
            NodesForQueue n=queue.peek();
            
            if(n.node.nodeString.equals(goal.nodeString)){
                //System.out.println("Reached goal node-"+n.node.nodeString+" with distance- "+n.distance);
                List<NodesForQueue> st= new ArrayList<NodesForQueue>();
                while(!n.node.nodeString.equals(start.nodeString)){
                    
                    st.add(n);
                    n=explored.get(n.parent.nodeString);
                }
                
                st.add(n);
                Collections.reverse(st);
                //System.out.println("Nodes -"+st.size());
                
                try{
                    File output= new File("output.txt");
                    BufferedWriter writer= new BufferedWriter(new FileWriter(output));

                    writer.write(String.valueOf(st.get(st.size()-1).distance));
                    writer.newLine();
                    writer.write(String.valueOf(st.size()));
                    writer.newLine();
                    //System.out.println(dist);
                    //System.out.println(st.size());

                    /** =================Printing output Nodes=======================**/
                    //System.out.println(start.nodeString+" 0");
                    writer.write(start.nodeString+" 0");
                    for(int i=1;i<st.size();i++){
                        writer.newLine();
                        NodesForQueue r=st.get(i);
                        NodesForQueue k=st.get(i-1);
                        writer.write(r.node.nodeString+" "+String.valueOf(r.distance-k.distance));
                        //System.out.println(r.node.nodeString+" "+String.valueOf(r.distance-k.distance));
                    }
                    writer.flush();
                    writer.close();
                }
                catch(Exception e){
                e.printStackTrace(System.out);}
                break;
            }
            
            explored.get(n.node.nodeString).explored=true;
            Vector<String> adjOfNode= adjacancyList.get(n.node.nodeString);
            Iterator<String> iterator = adjOfNode.iterator();
            
            
            while(iterator.hasNext()){
                NodesForQueue childNode=explored.get(iterator.next());
                //==============added a condition=================
                if(childNode!=null && !childNode.explored){
                    childNode.parent=n.node;
                    childNode.distance=n.distance+(int)childNode.node.BFSdistance(n.node);
                    queue.add(childNode);
                }
            }
            queue.poll();
        }
        if(queue.isEmpty()){
            try{
                File output= new File("output.txt");
                BufferedWriter writer= new BufferedWriter(new FileWriter(output));
                writer.write("FAIL");
                writer.flush();
                writer.close();
            }
            catch(Exception e){
                e.printStackTrace(System.out);}
            //System.out.println("FAIL");
        }
    }
   
    void UCS(Node start,Node goal, Node gridBoundary, HashMap<String, NodesForQueue> explored, HashMap<String, Vector<String>> adjacancyList){
        //System.out.println("Executing UCS");
        int cost=0;
        PriorityQueue<NodesForQueue> p= new PriorityQueue<NodesForQueue> (new Comparator<NodesForQueue> () {
            public int compare(NodesForQueue x, NodesForQueue y) {
               return (int)Math.round(x.distance - y.distance);
            }
         }
        );
        //===========added the check===================
        if(explored.get(start.nodeString)!= null){
            explored.get(start.nodeString).distance=0;
            p.add(explored.get(start.nodeString));
        }
        while(!p.isEmpty()){
            NodesForQueue n=p.peek();
            
            if(n.node.nodeString.equals(goal.nodeString)){
                //System.out.println("Reached goal node-"+n.node.nodeString+" with distance- "+n.distance);
                List<NodesForQueue> st= new ArrayList<NodesForQueue>();
                while(!n.node.nodeString.equals(start.nodeString)){
                    
                    st.add(n);
                    n=explored.get(n.parent.nodeString);
                }
                
                st.add(n);
                Collections.reverse(st);
                //System.out.println("Nodes -"+st.size());
                //System.out.println(st.get(st.size()-1).distance);
                //System.out.println(st.size());
                /** =================Printing output Nodes=======================**/
                try{
                    File output= new File("output.txt");
                    BufferedWriter writer= new BufferedWriter(new FileWriter(output));

                    writer.write(String.valueOf(st.get(st.size()-1).distance));
                    writer.newLine();
                    writer.write(String.valueOf(st.size()));
                    writer.newLine();
                    //System.out.println(dist);
                    //System.out.println(st.size());

                    /** =================Printing output Nodes=======================**/
                    //System.out.println(start.nodeString+" 0");
                    writer.write(start.nodeString+" 0");
                    for(int i=1;i<st.size();i++){
                        writer.newLine();
                        NodesForQueue r=st.get(i);
                        NodesForQueue k=st.get(i-1);
                        writer.write(r.node.nodeString+" "+String.valueOf(r.distance-k.distance));
                        //System.out.println(r.node.nodeString+" "+String.valueOf(r.distance-k.distance));
                    }
                    writer.flush();
                    writer.close();
                }
                catch(Exception e){
                System.out.println(e);}
                
                /**System.out.println(start.nodeString+" 0");
                for(int i=1;i<st.size();i++){
                    NodesForQueue r=st.get(i);
                    NodesForQueue k=st.get(i-1);
                    System.out.println(r.node.nodeString+" "+String.valueOf(r.distance-k.distance));
                }**/
                break;
            }
            
            explored.get(n.node.nodeString).explored=true;
            
            Vector<String> adjOfNode= adjacancyList.get(n.node.nodeString);
            Iterator<String> iterator = adjOfNode.iterator();
            
            while(iterator.hasNext()){
                NodesForQueue childNode=explored.get(iterator.next());
                //==============added a condition=================
                if(childNode!=null && !childNode.explored){
                    /**childNode.parent=n.node;
                    childNode.distance=n.distance+(int)childNode.node.UCSdistanceFrom(n.node);
                    p.add(childNode);**/
                    
                    if(childNode.parent==null){
                        childNode.parent= n.node;
                        childNode.distance=n.distance+(int)childNode.node.UCSdistanceFrom(n.node);
                    }
                    else if((n.distance+(int)childNode.node.UCSdistanceFrom(n.node))>childNode.distance){
                    }
                    else{
                        childNode.parent= n.node;
                        childNode.distance=n.distance+(int)childNode.node.UCSdistanceFrom(n.node);
                    }
                    p.add(childNode);
                }
            }
            p.poll();
        }
        if(p.isEmpty()){
            try{
                File output= new File("output.txt");
                BufferedWriter writer= new BufferedWriter(new FileWriter(output));
                writer.write("FAIL");
                writer.flush();
                writer.close();
            }
            catch(Exception e){
                System.out.println(e);}
            //System.out.println("FAIL");
        }
        
    }
    
    void AStar(Node start,Node goal, Node gridBoundary, HashMap<String, NodesForQueue> explored, HashMap<String, Vector<String>> adjacancyList){
        //System.out.println("Executing A*");
        int cost=0;
        PriorityQueue<NodesForQueue> p= new PriorityQueue<NodesForQueue> (new Comparator<NodesForQueue> () {
            public int compare(NodesForQueue x, NodesForQueue y) {
               return (int)Math.round(x.distance + x.heuristicDistance - (y.distance + y.heuristicDistance));
            }
         }
        );
        //===========added the check===================
        if(explored.get(start.nodeString)!= null){
            explored.get(start.nodeString).distance=0;
            p.add(explored.get(start.nodeString));
        }
        while(!p.isEmpty()){
            NodesForQueue n=p.peek();
            //System.out.println(n.node.nodeString+" with distance- "+n.distance +"-"+ n.heuristicDistance);
            
            if(n.node.nodeString.equals(goal.nodeString)){
                //System.out.println("Reached goal node-"+n.node.nodeString+" with distance- "+n.distance);
                List<NodesForQueue> st= new ArrayList<NodesForQueue>();
                
                while(!n.node.nodeString.equals(start.nodeString)){
                    st.add(n);
                    n=explored.get(n.parent.nodeString);
                }
                st.add(n);
                Collections.reverse(st);
                //System.out.println("Nodes -"+st.size());
                //System.out.println(st.get(st.size()-1).distance);
                //System.out.println(st.size());
                /** =================Printing output Nodes=======================**/
                
                try{
                    File output= new File("output.txt");
                    BufferedWriter writer= new BufferedWriter(new FileWriter(output));

                    writer.write(String.valueOf(st.get(st.size()-1).distance));
                    writer.newLine();
                    writer.write(String.valueOf(st.size()));
                    writer.newLine();
                    //System.out.println(dist);
                    //System.out.println(st.size());

                    /** =================Printing output Nodes=======================**/
                    //System.out.println(start.nodeString+" 0");
                    writer.write(start.nodeString+" 0");
                    for(int i=1;i<st.size();i++){
                        writer.newLine();
                        NodesForQueue r=st.get(i);
                        NodesForQueue k=st.get(i-1);
                        writer.write(r.node.nodeString+" "+String.valueOf(r.distance-k.distance));
                        //System.out.println(r.node.nodeString+" "+String.valueOf(r.distance-k.distance));
                    }
                    writer.flush();
                    writer.close();
                }
                catch(Exception e){
                System.out.println(e);}
                /**System.out.println(start.nodeString+" 0");
                for(int i=1;i<st.size();i++){
                    NodesForQueue r=st.get(i);
                    NodesForQueue k=st.get(i-1);
                    System.out.println(r.node.nodeString+" "+String.valueOf(r.distance-k.distance));
                }**/
                break;
            }
            
            explored.get(n.node.nodeString).explored=true;
            
            Vector<String> adjOfNode= adjacancyList.get(n.node.nodeString);
            Iterator<String> iterator = adjOfNode.iterator();
            
            while(iterator.hasNext()){
                NodesForQueue childNode=explored.get(iterator.next());
                //==============added a condition=================
                if(childNode!=null && !childNode.explored){
                                     
                    if(childNode.parent==null){
                        childNode.parent= n.node;
                        childNode.distance=n.distance+(int)childNode.node.UCSdistanceFrom(n.node);
                    }
                    
                    else if((n.distance+(int)childNode.node.UCSdistanceFrom(n.node))>childNode.distance){
                    }
                    else{
                        childNode.parent= n.node;
                        childNode.distance=n.distance+(int)childNode.node.UCSdistanceFrom(n.node);
                    }                   
                    childNode.heuristicDistance= (int)childNode.node.ManhattanDistance(goal);
                    //System.out.println(childNode.node.nodeString+"-mahattan distance"+childNode.heuristicDistance);
                    p.add(childNode);
                }
            }
            p.poll();
        }
        if(p.isEmpty()){
            try{
                File output= new File("output.txt");
                BufferedWriter writer= new BufferedWriter(new FileWriter(output));
                writer.write("FAIL");
                writer.flush();
                writer.close();
            }
            catch(Exception e){
                System.out.println(e);}
            //System.out.println("FAIL");
        }
    }
    
    Vector<String> getAdjacentNodes(Node node, String s, Node gridBoundary){
        String[] coords=s.split(" ");
        Vector<String> v=new Vector<String>();
        //System.out.println("Neighbours of "+node.nodeString);
        for(int i=3;i<coords.length;i++){
                   Node n=node.add(new Node(legalMoves.get(Integer.valueOf(coords[i]))));
                    if(node.x >=0 && node.x<gridBoundary.x && node.y>=0 && node.y<gridBoundary.y && node.z >=0 && node.z<gridBoundary.z)
                    { v.add(n.nodeString);
                  // System.out.println(n.nodeString);
                    }
         } 
        return v;
    }
    
    public class Node {
        int x;
        int y;
        int z;
        String nodeString;
        
        Node add(Node n){
           return new Node(this.x+n.x,this.y+n.y,this.z+n.z);
        }
        double UCSdistanceFrom(Node n){
            
            int x1=Math.abs(this.x-n.x);
            int y1=Math.abs(this.y-n.y);
            int z1=Math.abs(this.z-n.z);
            if(x1>0 && y1>0 && z1>0){
                // all 3 nodes change , distance is 10 + 24
                return 24; 
            }else if((x1>0 && y1>0 && z1==0) || (x1==0 && y1>0 && z1>0) || (x1>0 && y1==0 && z1>0)) {
                // only change in 2D - distance is 14
                return 14;
            }
            else if((x1>0 && y1==0 && z1==0) || (x1==0 && y1>0 && z1==0) || (x1==0 && y1==0 && z1>0)) {
                //change in 1D , distance is 10
                return 10;
            }
           //return (int)Math.round(Math.pow(Math.pow(10*(this.x-n.x),2)+Math.pow(10*(this.y-n.y),2)+Math.pow(10*(this.z-n.z),2),0.5));
            return 0;
        }
        double BFSdistance(Node n){
            return (double)1;
        }
        double ManhattanDistance(Node n){
            return (double)(Math.abs(this.x-n.x)+Math.abs(this.y-n.y)+Math.abs(this.z-n.z));
        }
        
        Node(String s){
           String[] coords=s.split(" ");
           this.x=Integer.valueOf(coords[0]);
           this.y=Integer.valueOf(coords[1]);
           this.z=Integer.valueOf(coords[2]);
           this.nodeString=new String(""+this.x+" "+this.y+" "+this.z);
        }
        Node(int x, int y, int z){
            this.x=x;
            this.y=y;
            this.z=z;
            this.nodeString=new String(""+x+" "+y+" "+z);
        }
    }
    public class NodesForQueue {
        Node node;
        int distance;
        Node parent;
        boolean explored;
        int heuristicDistance;
        
        NodesForQueue(Node node, int distance, Node parent, boolean explored){
            this.node=node;
            this.distance=distance;
            this.parent=parent;
            this.explored=explored;
        }
    }
    
}
