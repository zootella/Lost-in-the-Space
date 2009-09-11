package lost.in.the.space.program;

import java.util.HashSet;
import java.util.Set;


public class Snippet {
	
	public static void snippet() throws Exception {
		
		
		
		
		Set<String> set = new HashSet<String>();
		print(set);
		
		set.add("hello");
		print(set);
		
		set.add("hello");
		print(set);
		

		
		
		
	}
	
	private static void print(Set set) {
		System.out.println(set.size()  + " items:");
		for (Object o : set)
			System.out.println(o.toString());
	}
}
