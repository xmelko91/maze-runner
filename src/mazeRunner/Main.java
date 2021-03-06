package mazeRunner;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class Main {


    public static void main(String[] args) throws IOException {
        mm();
    }




    private static void mm() throws IOException {
        Brick brick = null;
        boolean menuState = false;

        Scanner sc = new Scanner(System.in);
        int choise = -1;
        while (true) {
            choise = menuOut(menuState);
            switch (choise) {
                case 1:
                    try {
                        System.out.println("Input size of maze:");
                        brick = new Brick(sc.nextInt(), sc.nextInt());
                    } catch (Exception e) {
                    }
                    System.out.println("Maze generated!");
                    menuState = true;
                    break;
                case 2:
                    System.out.println("Input path to file");
                    String path = sc.nextLine();
                    try {
                        brick = new Brick(path);
                    }catch (Exception e){
                        System.out.println("No file found!");
                        break;
                    }
                    System.out.println("Maze loaded!");
                    menuState = true;
                    break;
                case 3:
                    System.out.println("Input file name");
                    String name = null;
                    name = sc.nextLine();
                    name = sc.nextLine();
                    assert brick != null;
                    brick.saveBrick(name);
                    break;
                case 4:
                    assert brick != null;
                    brick.printBrick();
                    break;
                case 5:
                    assert brick != null;
                    brick.findWay();
                    brick.printBrick();
                    break;
                case 0:
                    System.out.println("Bye bye!");
                    System.exit(1);
            }
        }
    }

    private static int menuOut(boolean nm){
        Scanner sc = new Scanner(System.in);
        System.out.println("---Menu---");
        System.out.println("1.Generate new maze");
        System.out.println("2.Load a maze");
        if (nm)
            System.out.println("3.Save the maze\n4.Display the maze\n5.Find way out!");
        System.out.println("0.Exit");

        int choise = -1;
        while (choise == -1){
            String chois = sc.nextLine();
            try {
                choise = Integer.parseInt(chois);
            }catch (Exception e){
            }
            if (choise < 0 || choise >((nm)? 5 :2)){
                choise = -1;
                System.out.println("Bad input!");
            }
        }
        return choise;
    }
}

class Point implements Comparable<Point>, Comparator<Point>{
    int x;
    int y;
    int sum;
    Point parent;

    Point(int y, int x){
        this.x = x;
        this.y = y;
    }

    @Override
    public int compareTo(Point other){
        return (x == other.x && y == other.y? 1 : 0);
    }

    @Override
    public int compare(Point o1, Point o2) {
        return o1.sum > o2.sum? o2.sum : o1.sum;
    }
    @Override
    public boolean equals(Object p){
        Point z = (Point)p;
        if (x == z.x && y == z.y)
            return true;
        return false;
    }
}

class Sort implements Comparator<Point>{

    @Override
    public int compare(Point o1, Point o2) {
        return o1.sum > o2.sum? o2.sum : o1.sum;
    }

}

class Brick{
    int width;
    int height;
    int[][]maze;
    int[] end;
    ArrayList<String> zones = new ArrayList<>();

    Brick(String str) throws IOException {
        File file = new File(str);
        List<String> st = Files.readAllLines(file.toPath());
        maze = new int[st.size()][];
        for (int i = 0; i < st.size(); i++){
            String lline = st.get(i);
            maze[i] = new int[lline.length()];
            for (int k = 0; k < lline.length(); k++){
                maze[i][k] = (lline.charAt(k) == '1'? 1 : 0);
            }
        }
    }

    Brick(int width, int height){
        this.width = width % 2 == 0? width + 1 : width;
        this.height = height % 2 == 0? height + 1 : height;
        this.maze = new int[this.height][this.width];
        for (int[] m : this.maze)
            Arrays.fill(m, 1);
        fillZones();
        fillMaze();
    }


    public void findWay(){
        TreeSet<Point> points = new TreeSet<>(new Sort());
        TreeSet<Point> closed = new TreeSet<>(new Sort());
        Point buf;
        buf = new Point(1,1);
        buf.sum = getSum(buf);
        buf.parent = null;
        points.add(buf);


        maze[0][1] = 1;
        while (!points.isEmpty()){

            Point p = points.first();

            if (p.y == end[0] && p.x == end[1]){
                goParents(p);
                break;
            }
            if (maze[p.y + 1][p.x] == 0){
                buf = new Point(p.y + 1, p.x);
                if (!checkTree(points, buf) && !checkTree(closed, buf)) {
                    buf.parent = p;
                    buf.sum = getSum(buf);
                    points.add(buf);
                }
            }
            if (maze[p.y - 1][p.x] == 0){
                buf = new Point(p.y - 1, p.x);
                if (!checkTree(points, buf) && !checkTree(closed, buf)){
                    buf.parent = p;
                    buf.sum = getSum(buf);
                    points.add(buf);
                }
            }
            if (maze[p.y][p.x + 1] == 0){
                buf = new Point(p.y, p.x + 1);
                if (!checkTree(points, buf) && !checkTree(closed, buf)){
                    buf.parent = p;
                    buf.sum = getSum(buf);
                    points.add(buf);
                }
            }
            if (maze[p.y][p.x - 1] == 0){
                buf = new Point(p.y, p.x - 1);
                if (!checkTree(points, buf) && !checkTree(closed, buf)){
                    buf.parent = p;
                    buf.sum = getSum(buf);
                    points.add(buf);
                }
            }
            closed.add(p);
            points.pollFirst();



        }
        maze[0][1] = 0;
    }

    private boolean checkTree(TreeSet<Point> tree, Point q){
        for (Point p : tree){
            if (p.x == q.x && p.y == q.y)
                return true;
        }
        return false;
    }

    private void goParents(Point p){
        while (p.parent != null){
            maze[p.y][p.x] = 2;
            p = p.parent;
        }
        maze[0][1] = 2;
        maze[1][1] = 2;
    }

    private int getSum(Point p){
        return (end[0] - p.y + end[1] - p.x + 1);
    }

    private void fillMaze(){
        maze[0][1] = 0;

        maze[1][1] = 0;
        zones.remove("1 1");
        int[] currCoord = new int[2];
        currCoord[0] = 1;
        currCoord[1] = 1;
        while (zones.size() > 1 && currCoord[0] != -1){
            int rand = (int)(Math.random() * 4);
            for (int i = 0; i < 4; i ++){
                if (checkStep(currCoord, (rand + i) % 4)) {
                    break;
                }

                if (i == 3){
                    while (zones.size() > 1){
                        currCoord = newStart(new int[]{Integer.parseInt(zones.get(0).split("\\s+")[0]),Integer.parseInt(zones.get(0).split("\\s+")[1])});
                        if (currCoord[0] != -1)
                            break;
                    }
                }
            }
        }
        int n = width;
        while (true){
            if (maze[height - 2][n - 2] == 0) {
                maze[height - 1][n - 2] = 0;
                end = new int[]{height - 1, n - 2};
                break;
            }
            n--;
        }
    }

    private int[] newStart(int[] point){
        if (point[1] > 2 && maze[point[0]][point[1] - 2] == 0){
            zones.remove(point[0] + " " + point[1]);
            maze[point[0]][point[1]] = 0;
            maze[point[0]][point[1] - 1] = 0;
            return new int[]{point[0], point[1]};
        }
        else if (point[0] > 2 && maze[point[0] - 2][point[1]] == 0){
            zones.remove(point[0] + " " + point[1]);
            maze[point[0]][point[1]] = 0;
            maze[point[0] - 1][point[1]] = 0;
            return new int[]{point[0], point[1]};
        }
        else if (point[1] < maze[0].length - 2 && maze[point[0]][point[1] + 2] == 0){
            maze[point[0]][point[1]] = 0;
            maze[point[0]][point[1] + 1] = 0;
            zones.remove(point[0] + " " + point[1]);
            return new int[]{point[0], point[1]};
        }
        else if (point[0] < maze.length - 2 && maze[point[0]+ 2][point[1]] == 0){
            maze[point[0]][point[1]] = 0;
            maze[point[0] + 1][point[1]] = 0;
            zones.remove(point[0] + " " + point[1]);
            return new int[]{point[0], point[1]};
        }
        else {
            zones.remove(0);
            return new int[]{-1, -1};
        }
    }

    public void saveBrick(String name) throws IOException {
        File file = new File(name);
        FileWriter fw = new FileWriter(file,false);
        for (int[] nb : maze){
            for (int n : nb){
                fw.append(String.valueOf(n));
            }
            fw.write("\n");
        }
        fw.flush();
        fw.close();
    }

    private boolean checkStep(int[] coord, int nb){
        int[] t = new int[2];
        t[0] = coord[0];
        t[1] = coord[1];
        switch (nb){
            case 0:
                t[0] -= 2;
                if (chAr(t)){
                    zones.remove(t[0] + " " + t[1]);
                    maze[coord[0] - 2][coord[1]] = 0;
                    maze[coord[0] - 1][coord[1]] = 0;
                    coord[0] -= 2;
                    return true;
                }
                break;
            case 1:
                t[1] += 2;
                if (chAr(t)){
                    zones.remove(t[0] + " " + t[1]);
                    maze[coord[0]][coord[1] + 2] = 0;
                    maze[coord[0]][coord[1] + 1] = 0;
                    coord[1] += 2;
                    return true;
                }
                break;
            case 2:
                t[0] += 2;
                if (chAr(t)){
                    zones.remove(t[0] + " " + t[1]);
                    maze[coord[0] + 2][coord[1]] = 0;
                    maze[coord[0] + 1][coord[1]] = 0;
                    coord[0] += 2;
                    return true;
                }
                break;
            case 3:
                t[1] -= 2;
                if (chAr(t)){
                    zones.remove(t[0] + " " + t[1]);
                    maze[coord[0]][coord[1] - 2] = 0;
                    maze[coord[0]][coord[1] - 1] = 0;
                    coord[1] -= 2;
                    return true;
                }
                break;
        }
        return false;
    }

    private boolean chAr(int[] nb){
        for (String a : zones){
            if (Integer.parseInt(a.split("\\s+")[0]) == nb[0] && Integer.parseInt(a.split("\\s+")[1]) == nb[1])
                return true;
        }
        return false;
    }


    private void fillZones(){
        for (int i = 1; i < maze.length; i+=2){
            for (int k = 1; k < maze[0].length; k+=2){
                zones.add(i + " " + k);
            }
        }
    }

    public int[][] getMaze() {
        return maze;
    }

    void printBrick(){
        for (int[] i : maze){
            for (int k : i){
                outBrick(k);
            }
            System.out.println("");
        }

    }

    private void outBrick(int i){
        if (i == 2) {
            System.out.print("\u2593" + "\u2593");
            return;
        }
        System.out.print(i == 0? "  " : "\u2592" + "\u2592");
    }
}