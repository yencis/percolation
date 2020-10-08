import java.util.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
public class Percolate {
	
	private static boolean[] grid;
	private static int[] arr; //unionset
	private static int[] size;
	private static boolean[] opensurface; //an N^2 array mapping root to boolean whether or not filled with water
	private static boolean percolates;
	
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		int N = sc.nextInt();
		//double hole = sc.nextDouble();
		//experiment parameters
		int min = sc.nextInt(); //minimum percent
		int max=sc.nextInt(); //maximum percent
		double inc = sc.nextDouble(); //increment
		int G = sc.nextInt(); //trials
		double[][] data = new double[2][(max-min+1)*(int)(1/inc)+100];
		int x = 0;
		for (double i = min;i<=max;i+=inc,x++) {
			double t = 0;
			for (int j = 0;j<G;j++) {
				if (genGrid(N,i))
					t++;
			}
			System.out.println(i+": "+t/G);
			data[0][x] = i;
			data[1][x] = t/G;
		}
		render(data);
		/*
		while(true) {
			int a = sc.nextInt();
			int b = sc.nextInt();
			System.out.println(root(a)+" "+root(b));
			//System.out.println(find())
		}*/
	}
	
	public static void render(double[][] data) {
		/*
		DefaultPieDataset objDataset = new DefaultPieDataset();
		objDataset.setValue("Apple",29);
		objDataset.setValue("HTC",15);
		objDataset.setValue("Samsung",24);
		objDataset.setValue("LG",7);
		objDataset.setValue("Motorola",10);
		JFreeChart objChart = ChartFactory.createPieChart (
			    "Demo Pie Chart",   //Chart title
			    objDataset,          //Chart Data 
			    true,               // include legend?
			    true,               // include tooltips?
			    false               // include URLs?
			    );
		ChartFrame frame = new ChartFrame("Demo", objChart);
		frame.pack();
		frame.setVisible(true);*/
		DefaultXYDataset objDataset = new DefaultXYDataset();
		objDataset.addSeries("Chance",data);
		JFreeChart objChart = ChartFactory.createScatterPlot("Percentage of Percolation", "Percentage of Board Holed", "Chance of Percolating", objDataset);
		ChartFrame frame = new ChartFrame("Percolation",objChart);
		frame.pack();
		frame.setVisible(true);
	}
	
	public static boolean genGrid(int N, double hole) {
		percolates = false;
		int[] dir = {1,-1,N,-N};
		grid = new boolean[N*N];
		arr = new int[N*N];
		size = new int[N*N];
		opensurface = new boolean[N*N];
		//false no hole true hole
		//random grid in O(N^2)
		int holes = (int)((hole/100d)*(N*N));
		//System.out.println(holes);
		int[] holeloc = new int[N*N];
		ArrayList<Integer> h = new ArrayList<Integer>();
		for (int i = 0;i<N*N;i++) {
			arr[i] = i;
			size[i] = 1;
			holeloc[i] = i;
		}
		int count = N*N-1;
		for (int i =0;i<holes;i++) {
			int index = (int)(Math.random()*count);
			//System.out.println(index);
			h.add(holeloc[index]);
			int a = holeloc[index];
			int b = holeloc[count];
			holeloc[count]=a;
			holeloc[index] = b;
			count--;
		}
		Collections.sort(h);
		/*makes holes, unions holes with 4 neighbors
		 * if the hole is top row, it will put opensurface[root of itself after union] to true
		 * if the hole is bottom row, it will check if the root of itself after union is open to surface, if so, percolates = true */
		//for (int i=count+1;i<N*N;i++) {
		for (int i = 0;i<h.size();i++) {
			holeloc[i] = h.get(i);
			grid[holeloc[i]] = true;
			for (int j = 0;j<4;j++) {
				if (holeloc[i]+dir[j]>=0&&holeloc[i]+dir[j]<N*N) {
					if (isOpen(holeloc[i]+dir[j])) {
						boolean messy = false;
						if (opensurface[root(holeloc[i])]||opensurface[root(holeloc[i]+dir[j])])
							messy = true;
						weightedunion(holeloc[i]+dir[j],holeloc[i]);
						if (messy)
							opensurface[root(holeloc[i])] = true;
						if (holeloc[i]<N) {
							opensurface[root(holeloc[i])] = true; 
						}else if (holeloc[i]>=N*N-N) {
							root(holeloc[i]);
							if (opensurface[root(holeloc[i])]) {
								//System.out.println(root(holeloc[i])+" "+holeloc[i]);
								percolates = opening(holeloc[i])||opensurface[holeloc[i]];
								//percolates = true;
							}
						}
					}
				}
			}
		}
		//System.out.println(Arrays.toString(grid));
		int index = 0;
		/*
		for (int i = 0;i<N;i++) {
			for (int z=0;z<N;index++,z++) {
				System.out.print(((grid[index])?"o":"I")+" ");
			}
			System.out.print("\n");
		}*/
		//System.out.println("------------");
		index = 0;
		/*
		for (int i = 0;i<N;i++) {
			for (int z=0;z<N;index++,z++) {
				System.out.print(((opensurface[index])?"o":"I")+" ");
			}
			System.out.print("\n");
		}*/
		//System.out.println(percolates);
		return percolates;
	}
	
	public static boolean isOpen(int N) {
		return grid[N];
	}
	
	public static int convert2D(int r, int c, int N) {
		return r*(c/N)+c%N;
	}
	
	//functions for unionset
	public static int root2(int i)
    {
        while(arr[ i ] != i)           //chase parent of current element until it reaches root
        {
         i = arr[ i ];
        }
        return i;
    }

	public static int root(int i){
	    while(arr[ i ] != i){
	    	if (opensurface[i]||opensurface[arr[i]]||opensurface[arr[arr[i]]]) {
	        	opensurface[i] = true;
	        	opensurface[arr[i]] = true;
	        	opensurface[arr[arr[i]]] = true;
	        }
	        arr[ i ] = arr[ arr[ i ] ] ; 
	        i = arr[ i ]; 
	        
	    }
	    return i;
	}
	
	public static boolean opening(int i){
		boolean check = false;
	    while(arr[ i ] != i){
	        arr[ i ] = arr[ arr[ i ] ] ; 
	        i = arr[ i ]; 
	        if (opensurface[i]||opensurface[arr[i]]||opensurface[arr[arr[i]]]) {
	        	check=true;
	        	break;
	        }
	    }
	    return check;
	}
	

    /*modified union function where we connect the elements by changing the root of one of the elements*/

	public static void weightedunion(int A,int B)
    {
        int root_A = root(A);
        boolean open_A = opensurface[A];
        int root_B = root(B);
        boolean open_B = opensurface[B];
        if (opensurface[A]||opensurface[B]) {
        	opensurface[A] = true;
        	opensurface[B] = true;
        }
        if(size[root_A] < size[root_B]){
		    arr[root_A] = arr[root_B];
		    size[root_B] += size[root_A];
	    }else{
	       arr[root_B] = arr[root_A];
	       size[root_A] += size[root_B];
	    }

    }
	
    public static boolean find(int A,int B)
    {
        if(root(A)==root(B))       //if A and B have the same root, it means that they are connected.
        	return true;
        else
        	return false;
    }

}
