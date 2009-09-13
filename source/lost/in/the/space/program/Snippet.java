package lost.in.the.space.program;

import java.util.HashSet;
import java.util.Set;

import org.zootella.cheat.file.Name;
import org.zootella.cheat.net.name.Ip;


public class Snippet {
	
	public static void snippet() throws Exception {
		
		
		
		
		Set<Ip> set = new HashSet<Ip>();
		print(set);
		
		set.add(new Ip("1.2.3.4"));
		print(set);
		
		set.add(new Ip("1.2.3.5"));
		print(set);
		
		set.add(new Ip("1.2.3.5"));
		print(set);
		
		set.add(new Ip("1.2.3.4"));
		print(set);
		
		

		
		
		
	}
	
	private static void print(Set set) {
		System.out.println(set.size()  + " items:");
		for (Object o : set)
			System.out.println(o.toString());
	}
}
